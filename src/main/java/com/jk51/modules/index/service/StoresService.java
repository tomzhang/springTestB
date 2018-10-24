package com.jk51.modules.index.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.CityHasStores;
import com.jk51.model.MerchantClerkInfo;
import com.jk51.model.Stores;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.order.Meta;
import com.jk51.model.order.Store;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.mapper.MetaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Service
public class StoresService {

    private static final Logger logger = LoggerFactory.getLogger(StoresService.class);
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private MetaMapper metaMapper;
    @Autowired
    private MerchantExtMapper merchantExtMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;

    /**
     * 商户所有门店ids
     *
     * @param siteId
     * @return
     */
    public String selectStoreIdsBysiteId(Integer siteId) {
        String allids = "";
        for (Store b : storesMapper.selectAllStore(siteId, null, null, null)) {
            allids += b.getId() + ",";
        }
        return allids;
    }

    /**
     * 商户开启状态门店ids
     *
     * @param siteId
     * @return
     */
    public String selectStoreIdsBysiteIdandStatus(Integer siteId) {
        String allids = "";
        for (Store b : storesMapper.selectAllStoreByStatus(siteId, 1)) {
            allids += b.getId() + ",";
        }
        return allids;
    }

    public int insertStores(Stores stores) {
        return storesMapper.insertStores(stores);
    }

    public int updateMyStores(Stores stores) {
        return storesMapper.updateStores(stores);
    }

    public Stores getStore(int storeId, int siteId) {
        return storesMapper.getStore(storeId, siteId);
    }

    public int updateOriginStoreId(Stores stores) {
        return storesMapper.updateOriginStoreId(stores);
    }

    public List<CityHasStores> GroupStoresByCityAndServiceSupport(Integer siteId, String servicesupport) {
        return storesMapper.GroupStoresByCityAndServiceSupport(siteId, servicesupport);
    }

    public List<Store> selectStoresByCityAndServiceSupport(Integer siteId, Integer cityId, String serviceSupport) {
        return storesMapper.selectStoresByCityAndServiceSupport(siteId, cityId, serviceSupport);
    }

    public Map<String, Object> selectByOwnPricingTypeAndSiteId(Integer siteId) {
        Map<String, Object> ownPriceType = new HashMap<>();
        List<Integer> list = storesMapper.selectByOwnPricingTypeAndSiteId(siteId);
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "OwnPricingType", "lasttime");
        if (!StringUtil.isEmpty(meta)) {
            ownPriceType.put("type", meta.getMetaVal());
        } else {
            ownPriceType.put("type", 1);
        }

