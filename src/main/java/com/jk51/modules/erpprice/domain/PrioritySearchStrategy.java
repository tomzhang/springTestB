package com.jk51.modules.erpprice.domain;

import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.erpprice.BGoodsErpExample;
import com.jk51.modules.erpprice.domain.pojo.ErpSettingPO;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import com.jk51.modules.erpprice.mapper.ErpPriceSettingMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 多门店ERP价格优先级查询
 */
public class PrioritySearchStrategy implements SearchStrategy {
    private final BGoodsErpExample erpExample = new BGoodsErpExample();
    private final ErpPriceSettingMapper erpPriceSettingMapper;
    private final BGoodsErpExample.Criteria criteria;
    private final Integer siteId;
    private final Integer erpAreaCode;
    private List<ErpSettingPO> erpSettings;

    public PrioritySearchStrategy(Integer siteId, Integer erpAreaCode, List<Integer> goodsIds, ErpPriceSettingMapper erpPriceSettingMapper) {
        this.erpPriceSettingMapper = erpPriceSettingMapper;
        this.siteId = siteId;
        this.erpAreaCode = erpAreaCode;

        criteria = erpExample.createCriteria()
            .andSiteIdEqualTo(siteId);
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            criteria.andGoodsIdIn(goodsIds).andStatusEqualTo((byte) 10);
        }
    }

    @Override
    public List<BGoodsErp> search(BGoodsErpMapper mapper) {
        erpSettings = erpPriceSettingMapper.selectStoreIdsByStoreIdAndAreaCode(siteId, erpAreaCode);
        if (CollectionUtils.isEmpty(erpSettings)) {
            return Collections.emptyList();
        }

        List<Integer> storeIds = erpSettings.stream().map(ErpSettingPO::getStoreId).collect(toList());
        criteria.andStoreIdIn(storeIds);

        return mapper.selectByExample(erpExample);
    }

    @Override
    public Map<Integer, BGoodsErp> convertMap(List<BGoodsErp> erpGoods) {
        Map<Integer, BGoodsErp> erpPriceMap = new HashMap<>(erpGoods.size());
        Map<Integer, Integer> priorityStoreMap = erpSettings.stream().collect(HashMap::new, (m, e) -> m.put(e.getStoreId(), e.getPriority()), (m1, m2) -> m1.putAll(m2));
        for (BGoodsErp erpGood : erpGoods) {
            Integer goodsId = erpGood.getGoodsId();
            if (erpPriceMap.containsKey(erpGood.getGoodsId())) {
                // 优先级高的覆盖
                Integer preStoreId = erpPriceMap.get(goodsId).getStoreId();
                if (priorityStoreMap.get(preStoreId) < priorityStoreMap.get(erpGood.getStoreId())) {
                    erpPriceMap.put(goodsId, erpGood);
                }
            } else {
                erpPriceMap.put(goodsId, erpGood);
            }
        }

        return erpPriceMap;
    }
}
