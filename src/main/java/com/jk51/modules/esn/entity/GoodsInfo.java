package com.jk51.modules.esn.entity;

import com.jk51.modules.es.utils.PinYinUtil;
import com.jk51.modules.esn.util.JsonDateSerializer;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Date;
//75
public class GoodsInfo {

    private Long goods_id;

    private String adverse_reactioins;

    private Long browse_number;

    private int cost_price;

    private String forpeople_desc;

    private String goods_action;

    private String goods_batch_no;

    private String goods_code;

    private String goods_contd;

    private String goods_deposit;

    private String goods_desc;

    private String goods_description;

    private String goods_indications;

    private String goods_note;

    private String goods_usage;

    private String main_ingredient;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date product_date;

    private String qualification;

    private int wx_purchase_way;

    private String category_path;

    private String keywords;

    private String drug_name;

    private String drug_name_py;

    private String com_name;

    private String specif_cation;

    @JsonProperty("brand_id")
    private Long barnd_id;

    private Long drug_category;

    private Long goods_property;

    private String goods_use;

    private Long goods_forts;

    private String goods_forpeople;

    private String user_cateid;

    private String goods_title;

    private String goods_tagsid;

    private Long market_price;

    private Long shop_price;

    private Long discount_price;

    private Long in_stock;

    private Long goods_weight;

    private Long control_num;

    private Long goods_status;

    private Long freight_payer;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date list_time;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date delist_time;

    private Long postage_id;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date create_time;

    @JsonSerialize(using = JsonDateSerializer.class)
    private Date update_time;

    private Long detail_tpl;

    private int purchase_way;

    private int is_medicare; //是否医保

    private String medicare_code; //医保编码

    private Long medicare_top_price;

    private String bar_code;

    private String bar_code_status;

    private String mnemonic_code;

    private String goods_use_method;

    private String approval_number;

    private String goods_company;

    private Long goods_validity;

    private Long trans_mumber;

    private Long shopping_number;

    private Long goodsextd_id;

    @JsonProperty("brand_name")
    private String barnd_name;

    @JsonProperty("brand_desc")
    private String barnd_desc;

    private Long parent_id;

    private String cate_name;

    private Long cate_ishow;

    private Long cate_code;

    private String goods_url="";

    private String def_url="";

    private String goods_pinyin;

    private String goods_shouzimu;

    private String cateCode;

    private String isDistribute;

    private Integer goods_img=0;

    private Integer goods_num;  //销量

    private Long erp_price;

    private String keyword;
    private String keyword_py;

    private String goods_code_status;//有无商品编码

    private String user_cateid_status;//有无商品分类

    private Long app_goods_status; //单个商品状态

    private Integer gross_profit;//毛利

    private Integer is_main_push;//是否主推

    public Integer getGross_profit() {
        return gross_profit;
    }

    public void setGross_profit(Integer gross_profit) {
        this.gross_profit = gross_profit;
    }

    public Integer getIs_main_push() {
        return is_main_push;
    }

    public void setIs_main_push(Integer is_main_push) {
        this.is_main_push = is_main_push;
    }

    public Long getApp_goods_status() {
        return app_goods_status;
    }

    public void setApp_goods_status(Long app_goods_status) {
        this.app_goods_status = app_goods_status;
    }

    public String getGoods_code_status() {
        return goods_code_status;
    }

    public void setGoods_code_status(String goods_code_status) {
        this.goods_code_status = goods_code_status;
    }

    public String getUser_cateid_status() {
        return user_cateid_status;
    }

    public void setUser_cateid_status(String user_cateid_status) {
        this.user_cateid_status = user_cateid_status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword_py() {
        return keyword_py;
    }

    public void setKeyword_py(String keyword_py) {
        this.keyword_py = keyword_py;
    }

    public void setErp_price(Long erp_price) {
        this.erp_price = erp_price;
    }

    public Long getErp_price() {
        return erp_price;
    }

    public Integer getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Integer goods_num) {
        this.goods_num = goods_num;
    }

    public Integer getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(Integer goods_img) {
        this.goods_img = goods_img;
    }

    public String getGoods_pinyin() {
        return goods_pinyin;
    }

    public void setGoods_pinyin(String goods_pinyin) {
        this.goods_pinyin = goods_pinyin;
    }

    public String getGoods_shouzimu() {
        return goods_shouzimu;
    }

    public void setGoods_shouzimu(String goods_shouzimu) {
        this.goods_shouzimu = goods_shouzimu;
    }

