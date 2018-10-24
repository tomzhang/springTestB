package com.jk51.modules.es.utils;

import com.jk51.commons.string.StringUtil;
import com.jk51.modules.es.entity.GoodsInfosAdminReq;
import com.jk51.modules.es.service.AppEsService;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppEsQuery
 * @Description APP中ES搜索
 * @Date 2018-06-11 14:21
 */
@Component
public class AppEsQuery extends QueryUtils {

    @Autowired
    TransportClient transportClient;


    @Override
    public TransportClient openClient() {
        return transportClient;
    }

    @Override
    public BoolQueryBuilder buildBool(Object obj) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        //判断对象类型
//        if(obj instanceof  GoodsInfosAdminReq) {
        GoodsInfosAdminReq gInfosReq = (GoodsInfosAdminReq)obj;
        StringBuffer sb = new StringBuffer();
        String goods_name = gInfosReq.getGoods_name();
        String goodsName = "";
        if(StringUtil.isNotEmpty(goods_name)) {
            for (int i = 0; i < goods_name.length(); i++) {
                char c = goods_name.charAt(i);
                if (c != ' ' && c != ',' && c != '。' && c != '、' && c != '，') {
                    sb.append(c);
                }
            }
            goodsName = sb.toString();
        }



        if(!JKStringUtils.isBlank(goodsName)){
            qb.should(QueryBuilders.matchPhraseQuery("drug_name", goodsName));
            qb.should(QueryBuilders.matchQuery("com_name", goodsName));
            qb.should(QueryBuilders.matchQuery("goods_tagsid", goodsName));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getGoods_title())){
            qb.must(QueryBuilders.matchPhraseQuery("goods_title", gInfosReq.getGoods_title()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getApproval_number())){
            qb.must(QueryBuilders.matchPhraseQuery("approval_number", gInfosReq.getApproval_number()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getGoods_code())){
            qb.must(QueryBuilders.matchPhraseQuery("goods_code", gInfosReq.getGoods_code()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getUser_cateid())){
            qb.must(QueryBuilders.prefixQuery("cateCode", gInfosReq.getUser_cateid()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getIsDistribute())){
            qb.must(QueryBuilders.termQuery("isDistribute", gInfosReq.getIsDistribute()));
        }
        if(null != gInfosReq.getCate_code()){
            qb.must(QueryBuilders.termQuery("cate_code", gInfosReq.getCate_code()));
        }
        //1,有图片;2,无图片
        if(gInfosReq.getGoods_img() == 1){
            qb.must(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("goods_img",1)));
        }else if(gInfosReq.getGoods_img() == 2){
            qb.must(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("goods_img",0)));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getGoods_usage())){
            qb.must(QueryBuilders.termQuery("goods_usage", gInfosReq.getGoods_usage()));
        }
        if(gInfosReq.getGoods_forts() != 0){
            qb.must(QueryBuilders.termQuery("goods_forts", gInfosReq.getGoods_forts()));
        }
        if(gInfosReq.getGoods_forpeople() != 0){
            qb.must(QueryBuilders.termQuery("goods_forpeople", gInfosReq.getGoods_forpeople()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getGoods_tagsid())){
            qb.must(QueryBuilders.termQuery("goods_tagsid", gInfosReq.getGoods_tagsid()));
        }
        if(gInfosReq.getGoods_weight() != 0){
            qb.must(QueryBuilders.termQuery("goods_weight", gInfosReq.getGoods_weight()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getBrand_id())){
            qb.must(QueryBuilders.termQuery("brand_id", gInfosReq.getBrand_id()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getBrand_name())){
            qb.must(QueryBuilders.matchPhraseQuery("brand_name", gInfosReq.getBrand_name()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getMinPrice()) && !JKStringUtils.isBlank(gInfosReq.getMaxPrice())){
            qb.must(QueryBuilders.rangeQuery("shop_price").from(gInfosReq.getMinPrice()).to(gInfosReq.getMaxPrice()).includeLower(true).includeUpper(true));
        }
        //传值1、2、4， -1相当于1和2，其它数字时不对该字段做查询条件(查询全部)
        //上架
        /*if(gInfosReq.getGoods_status() == 1){
            qb.must(QueryBuilders.termQuery("goods_status", "1"));
        }else if(gInfosReq.getGoods_status() == 2){//下架
            qb.must(QueryBuilders.termQuery("goods_status", "2"));
        }else if(gInfosReq.getGoods_status() == 4){//软删除(回收站)
            qb.must(QueryBuilders.termQuery("goods_status", "4"));
        }else if(gInfosReq.getGoods_status() == -1){
            qb.must(QueryBuilders.termsQuery("goods_status", "1","2"));
        }*/
        if(gInfosReq.getDetail_tpl() != 0){
            qb.must(QueryBuilders.termQuery("detail_tpl", gInfosReq.getDetail_tpl()));
        }
        if(gInfosReq.getWx_purchase_way() != 0){
            qb.must(QueryBuilders.termQuery("wx_purchase_way", gInfosReq.getWx_purchase_way()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getBar_code())){
            qb.must(QueryBuilders.matchPhraseQuery("bar_code", gInfosReq.getBar_code()));
        }
        //1,有条形码;0,无条形码;-1或者其它数字不处理
        if(gInfosReq.getBar_code_status() == 1){
            qb.must(QueryBuilders.termQuery("bar_code_status", "1"));
        }else if(gInfosReq.getBar_code_status() == 0){//无条形码
            qb.must(QueryBuilders.termQuery("bar_code_status", "2"));
        }
        /*if(gInfosReq.getDrug_category() != 0&&gInfosReq.getDrug_category() != -1){
            qb.must(QueryBuilders.termQuery("drug_category", gInfosReq.getDrug_category()));
        }else if(gInfosReq.getDrug_category() == -1){
            qb.must(QueryBuilders.termsQuery("drug_category", "110","120","150","160","170","180","190","0"));
        }*/
        if(gInfosReq.getGoods_property() != 0){
            qb.must(QueryBuilders.termQuery("goods_property", gInfosReq.getGoods_property()));
        }
        if(gInfosReq.getPurchase_way() != 0){
            qb.must(QueryBuilders.termQuery("purchase_way", gInfosReq.getPurchase_way()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getDrug_name_and_approval_number())){
            qb.must(QueryBuilders.termQuery("drug_name", gInfosReq.getDrug_name_and_approval_number()));
            qb.should(QueryBuilders.termQuery("approval_number", gInfosReq.getDrug_name_and_approval_number()));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getGoods_id())){
            qb.must(QueryBuilders.termsQuery("goods_id", gInfosReq.getGoods_id().split(",")));
        }
        if(!JKStringUtils.isBlank(gInfosReq.getDrug_name())){
            qb.must(QueryBuilders.matchPhraseQuery("drug_name", gInfosReq.getDrug_name())); //短句查询  不分词
//            qb.must(QueryBuilders.termsQuery("drug_name", gInfosReq.getDrug_name()));
//            qb.must(QueryBuilders.matchQuery("drug_name", gInfosReq.getDrug_name()));//分词查询
        }
        return qb;


//        }else {//拼音,小数
//            String key=(String) obj;
//            判断是拼音还是小数
//            if (AppEsService.isAllNum(key)) {
//                qb.should(QueryBuilders.queryStringQuery("shop_price:"+key));
//                qb.should(QueryBuilders.matchQuery("shop_price",key));
//            }else {
//                qb.should(QueryBuilders.queryStringQuery("keyword_py:"+PinYinUtil.getPinYin(key,"")));
//            }
//            return qb;
//        }



    }

    @Override
    public MatchQueryBuilder buildMatchQuery(Object obj) {
        return null;
    }

    public static String[] getGoodsListFields(){
        return new String[]{"goods_id","def_url","drug_name","com_name","brand_name","specif_cation","goods_code","goods_title","goods_company","cate_code","bar_code","approval_number","shop_price","market_price","in_stock","goods_status","purchase_way","wx_purchase_way","user_cateid","update_time","goods_num","drug_category","goods_indications","goods_img","forpeople_desc","main_ingredient","keyword", "app_goods_status", "is_medicare", "gross_profit", "is_main_push"};
    }


}
