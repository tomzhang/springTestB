package com.jk51.modules.es.entity;

import com.jk51.modules.es.utils.AdminQuery;
import com.jk51.modules.es.utils.AppEsQuery;
import com.jk51.modules.es.utils.KeywordsQuery;
import org.apache.commons.lang.StringUtils;

public class QueryDto {

    private String types;
    private int from;
    private int size;
    private String minPrice;
    private String maxPrice;
    private String brandid;
    private String[] fields;
    private Object obj;
    private String sortField;
    private String sortType;
    private String index;
    private String status;
    private String goods_ids;
    private String goods_condition;

    private boolean isPriceFilter=false;
    private boolean isBrandFilter=false;
    private boolean isSort=false;
    private boolean isStatus=false;

    public QueryDto() {
    }

    public QueryDto(int size, String index, int from, String[] fields, String types, Object obj) {
        this.size = size;
        this.from=from;
        this.index = index;
        this.fields = fields;
        this.types=types;
        this.obj = obj;
    }

    public String getGoods_condition() {
        return goods_condition;
    }

    public void setGoods_condition(String goods_condition) {
        this.goods_condition = goods_condition;
    }
    public boolean isStatusFilter(){
        return isStatus;
    }

    public boolean isPriceFilter(){
        return isPriceFilter;
    }

    public boolean isBrandFilter(){
        return isBrandFilter;
    }

