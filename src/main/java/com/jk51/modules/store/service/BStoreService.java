package com.jk51.modules.store.service;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.MerchantClerkInfo;
import com.jk51.model.StoreAdmin;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.Stores;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.Meta;
import com.jk51.model.order.SBStores;
import com.jk51.model.order.Store;
import com.jk51.model.role.Role;
import com.jk51.model.treat.YbSettlementdayConfig;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.authority.service.RoleService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.mapper.MetaMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.merchant.mapper.YbSettlementdayConfigMapper;
import com.jk51.modules.persistence.mapper.SBStoresMapper;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-24
 * 修改记录:
 */
@Service
public class BStoreService {
    private static final Logger log = LoggerFactory.getLogger(BStoreService.class);
    @Autowired
    private StoresMapper bStoresMapper;
    @Autowired
    private BStoresMapper mapper;
    @Autowired
    private SBStoresMapper sbStoresMapper;

    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;
    @Autowired
    private MerchantExtMapper merchantExtMapper;
    @Autowired
    private MetaMapper metaMapper;

    @Autowired
    private YbMerchantMapper merchantMapper;

    @Autowired
    private YbSettlementdayConfigMapper ybSettlementdayConfigMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private QrcodeService qrcodeService;

    private String image_url = "http://img-dev.51jk.com/";


