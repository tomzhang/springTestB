package com.jk51.modules.esn.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.es.utils.PinYinUtil;
import com.jk51.modules.esn.entity.DefUrl;
import com.jk51.modules.esn.entity.GoodsInfo;
import com.jk51.modules.esn.entity.GoodsUrl;
import com.jk51.modules.esn.entity.ImageInfo;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.esn.util.BulkUpdateUtils;
import com.jk51.modules.esn.util.SuggestUpdateUtils;
import com.jk51.modules.esn.util.UpdateUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
public class GoodsEsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsEsService.class);

    @Autowired
    private UpdateUtils updateUtils;
    @Autowired
    private GoodsEsMapper goodsEsMapper;
    @Autowired
    private BulkUpdateUtils bulkUpdateUtils;
    @Autowired
    private SuggestUpdateUtils suggestUpdateUtils;
    @Autowired
    private TransportClient client;

    @Value("${es.goods.index}")
    private String index;

    @Value("${es.suggest.index}")
    private String sIndex;

    public void updateGoodsInxALlIndices(String siteIdStr, String goodId, String has_erp_price) throws Exception {
        String siteId = siteIdStr.split("_")[2];
        GoodsInfo goods;
//        if ("1".equals(has_erp_price)){
//            goods = goodsEsMapper.queryGoodsDetail4erp(siteId, goodId).get(0);
//            if(goods.getErp_price()!=null&&goods.getErp_price()!=0){
//                goods.setShop_price(goods.getErp_price());
//            }
//        } else {
            goods = goodsEsMapper.queryGoodsDetail(siteId, goodId).get(0);
//        }
        if(null == goods.getBar_code() || "".equals(goods.getBar_code())){
            goods.setBar_code_status("2");//2：无条形码
        }else{
            goods.setBar_code_status("1");//1：有条形码
        }
        int distribute = goodsEsMapper.queryDistribute(siteId, goodId) > 0 ? 1 : 0;//yes=1，no=0
        goods.setIsDistribute(String.valueOf(distribute));
        DefUrl defUrl = new DefUrl();
        defUrl.setImageId(goodsEsMapper.queryGoodsPrimaryImage(siteId, goodId));
        ObjectMapper om = new ObjectMapper();
        goods.setDef_url(om.writeValueAsString(defUrl).replace("null", "").replace("NULL", ""));
        /*String cateCode = goodsEsMapper.queryCategoryCode(siteId,goods.getUser_cateid());
        if (StringUtil.isNotEmpty(cateCode)) {
            goods.setUser_cateid(cateCode);
        }else {
            goods.setUser_cateid("");
        }*/

        //设置拼音首字母
        if (StringUtil.isNotEmpty(goods.getDrug_name())) {
            String as[] = PinYinUtil.getPinYin(goods.getDrug_name(), " ").split(" ");
            StringBuffer stringbuffer2 = new StringBuffer();
            for(int k1 = 0; k1 < as.length; k1++){
                if(org.apache.commons.lang.StringUtils.isNotBlank(as[k1])){
                    stringbuffer2.append(as[k1].charAt(0));
                }
            }
//            this.setGoods_pinyin(PinYinUtil.getPinYin(this.getDrug_name(), ""));
            goods.setGoods_pinyin(PinYinUtil.getPinYin(stringbuffer2.toString(), ""));
            goods.setGoods_shouzimu(stringbuffer2.toString());
        }
        logger.info(goods.toString()+"--------------------------------------");
        updateUtils.update(index, siteId, goods, "goods_id");
        //设置关键字用于搜索建议
        goods.setKeyword(goods.getDrug_name());
        goods.setKeyword_py(goods.getDrug_name());
        updateUtils.update(sIndex, siteId, goods, "goods_id");
    }

    public void upgradeGoodsInx(String siteIdStr, String goodId) throws Exception {
        String siteId = siteIdStr.split("_")[2];
        GoodsInfo goods = goodsEsMapper.queryGoodsDetail(siteId, goodId).get(0);
        int distribute = goodsEsMapper.queryDistribute(siteId, goodId) > 0 ? 1 : 0;//yes=1，no=0
        goods.setIsDistribute(String.valueOf(distribute));
        DefUrl defUrl = new DefUrl();
        defUrl.setImageId(goodsEsMapper.queryGoodsPrimaryImage(siteId, goodId));
        ObjectMapper om = new ObjectMapper();
        goods.setDef_url(om.writeValueAsString(defUrl).replace("null", "").replace("NULL", ""));
        updateUtils.update(index, siteId, goods, "goods_id");
    }
    @Async
    public void batchUpdateGoods(String siteIdStr, String has_erp_price){
        logger.info("BatchService.batchUpdateGoods :{}", siteIdStr);
        String siteId=siteIdStr.split("_")[2];
        List<GoodsInfo> gInfos;
//        if("1".equals(has_erp_price)){
//            gInfos=goodsEsMapper.queryGoodsDetail4erp(siteId, null);
//        }else{
            gInfos=goodsEsMapper.queryGoodsDetail(siteId, null);
//        }
        logger.info("商户：{}，更新ES商品数量 :{}",siteIdStr, gInfos.size());
        bulkUpdateUtils.defineIndexTypeMapping(index,siteId);
        bulkUpdateUtils.delByBrandId(index,siteId);
        merge(gInfos,goodsEsMapper.queryGoodsImageBySiteId(siteId),goodsEsMapper.queryDistributeBySiteId(siteId));
        bulkUpdateUtils.batchInsert(index, siteId, gInfos, "goods_id");
    }

    private void merge(List<GoodsInfo> gInfos, List<ImageInfo> imageInfoList, List<GoodsInfo> distributes){
        logger.info("BatchService.merge");
        Map<String,Map<String,String>> maps=imageProcess(imageInfoList);
        for(GoodsInfo f:gInfos){
            Map<String,String> doc=maps.get(f.getGoods_id().toString());
            if(doc!=null){
                f.setDef_url(doc.get("def_url"));
                f.setGoods_url(doc.get("goods_url"));
            }
            if(distributes!=null&&distributes.size()>0)
                for(GoodsInfo distribute:distributes){
                    if(f.getGoods_id().equals(distribute.getGoods_id()))
                        f.setIsDistribute("0");
                }
            if(StringUtils.isEmpty(f.getIsDistribute())){
                f.setIsDistribute("1");
            }
            if(f.getErp_price()!=null&&f.getErp_price()!=0){
                f.setShop_price(f.getErp_price());
            }
            if (StringUtil.isNotEmpty(f.getDrug_name())) {
                f.setDrug_name_py(f.getDrug_name());
            }
            /*String cateCode = goodsEsMapper.queryCategoryCode(siteId,f.getUser_cateid());
            if (StringUtil.isNotEmpty(cateCode)) {
                f.setUser_cateid(cateCode);
            }else {
                f.setUser_cateid("");
            }*/
            if(null == f.getBar_code() || "".equals(f.getBar_code())){
                f.setBar_code_status("2");//2：无条形码
            }else{
                f.setBar_code_status("1");//1：有条形码
            }
            f.buildKeyWd();
        }
    }

    private Map<String,Map<String,String>> imageProcess(List<ImageInfo> imageInfos){
        Map<String,Map<String,String>> result=new HashMap<>();

        List<ImageInfo> images=imageInfos.parallelStream()
            .sorted(comparing(ImageInfo::getGoods_id))
            .collect(Collectors.toList());

        List<ImageInfo> temp=new ArrayList<>();
        for (ImageInfo i:images) {
            if(temp.size()!=0&&!temp.get(0).getGoods_id().equals(i.getGoods_id())){
                result.put(temp.get(0).getGoods_id(),buildImageUpdateMap(temp));
                temp.clear();
                temp.add(i);
            }else {
                temp.add(i);
            }
        }

        if(temp.size()>0)
            result.put(temp.get(0).getGoods_id(),buildImageUpdateMap(temp));

        return result;
    }

    private Map<String,String> buildImageUpdateMap(List<ImageInfo> arrayList){
        Map<String,String> map=new HashMap<>();
        ArrayList<GoodsUrl> goodsUrls=new ArrayList<>();
        DefUrl defUrl=null;
        for(ImageInfo i:arrayList){
            if (i.getIs_default().equals(1)){
                defUrl=new DefUrl();
                defUrl.setHostId(StringUtil.isEmpty(i.getHost_id())? "" : i.getHost_id());
                defUrl.setImageId(StringUtil.isEmpty(i.getHash())? "" : i.getHash());
            }else{
                GoodsUrl url=new GoodsUrl();
                url.setHostId(StringUtil.isEmpty(i.getHost_id())? "" : i.getHost_id());
                url.setImageId(StringUtil.isEmpty(i.getHash())? "" : i.getHash());
                goodsUrls.add(url);
            }
        };
        ObjectMapper om=new ObjectMapper();
        try {
            map.put("goods_url",om.writeValueAsString(goodsUrls.toArray()));
            map.put("def_url",om.writeValueAsString(defUrl).replace("null",""));
            map.put("goods_id",arrayList.get(0).getGoods_id());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return map;
    }

    @Async
    public void updateSuggestByBrandId(String brandId) {
        String shopid = brandId.split("_")[2];
        suggestUpdateUtils.defineSuggestTypeMapping(sIndex, shopid);
        suggestUpdateUtils.delByBrandId(sIndex, shopid);
        List<GoodsInfo> goods =goodsEsMapper.querySuggestGoods(shopid);
        bulkUpadteSuggest(goods, sIndex, shopid);
    }

    private void bulkUpadteSuggest(List<GoodsInfo> goods, String index, String type){

        logger.info("批量更新提示开始，本次数据有{}条",goods.size());
        //if (goods.size() == 0) return;
        BulkRequestBuilder bq=client.prepareBulk();

        goods.forEach(goodsInfo -> {
            if(StringUtils.isNotBlank(goodsInfo.getDrug_name()))
                bq.add(client.prepareIndex(index,type, UUID.randomUUID().toString())
                    .setRouting(type).setSource(convertDrug_name.apply(goodsInfo)));
        });
        if (bq.numberOfActions() == 0) return;
        BulkResponse resp =bq.execute().actionGet();

        logger.info("批量更新结束，处理总时间{}",resp.getTookInMillis());

    }

    private Function<GoodsInfo, Map<String, String>> convertDrug_name = goodsInfo -> {
        Map<String, String> result = new HashMap<>();
        result.put("keyword", goodsInfo.getDrug_name());
        result.put("keyword_py", goodsInfo.getDrug_name());
        result.put("shop_price",String.valueOf(goodsInfo.getShop_price()));
        return result;
    };
    @Async
    public void updateGoodsByCateIdNNew(String siteIdStr, String cateId) throws Exception {
        logger.info("BatchService.batchUpdateGoodsyCateId siteId : {}, cateIds : {}", siteIdStr, cateId);
        String siteId = siteIdStr.split("_")[2];
        List<GoodsInfo> gInfos;
        String[] cateIds = cateId.split("-");
        //查询分类下的cate_code集合
        String [] cateCodes = goodsEsMapper.queryCateCodeByCateId(siteId,cateIds);
        gInfos = goodsEsMapper.queryGoodsDetailByCateId(siteId, cateCodes);
        for (GoodsInfo goodsInfo:gInfos){
            updateGoodsInxALlIndices(siteIdStr, goodsInfo.getGoods_id().toString(), null);
        }
    }
    @Async
    public void updateGoodsByCateId(String siteIdStr, String cateId) throws Exception {
        logger.info("BatchService.batchUpdateGoodsByCateId siteId : {}, cateIds : {}", siteIdStr, cateId);
        String siteId = siteIdStr.split("_")[2];
        List<GoodsInfo> gInfos;
        String[] cateIds = cateId.split("-");
        //查询分类下的cate_code集合
        String [] cateCodes = goodsEsMapper.queryCateCodeByCateId(siteId,cateIds);
        gInfos = goodsEsMapper.queryGoodsDetailByCateId(siteId, cateCodes);


//        updateUtils.update(index, siteId, goods, "goods_id");
//        goods.setKeyword(goods.getDrug_name());
//        goods.setKeyword_py(goods.getDrug_name());
//        updateUtils.update(sIndex, siteId, goods, "goods_id");

        if (Objects.nonNull(gInfos) && gInfos.size() > 0)
        batchUpdateByCateId(index, siteId, gInfos, "goods_id",goodsEsMapper.queryGoodsImageBySiteId(siteId),goodsEsMapper.queryDistributeBySiteId(siteId));

//        bulkUpdateUtils.defineIndexTypeMapping(index,siteId);
//        bulkUpdateUtils.delByBrandId(index,siteId);
//        merge(gInfos,goodsEsMapper.queryGoodsImageBySiteId(siteId),goodsEsMapper.queryDistributeBySiteId(siteId));
//        bulkUpdateUtils.batchInsert(index, siteId, gInfos, "goods_id");
    }


    @Async
    public void updateSuggestByBrandIdAndCateId(String brandId, String cateId) {
        String shopid = brandId.split("_")[2];
//        suggestUpdateUtils.defineSuggestTypeMapping(sIndex, shopid);

//        suggestUpdateUtils.delByBrandId(sIndex, shopid);
//        List<GoodsInfo> goods =goodsEsMapper.querySuggestGoods(shopid);
        String[] cateIds = cateId.split("-");
        String [] cateCodes = goodsEsMapper.queryCateCodeByCateId(shopid,cateIds);
        List<GoodsInfo> goods =goodsEsMapper.querySuggestGoodsByCateId(shopid,cateCodes);
        if (Objects.nonNull(goods) && goods.size() > 0)
        bulkUpadteSuggestByCateId(goods, sIndex, shopid,"goods_id");
    }

    //根据cateId批量更新
    @SuppressWarnings("all")
    public void batchUpdateByCateId(String index, String siteId, List<GoodsInfo> gInfos, String idName, List<ImageInfo> imageInfoList, List<GoodsInfo> distributes) {
        logger.info("batchUpdateByCateId index: {},siteId: {}, goodsInfos: {}, distributes: {} ",index,siteId,gInfos,distributes);
        Map<String,Map<String,String>> maps=imageProcess(imageInfoList);
        for(GoodsInfo f:gInfos){
            Map<String,String> doc = maps.get(f.getGoods_id().toString());
            if(doc!=null){
                f.setDef_url(doc.get("def_url"));
                f.setGoods_url(doc.get("goods_url"));
            }
            if(distributes != null && distributes.size() > 0)
                for(GoodsInfo distribute:distributes){
                    if(f.getGoods_id().equals(distribute.getGoods_id()))
                        f.setIsDistribute("0");
                }
            if(StringUtils.isEmpty(f.getIsDistribute())){
                f.setIsDistribute("1");
            }
            if(f.getErp_price() != null && f.getErp_price() != 0){
                f.setShop_price(f.getErp_price());
            }
            f.buildKeyWd();

        }

        //使用流批量更新
        Long time = System.currentTimeMillis();
        gInfos.stream().forEach(g -> {
            ObjectMapper om =new ObjectMapper();
            String goodsid="";
            try {
                String value =om.writeValueAsString(g).replaceAll("NULL","");
                goodsid= BeanUtils.getProperty(g,idName);
                client.prepareDelete(index,siteId,goodsid).setRouting(siteId).get();//根据ID删除
                IndexResponse response=client.prepareIndex(index,siteId,goodsid).setRouting(siteId).setSource(value).get();
//                logger.info("商品更新完成：{} {}",response.toString(),value);

                /*if(index.equals(index)){
                    goodsEsMapper.insertLog(siteId, value, response.toString());
                }*/
            }catch (Exception e) {
                logger.error("batchUpdate error:{} -----------goodsid:{}",e.getMessage(),goodsid);
                try {
                    goodsEsMapper.insertLog(siteId, time+":"+ JacksonUtils.obj2json(g), "更新异常："+e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        goodsEsMapper.insertLog(siteId,time+":"+"批量更新"+gInfos.size()+"条数据", "更新完成");

    }


    private void bulkUpadteSuggestByCateId(List<GoodsInfo> goods, String index, String type, String idName){

        logger.info("批量更新提示开始，本次数据有{}条",goods.size());

        goods.forEach(goodsInfo -> {
            if(StringUtils.isNotBlank(goodsInfo.getDrug_name())) {
                try {
                    String goodsid = BeanUtils.getProperty(goodsInfo,idName);
                    client.prepareIndex(index,type, goodsid).setRouting(type).setSource(convertDrug_name.apply(goodsInfo));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("bulkUpadteSuggestByCateId Exception: {}",e.getMessage());
                }
            }
        });
        logger.info("批量更新结束，处理总条数 {}",goods.size());

    }
}
