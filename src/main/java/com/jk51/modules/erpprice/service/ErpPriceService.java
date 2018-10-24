package com.jk51.modules.erpprice.service;

import com.github.pagehelper.PageHelper;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.erpprice.*;
import com.jk51.model.order.Store;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.common.gaode.GaoDeService;
import com.jk51.modules.common.pojo.BatchResultDTO;
import com.jk51.modules.common.pojo.Point;
import com.jk51.modules.common.pojo.WalkResultDTO;
import com.jk51.modules.erpprice.domain.*;
import com.jk51.modules.erpprice.domain.pojo.ErpSettingPO;
import com.jk51.modules.erpprice.domain.pojo.ErpStorePO;
import com.jk51.modules.erpprice.mapper.BErpSettingMapper;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import com.jk51.modules.erpprice.mapper.ErpPriceSettingMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.persistence.mapper.SYbStoresGoodsPriceMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jk51.modules.erpprice.domain.ERPPriceType.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-10-27
 * 修改记录:
 */
@Service
public class ErpPriceService {
    public static final Logger logger = LoggerFactory.getLogger(ErpPriceService.class);

    private static final double SORT_FACTOR = .3D;

    @Autowired
    BGoodsErpMapper bGoodsErpMapper;
    @Autowired
    BStoresMapper bStoresMapper;

    @Autowired
    BErpSettingMapper bErpSettingMapper;

    @Autowired
    MerchantExtTreatMapper merchantExtTreatMapper;

    @Autowired
    GaoDeService gaoDeService;

    @Autowired
    StoresMapper storesMapper;

    @Autowired
    ErpPriceSettingMapper erpPriceSettingMapper;

    @Autowired
    SYbStoresGoodsPriceMapper storesGoodsPriceMapper;

    @Autowired
    ErpPriceImportService erpPriceImportService;
    @Autowired
    SyncService syncService;

    /**
     * 列表展示
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> erpPriceList(Map<String, Object> param) {
        Integer pageNum = Integer.parseInt(param.get("pageno").toString());
        Integer pageSize = Integer.parseInt(param.get("pageSize").toString());
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> result = bGoodsErpMapper.erpPriceList(param);
        return result;
    }

    /**
     * 本商户下门店所在的区域 type=3查市，type=4查区
     *
     * @param siteId
     * @param type
     * @return
     */
    public List<String> storeArea(Integer siteId, Integer type) {
        return bStoresMapper.storeAreaBySiteId(siteId, type);
    }

    public boolean deletePrice(int[] id) {
        BGoodsErpExample bGoodsErpExample = new BGoodsErpExample();
        BGoodsErpExample.Criteria bGoodsErpCriteria = bGoodsErpExample.createCriteria();
        bGoodsErpCriteria.andIdIn(Arrays.stream(id).boxed().collect(toList()));
        BGoodsErp bGoodsErp = new BGoodsErp();
        bGoodsErp.setStatus((byte) 20);
        int count = bGoodsErpMapper.updateByExampleSelective(bGoodsErp, bGoodsErpExample);
        if (count == id.length) {
            return true;
        } else {
            return false;
        }
    }

    public boolean batchChangePrice(String[] ids, Integer price) {
        int count = bGoodsErpMapper.batchChangePrice(ids, price);
        List<BGoodsErp> bGoodsErpList = bGoodsErpMapper.selectChangePriceInfos(ids);//获取所有的要更改的商品信息，同一种商品不同门店
        List<String> gCodesList = new ArrayList<>();
        for (BGoodsErp bGoodsErp : bGoodsErpList) {
            if (gCodesList.contains(bGoodsErp.getGoodsCode())) {
                continue;
            } else {
                gCodesList.add(bGoodsErp.getGoodsCode());
            }
        }
        syncService.syncStorePriceFromApp(bGoodsErpList.get(0).getSiteId(), gCodesList);
        if (count == ids.length) {
            return true;
        } else {
            return false;
        }
    }