    /**
     * 新增门店的同时赋予门店一个超级管理员，
     *
     * @return
     */
    @Transactional
    public String insertStoreInfo(Map<String, Object> map) {
        Integer siteId = Integer.parseInt(map.get("siteId").toString());
        String storesNumber = map.get("storesNumber").toString();
        String admin_name = map.get("admin").toString();//超级管理员
        if (StringUtil.isEmpty(storeAdminMapper.selectByUsername(siteId, admin_name))) {
            if (StringUtil.isEmpty(bStoresMapper.selectAllStoreByStoresstatus(siteId, null, storesNumber, null, null))) {
                map.put("siteId", siteId);
                getDeviceNumImg(map);
                bStoresMapper.insertstores(map);//返回新增门店的storeid
                Integer storeId = Integer.parseInt(map.get("id").toString());
                try {
                    StoreAdmin storeAdmin = new StoreAdmin();
                    storeAdmin.setSite_id(siteId);
                    storeAdmin.setStore_id(storeId);
                    storeAdmin.setIs_del(0);
                    storeAdmin.setStatus(1);
                    storeAdmin.setUser_type(1);
                    storeAdmin.setUser_pwd(EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(map.get("pwd").toString())));
                    storeAdmin.setUser_name(admin_name);
                    storeAdminMapper.insert(storeAdmin);
                    List<StoreAdminExt> storeAdminExts = storeAdminextMapper.selectBySiteIdAndStoreAdminId(siteId, storeAdmin.getId());
                    if (CollectionUtils.isEmpty(storeAdminExts)) {
                        StoreAdminExt storeAdminExt = new StoreAdminExt();
                        storeAdminExt.setSite_id(siteId);
                        storeAdminExt.setStore_id(storeId);
                        storeAdminExt.setStoreadmin_id(storeAdmin.getId());
                        storeAdminExt.setName(admin_name);
                        storeAdminExt.setIs_del(0);
                        storeAdminExt.setStoreadmin_status(1);
                        storeAdminextMapper.insertSelective(storeAdminExt);
                    } else {
                        StoreAdminExt storeAdminExt = new StoreAdminExt();
                        storeAdminExt.setId(storeAdminExts.get(0).getId());
                        storeAdminExt.setSite_id(siteId);
                        storeAdminExt.setStore_id(storeAdmin.getStore_id());
                        storeAdminExt.setStoreadmin_id(storeAdmin.getId());
                        storeAdminExt.setName(storeAdmin.getUser_name());
                        storeAdminExt.setIs_del(0);
                        storeAdminExt.setStoreadmin_status(1);
                        storeAdminextMapper.updateByPrimaryKeySelective(storeAdminExt);
                    }
                    //生成店长店员两个角色
                    Role role = new Role();
                    role.setSiteId(siteId);
                    role.setStoreId(storeId);
                    role.setPlatform(130);
                    roleService.addRoleDefault(role);

                    return "200";//新增管理员成功
                } catch (Exception e) {
                    log.error("新增管理员失败" + e);
                    return "500";//密码加密方法有误
                }
            } else {
                return "300";//门店编号已存在
            }
        } else {
            return "600";//登录账户已存在
        }
    }

    /**
     * 更新门店信息
     *
     * @return
     */
    @Transactional
    public String updateStore(Map map) {
        Integer siteId = Integer.parseInt(map.get("siteId").toString());
        Integer i = 0;
        log.info("map" + map);
        List<Store> storeList = bStoresMapper.selectAllStoreByStoresstatus(siteId, null, map.get("storesNumber").toString(), null, null);
        if (storeList.size() >= 2) {
            return "300";
        } else if (storeList.size() == 1) {
            if (storeList.get(0).getId() != Integer.parseInt(map.get("id").toString())) {
                return "300";
            }
        }
        StoreAdmin storeAdmin = new StoreAdmin();
        storeAdmin.setSite_id(siteId);
        storeAdmin.setStore_id(Integer.parseInt(map.get("id").toString()));
        storeAdmin.setUser_name(map.get("admin").toString());
        storeAdmin.setId(Integer.parseInt(map.get("storeAdminId").toString()));
        storeAdmin.setIs_del(0);
        storeAdmin.setStatus(1);
        storeAdmin.setUser_type(1);
        if (map.get("pwd") != null && map.get("pwd").toString() != "") {
            try {
                storeAdmin.setUser_pwd(EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(map.get("pwd").toString())));
                storeAdminMapper.updateByPrimaryKey(storeAdmin);
            } catch (Exception e) {
                log.error("密码加密有误" + e);
            }
        }
        try {
            List<StoreAdminExt> storeAdminExts = storeAdminextMapper.selectBySiteIdAndStoreAdminId(siteId, storeAdmin.getId());
            if (CollectionUtils.isEmpty(storeAdminExts)) {
                StoreAdminExt storeAdminExt = new StoreAdminExt();
                storeAdminExt.setSite_id(siteId);
                storeAdminExt.setStore_id(storeAdmin.getStore_id());
                storeAdminExt.setStoreadmin_id(storeAdmin.getId());
                storeAdminExt.setName(storeAdmin.getUser_name());
                storeAdminExt.setIs_del(0);
                storeAdminExt.setStoreadmin_status(1);
                storeAdminextMapper.insertSelective(storeAdminExt);
            } else {
                StoreAdminExt storeAdminExt = new StoreAdminExt();
                storeAdminExt.setId(storeAdminExts.get(0).getId());
                storeAdminExt.setSite_id(siteId);
                storeAdminExt.setStore_id(storeAdmin.getStore_id());
                storeAdminExt.setStoreadmin_id(storeAdmin.getId());
                storeAdminExt.setName(storeAdmin.getUser_name());
                storeAdminExt.setIs_del(0);
                storeAdminExt.setStoreadmin_status(1);
                storeAdminextMapper.updateByPrimaryKeySelective(storeAdminExt);
            }
        } catch (Exception e) {
            log.error("更新门店信息时添加主管理员信息" + e);
        }
        getDeviceNumImg(map);
        i = bStoresMapper.updatestorebymap(map);
        if (0 != i) {
            log.info("");
            return "200";
        }
        log.error("门店信息修改失败");
        return "500";
    }

    //生成设备号二维码
    public void getDeviceNumImg(Map map){
        Integer siteId = Integer.parseInt(map.get("siteId").toString());
        //如果填写了设备号，生成一个图片地址保存
        if (!StringUtil.isEmpty(map.get("deviceNum"))){
            String deviceNum = String.valueOf(map.get("deviceNum"));
            Map<String, Object> param = new HashMap<>();
            param.put("siteId",siteId);
            param.put("sceneStr",deviceNum);
            param.put("type",4);
            Map<String, Object> qrcodeMap = qrcodeService.createQrcode(param);
            if (!StringUtil.isEmpty(qrcodeMap)){
                String url = String.valueOf(qrcodeMap.get("url"));
                map.put("deviceImgUrl",url);
            }
        }
    }


    /**
     * 展示商户所有门店信息
     *
     * @return
     */
    public List<Store> selectAllStores(Integer site_id, String name, String storeNumber) {
        return bStoresMapper.selectAllStore(site_id, name, storeNumber, null);
    }

    /**
     * 展示可用的门店信息
     */
    public List<Store> selectAllStore(Integer site_id, String storeName, String storeNumber) {
        return bStoresMapper.selectAllStore(site_id, storeName, storeNumber, null);
    }

    /**
     * 展示所有的门店信息
     */
    public List<Store> selectStoreAll(Integer site_id) {
        return bStoresMapper.selectStoreAll(site_id);
    }
    /**
     * 更改门店状态
     */
    public String updateStoreStatusByStoreIds(Integer siteId, Integer type, String ids) {
        if (!"".equals(ids)) {
            String[] shopIds = ids.split(",");
            List<Integer> integerList = new ArrayList<>();
            for (String s : shopIds) {
                integerList.add(Integer.valueOf(s));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("site_id", siteId);
            map.put("stores_status", type);
            map.put("merchantIds", integerList);
            Integer i = bStoresMapper.updatestoreStatusBystoreId(map);
            if (ids.length() == i)
                return "200";
            else
                return "500";

        } else {
            return "500";
        }

    }


    /**
     * 条件查询门店信息
     *
     * @return
     */
    public List<Store> selectByFuzzy(Integer siteId, String name, String storeNumber, Integer type, Integer storesStatus, Integer isQjd, String serviceSupport) {
        return bStoresMapper.selectByFuzzy(siteId, name, storeNumber, type, storesStatus, isQjd, serviceSupport);
    }

    /**
     * 根据门店主键查门店信息
     *
     * @param site_id 商户站点
     * @param id      门店id
     * @return
     */
    public Store selectStoreByStoreKey(Integer site_id, Integer id) {
        Store bStores = bStoresMapper.selectByPrimaryKey(site_id, id);
        if(StringUtil.isEmpty(bStores)){
            return null;
        }
        StoreAdmin storeAdmin = storeAdminMapper.selectbysiteandstore(site_id, id);
        Integer device_flag = storeAdminextMapper.getDeviceFlag(site_id);
        Integer storeAdminId = 0;
        String username = "";
        String password = "";
        if (!StringUtil.isEmpty(storeAdmin)) {
            if (!StringUtil.isEmpty(storeAdmin.getUser_name())) {
                username = storeAdmin.getUser_name();
            }
            if (!StringUtil.isEmpty(storeAdmin.getUser_pwd())) {
                password = storeAdmin.getUser_pwd();
            }
            if (!StringUtil.isEmpty(storeAdmin.getId())) {
                storeAdminId = storeAdmin.getId();
            }
        }
        bStores.setAdmin(username);
        bStores.setPwd(password);
        bStores.setStoreAdminId(storeAdminId);
        bStores.setDeviceFlag(device_flag);
        return bStores;
    }


    /**
     * 更改门店自主定价权限
     *
     * @param OwnPricingType 0:门店不参加自主定价；1:门店参加自主定价
     * @param siteId         站点id
     * @param ids            门店编号，以逗号隔开
     * @return
     */
    @Transactional
    public String updateOwnPricingType(Integer OwnPricingType, Integer siteId, String ids) {
        String[] shopIds = ids.split(",");
        List<Integer> integerList = new ArrayList<>();
        if (shopIds.length != 0) {
            for (String s : shopIds) {
                integerList.add(Integer.valueOf(s));
            }
        }
        Integer result = 1;
        if (OwnPricingType == 1) {
            result = 0;
        }
        Meta metas = metaMapper.selectByMetaTypeAndKey(siteId, "OwnPricingType", "lasttime");
        if (StringUtil.isEmpty(metas)) {
            Meta meta = new Meta();
            meta.setSiteId(siteId);
            meta.setMetaType("OwnPricingType");
            meta.setMetaVal(String.valueOf(OwnPricingType));
            meta.setMetaStatus(1);
            meta.setMetaKey("lasttime");
            meta.setMetaDesc("用户最后一次保存自主定价权限的类型。1：指定门店参加0指定门店不参加");
            metaMapper.insertSelective(meta);
        } else {
            metas.setMetaVal(String.valueOf(OwnPricingType));
            metaMapper.updateByPrimaryKeys(metas);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("site_id", siteId);
        map.put("ownPricingType", OwnPricingType);
        map.put("merchantIds", integerList);
        bStoresMapper.initOwnPricingTypeBySiteId(result, siteId);
        Integer i = bStoresMapper.updateOwnPricingType(map);
        if (i == shopIds.length)
            return "success";
        else
            return "faild";
    }


    /**
     * 更改允许门店修改会员积分和退款的权限
     *
     * @return
     */
    @Transactional
    public Integer updatePermission(String type, String ids, Integer siteId, String metaType) {
        metaMapper.updateStatus(siteId, metaType);//设置原来的门店权限失效
        int result = -1;
        Meta meta = new Meta();
        meta.setSiteId(siteId);
        meta.setMetaType(metaType);
        meta.setMetaVal(type);
        meta.setMetaStatus(1);
        if (metaType.equals("integralpermission_type")) {
            meta.setMetaKey("store_permission_type");
            meta.setMetaDesc("1、禁止所有门店修改会员积分 2、允许所有门店修改会员积分 3、允许指定门店修改会员积分");
        } else if (metaType.equals("store_refund_permission")) {
            meta.setMetaKey("store_refund_permission_type");
            meta.setMetaDesc("1、禁止所有门店退款 2、允许所有门店退款 3、允许指定门店退款");
        }
        result = metaMapper.addMeta(meta);
        if (type.equals("3")) {
            meta.setMetaDesc("多个门店id用 , 隔开");
            meta.setMetaKey("store_ids");
            meta.setMetaVal(ids);
            metaMapper.addMeta(meta);
        }
        return result;
    }

    /**
     * 会员注册邀请码更改设置
     */
    @Transactional
    public Integer updateShowInviteCode(Integer compuInviteCode, Integer winxinInviteCode, Integer siteId) {
        return merchantExtMapper.updateInviteCode(winxinInviteCode, compuInviteCode, siteId);
    }

    /**
     * 门店自动分单设置
     *
     * @param orderAssignType 分单模式
     * @param merchantId      商家id
     * @param storesIds       总仓门店ids
     * @param asssids         分单门店ids
     * @param time            分单事件
     * @param storetype       分单类型
     * @return
     */
    @Transactional
    public Integer setOrderAssignType(Integer orderAssignType, Integer merchantId, String storesIds, String asssids, String time, Integer storetype, String storesIdslin) {
        if (setordertype(orderAssignType, merchantId, time) > 0) {
            if (setorderids(asssids, merchantId, storetype) > 0) {
                if (setwareids(merchantId, storesIds) > 0) {
                    if (setwareidsLin(merchantId, storesIdslin) > 0) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 更改商家分单模式
     *
     * @return
     */
    public Integer setordertype(Integer orderAssignType, Integer merchantId, String time) {
        Integer result = merchantExtMapper.setOrderAssignType(orderAssignType, merchantId);//保存分单模式
        if (orderAssignType == 130) {
            metaMapper.updateStatus(merchantId, "order_assign_type");
            Meta metas = new Meta();
            metas.setSiteId(merchantId);
            metas.setMetaType("order_assign_type");
            metas.setMetaStatus(1);
            metas.setMetaKey("order_assign_type_130_time");
            metas.setMetaDesc("分单时间");
            metas.setMetaVal(time);
            metaMapper.addMeta(metas); //定时自动分单时保存分单时间
        }
        return result;
    }

    /**
     * 更改分单门店ids
     *
     * @return
     */
    public Integer setorderids(String asssids, Integer merchantId, Integer storetype) {
        metaMapper.updateStatus(merchantId, "auto_assign_type");
        Meta meta = new Meta();
        meta.setSiteId(merchantId);
        meta.setMetaType("auto_assign_type");
        if (storetype == 1) {
            meta.setMetaVal("all");
        } else {
            meta.setMetaVal(asssids);
        }
        meta.setMetaStatus(1);
        meta.setMetaKey("assign_store_ids");
        return metaMapper.addMeta(meta);
    }

    /**
     * 更改总仓门店ids
     *
     * @return
     */
    public Integer setwareids(Integer merchantId, String storesIds) {
        metaMapper.updateStatus(merchantId, "site_general_warehouse_config");
        Meta meta = new Meta();
        meta.setSiteId(merchantId);
        meta.setMetaType("site_general_warehouse_config");
        meta.setMetaStatus(1);
        meta.setMetaKey("site_general_warehouse_config");
        meta.setMetaDesc("总仓设置");
        meta.setMetaVal(storesIds);
        return metaMapper.addMeta(meta); //设置总仓ids
    }
    /**
     * 设置临时处理门店ids
     *
     * @return
     */
    public Integer setwareidsLin(Integer merchantId, String storesIds) {
        int i=metaMapper.updateStatus(merchantId, "site_general_warehouse_config_lin");
        Meta meta = new Meta();
        meta.setSiteId(merchantId);
        meta.setMetaType("site_general_warehouse_config_lin");
        meta.setMetaStatus(1);
        meta.setMetaKey("site_general_warehouse_config_lin");
        meta.setMetaDesc("设置临时处理门店");
        meta.setMetaVal(storesIds);
        return metaMapper.addMeta(meta); //设置总仓ids
    }

    public List<Store> findStoreBycity(Integer siteId, String city) {
        return bStoresMapper.findShopbyCity(siteId, city);
    }

    public List<SBStores> selectBySiteIdAndCityAndStoreName(Integer siteId, String city, String storeName) {
        return sbStoresMapper.selectBySiteIdAndCityAndStoreName(siteId, city, storeName);
    }

    //获取商家门店最大id
    public Integer getStoreId(Integer siteId) {
        int id = bStoresMapper.selectMaxId(siteId);
        log.info("sigeId最大值" + id);
        return ++id;
    }

    //获取商家所有门店ids
    public String allstoreids(Integer siteId) {
        List<Store> bStoresList = bStoresMapper.selectAllStore(siteId, null, null, null);
        String ids = "";
        for (Store b : bStoresList) {
            ids += b.getId() + ",";
        }
        return ids.substring(0, ids.length() - 1);
    }

    public Map<String, Object> selectByPremaryKey(Integer merchantId) {
        Map<String, Object> result = new HashMap<>();
        try {
            YbMerchant merchant = null;
            MerchantExt merchantExt = null;
            merchant = merchantMapper.selectBySiteId(merchantId);
            if (null != merchant) {
                // 不返回密码字段
                merchant.setSeller_pwd("");
                merchantExt = merchantExtMapper.selectByMerchantId(merchant.getMerchant_id());
            }
            Map param = new HashMap();
            param.put("sellerId", merchantId);
            Map<String, Object> seller;
            seller = merchantMapper.querySeller(param);

            result.put("merchant", merchant);
            result.put("merchantExt", merchantExt);
            if (null == seller || seller.size() < 1) {
                seller = new HashMap<>();
                seller.put("change_num", "");
                seller.put("seller_name", "");
                seller.put("bank_name", "");
                seller.put("bank_id", "");
                seller.put("beneficiary_party", "");
                seller.put("taxpayer_number", "");
                seller.put("merchant_name", "");
                seller.put("shop_address", "");
                seller.put("service_phone","service_phone");
            }
            if (null != merchant) {
                List<YbSettlementdayConfig> ybSettlementdayConfig = ybSettlementdayConfigMapper.findSettlementday(String.valueOf(merchant.getMerchant_id()));
                if (ybSettlementdayConfig != null && ybSettlementdayConfig.size() != 0) {
                    result.put("settle", ybSettlementdayConfig.get(0));
                } else {
                    result.put("settle", null);
                }
            }

            Map<String, String> map = merchantMapper.selectAreaByAreaId(merchant.getShop_area());

            String pre = map.get("sheng") + map.get("shi") + map.get("qu");
            String address = merchant.getShop_address();
            if (merchant.getShop_address().contains("省")) {
                address = address.split("省", 2)[1];

            }
            if (merchant.getShop_address().contains("市")) {
                address = address.split("市", 2)[1];

            }
            if (merchant.getShop_address().contains("区") && !merchant.getShop_address().contains("小区")) {
                address = address.split("区", 2)[1];

            }
            if (address.contains("小区")) {
                String s = address.split("小区", 2)[0];
                if (s.contains("区")) {
                    address = address.split("区", 2)[1];
                }
            }
            address = pre + address;
            merchant.setShop_address(address);

            if ("".equals(seller.get("beneficiary_party"))) {
                seller.put("beneficiary_party", merchant.getCompany_name());
            }

            if ("".equals(seller.get("shop_address"))) {
                seller.put("shop_address", merchant.getShop_address());
            }
            if ("".equals(seller.get("service_phone"))) {
                seller.put("service_phone", merchant.getService_phone());
            }
            result.put("seller", seller);
        } catch (Exception e) {
            log.info("selectByPremaryKey:{}", e);
        }
        return result;
    }


    /**
     * 更改商家发票管理
     *
     * @param merchant_id
     * @param invoice_is
     * @return
     */
    @Transactional
    public Integer updateInvoice(Integer merchant_id, Integer invoice_is) {
        return merchantMapper.updateInvoice(merchant_id, invoice_is);
    }

    public void editSeller(Map param) {
        if (StringUtil.isEmpty(querySeller(param))) {
            merchantMapper.insSeller(param);
            return;
        }
        merchantMapper.updateSeller(param);
    }

    public Map querySeller(Map param) {
        return merchantMapper.querySeller(param);
    }

    /**
     * 更改店员聊天开关权限
     *
     * @param siteId
     * @param meta_val
     * @return
     */
    @Transactional
    public Integer updateClerkChatType(Integer siteId, String meta_val, String storeIds) {
        Meta metas = metaMapper.selectBysiteIdAndMetaType(siteId, "clerk_chat_type");
        Meta meta = new Meta();
        meta.setSiteId(siteId);
        meta.setMetaType("clerk_chat_type");
        meta.setMetaVal(meta_val);
        meta.setMetaStatus(1);
        meta.setMetaKey("clerk_chat_type");
        meta.setMetaDesc("0：禁止所有，1：允许所有，2：指定部分店员");
        if (StringUtil.isEmpty(metas)) {
            metaMapper.addMeta(meta);
        } else {
            meta.setMetaId(metas.getMetaId());
            metaMapper.updateMeta(meta);
        }
        Map map = new HashMap();
        map.put("siteId", siteId);
        if (meta_val.equals("1")) {
            return storeAdminextMapper.updateAllclerkChatType(siteId, 0);
        } else if (meta_val.equals("2")) {
            return storeAdminextMapper.updateAllclerkChatType(siteId, 1);
        } else if (meta_val.equals("3")) {
            storeAdminextMapper.updateAllclerkChatType(siteId, 0);
            String[] clerklist = storeIds.split(",");
            map.put("chat", 1);
            map.put("clerk_ids", clerklist);
            return storeAdminextMapper.updateClerkChatType(map);
        }
        return 1;
    }

    /**
     * 获取该商家下所有店员
     *
     * @param siteId
     * @param storename
     * @param mobile
     * @param clerkname
     * @return
     */
    public List<MerchantClerkInfo> selectAllClerk(Integer siteId, String storename, String mobile, String clerkname) {
        List<MerchantClerkInfo> merchantClerkInfos = storeAdminextMapper.selectClerkChat(siteId, mobile, clerkname, storename, null, 1);
        return merchantClerkInfos;
    }

    /**
     * @param siteId
     * @param mobile
     * @param name
     * @param ivcode
     * @param storeId
     * @param status
     * @return
     */
    public List<MerchantClerkInfo> selectAllClerk(Integer siteId, String mobile, String name, String ivcode, Integer storeId, Integer status) {
        List<MerchantClerkInfo> merchantClerkInfos = storeAdminextMapper.selectClerkInfo(siteId, mobile, name, ivcode, storeId, status);
        return merchantClerkInfos;
    }

    /**
     * 根据id查询相应的门店信息
     *
     * @param siteId
     * @param ids
     * @param storesStatus
     * @return
     */
    public ReturnDto selectByids(Integer siteId, String ids, Integer storesStatus) {
        if (siteId == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (storesStatus == null)
            return ReturnDto.buildFailedReturnDto("storesStatus不能为空");
        if (!ids.matches("^(\\d+)|((\\d+,)+\\d+)$")) {
            return ReturnDto.buildFailedReturnDto("ids不符合以逗号分隔的数字这一规则");
        }

        return ReturnDto.buildSuccessReturnDto(bStoresMapper.selectStoreByIds(siteId, ids, storesStatus));
    }

    public ReturnDto selectByStoreId(Integer siteId, Integer storeId, Integer storesStatus) {
        if (siteId == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (storesStatus == null)
            return ReturnDto.buildFailedReturnDto("storesStatus不能为空");
        if (storeId == null) {
            return ReturnDto.buildFailedReturnDto("storeId不能为空");
        }
        Store store = bStoresMapper.selectByStoreId(siteId, storeId, storesStatus);
        return ReturnDto.buildSuccessReturnDto(store);
    }

    /**
     * 更改订单改价权限
     *
     * @returnp
     */
    @Transactional
    public String updateUpricingType(Integer UpPricingType, Integer siteId, String ids) {
        String[] shopIds = ids.split(",");
        List<Integer> integerList = new ArrayList<>();
        if (shopIds.length != 0) {
            for (String s : shopIds) {
                integerList.add(Integer.valueOf(s));
            }
        }
        Integer result = 1;
        if (UpPricingType == 1) {
            result = 0;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("site_id", siteId);
        map.put("upPricingType", UpPricingType);
        map.put("merchantIds", integerList);
        bStoresMapper.initUpPricingTypeBySiteId(result, siteId);
        Integer i = bStoresMapper.updateUpPricingType(map);
        if (i == shopIds.length)
            return "success";
        else
            return "faild";
    }

    public List<Stores> selectStoreInfoByCityIds(Integer siteId, String cityIds) {
        List<String> cityIdsList = Stream.of(cityIds.split(","))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        return mapper.getStoreByCityAndSiteId(cityIdsList, siteId);
    }

    /**
     * 获取已知条件外的门店id，用到后隔开
     */
    public String selectStoreIds(Integer siteId, String storeIds) {
        return bStoresMapper.selectStoreIds(siteId, storeIds);
    }
}
