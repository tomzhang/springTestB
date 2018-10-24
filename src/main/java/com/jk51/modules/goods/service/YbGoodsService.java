package com.jk51.modules.goods.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.goods.*;
import com.jk51.modules.goods.dto.JoinCateDto;
import com.jk51.modules.goods.mapper.YbCategoryMapper;
import com.jk51.modules.goods.mapper.YbGoodsMapper;
import com.jk51.modules.goods.mapper.YbGoodsSyncMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class YbGoodsService {
    @Autowired
    private YbGoodsMapper ybGoodsMapper;

    @Autowired
    YbCategoryMapper categoryMapper;

    @Autowired
    YbGoodsSyncMapper ybGoodsSyncMapper;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 获取后台管理商品列表
     *
     * @param page
     * @param pageSize
     * @param ybGoodsGrid
     * @return
     */
    //@Cacheable(value="ybGoods" ,keyGenerator = "defaultKeyGenerator")
    public PageInfo<YbGoods> queryGoodsList(Integer page, Integer pageSize, YbGoodsGrid ybGoodsGrid) {
        if (ybGoodsGrid.getApproval_number()!=null)
            ybGoodsGrid.setApproval_number(ybGoodsGrid.getApproval_number().trim());
        if (ybGoodsGrid.getSpecif_cation()!=null)
            ybGoodsGrid.setSpecif_cation(ybGoodsGrid.getSpecif_cation().trim());
        if (ybGoodsGrid.getSpecif_code()!=null)
            ybGoodsGrid.setSpecif_code(ybGoodsGrid.getSpecif_code().trim());
        if (ybGoodsGrid.getGoods_company()!=null)
            ybGoodsGrid.setGoods_company(ybGoodsGrid.getGoods_company().trim());
        if (StringUtil.isNotBlank(ybGoodsGrid.getCate_id())) {//如果类目存在就要获取该类目的所有子类目cate_code
            Integer cateId = categoryMapper.getCodeId(ybGoodsGrid.getCate_id());
            List cats = this.getCatAllChildren(cateId);
            ybGoodsGrid.setCats(cats);
        }

        PageHelper.startPage(page, pageSize);//开启分页
        List list = new ArrayList();
        //判断是否对图片是否存在有要求
        try{
            if (ybGoodsGrid.getHas_image() == null) {
                list = this.ybGoodsMapper.getListIgnoreImgExists(ybGoodsGrid);
            } else {
                list = this.ybGoodsMapper.getList(ybGoodsGrid);
            }
        }catch (Exception e){e.printStackTrace();}

        return new PageInfo<>(list);
    }

    public PageInfo<YbGoods> queryGoodsList2(Integer page, Integer pageSize, YbGoodsGrid ybGoodsGrid) {
        if (ybGoodsGrid.getApproval_number()!=null)
            ybGoodsGrid.setApproval_number(ybGoodsGrid.getApproval_number().trim());
        if (ybGoodsGrid.getSpecif_cation()!=null)
            ybGoodsGrid.setSpecif_cation(ybGoodsGrid.getSpecif_cation().trim());
        if (ybGoodsGrid.getSpecif_code()!=null)
            ybGoodsGrid.setSpecif_code(ybGoodsGrid.getSpecif_code().trim());
        if (ybGoodsGrid.getGoods_company()!=null)
            ybGoodsGrid.setGoods_company(ybGoodsGrid.getGoods_company().trim());

        PageHelper.startPage(page, pageSize);//开启分页
        List list = new ArrayList();
        //判断是否对图片是否存在有要求
        try{
            list = this.ybGoodsMapper.getList2(ybGoodsGrid);
        }catch (Exception e){e.printStackTrace();}

        return new PageInfo<>(list);
    }

    /**
     * 获取商家台管理商品列表
     *
     * @param page
     * @param pageSize
     * @param ybGoodsGrid
     * @return
     */
    //@Cacheable(value="ybGoods" ,keyGenerator = "defaultKeyGenerator")
    public PageInfo<YbGoods> queryMerchantLis(Integer page, Integer pageSize, YbGoodsGrid ybGoodsGrid) {
        if (ybGoodsGrid.getApproval_number()!=null)
            ybGoodsGrid.setApproval_number(ybGoodsGrid.getApproval_number().trim());
        if (ybGoodsGrid.getSpecif_cation()!=null)
            ybGoodsGrid.setSpecif_cation(ybGoodsGrid.getSpecif_cation().trim());
        if (ybGoodsGrid.getSpecif_code()!=null)
            ybGoodsGrid.setSpecif_code(ybGoodsGrid.getSpecif_code().trim());
        if (ybGoodsGrid.getGoods_company()!=null)
            ybGoodsGrid.setGoods_company(ybGoodsGrid.getGoods_company().trim());
        if (StringUtil.isNotBlank(ybGoodsGrid.getCate_id())) {//如果类目存在就要获取该类目的所有子类目cate_code
            Integer cateId = categoryMapper.getCodeId(ybGoodsGrid.getCate_id());
            List cats = this.getCatAllChildren(cateId);
            ybGoodsGrid.setCats(cats);
        }

        PageHelper.startPage(page, pageSize);//开启分页
        List list = new ArrayList();
        //判断是否对图片是否存在有要求
        try{

                list = this.ybGoodsMapper.getMerchantList(ybGoodsGrid);

        }catch (Exception e){e.printStackTrace();}

        return new PageInfo<>(list);
    }


    /**
     * 批删除
     *
     * @param ids
     */
    @Transactional
    public void batchDel(String[] ids) {
        if (ids.length != 0 && ids != null) {
            this.ybGoodsMapper.batchDel(ids);//删除商品信息--软删除（修改goods_status为3）
//            this.ybGoodsMapper.batchDelPic(ids);//删除商品所有图片--软删除（修改flag为1）
        }

    }

    /**
     * 批处理后台商品图片状态为已处理
     *
     * @param ids
     */
    @Transactional
    public void batchUpdateImg(String[] ids) {
        if (ids.length != 0 && ids != null) {
            this.ybGoodsMapper.batchUpdateImg(ids);
        }

    }

    /**
     * 删除单张图片
     *
     * @param goodId
     * @param hashId
     */
    @Transactional
    public void delSinglePic(Integer goodId, String hashId) {
        this.ybGoodsMapper.delSinglePic(goodId, hashId);

        //重新设置主图
        //判断主图是否存在
        YbImagesAttr ybImagesAttr = this.ybGoodsMapper.queryDefaultImg(goodId);
        if (ybImagesAttr == null) {
            //主图不存在，查询出该商品第一张图片并设置成主图
            YbImagesAttr ybImagesAttr_first = this.ybGoodsMapper.queryImgLimitOne(goodId);
            if (ybImagesAttr_first != null && StringUtil.isNotBlank(ybImagesAttr_first.getHash())) {
                this.ybGoodsMapper.updateGoodDefaultImg(goodId, ybImagesAttr_first.getHash());
            }
        }
    }

    /**
     * 获取单张图片信息
     *
     * @param goodId
     * @return
     */
    public Map<String, Object> querySingleGoodDetail(Integer goodId) {

        return this.ybGoodsMapper.querySingleGoodDetail(goodId);
    }

    /**
     * 更新后台商品信息（包含品牌，包含扩展信息）
     *
     * @param pageData
     */
    @Transactional
    public void updateGood(PageData pageData) {
        if (StringUtils.isNotEmpty(pageData.getString("barnd_name"))) {
            this.setBrandId(pageData);
        }
        //开始更新good和扩展表
        this.ybGoodsMapper.updateItem(pageData);
        this.ybGoodsMapper.updateGoodExtd(pageData);
    }

    public void setBrandId(PageData pageData){
        //获取品牌id，将品牌id放入barnd_name
        YbBarnd ybBarnd = this.ybGoodsMapper.queryBarnd(pageData.getString("barnd_name"));
        if (ybBarnd != null) {
            if (ybBarnd.getBarnd_id() != null) {
                pageData.put("barnd_name", ybBarnd.getBarnd_id());
            }
        } else {
            //在品牌表新增品牌
            this.ybGoodsMapper.saveBarnd(pageData.getString("barnd_name"));
            ybBarnd = this.ybGoodsMapper.queryBarnd(pageData.getString("barnd_name"));
            pageData.put("barnd_name", ybBarnd.getBarnd_id());
        }
    }

    /**
     * 新增后台商品（包含扩展信息）
     *
     * @param pageData
     */
    @Transactional
    public void saveGood(PageData pageData) {
        if (StringUtils.isNotEmpty(pageData.getString("barnd_name"))) {
            this.setBrandId(pageData);
        }
        this.ybGoodsMapper.saveGood(pageData);
        this.ybGoodsMapper.saveGoodExtd(pageData);
    }

    /**
     * 保存商品图片到图片表并根据主图是否存在设置主图
     *
     * @param fileUploadResult
     * @return
     */
    @Transactional
    public YbImagesAttr saveImg(YbImagesAttr fileUploadResult) {
        fileUploadResult.setIs_default(false);
        //判断图片是否重复
        YbImagesAttr checkExist = this.ybGoodsMapper.queryImgsByGoodIdAndHash(fileUploadResult.getGoods_id(),fileUploadResult.getHash());
        if (checkExist!=null){
            return null;
        }
        //判断主图是否存在
        YbImagesAttr ybImagesAttr = this.ybGoodsMapper.queryDefaultImg(fileUploadResult.getGoods_id());
        if (ybImagesAttr == null) {
            //主图不存在，查询出该商品第一张图片并设置成主图
            YbImagesAttr ybImagesAttr_first = this.ybGoodsMapper.queryImgLimitOne(fileUploadResult.getGoods_id());
            if (ybImagesAttr_first != null && StringUtil.isNotBlank(ybImagesAttr_first.getHash())) {
                //图片存在
                this.ybGoodsMapper.updateGoodDefaultImg(fileUploadResult.getGoods_id(), ybImagesAttr_first.getHash());
            } else {
                //图片不存在，设置本图为主图
                fileUploadResult.setIs_default(true);
            }
        }
        this.ybGoodsMapper.saveSinglePic(fileUploadResult);//保存图片
        //设置商品图片状态为未处理
        this.ybGoodsMapper.setGoodImgStatusToUnhandle(fileUploadResult.getGoods_id());
        return fileUploadResult;
    }

    /**
     * 根据后台商品id获取该商品所有图片
     *
     * @param goodId
     * @return
     */
    public List<YbImagesAttr> queryImgsByGoodId(Integer goodId) {
        return this.ybGoodsMapper.queryImgsByGoodId(goodId);
    }

    /**
     * 设置商品主图
     *
     * @param
     */
    @Transactional
    public void setDefaultImg(Integer goodId, String hashId) {
        /*YbImagesAttr defaultImg = this.ybGoodsMapper.queryDefaultImg(goodId);
        if (defaultImg != null) {
            //将原主图设置为 not default
            this.ybGoodsMapper.updateImgToNotDefault(defaultImg.getId());
        }*/
        this.ybGoodsMapper.updateImgToNotDefault(goodId);
        //设置本图为default
        this.ybGoodsMapper.updateGoodDefaultImg(goodId, hashId);
    }

    /**
     * 将自商户管理系统的商品新增到总部后台系统
     *
     * @param pageData
     */
    @Transactional
    public String saveGoodOfMerchant(PageData pageData) {
        int detail_tpl =Integer.parseInt(pageData.getString("detail_tpl"));
        List<PageData> list =ybGoodsSyncMapper.queryGoodsConfigList(detail_tpl);
        if (list.get(0).getInteger("allow_add")==0){
            return "notAllowAdd";
        }
        PageData page=new PageData();
        String fields =list.get(0).getString("fields");
        String[]  field =fields.split(",");
        for (int i = 0; i < field.length; i++) {
            if(pageData.containsKey(field[i])) {
                String value = pageData.getString(field[i]);
                page.put(field[i], value);
            }
        }

        page.put("detail_tpl",pageData.getInt("detail_tpl"));
        page.put("syncGood_id",pageData.getInt("syncGood_id"));
        page.put("shop_price","0.00");
        page.put("market_price","0.00");
        page.put("goods_title",pageData.getString("goods_title")==""?null:pageData.getString("goods_title"));
        page.put("drug_name",pageData.get("drug_name"));
        page.put("approval_number",pageData.get("approval_number"));
        page.put("goods_status",2);
        page.put("update_status",0);
        Integer updateNum = this.ybGoodsMapper.saveGoodOfMerchant(page);//将商户商品表中商品作为新的商品保存，并获取主键值
        int goods_id = page.getInt("goods_id");
        int syncGood_id =page.getInt("syncGood_id");
        this.ybGoodsSyncMapper.updatePic(syncGood_id,goods_id);
        this.ybGoodsMapper.saveGoodExtdOfMerchant(page);//保存商户商品表的扩展信息
        this.ybGoodsSyncMapper.setStatus(pageData.get("syncGood_id"));
        return  "ok";
    }

    /**
     * 将来自商户管理系统的商品信息更新到总部后台系统
     *
     * @param pageData
     */
    @Transactional
    public String updateGoodOfMerchant(PageData pageData) {
        int detail_tpl =Integer.parseInt(pageData.getString("detail_tpl"));
        List<PageData> list =ybGoodsSyncMapper.queryGoodsConfigList(detail_tpl);
        // (PageData model:list) {
        if (list.get(0).getInteger("allow_update")==0){
            return "notAllowUpdate";
        }
        // }
        PageData page=new PageData();
        String fields =list.get(0).getString("fields");
        String[]  field =fields.split(",");
        for (int i = 0; i < field.length; i++) {
            if(pageData.containsKey(field[i])) {
                String value = pageData.getString(field[i]);
                page.put(field[i], value);
            }
        }
        page.put("detail_tpl",pageData.getInt("detail_tpl"));
        page.put("syncGood_id",pageData.getInt("syncGood_id"));
        page.put("shop_price","0.00");
        page.put("market_price","0.00");
        page.put("goods_title",pageData.getString("goods_title"));

        PageData ybGood = this.ybGoodsMapper.queryYbGoodByPzwhAndGg(page.getString("approval_number"), page.getString("specif_cation"),page.getString("detail_tpl"));
        Object goodId =ybGood.get("goods_id");
        page.put("goods_id",goodId);
        page.put("goods_status",2);
        page.put("update_status",0);
        this.ybGoodsMapper.updateItem(page);//将商户商品表基本信息更新到后台商品表
        this.ybGoodsMapper.updateGoodExtd(page);//将商户商品扩展信息更新到后台商品扩展表
        this.ybGoodsSyncMapper.setStatus(page.get("syncGood_id"));
        return  "ok";
    }

    public boolean restore(int goodsId) {
        return ybGoodsMapper.restore(goodsId);
    }

    /**
     * 获取一个类目的所有子类目的cate_code集合
     *
     * @param cateId
     * @return
     */
    public List getCatAllChildren(Integer cateId) {
        List cats = new ArrayList<>();
        YbCategory category = this.categoryMapper.queryCatByCateId(cateId);
        cats.add(category.getCate_code());
        List<YbCategory> catList = this.categoryMapper.getChildren(cateId);
        for (YbCategory cat : catList
                ) {
            cats.add(cat.getCate_code());
            if (cat.getSubYbCategory() != null && cat.getSubYbCategory().size() > 0) {
                for (YbCategory cat_second : cat.getSubYbCategory()
                        ) {
                    cats.add(cat_second.getCate_code());
                    if (cat_second.getSubYbCategory() != null && cat_second.getSubYbCategory().size() > 0) {
                        for (YbCategory cat_third : cat_second.getSubYbCategory()
                                ) {
                            cats.add(cat_third.getCate_code());
                        }
                    }
                }
            }
        }
        return cats;
    }


    //修改商品分类
    @CacheEvict(value="ybGoods",allEntries=true)
    @Transactional
    public Map<String,Object> updateGoodsCate(String gls, String cateId) {

        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtil.isEmpty(gls)||StringUtil.isEmpty(cateId)){
            result.put("status","ERROR");
            result.put("errorMessage","参数不全");
            return result;
        }
        YbCategory ybCategory = categoryMapper.queryCatByCateId(Integer.valueOf(cateId));
        String cate_code = "";
        if(!StringUtil.isEmpty(ybCategory)){
            cate_code = ybCategory.getCate_code();
        }
        int num = 0;
        if(!StringUtil.isEmpty(cate_code)){
            num = ybGoodsMapper.updateGoodsCate(gls,cate_code);
        }
        if(num==1){
            result.put("status","OK");
            return result;
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","修改商品分类失败");
            return result;
        }
    }

    public YbCategory queryCateByCateCode(long userCateCode) {
        return categoryMapper.queryCatByCateCode(userCateCode);
    }

    public Map join51jkByCode(JoinCateDto joinCateDto) {
        return categoryMapper.join51jkByCode(joinCateDto.getCateCode(), joinCateDto.getSiteId());
    }

    //查询商品条形码是否存在
    public Map<String, Object> barCodeOne(String bar_code) {
        Map result = new HashMap();
        Map<String, Object> map = ybGoodsMapper.findYbGoodsByBarCode(bar_code);
        if (StringUtil.isEmpty(map)) {
            result.put("status", "OK");
        } else {
            result.put("status", "ERROR");
        }
        return result;
    }
}