    public boolean editPrice(String id, Integer price) {
        int count = this.bGoodsErpMapper.ChangePrice(id, price);
        BGoodsErp bGoodsErp = bGoodsErpMapper.selectByPrimaryKey(Integer.parseInt(id));
        List<String> gcodes = new ArrayList<>();
        gcodes.add(bGoodsErp.getGoodsCode());
        syncService.syncStorePriceFromApp(bGoodsErp.getSiteId(), gcodes);
        if (count == 1) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获取ERP价格门店
     *
     * @param siteId
     * @param areaCode
     * @param lat
     * @param lng
     */
    public ErpSettingPO getErpStore(Integer siteId, Integer areaCode, Float lat, Float lng) {
        Optional<MerchantExtTreat> merchantExt = Optional.of(merchantExtTreatMapper.selectByMerchantId(siteId));
        if (merchantExt.map(m -> m.getHas_erp_price()).orElse(0) == 0) {
            // 没有开启ERP
            return null;
        }

        ErpStorePO erpStore = bErpSettingMapper.selectErpStore(siteId);
        ErpSettingPO selected = null;

        if (Objects.nonNull(erpStore)) {
            byte type = erpStore.getType();
            if (type == STORE_BASE.toByte()) {
                Optional<Integer> bestStore = getLocationStore(siteId, lat, lng);
                if (bestStore.isPresent()) {
                    // 门店价  根据地理位置获取最近门店
                    selected = new ErpSettingPO();
                    selected.setAreaCode(areaCode);
                    selected.setPriority(100);
                    selected.setStoreId(bestStore.get());
                }
            } else {
                List<ErpSettingPO> erpSettings = erpStore.getErpSettings();
                if (CollectionUtils.isNotEmpty(erpSettings)) {
                    erpSettings.sort(Comparator.comparing(ErpSettingPO::getPriority).reversed());
                    if (type == UNIQUE_BASE.toByte()) {
                        // 总部价
                        selected = erpStore.getErpSettings().get(0);
                    } else {
                        if (type == CITY_BASE.toByte()) {
                            // 市级价 根据areaCode 查找市 areaCode是区编码
                            areaCode = areaCode / 100 * 100;
                        }
                        selected = findAreaCode(erpSettings, areaCode);
                    }
                }
            }
        }

        return selected;
    }

    private Optional<Integer> getLocationStore(Integer siteId, Float lat, Float lng) {
        List<Store> stores = storesMapper.selectAllStoreByStatus(siteId, 1);
        if (CollectionUtils.isEmpty(stores)) {
            // 没有门店
            return null;
        }

        Point origin = new Point(lat, lng);
        // 算直线距离 取前SORT_FACTOR% 获取步行距离
        List<StoreDirection> list = stores.parallelStream()
            .map(this::storePoint)
            .map(destination -> new StoreDirection(origin, destination))
            .sorted(Comparator.comparingInt(StoreDirection::getDirection))
            .limit((int) Math.ceil(stores.size() * SORT_FACTOR))
            .collect(toList());

        // 调用高德接口
        List<BatchResultDTO> batchResults = gaoDeService.batchRequest(list);
        for (int i = 0; i < list.size(); i++) {
            BatchResultDTO b = batchResults.get(i);
            StoreDirection storeDirection = list.get(i);
            int direction = -1;
            if (b.getStatus() == 200 && MapUtils.getInteger(b.getBody(), "status") == 1) {
                WalkResultDTO walkResultDTO = JacksonUtils.map2pojo(b.getBody(), WalkResultDTO.class);
                direction = gaoDeService.calcWalkDistance(walkResultDTO);
            }
            storeDirection.setDirection(direction);
        }

        list.sort(Comparator.comparingInt(StoreDirection::getDirection));

        return list.stream()
            .filter(s -> s.getDirection() != -1)
            .sorted(Comparator.comparingInt(StoreDirection::getDirection))
            .map(StoreDirection::getStoreId)
            .findFirst();
    }

    /**
     * 门店经纬度坐标点
     *
     * @param store
     * @return
     */
    private StorePoint storePoint(Store store) {
        float desLng = NumberUtils.toFloat(store.getGaodeLng());
        float desLat = NumberUtils.toFloat(store.getGaodeLat());

        return new StorePoint(store.getId(), desLat, desLng);
    }

    private ErpSettingPO findAreaCode(List<ErpSettingPO> erpSettings, Integer areaCode) {
        for (ErpSettingPO erpSetting : erpSettings) {
            if (Objects.equals(erpSetting.getAreaCode(), areaCode)) {
                return erpSetting;
            }
        }

        return null;
    }

    /**
     * 获取商品ERP价格
     *
     * @param siteId     商户ID
     * @param goodsIds   商品ID列表
     * @param erpStoreId erp门店
     * @return
     */
    public Map<Integer, BGoodsErp> selectERPPrice(Integer siteId, List<Integer> goodsIds, Integer erpStoreId) {
        if (Objects.isNull(siteId) || Objects.isNull(erpStoreId) || Objects.isNull(CollectionUtils.isEmpty(goodsIds))) {
            return null;
        }

        BGoodsErpExample erpExample = new BGoodsErpExample();
        BGoodsErpExample.Criteria criteria = erpExample.createCriteria()
            .andSiteIdEqualTo(siteId)
            .andStoreIdEqualTo(erpStoreId);
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            criteria.andGoodsIdIn(goodsIds).andStatusEqualTo((byte) 10);
        }
        List<BGoodsErp> erpGoods = bGoodsErpMapper.selectByExample(erpExample);
        Map<Integer, BGoodsErp> erpPriceMap = new HashMap<>(erpGoods.size());

        for (BGoodsErp erpGood : erpGoods) {
            erpPriceMap.put(erpGood.getGoodsId(), erpGood);
        }

        return erpPriceMap;
    }

