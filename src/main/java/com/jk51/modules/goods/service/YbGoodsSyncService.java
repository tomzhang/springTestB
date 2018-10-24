package com.jk51.modules.goods.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbGoodsSyncGrid;
import com.jk51.model.goods.YbImagesAttr;
import com.jk51.modules.goods.library.ResultMap;
import com.jk51.modules.goods.mapper.YbGoodsMapper;
import com.jk51.modules.goods.mapper.YbGoodsSyncMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Service
public class YbGoodsSyncService {
    @Autowired
    YbGoodsSyncMapper ybGoodsSyncMapper;
    @Autowired
    YbGoodsMapper ybGoodsMapper;

    /**
     * 获取需要新增或者更新的商家商品列表
     *
     * @param ybGoodsSyncGrid
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfo<?> querySyncList(YbGoodsSyncGrid ybGoodsSyncGrid, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            if (ybGoodsSyncGrid.getStart_date() != null && !ybGoodsSyncGrid.getStart_date().equals("")){
                String sDate = fm.format(ybGoodsSyncGrid.getStart_date());
                ybGoodsSyncGrid.setStart_date(format.parse(sDate + " 00:00:00"));
            }
            if (ybGoodsSyncGrid.getEnd_date() != null && !ybGoodsSyncGrid.getEnd_date().equals("")){
                String eDate = fm.format(ybGoodsSyncGrid.getEnd_date());
                ybGoodsSyncGrid.setEnd_date(format.parse(eDate + " 23:59:59"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (ybGoodsSyncGrid.getMerchant_name()!=null) ybGoodsSyncGrid.setMerchant_name(ybGoodsSyncGrid.getMerchant_name().trim());
        if (ybGoodsSyncGrid.getDrug_name()!=null) ybGoodsSyncGrid.setDrug_name(ybGoodsSyncGrid.getDrug_name().trim());
        if (ybGoodsSyncGrid.getBar_code()!=null) ybGoodsSyncGrid.setBar_code(ybGoodsSyncGrid.getBar_code().trim());
        if (ybGoodsSyncGrid.getSpecif_cation()!=null) ybGoodsSyncGrid.setSpecif_cation(ybGoodsSyncGrid.getSpecif_cation().trim());
        if (ybGoodsSyncGrid.getApproval_number()!=null) ybGoodsSyncGrid.setApproval_number(ybGoodsSyncGrid.getApproval_number().trim());

        List<PageData> list = this.ybGoodsSyncMapper.querySyncGoodsList(ybGoodsSyncGrid);

        return new PageInfo<>(list);
    }

    /**
     * 批量删除
     *
     * @param good_ids
     */
    @Transactional
    public void batchDelSyncGoods(String[] good_ids) {
        this.ybGoodsSyncMapper.batchDelSyncGoods(good_ids);
    }

    public List<PageData> queryGoodsConfigList(Integer detail_tpl) {
        return this.ybGoodsSyncMapper.queryGoodsConfigList(detail_tpl);
    }

    /**
     * 设定同步字段表是否允许新增或者更新，可以新增或者更新的字段
     *
     * @param pageData
     */
    @Transactional
    public void setGoodConfig(PageData pageData) {
        this.ybGoodsSyncMapper.setGoodConfig(pageData);
    }

    public Map<String, Object> querySyncGoodById(Integer sync_draft_id) {

        PageData syncGood = this.ybGoodsSyncMapper.querySyncGoodBySyncDraftId(sync_draft_id);
        List<String> columns = ybGoodsMapper.queryYbGoodsColumns();
        String types = syncGood.getString("goods_forpeople");
        String[] typeArray = types.split(",");
        System.out.print(typeArray[0]);
        StringBuffer typeCast = new StringBuffer();
        for (int i = 0; i < typeArray.length; i++) {
            if (typeArray[i].equals("120")) {
                typeCast.append("成人,");
            } else if (typeArray[i].equals("130")) {
                typeCast.append("婴幼儿,");
            } else if (typeArray[i].equals("140")) {
                typeCast.append("儿童,");
            } else if (typeArray[i].equals("150")) {
                typeCast.append("男性,");
            } else if (typeArray[i].equals("160")) {
                typeCast.append("妇女,");
            } else if (typeArray[i].equals("170")){
                typeCast.append("中老年,");
                System.out.print(typeArray[i]);
            } else {
                typeCast.append("不限,");
            }
        }
        String typeJson = typeCast.toString();
        typeJson = typeJson.substring(0, typeJson.length() - 1);
        syncGood.put("goods_forpeople", typeJson);
        String fields_s = syncGood.getString("fields");
        String[] fields = StringUtils.split(fields_s, ",");
        for (String column : columns){

            if (!syncGood.containsKey(column)){
                syncGood.put(column,null);
            }
        }

        PageData ybGood = null;

        if (Integer.parseInt(syncGood.get("sync_type").toString()) == 2) {
            //更新---只有在更新的时候需要查询出yb_goods表中对应数据
            //（根据approval_number和specif_cation确定一条数据）
            ybGood = this.ybGoodsMapper.queryYbGoodByPzwhBarCode(
                    syncGood.get("approval_number").toString(),
                    syncGood.get("bar_code").toString(),
                    syncGood.get("detail_tpl").toString());

        }
        if (ybGood != null) {
            String types1 = ybGood.getString("goods_forpeople");
            String[] typeArray1 = types1.split(",");
            StringBuffer typeCast1 = new StringBuffer();
            for (int i = 0; i < typeArray1.length; i++) {
                if (typeArray1[i].equals("120")) {
                    typeCast1.append("成人,");
                } else if (typeArray1[i].equals("130")) {
                    typeCast1.append("婴幼儿,");
                } else if (typeArray1[i].equals("140")) {
                    typeCast1.append("儿童,");
                } else if (typeArray1[i].equals("150")) {
                    typeCast1.append("男性,");
                } else if (typeArray1[i].equals("160")) {
                    typeCast1.append("妇女,");
                } else if (typeArray1[i].equals("170")){
                    typeCast1.append("中老年,");
                    System.out.print(typeArray1[i]);
                }else {
                    typeCast1.append("不限,");
                }
            }
            String typeJson1 = typeCast1.toString();
            typeJson1 = typeJson1.substring(0, typeJson1.length() - 1);
            ybGood.put("goods_forpeople", typeJson1);
            for (String column : columns){
                if (!ybGood.containsKey(column)){
                    ybGood.put(column,null);
                }
            }
        }




        Map<String, Object> map = new HashedMap();
        map.put("good", ybGood);
        map.put("syncGood", syncGood);
        map.put("field", fields);
        return map;
    }