        String str = "";
        if (list.size() > 0) {
            for (Integer i : list) {
                str += i + ",";
            }
            ownPriceType.put("storeIds", str.substring(0, str.length() - 1));
        } else {
            ownPriceType.put("storeIds", "");
        }
        logger.info("获取商户自注定定价权限{}", ownPriceType.toString());
        return ownPriceType;
    }

    public String selectByUpPricingTypeAndSiteId(Integer siteId) {
        List<Integer> list = storesMapper.selectByUpPricingTypeAndSiteId(siteId);
        String str = "";
        if (list.size() > 0) {
            for (Integer i : list) {
                str += i + ",";
            }
            return str.substring(0, str.length() - 1);
        }
        return null;
    }

    public Boolean selectByOwnPricingTypeAndSiteIdAndStoreId(Integer siteId, Integer id) {
        return !StringUtil.isEmpty(storesMapper.selectByOwnPricingTypeAndSiteIdAndStoreId(siteId, id));
    }

    public String selectByOwnPromotionTypeAndSiteId(Integer siteId) {
        List<Integer> list = storesMapper.selectByOwnPromotionTypeAndSiteId(siteId);
        String str = "";
        for (Integer i : list) {
            str += i + ",";
        }
        return str.substring(0, str.length() - 1);
    }

    public Boolean isByOwnPromotionTypeAndSiteIdAndStoreId(Integer siteId, Integer id) {
        return !StringUtil.isEmpty(storesMapper.selectByOwnPromotionTypeAndSiteIdAndStoreId(siteId, id));
    }

    public String selectidsByPermissionTypeFromMeta(Integer siteId) {
        String ids = "";
        Meta metas = metaMapper.selectByMetaTypeAndKey(siteId, "integralpermission_type",
            "store_permission_type");
        if (!StringUtil.isEmpty(metas)) {
            String a = metas.getMetaVal();
            if ("3".equals(a)) {//指定门店
                Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "integralpermission_type", "store_ids");
                if (!StringUtil.isEmpty(meta)) {
                    ids = meta.getMetaVal();
                }
            } else if ("2".equals(a)) {//所有门店
                ids = selectStoreIdsBysiteId(siteId);
            }
        }
        return ids;
    }

    public String selectIdsByStorereFundPermissionFromMeta(Integer siteId) {
        String ids = "";
        Meta metas = metaMapper.selectByMetaTypeAndKey(siteId, "store_refund_permission",
            "store_refund_permission_type");
        String a = "";
        if (!StringUtil.isEmpty(metas)) {
            a = metas.getMetaVal();
            if ("3".equals(a)) {
                Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "store_refund_permission", "store_ids");
                if (!StringUtil.isEmpty(meta)) {
                    ids = meta.getMetaVal();
                }
            } else if ("2".equals(a)) {
                ids = selectStoreIdsBysiteIdandStatus(siteId);
            }
        } else {
            ids = selectStoreIdsBysiteIdandStatus(siteId);
        }
        return ids;
    }

    public String selectidsBywarehouseFromMeta(Integer siteId) {
        String ids = "";
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "site_general_warehouse_config",
            "site_general_warehouse_config");
        if (!StringUtil.isEmpty(meta) && !"NULL".equals(meta.getMetaVal()) && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
        }
        return ids;
    }
    public String selectidsBywarehouseFromMetaLin(Integer siteId) {
        String ids = "";
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "site_general_warehouse_config_lin",
                "site_general_warehouse_config_lin");
        if (!StringUtil.isEmpty(meta) && !"NULL".equals(meta.getMetaVal()) && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
        }
        return ids;
    }

    public String selectassignFromMeta(Integer siteId) {
        String ids = "";
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "auto_assign_type",
            "assign_store_ids");
        if (!StringUtil.isEmpty(meta) && !"NULL".equals(meta.getMetaVal()) && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
        }
        return ids;
    }

    public String selectSelfSupportBySiteIdAndStoreId(Integer siteId, Integer Storeid) {
        return storesMapper.getStore(Storeid, siteId).getService_support();
    }

    /**
     * 获取门店分单设置
     *
     * @param siteId
     * @return
     * @metaType auto_assign_type
     */
    public Map selectassign(Integer siteId) {
        Map assigntype = new HashMap();
        Integer order_assign_type = 110;
        String time = "8:00--18:00";//初始化时间
        MerchantExt merchantExt = merchantExtMapper.selectByMerchantId(siteId);
        if (!StringUtil.isEmpty(merchantExt)) {
            order_assign_type = merchantExt.getOrder_assign_type();
        }
        assigntype.put("type", order_assign_type);//分单模式
        if (order_assign_type == 130) {
            Meta metas = metaMapper.selectBysiteIdAndMetaType(siteId, "order_assign_type");
            if (!StringUtil.isEmpty(metas)) {
                time = metas.getMetaVal();
            }
        }
        assigntype.put("order_time", time);
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "auto_assign_type", "assign_store_ids");
        String ids = "";
        if (!StringUtil.isEmpty(meta) && !"NULL".equals(meta.getMetaVal()) && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
        }
        assigntype.put("storeids", ids);
        return assigntype;
    }

    /**
     * 获取邀请码显示设置
     */
    public Map selectinvitecode(Integer siteId) {
        Map inviCodemap = new HashMap();
        MerchantExt merchantExt = merchantExtMapper.selectByMerchantId(siteId);
        if (!StringUtil.isEmpty(merchantExt)) {
            inviCodemap.put("weixincode", merchantExt.getWei_show_invite_code());
            inviCodemap.put("compucode", merchantExt.getCompu_show_invite_code());
        }
        return inviCodemap;
    }


    /**
     * 获取具有退款设置
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    public Map selectIdsByStorereFundPermission(Integer siteId) {
        Map fundmap = new HashMap();
        String ids = "";
        String a = "";
        Meta metas = metaMapper.selectByMetaTypeAndKey(siteId, "store_refund_permission",
            "store_refund_permission_type");
        if (!StringUtil.isEmpty(metas)) {
            a = metas.getMetaVal();
            fundmap.put("fundtype", a);
            if ("3".equals(a)) {
                Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "store_refund_permission", "store_ids");
                if (!StringUtil.isEmpty(meta)) {
                    ids = meta.getMetaVal();
                }
            } else if ("2".equals(a)) {
                ids = selectStoreIdsBysiteIdandStatus(siteId);
            }
        } else {
            ids = selectStoreIdsBysiteIdandStatus(siteId);
        }
        fundmap.put("fundstores", ids);
        return fundmap;
    }

    /**
     * 获取门店积分设置
     * 1:禁止门店;2:所有门店;3:指定门店;
     *
     * @return
     */
    public Map selectidsByPermissionType(Integer siteId) {
        Map integralmap = new HashMap();
        String ids = "";
        Meta metas = metaMapper.selectByMetaTypeAndKey(siteId, "integralpermission_type",
            "store_permission_type");
        if (!StringUtil.isEmpty(metas)) {
            String a = metas.getMetaVal();
            integralmap.put("integaltype", a);
            if (a.equals("3")) {//指定门店
                Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "integralpermission_type", "store_ids");
                if (!StringUtil.isEmpty(meta)) {
                    ids = meta.getMetaVal();
                }
            } else if (a.equals("2")) {//所有门店
                ids = selectStoreIdsBysiteIdandStatus(siteId);
            }
        }
        integralmap.put("ids", ids);
        return integralmap;
    }

    /**
     * 判断该门店是否具有退款权限
     */
    public Boolean selectRefundByprimaryId(Integer siteId, Integer storeId) {
        boolean flag = true;
        String ids = selectIdsByStorereFundPermissionFromMeta(siteId);
        if (ids == "") {
            flag = false;
        } else {
            String[] storeIds = ids.split(",");
            List<String> list = Arrays.asList(storeIds);
            if (!list.contains(String.valueOf(storeId))) {
                flag = false;
            }
        }
        return flag;
    }

    //获取店员chat权限设置总开关
    public String selectClerkChatType(Integer siteId) {
        Meta meta = metaMapper.selectBysiteIdAndMetaType(siteId, "clerk_chat_type");
        String clerkChatStatus = "1";
        if (!StringUtil.isEmpty(meta)) {
            clerkChatStatus = meta.getMetaVal().toString();
        }
        return clerkChatStatus;
    }

    /**
     * 查询具有控制聊天功能的店员ids
     *
     * @param siteId
     * @return
     */
    public List<Integer> selectClerkChat(Integer siteId) {
        List<MerchantClerkInfo> clerkList = new ArrayList<>();
        List<Integer> clerk_chatIds = new ArrayList<>();
        String result = selectClerkChatType(siteId);
        if (!result.equals("1")) {
            clerkList = storeAdminextMapper.selectClerkChat(siteId, null, null, null, 1, 1);
            for (MerchantClerkInfo m : clerkList) {
                clerk_chatIds.add(m.getId());
            }
        }
        return clerk_chatIds;
    }

    public PageInfo searchStores(String siteId, String keyword, int pageNum, int pageSize, String city, String storeIds) {
        PageHelper.startPage(pageNum, pageSize);
        List<String> store_list=null;
        if(storeIds!=null){
            store_list = Arrays.asList(storeIds.split(","));
        }
        List<Map> list = storesMapper.searchStores(siteId, keyword, city, store_list);
        return new PageInfo<>(list);
    }

    public PageInfo searchStoresPro(String siteId, String keyword, int pageNum, int pageSize, String storesNumber, String isQjd, String type, String storesStatus, String name, String dStatus,String implementationPhase) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map> list = storesMapper.searchStoresPro(siteId, keyword, storesNumber, isQjd, type, storesStatus, name, dStatus, implementationPhase);
        return new PageInfo<>(list);
    }

    public PageInfo queryClicks(String siteId, String storeId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map> list = storesMapper.queryClicks(siteId, storeId);
        return new PageInfo<>(list);
    }

    public PageInfo queryClickDiss(String siteId, String id, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map> list = storesMapper.queryClickDiss(siteId, id);
        return new PageInfo<>(list);
    }

    public int updateStoreEleStatus(Stores stores) {
        return storesMapper.updateStoreEleStatus(stores);
    }

    /**
     * 商户开启状态门店
     *
     * @param siteId
     * @return
     */
    public List<Store> selectStoreIdsBysiteIdand(Integer siteId) {

        return storesMapper.selectAllStoreByStatus(siteId, 1);
    }
}