    public String getAdverse_reactioins() {
        return adverse_reactioins;
    }

    public void setAdverse_reactioins(String adverse_reactioins) {
        this.adverse_reactioins = adverse_reactioins;
    }

    public Long getBrowse_number() {
        return browse_number;
    }

    public void setBrowse_number(Long browse_number) {
        this.browse_number = browse_number;
    }

    public String getBar_code_status() {
        return bar_code_status;
    }

    public void setBar_code_status(String bar_code_status) {
        this.bar_code_status = bar_code_status;
    }

    public int getCost_price() {
        return cost_price;
    }

    public void setCost_price(int cost_price) {
        this.cost_price = cost_price;
    }

    public String getForpeople_desc() {
        return forpeople_desc;
    }

    public void setForpeople_desc(String forpeople_desc) {
        this.forpeople_desc = forpeople_desc;
    }

    public String getGoods_action() {
        return goods_action;
    }

    public void setGoods_action(String goods_action) {
        this.goods_action = goods_action;
    }

    public String getGoods_batch_no() {
        return goods_batch_no;
    }

    public void setGoods_batch_no(String goods_batch_no) {
        this.goods_batch_no = goods_batch_no;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
        if(null == goods_code || "".equals(goods_code)){
            this.goods_code_status = "2";//2：无商编
        }else{
            this.goods_code_status = "1";//1：有商编
        }
    }

    public String getGoods_contd() {
        return goods_contd;
    }

    public void setGoods_contd(String goods_contd) {
        this.goods_contd = goods_contd;
    }

    public String getGoods_deposit() {
        return goods_deposit;
    }

    public void setGoods_deposit(String goods_deposit) {
        this.goods_deposit = goods_deposit;
    }

    public String getGoods_desc() {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc) {
        this.goods_desc = goods_desc;
    }

    public String getGoods_description() {
        return goods_description;
    }

    public void setGoods_description(String goods_description) {
        this.goods_description = goods_description;
    }

    public String getGoods_indications() {
        return goods_indications;
    }

    public void setGoods_indications(String goods_indications) {
        this.goods_indications = goods_indications;
    }

    public String getGoods_note() {
        return goods_note;
    }

    public void setGoods_note(String goods_note) {
        this.goods_note = goods_note;
    }

    public String getGoods_usage() {
        return goods_usage;
    }

    public void setGoods_usage(String goods_usage) {
        this.goods_usage = goods_usage;
    }

    public String getMain_ingredient() {
        return main_ingredient;
    }

    public void setMain_ingredient(String main_ingredient) {
        this.main_ingredient = main_ingredient;
    }

    public Date getProduct_date() {
        return product_date;
    }