    /**
     * 忽略更新商家商品
     *
     * @param good_sync_id
     */
    @Transactional
    public void ignoreUpdate(Integer good_sync_id) {
        this.ybGoodsSyncMapper.ignoreUpdate(good_sync_id);
    }

    public Map<String,Object> getSyncImgs(Integer id) {//yb_goods_sync_draft ---id
        Map<String, Object> map = new HashMap<>();
        PageData pageData = this.ybGoodsSyncMapper.querySyncGoodBySyncDraftId(id);
        if (pageData.get("yb_goods_id")==null){
            map.put("formerImgs",null);
        }else{
            String yb_goods_id = pageData.get("yb_goods_id").toString();
            List<YbImagesAttr> imgsByGoodId = this.ybGoodsMapper.queryImgsByGoodId(Integer.parseInt(yb_goods_id));
            map.put("formerImgs",imgsByGoodId);
        }
        List<Map> imgs = null;
        if (pageData.get("goods_imgs")==null){
            map.put("syncImgs",null);
        }else{
            try {
                imgs = JacksonUtils.json2list(pageData.get("goods_imgs").toString(),Map.class);
                map.put("syncImgs",imgs);
            } catch (Exception e) {
            }
        }
        if(pageData.get("yb_goods_id")==null){
            pageData.put("yb_goods_id","");
        }
        map.put("good",pageData);

        return ResultMap.successResult(map);
    }

    public Map<String, Object> updatePic(Map<String, Object> parameterMap) {
        YbImagesAttr ybImagesAttr = new YbImagesAttr();

        try {
            int goods_id = Integer.parseInt(parameterMap.get("goods_id").toString());
            if (goods_id == 0) {
                return ResultMap.errorResult("notUpdate");
            }
            // 根据hash从i_yf_image取出not null 字段
            /*YbImagesAttr ybImages = this.ybGoodsMapper.queryDefaultImg(goods_id);
            if (ybImages == null) {
                ybImagesAttr.setIs_default(true);
            }*/
            HashMap img = this.ybGoodsSyncMapper.queryIyfImageByMd5(parameterMap.get("hash").toString());
            ybImagesAttr.setFlag(true);
            ybImagesAttr.setHeight(Short.parseShort(img.get("height").toString()));
            ybImagesAttr.setWidth(Short.parseShort(img.get("width").toString()));
            ybImagesAttr.setSize(Integer.parseInt(img.get("size").toString()));
            ybImagesAttr.setGoods_id(Integer.parseInt(parameterMap.get("goods_id").toString()));
            ybImagesAttr.setHash(parameterMap.get("hash").toString());
            if ("1".equals(img.get("is_default").toString())) {
                ybImagesAttr.setIs_default(true);
            } else {
                ybImagesAttr.setIs_default(false);
            }
            this.ybGoodsMapper.saveSinglePic(ybImagesAttr);
            return ResultMap.successResult(new HashMap<>());
        } catch (NumberFormatException e) {
            return ResultMap.errorResult("更新图片失败");
        }
    }

    public Map<String, Object> handleUpdateImgStatus(Map<String, Object> parameterMap) {
        try {
            this.ybGoodsSyncMapper.handleUpdateImgStatus(parameterMap.get("id"));
            return ResultMap.successResult(new HashMap<>());
        } catch (Exception e) {
            return ResultMap.errorResult("标记图片状态为已处理失败");
        }
    }
    //兼容老图片数据
    public String queryByHash(String hash) {
        String hashKey=ybGoodsSyncMapper.queryIyfImageByHash(hash);
        if(StringUtils.isEmpty(hashKey)||"NULL".equals(hashKey)){
            return hash;
        }
        return hashKey.replace("group1/M00/","");
    }

    public PageInfo<?> goodsSyncQueryList(Map<String, Object> parameterMap, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);


        List<PageData> list = this.ybGoodsSyncMapper.goodsSyncQueryList(parameterMap);

        return new PageInfo<>(list);
    }

    public void batchSyncDel(String[] ids) {
        if (ids.length != 0 && ids != null) {
            this.ybGoodsSyncMapper.batchSyncDel(ids);
        }
    }
}
