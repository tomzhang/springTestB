package com.jk51.modules.goods.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.YbConfigGoodsSync;
import com.jk51.model.goods.YbGoodsSyncDraft;
import com.jk51.model.goods.YbGoodsSyncDraftExtWithBLOBs;
import com.jk51.model.goods.YbImagesAttr;
import com.jk51.modules.goods.job.GoodsSync;
import com.jk51.modules.goods.mapper.*;
import com.jk51.modules.goods.request.GoodsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncTaskService {
    private static Logger logger = LoggerFactory.getLogger(SyncTaskService.class);
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    YbGoodsSyncDraftMapper ybGoodsSyncDraftMapper;
    @Autowired
    YbGoodsSyncDraftExtMapper ybGoodsSyncDraftExtMapper;
    @Autowired
    YbConfigGoodsSyncMapper ybConfigGoodsSyncMapper;
    @Autowired
    YbGoodsMapper ybGoodsMapper;

    private List<Map<String, String>> diffImgs = new ArrayList();

    public Map<String, String> findGoods(Map<String, Object> param) {
        return goodsMapper.getGoodsD(param);
    }

    /**
     * 构建一个GoodsData对象 方便操作
     * @param goodsInfo
     * @return
     */
    private GoodsData buildGoodsData(Map<String, String> goodsInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁用注解
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);

        GoodsData goodsData = objectMapper.convertValue(goodsInfo, GoodsData.class);

        return goodsData;
    }

    /**
     * 根据商品模板获取同步设置
     * @param detailTpl 商品模板
     * @return
     */
    private YbConfigGoodsSync getTplConfig(int detailTpl) {
        return ybConfigGoodsSyncMapper.findByDetailTplFirst(detailTpl);
    }

    /**
     * 根据批准文号、条形码、模板获取yb_goods数据
     * @param approvalNumber
     * @param barCode
     * @param detailTpl
     * @return
     */
    private Map getYbData(String approvalNumber, String barCode, int detailTpl) {
        return ybGoodsMapper.queryYbGoodByPzwhBarCode(approvalNumber,barCode,  String.valueOf(detailTpl));
    }

    /**
     * 获取同步类型 1 新增 2更新0 拒绝
     * @param pageData yb商品数据
     * @return
     */
    private byte getSyncType(Map pageData) {
        byte syncType = 1;
        try {
            if (pageData != null) {
                if ("1".equals(pageData.get("update_status") + "")) {
                    syncType = 2;
                } else {
                    logger.info("{} 商品更新状态锁定 不能更新", pageData.get("goods_id"));
                    syncType = 0;
                }
            }
        } catch (Exception e) {
            syncType = 0;
            logger.debug("{} 获取同步类型发生错误 {}", pageData.get("goodsId"), e.getMessage());
        }

        return syncType;
    }

    /**
     * 获取商家商品图片
     * @param goodsId
     * @param siteId
     * @return
     */
    private List<Map<String, String>> getGoodsImgs(int goodsId, int siteId) {
        Map<String, Object> param = new HashMap();
        param.put("goodsId", goodsId);
        param.put("siteId", siteId);
        return goodsMapper.getGoodsImg(param);
    }

    /**
     * 获取图片同步状态
     * @param shopImages
     * @param ybzfImages
     * @return
     */
    private byte getImgSyncStatus(List<Map<String, String>> shopImages, List<YbImagesAttr> ybzfImages)  {
        byte imgSyncStatus = 0;
        if (ybzfImages.size() == 0) {
            imgSyncStatus = 110;
            diffImgs = shopImages;
        } else {
            // 一个双层循环查找
            for (Map<String, String> shopImgInfo : shopImages) {
                for (YbImagesAttr ybImgInfo : ybzfImages) {
                    if (!StringUtil.equals(ybImgInfo.getHash(), shopImgInfo.get("hash"))) {
                        try {
                            diffImgs.add(shopImgInfo);
                        } catch (Exception e) {
                            logger.error("图片转json出错 {}", e.getMessage());
                        }
                        imgSyncStatus = 110;
                        break;
                    }
                }
            }
        }

        return imgSyncStatus;
    }

    /**
     * 添加一条同步记录
     * @param goodsInfo
     * @param type
     * @throws Exception
     */
    public void addSyncRecord(Map<String, String> goodsInfo, int type) throws Exception {
        GoodsData goodsData = buildGoodsData(goodsInfo);

        int detailTpl = goodsData.getDetailTpl();
        YbConfigGoodsSync ybConfigGoodsSyncRecord = getTplConfig(detailTpl);

        if (ybConfigGoodsSyncRecord == null) {
            logger.info("{} 该模板没有设置数据需要同步", detailTpl);
            return;
        }

        String approvalNumber = goodsData.getApprovalNumber();
        //String specifCation = goodsData.getSpecifCation();
        String barCode = goodsData.getBarCode();
        // 根据批准文号条形码和规格获取ybzf数据
        Map pageData = getYbData(approvalNumber, barCode, detailTpl);
        logger.info("条形码和批准文号查询出的结果：" + pageData);
        // 1新增2更新 没有找到ybzf中对应的商品 标记为新增
        byte syncType = getSyncType(pageData);
        if (syncType == 0) {
            return;
        }

        // 信息同步状态 110(待同步，默认) 120(忽略 更新) 130(已更新/同步)
        int infoSyncStatus = 110;
        // 图片同步状态 110(待同步，默认) 120(忽略更新)  130(已更新/同步)
        int imgSyncStatus = 0;
        int ybGoodsId = 0;
        boolean checkImg = false;

        if (syncType == 2) {
            if (ybConfigGoodsSyncRecord.getAllowUpdate()) {
                checkImg = true;
                // 比较两个数据的差异 如果允许更新字段有不同则插入一条记录
                String[] allowFields = ybConfigGoodsSyncRecord.getFields().split(",");
                if (!GoodsSync.hasDiffDataOnAllowUpdateField(goodsInfo, pageData, allowFields)) {
                    // 将信息状态标记为无
                    infoSyncStatus = 0;
                }
            } else {
                logger.info("{} {} {}  不允许更新", goodsData.getGoodsId(), goodsData.getApprovalNumber(), goodsData.getDetailTpl());
                return;
            }
        } else if (syncType == 1) {
            if (ybConfigGoodsSyncRecord.getAllowAdd()) {
                if(pageData != null){
                    checkImg = true;
                }
            } else {
                logger.info("{} {} {}  不允许新增", goodsData.getGoodsId(), goodsData.getApprovalNumber(), goodsData.getDetailTpl());
                return;
            }
        }

        String goodsImgsJson = "";
        if (checkImg) {
            // 检查下图片是不是不同
            List<Map<String, String>> shopImages = getGoodsImgs(goodsData.getGoodsId(), goodsData.getSiteId());
            if (shopImages.size() > 0 && syncType == 1) {
                imgSyncStatus = 110;
                goodsImgsJson = JacksonUtils.obj2json(shopImages);
            } else {
                ybGoodsId = StringUtil.convertToInt(pageData.get("goods_id") + "");
                List<YbImagesAttr> ybzfImages = ybGoodsMapper.queryImgsByGoodId(ybGoodsId);
                imgSyncStatus = getImgSyncStatus(shopImages, ybzfImages);
                goodsImgsJson = JacksonUtils.obj2json(diffImgs);
            }
        }

        try {
            YbGoodsSyncDraft ybGoodsSyncDraft = buildSyncDraft(goodsData, syncType, infoSyncStatus, imgSyncStatus, ybGoodsId);
            YbGoodsSyncDraftExtWithBLOBs ybGoodsSyncDraftExtWithBLOBs = buildExtData(goodsData);
            ybGoodsSyncDraftExtWithBLOBs.setGoodsImgs(goodsImgsJson);
            saveDB(ybGoodsSyncDraft, ybGoodsSyncDraftExtWithBLOBs);
        } catch (Exception e) {
            logger.debug("FAIL!!! save to db {}", e.getMessage());
            throw e;
        }
    }

    private YbGoodsSyncDraftExtWithBLOBs buildExtData(GoodsData goodsData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        YbGoodsSyncDraftExtWithBLOBs ybGoodsSyncDraftExtWithBLOBs = objectMapper.convertValue(goodsData, YbGoodsSyncDraftExtWithBLOBs.class);

        return ybGoodsSyncDraftExtWithBLOBs;
    }

    @Transactional
    private void saveDB(YbGoodsSyncDraft ybGoodsSyncDraft, YbGoodsSyncDraftExtWithBLOBs ybGoodsSyncDraftExtWithBLOBs) {
        // 保存到数据库
        ybGoodsSyncDraftMapper.insert(ybGoodsSyncDraft);
//        System.out.print(ybGoodsSyncDraft.getId());
        ybGoodsSyncDraftExtWithBLOBs.setSyncDraftId(ybGoodsSyncDraft.getId());
        ybGoodsSyncDraftExtMapper.insert(ybGoodsSyncDraftExtWithBLOBs);
    }

    private YbGoodsSyncDraft buildSyncDraft(GoodsData goodsData, byte syncType, int infoSyncStatus, int imgSyncStatus, int ybGoodsId) {
        YbGoodsSyncDraft ybGoodsSyncDraft = new YbGoodsSyncDraft();

        ybGoodsSyncDraft.setApproval_number(goodsData.getApprovalNumber());
        ybGoodsSyncDraft.setBrand_id(goodsData.getBarndId());
        ybGoodsSyncDraft.setDetail_tpl(goodsData.getDetailTpl());
        ybGoodsSyncDraft.setDrug_name(goodsData.getDrugName());
        ybGoodsSyncDraft.setSpecif_cation(goodsData.getSpecifCation());
        ybGoodsSyncDraft.setSync_type(syncType);
        ybGoodsSyncDraft.setInfo_sync_status(infoSyncStatus);
        ybGoodsSyncDraft.setYb_goods_id(ybGoodsId);
        ybGoodsSyncDraft.setSite_id(goodsData.getSiteId());
        ybGoodsSyncDraft.setImg_sync_status(imgSyncStatus);

        return ybGoodsSyncDraft;
    }

    private String getStrValue(Object field) {
        if (field == null) {
            return "";
        }

        return String.valueOf(field);
    }

    /**
     * @deprecated
     * @param goodsInfo
     * @return
     */
    protected YbGoodsSyncDraft buildSyncDraft(Map goodsInfo) {

//        return objectMapper.convertValue(goodsInfo, YbGoodsSyncDraft.class);
        YbGoodsSyncDraft ybGoodsSyncDraft = new YbGoodsSyncDraft();
        ybGoodsSyncDraft.setApproval_number(String.valueOf(goodsInfo.get("approvalNumber")));

        if ( StringUtil.isNotEmpty(getStrValue(goodsInfo.get("brandI")) ) ) {
            int brandId = StringUtil.convertToInt(String.valueOf(goodsInfo.get("brandId")));
            ybGoodsSyncDraft.setBrand_id(brandId);
        }
        if (StringUtil.isNotEmpty(getStrValue(goodsInfo.get("detailTpl")))) {
            int detailTpl = StringUtil.convertToInt(String.valueOf(goodsInfo.get("detailTpl")));
            ybGoodsSyncDraft.setDetail_tpl(detailTpl);
        }
        ybGoodsSyncDraft.setDrug_name(String.valueOf(goodsInfo.get("drugName")));

//        int siteId = StringUtil.convertToInt(String.valueOf(goodsInfo.get("siteId")));
//        ybGoodsSyncDraft.setSite_id(siteId);
        ybGoodsSyncDraft.setSpecif_cation(String.valueOf(goodsInfo.get("specifCation")));

        return ybGoodsSyncDraft;
    }
}