    public void setProduct_date(Date product_date) {
        this.product_date = product_date;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getWx_purchase_way() {
        return wx_purchase_way;
    }

    public void setWx_purchase_way(int wx_purchase_way) {
        this.wx_purchase_way = wx_purchase_way;
    }

    public String getCategory_path() {
        return category_path;
    }

    public void setCategory_path(String category_path) {
        this.category_path = category_path;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Long getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Long goods_id) {
        this.goods_id = goods_id;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getDrug_name_py() {
        return drug_name_py;
    }

    public void setDrug_name_py(String drug_name_py) {
        this.drug_name_py = drug_name_py;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
    }

    public String getSpecif_cation() {
        return specif_cation;
    }

    public void setSpecif_cation(String specif_cation) {
        this.specif_cation = specif_cation;
    }

    public Long getBarnd_id() {
        return barnd_id;
    }

    public void setBarnd_id(Long barnd_id) {
        this.barnd_id = barnd_id;
    }

    public Long getDrug_category() {
        return drug_category;
    }

    public void setDrug_category(Long drug_category) {
        this.drug_category = drug_category;
    }

    public Long getGoods_property() {
        return goods_property;
    }

    public void setGoods_property(Long goods_property) {
        this.goods_property = goods_property;
    }

    public String getGoods_use() {
        return goods_use;
    }

    public void setGoods_use(String goods_use) {
        this.goods_use = goods_use;
    }

    public Long getGoods_forts() {
        return goods_forts;
    }

    public void setGoods_forts(Long goods_forts) {
        this.goods_forts = goods_forts;
    }

    public String getGoods_forpeople() {
        return goods_forpeople;
    }

    public void setGoods_forpeople(String goods_forpeople) {
        this.goods_forpeople = goods_forpeople;
    }

    public String getUser_cateid() {
        return user_cateid;
    }

    public void setUser_cateid(String user_cateid) {
        this.user_cateid = user_cateid;
        this.cateCode = user_cateid;
        if(null == user_cateid || "".equals(user_cateid)){
            this.user_cateid_status = "2";//2：无分类
        }else{
            this.user_cateid_status = "1";//1：有分类
        }
    }

    public String getGoods_title() {
        return goods_title;
    }

    public void setGoods_title(String goods_title) {
        this.goods_title = goods_title;
    }

    public String getGoods_tagsid() {
        return goods_tagsid;
    }

    public void setGoods_tagsid(String goods_tagsid) {
        this.goods_tagsid = goods_tagsid;
    }

    public Long getMarket_price() {
        return market_price;
    }

    public void setMarket_price(Long market_price) {
        this.market_price = market_price;
    }

    public Long getShop_price() {
        return shop_price;
    }

    public void setShop_price(Long shop_price) {
        this.shop_price = shop_price;
    }

    public Long getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Long discount_price) {
        this.discount_price = discount_price;
    }

    public Long getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(Long in_stock) {
        this.in_stock = in_stock;
    }

    public Long getGoods_weight() {
        return goods_weight;
    }

    public void setGoods_weight(Long goods_weight) {
        this.goods_weight = goods_weight;
    }

    public Long getControl_num() {
        return control_num;
    }

    public void setControl_num(Long control_num) {
        this.control_num = control_num;
    }

    public Long getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(Long goods_status) {
        this.goods_status = goods_status;
    }

    public Long getFreight_payer() {
        return freight_payer;
    }

    public void setFreight_payer(Long freight_payer) {
        this.freight_payer = freight_payer;
    }

    public Date getList_time() {
        return list_time;
    }

    public void setList_time(Date list_time) {
        this.list_time = list_time;
    }

    public Date getDelist_time() {
        return delist_time;
    }

    public void setDelist_time(Date delist_time) {
        this.delist_time = delist_time;
    }

    public Long getPostage_id() {
        return postage_id;
    }

    public void setPostage_id(Long postage_id) {
        this.postage_id = postage_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Long getDetail_tpl() {
        return detail_tpl;
    }

    public void setDetail_tpl(Long detail_tpl) {
        this.detail_tpl = detail_tpl;
    }

    public int getPurchase_way() {
        return purchase_way;
    }

    public void setPurchase_way(int purchase_way) {
        this.purchase_way = purchase_way;
    }

    public int getIs_medicare() {
        return is_medicare;
    }

    public void setIs_medicare(int is_medicare) {
        this.is_medicare = is_medicare;
    }

    public String getMedicare_code() {
        return medicare_code;
    }

    public void setMedicare_code(String medicare_code) {
        this.medicare_code = medicare_code;
    }

    public Long getMedicare_top_price() {
        return medicare_top_price;
    }

    public void setMedicare_top_price(Long medicare_top_price) {
        this.medicare_top_price = medicare_top_price;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
        if(null == bar_code || "".equals(bar_code)){
            this.bar_code_status = "2";//2：无条形码
        }else{
            this.bar_code_status = "1";//1：有条形码
        }
    }

    public String getMnemonic_code() {
        return mnemonic_code;
    }

    public void setMnemonic_code(String mnemonic_code) {
        this.mnemonic_code = mnemonic_code;
    }

    public String getGoods_use_method() {
        return goods_use_method;
    }

    public void setGoods_use_method(String goods_use_method) {
        this.goods_use_method = goods_use_method;
    }

    public String getApproval_number() {
        return approval_number;
    }

    public void setApproval_number(String approval_number) {
        this.approval_number = approval_number;
    }

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public String getDef_url() {
        return def_url;
    }

    public void setDef_url(String def_url) {
        this.def_url = def_url;
        if(StringUtils.isBlank(this.getDef_url())&&StringUtils.isBlank(this.getGoods_url()))
            this.setGoods_img(0);
        else
            this.setGoods_img(1);
    }

    public String getGoods_url() {

        return goods_url;
    }

    public void setGoods_url(String goods_url) {
        this.goods_url = goods_url;
        if(StringUtils.isBlank(this.getDef_url())&&StringUtils.isBlank(this.getGoods_url()))
            this.setGoods_img(0);
        else
            this.setGoods_img(1);
    }

    public Long getGoods_validity() {
        return goods_validity;
    }

    public void setGoods_validity(Long goods_validity) {
        this.goods_validity = goods_validity;
    }

    public Long getTrans_mumber() {
        return trans_mumber;
    }

    public void setTrans_mumber(Long trans_mumber) {
        this.trans_mumber = trans_mumber;
    }

    public Long getShopping_number() {
        return shopping_number;
    }

    public void setShopping_number(Long shopping_number) {
        this.shopping_number = shopping_number;
    }

    public Long getGoodsextd_id() {
        return goodsextd_id;
    }

    public void setGoodsextd_id(Long goodsextd_id) {
        this.goodsextd_id = goodsextd_id;
    }

    public String getBarnd_name() {
        return barnd_name;
    }

    public void setBarnd_name(String barnd_name) {
        this.barnd_name = barnd_name;
    }

    public String getBarnd_desc() {
        return barnd_desc;
    }

    public void setBarnd_desc(String barnd_desc) {
        this.barnd_desc = barnd_desc;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public Long getCate_ishow() {
        return cate_ishow;
    }

    public void setCate_ishow(Long cate_ishow) {
        this.cate_ishow = cate_ishow;
    }

    public Long getCate_code() {
        return cate_code;
    }

    public void setCate_code(Long cate_code) {
        this.cate_code = cate_code;
    }

    public String getCateCode() {
        return cateCode;
    }

    public void setCateCode(String cateCode) {
        this.cateCode = cateCode;
    }

    public String getIsDistribute() {
        return isDistribute;
    }

    public void setIsDistribute(String isDistribute) {
        this.isDistribute=isDistribute;
    }

    public void setImages(GoodsInfo imageInfo) {
        if (imageInfo != null) {
            this.setGoods_url(imageInfo.getGoods_url());
            this.setDef_url(imageInfo.getDef_url());
        }
    }

    public GoodsInfo() {
    }

	/*public void setImageInfo(ResultSet rs) {
		DefUrl defUrl = new DefUrl();

		try {
			rs.last();// 指针移到结果集的最后一行
			int r = rs.getRow();// 获取最后一行的行数
			if (r != 0) {
				rs.beforeFirst();// 因为需要循环，所以把指针重新移到第一行
				GoodsUrl[] goodsUrls = new GoodsUrl[r - 1];
				int i = 0;
				while (rs.next()) {
					if ("1".equals(rs.getString(3)))// 默认图片
					{
						defUrl.setHostId(rs.getString(1));
						defUrl.setImageId(rs.getString(2));
					} else// 其它图片
					{
						GoodsUrl goodsUrl = new GoodsUrl();
						goodsUrl.setHostId(rs.getString(1));
						goodsUrl.setImageId(rs.getString(2));
						goodsUrls[i] = goodsUrl;
						i++;
					}
				}
				ObjectMapper om = new ObjectMapper();

				this.def_url = om.writeValueAsString(defUrl);
				this.goods_url = om.writeValueAsString(goodsUrls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*public synchronized void  setImageInfo(List<ImageInfo> imgInfos) throws Exception {

		if (imgInfos == null || imgInfos.size() <= 0) {
			return;
		}
		DefUrl defUrl = new DefUrl();
		//GoodsUrl[] goodsUrls = new GoodsUrl[imgInfos.size() - 1];
		List<GoodsUrl> goodsUrls = new ArrayList<GoodsUrl>();
		for (ImageInfo imgInfo : imgInfos) {
			if (imgInfo.getIs_default() == 1) {
				defUrl.setHostId(imgInfo.getHost_id());
				defUrl.setImageId(imgInfo.getHash());
			} else if(imgInfo.getIs_default() == 0){
				GoodsUrl goodsUrl = new GoodsUrl();
				goodsUrl.setHostId(imgInfo.getHost_id());
				goodsUrl.setImageId(imgInfo.getHash());
				goodsUrls.add(goodsUrl);
			}
		}
		ObjectMapper om = new ObjectMapper();

		this.def_url = om.writeValueAsString(defUrl);
		this.goods_url = om.writeValueAsString(goodsUrls);

	}*/

    /**
     * 组装分类字段,组装规则如下 1000100010001 ==> 10001 100010001 1000100010001
     */
    public void setCategoryPath(GoodsInfo gInfo) {

        StringBuffer category_path = new StringBuffer();
        Long cate_code = gInfo.getCate_code();

        if (cate_code != null) {
            String cate_codeStr = String.valueOf(cate_code);
            if (cate_codeStr.length() > 4 && cate_codeStr.length() <= 8) {// 商品属于二级分类
                category_path.append(cate_codeStr.substring(0, 4)).append(" ")
                    .append(cate_codeStr.substring(0, cate_codeStr.length()));
            } else if (cate_codeStr.length() > 8) {// 属于三级分类
                category_path.append(cate_codeStr.substring(0, 4)).append(" ").append(cate_codeStr.substring(0, 8))
                    .append(" ").append(cate_codeStr.substring(0, cate_codeStr.length()));
            } else {// 属于一级分类
                category_path.append(cate_codeStr);
            }
            this.setCategory_path(category_path.toString());
        } else {
            this.setCategory_path("");
        }

    }

    /**
     * 组装keywords,关键词
     */
    public void buildKeyWd() {

        // 网站前期业务新建，目前只需组装keywords字段，用于默认搜索，
        // 目前存储drug_name、com_name，goods_title，barnd_name，barnd_desc，goods_tagsid,默认搜索字段，以后根据业务需求调整
        StringBuffer keywords = new StringBuffer();
        if (null != this.getDrug_name()) {
            keywords.append(this.getDrug_name()).append(" ");
        }
        if (null != this.getCom_name()) {
            keywords.append(this.getCom_name()).append(" ");
        }
        if (null != this.getDrug_name()) {
            keywords.append(this.getDrug_name()).append(" ");
            String as[] = PinYinUtil.getPinYin(this.getDrug_name(), " ").split(" ");
            StringBuffer stringbuffer2 = new StringBuffer();
            for(int k1 = 0; k1 < as.length; k1++){
                if(StringUtils.isNotBlank(as[k1])){
                    stringbuffer2.append(as[k1].charAt(0));
                }
            }
//            this.setGoods_pinyin(PinYinUtil.getPinYin(this.getDrug_name(), ""));
            this.setGoods_pinyin(PinYinUtil.getPinYin(stringbuffer2.toString(), ""));
            this.setGoods_shouzimu(stringbuffer2.toString());
        }
        if (null != this.getBarnd_name()) {
            keywords.append(this.getBarnd_name()).append(" ");
        }
        if (null != this.getBarnd_desc()) {
            keywords.append(this.getBarnd_desc()).append(" ");
        }
        if(null != keywords){
            this.setKeywords(keywords.toString());
        }

    }

    public XContentBuilder getXContentBuilder(){
        try {
            XContentBuilder xb= XContentFactory.jsonBuilder();
            ObjectMapper mapper = new ObjectMapper();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer goodsStr = new StringBuffer();
        String suffix = " | ";
        return goodsStr.append("GoodsInfo[").append(this.goods_id).append(suffix).append(this.drug_name).append(suffix)
            .append(this.com_name).append(suffix).append(this.specif_cation).append(suffix).append(this.barnd_id)
            .append(suffix).append(this.drug_category).append(suffix).append(this.goods_property).append(suffix)
            .append(this.goods_use).append(suffix).append(this.goods_forts).append(suffix)
            .append(this.goods_forpeople).append(suffix).append(this.user_cateid).append(suffix)
            .append(this.goods_title).append(suffix).append(this.goods_tagsid).append(suffix)
            .append(this.market_price).append(suffix).append(this.shop_price).append(suffix)
            .append(this.discount_price).append(suffix).append(this.in_stock).append(suffix)
            .append(this.goods_weight).append(suffix).append(this.control_num).append(suffix).append(this.app_goods_status).append(suffix)
            .append(this.goods_status).append(suffix).append(this.freight_payer).append(suffix)
            .append(this.list_time).append(suffix).append(this.delist_time).append(suffix).append(this.postage_id)
            .append(suffix).append(this.create_time).append(suffix).append(this.update_time).append(suffix)
            .append(this.detail_tpl).append(suffix).append(this.purchase_way).append(suffix)
            .append(this.is_medicare).append(suffix).append(this.medicare_code).append(suffix)
            .append(this.medicare_top_price).append(suffix).append(this.bar_code).append(suffix)
            .append(this.mnemonic_code).append(suffix).append(this.goods_use_method).append(suffix)
            .append(this.approval_number).append(suffix).append(this.goods_company).append(suffix)
            .append(this.goods_validity).append(suffix).append(this.trans_mumber).append(suffix)
            .append(this.shopping_number).append(suffix).append(this.goodsextd_id).append(suffix)
            .append(this.barnd_name).append(suffix).append(this.barnd_desc).append(suffix).append(this.parent_id)
            .append(suffix).append(this.cate_name).append(suffix).append(this.cate_ishow).append(suffix)
            .append(this.cate_code).append(this.goods_num).append("]").toString();
    }

}