    /**
     * 获取商品ERP价格
     *
     * @param siteId      商户ID
     * @param goodsIds    商品ID列表
     * @param erpStoreId  erp门店
     * @param erpAreaCode 行政区域编码
     * @return
     */
    public Map<Integer, BGoodsErp> selectERPPrice(Integer siteId, List<Integer> goodsIds, Integer erpStoreId, Integer erpAreaCode) {
        if (Objects.isNull(siteId) || Objects.isNull(erpStoreId) || Objects.isNull(erpAreaCode) || Objects.isNull(CollectionUtils.isEmpty(goodsIds))) {
            return Collections.emptyMap();
        }

        // 根据siteId查询erp价格设置类型
        Optional<BErpSetting> bErpSetting = Optional.ofNullable(bErpSettingMapper.selectBySiteId(siteId));

        SearchStrategy strategy;
        if (bErpSetting.map(BErpSetting::getType).orElse((byte) 0) == 40) {
            // 门店价 不需要根据areaCode处理多个门店价格
            strategy = new LatelyStoreSearchStrategy(siteId, erpStoreId, goodsIds);
        } else {
            strategy = new PrioritySearchStrategy(siteId, erpAreaCode, goodsIds, erpPriceSettingMapper);
        }

        List<BGoodsErp> erpGoods = strategy.search(bGoodsErpMapper);
        return strategy.convertMap(erpGoods);
    }

/*    //批量修改商品门店价格
    public void updateStoreGoodsPrice(String[] ids, Integer price) {
        List<BGoodsErp> bGoodsErpList = bGoodsErpMapper.selectChangePriceInfos(ids);//获取所有的要更改的商品信息，同一种商品不同门店
        List<Integer> storeIds = new ArrayList<>();
        String goodsCode = "";
        for (BGoodsErp bGoodsErp : bGoodsErpList) {
            storeIds.add(bGoodsErp.getStoreId());
            if (goodsCode != "" && goodsCode == bGoodsErp.getGoodsCode()) {
                goodsCode = bGoodsErp.getGoodsCode();
            }
        }
    }*/
}
