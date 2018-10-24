package com.jk51.modules.goods.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.goods.Category;
import com.jk51.modules.goods.mapper.CategoryMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.GoodsmMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper mapper;

    @Autowired
    private GoodsmMapper gMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    public Map<String,Object> getGoodsById(Map map){
        return goodsMapper.getGoodsD(map);
    }

    public List<Category> queryCategory(Map map) {
        return mapper.getList(map);
    }

    public List<Category> queryCategoryByPid(Map map) {
        return mapper.getListByPid(map);
    }

    public Category queryCategoryById(Map map) {
        return mapper.getByCateId(map);
    }

    public Category getByCateName(Map map) {
        return mapper.getByCateName(map);
    }

    public Map<String,Object> cateUpdate(Map<String,Object> map) {
        Map<String,Object> returnMap = new HashMap<String,Object>();
        String siteId = (String) map.get("siteId");
        List data = null;
        try {
            data = JacksonUtils.json2listMap((String) map.get("datas"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i=0;
        for (Object m:data) {
            String cateId = ((Map)m).get("cate_id").toString();
            String cateName = ((Map)m).get("value").toString();
            Map param = new HashedMap();
            param.put("siteId",siteId);
            param.put("cateId",cateId);
            Category category=mapper.getByCateId(param);
            category.setCateName(cateName);
            mapper.update(category);
            i++;
        }


        /*Iterator<Map.Entry<Integer, String>> it = (datas.get("datas")).entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, String> entry = it.next();
        }*/
        /*Iterator entries = ((Map)datas.get("datas")).entrySet().iterator();
        int i=0;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            Category category=mapper.getByCateId(map);
            category.setCateName(valueObj.toString());
            mapper.update(category);
           i++;
        }*/
        returnMap.put("msg", "修改了"+i+"条记录！");
        return returnMap;
    }

    public void cateIns(Category cate) {
        mapper.insert(cate);
    }

    public void cateDel(Map map) {
        mapper.del(map);
    }

    public List<Map<String, Object>> goodsSearch(Map map) {
        return gMapper.goodsSearch(map);
    }

    public String getCodeIncrease(Map map) {
        return mapper.getCodeIncrease(map);
    }

    public Category getByCateCode(Map map) {
        return mapper.getByCateCode(map);
    }

    public void updateGoodsCate(Map map) {
        mapper.updateGoodsCate(map);
    }

    public List<Map> getByGoodsId(Map map) {
        return gMapper.getByGoodsId(map);
    }

    public void insert(Map map) {
        gMapper.insert(map);
    }

    /**
     * 更新图片状态为无效
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public void del(Map map) throws BusinessLogicException {
        int goodsId = NumberUtils.toInt(String.valueOf(map.get("goodsId")));
        int siteId = NumberUtils.toInt(String.valueOf(map.get("siteId")));
        if (goodsId == 0 || siteId == 0) {
            throw new BusinessLogicException("参数有误");
        }
        Map<String, Integer> result = gMapper.isUpListShopMainImg(map);
        if (result.get("goods_status") != null && result.get("goods_status") == 1 && result.get("is_default") != null && result.get("is_default") == 1) {
            throw new BusinessLogicException("上架中的商品主图不能删除");
        }

        if (map.containsKey("nextImgId")) {
            Map defaultImgMap = new HashMap();
            defaultImgMap.putAll(map);
            defaultImgMap.put("imgHash", "nextImgId");
            gMapper.setDefaultImg(map);
        }

        gMapper.del(map);
    }

    @Transactional
    public void setDefaultImg(Map map) {
        gMapper.delDefaultImg(map);
        gMapper.setDefaultImg(map);
    }

    public int getGoodsCountByCateCode(Map m) {
        return gMapper.getGoodsCountByCateCode(m);
    }

    public int getByGoodsIdAndHash(Map map){ return gMapper.getByGoodsIdAndHash(map); }

    public int getByGoodsIdAndHashflag(Map map){return  gMapper.getByGoodsIdAndHashflag(map);}

    public int updateByGoodsIdAndHashFlag(Map map){return gMapper.updateByGoodsIdAndHashFlag(map);}
    
    public boolean saveImgMulti(int goods_id_old, int goods_id, int site_id){
        return gMapper.saveImgMulti(goods_id_old, goods_id, site_id);
    }

    public String queryGoodsImgExtra(Map param){
        return mapper.queryGoodsImgExtra(param);
    }

    public String querGoodsHashImg(Map param){
        return mapper.querGoodsHashImg(param);
    }

    public int updateCategoryImg(Map param){
        return mapper.updateCategoryImg(param);
    }

    public int delCategoryImg(Map<String, Object> param) {
        return mapper.delCategoryImg(param);
    }
}