    public boolean isSort(){
        return isSort;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
        if(StringUtils.isNotBlank(minPrice)&&StringUtils.isNotBlank(maxPrice)){
            isPriceFilter=true;
        }
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
        if(StringUtils.isNotBlank(minPrice)&&StringUtils.isNotBlank(maxPrice)){
            isPriceFilter=true;
        }
    }

    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
        if(StringUtils.isNotBlank(brandid)){
            isBrandFilter=true;
        }
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if(StringUtils.isNotBlank(status) && !"-1".equals(status)){
            isStatus=true;
        }
    }
    public String getGoods_ids() {
		return goods_ids;
	}

	public void setGoods_ids(String goods_ids) {
		this.goods_ids = goods_ids;
	}

	public void setSort(String order){
        if(StringUtils.isNotBlank(order)) {
            String sortStr = order.split(",")[0];
            String[] fields=sortStr.split(" ");
            setSortField(fields[0]);
            setSortType(fields[1]);
            isSort=true;
        }
    }

    public static QueryDto buildFristDto(GoodsInfosAdminReq gInfosReq, String index){
        QueryDto queryDto=new QueryDto();
        queryDto.setFrom(0);
        queryDto.setSize(1);
        queryDto.setTypes(gInfosReq.getDbname().split("_")[2]);
        queryDto.setObj(new String[]{gInfosReq.getKeyword()});
        queryDto.setFields(new String[]{"cateCode"});
        queryDto.setIndex(index);
        return queryDto;
    }

    public static QueryDto buildDtoByUser(GoodsInfosAdminReq gInfosReq, String cate_code, String index){
        int from = (gInfosReq.getCurrentPage() -1) * gInfosReq.getPageNum();
        int size = gInfosReq.getPageNum() == 0 ? 10 : gInfosReq.getPageNum();
        QueryDto queryDto=new QueryDto();
        queryDto.setFrom(from);
        queryDto.setSize(size);
        queryDto.setBrandid(gInfosReq.getBrand_id());
        queryDto.setMaxPrice(gInfosReq.getMaxPrice());
        queryDto.setMinPrice(gInfosReq.getMinPrice());
        queryDto.setTypes(gInfosReq.getDbname().split("_")[2]);
        queryDto.setObj(new String[]{gInfosReq.getKeyword(),cate_code});
        queryDto.setFields(KeywordsQuery.getGoodsListFields());
        queryDto.setSort(gInfosReq.getOrder());
        queryDto.setIndex(index);
        queryDto.setStatus(Constants.ES_GOODS_SALE_STATUS_YES);
        return queryDto;
    }
    
    public static QueryDto buildDtoByAdmin(GoodsInfosAdminReq gInfosReq, String index){
        int from = (gInfosReq.getCurrentPage() -1) * gInfosReq.getPageNum();
        int size = gInfosReq.getPageNum() == 0 ? 10 : gInfosReq.getPageNum();   //成员变量int初始值为0
        QueryDto queryDto=new QueryDto();
        queryDto.setFrom(from);
        queryDto.setSize(size);
        queryDto.setBrandid(gInfosReq.getBrand_id());
        queryDto.setMaxPrice(gInfosReq.getMaxPrice());
        queryDto.setMinPrice(gInfosReq.getMinPrice());
        queryDto.setTypes(gInfosReq.getDbname().split("_")[2]);
        queryDto.setFields(AdminQuery.getGoodsListFields());
        queryDto.setObj(gInfosReq);
        queryDto.setSort(gInfosReq.getOrder());
        queryDto.setIndex(index);
        queryDto.setGoods_condition(gInfosReq.getGoods_condition());
        queryDto.setGoods_ids(gInfosReq.getGoods_id());
        return queryDto;
    }

    public static QueryDto buildDtoByGoodsId(GoodsInfoReq req, String index){
    	QueryDto queryDto=new QueryDto();
    	queryDto.setIndex(index);
    	queryDto.setTypes(req.getDbname().split("_")[2]);
    	queryDto.setObj(req);
    	queryDto.setFrom(0);
    	queryDto.setSize(1);
    	return queryDto;
    }

    //汉字和整数
    public static QueryDto appBuildDto(GoodsInfosAdminReq gInfosReq, String index){
        int from = (gInfosReq.getCurrentPage() -1) * gInfosReq.getPageNum();
        int size = gInfosReq.getPageNum() == 0 ? 10 : gInfosReq.getPageNum();   //成员变量int初始值为0
        QueryDto queryDto=new QueryDto();
        queryDto.setFrom(from);
        queryDto.setSize(size);
        queryDto.setBrandid(gInfosReq.getBrand_id());
        queryDto.setMaxPrice(gInfosReq.getMaxPrice());
        queryDto.setMinPrice(gInfosReq.getMinPrice());
        queryDto.setTypes(gInfosReq.getDbname().split("_")[2]);
        queryDto.setFields(AppEsQuery.getGoodsListFields());
        queryDto.setObj(gInfosReq);
        queryDto.setSort(gInfosReq.getOrder());
        queryDto.setIndex(index);
        queryDto.setGoods_condition(gInfosReq.getGoods_condition());
        queryDto.setGoods_ids(gInfosReq.getGoods_id());
//        queryDto.setStatus(String.valueOf(gInfosReq.getGoods_status()));//设置商品状态
        queryDto.setStatus(String.valueOf(gInfosReq.getApp_goods_status()));//设置商品状态
        return queryDto;
    }


    //拼音
    public static QueryDto appBuildDtoPy(GoodsInfosAdminReq gInfosReq, String index){
        int from = (gInfosReq.getCurrentPage() -1) * gInfosReq.getPageNum();
        int size = gInfosReq.getPageNum() == 0 ? 10 : gInfosReq.getPageNum();   //成员变量int初始值为0
        QueryDto queryDto=new QueryDto();
        queryDto.setFrom(from);
        queryDto.setSize(size);
        queryDto.setBrandid(gInfosReq.getBrand_id());
        queryDto.setMaxPrice(gInfosReq.getMaxPrice());
        queryDto.setMinPrice(gInfosReq.getMinPrice());
        queryDto.setTypes(gInfosReq.getDbname().split("_")[2]);
        queryDto.setFields(AppEsQuery.getGoodsListFields());
        queryDto.setObj(gInfosReq);
        queryDto.setSort(gInfosReq.getOrder());
        queryDto.setIndex(index);
        queryDto.setGoods_condition(gInfosReq.getGoods_condition());
        queryDto.setGoods_ids(gInfosReq.getGoods_id());
        return queryDto;
    }
}
