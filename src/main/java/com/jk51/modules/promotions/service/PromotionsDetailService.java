package com.jk51.modules.promotions.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.detail.PromotionsDetailSqlParam;
import com.jk51.model.promotions.rule.ProCouponRuleView;
import com.jk51.modules.coupon.service.CouponNoEncodingService;
import com.jk51.modules.promotions.mapper.PromotionsDetailMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动使用详情相关的功能
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@Service
public class PromotionsDetailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsDetailMapper mapper;
    @Autowired
    private PromotionsRuleMapper ruleMapper;
    @Autowired
    private PromotionsRuleService ruleService;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    /**
     * 1表示创建成功，其他表示失败
     *
     * @param promotionsDetail
     * @return
     */
    public ReturnDto insert(PromotionsDetail promotionsDetail) {
        ReturnDto checkResult = isInvalidForInsert(promotionsDetail);
        if (checkResult != null) {
            return checkResult;
        }

        try {
            int i = mapper.insert(promotionsDetail);
            if (i != 1) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            logger.error("数据库放入活动详情失败", e);
            return ReturnDto.buildFailedReturnDto("数据库放入活动详情失败");
        }
        return ReturnDto.buildSuccessReturnDto();
    }

    private ReturnDto isInvalidForInsert(PromotionsDetail promotionsDetail) {
        if (promotionsDetail.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        return null;
    }

    public Object findCouponListTable(Map<String, Object> params) throws Exception{
        //获取前台传递的各种参数
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());
        int startNum = params.get("page") == null ? 1 : Integer.parseInt(params.get("page").toString());
        int pageSize = params.get("pageSize") == null ? 2000 : Integer.parseInt(params.get("pageSize").toString());
        Object exportFlag = params.get("export");
        Map<String, Object> itemPre = new HashMap<String, Object>();
        PromotionsRule promotionsRule = ruleMapper.getPromotionsRuleByIdAndSiteId(siteId, ruleId);
        Integer promotionsType = promotionsRule.getPromotionsType();
        String time = promotionsRule.getTimeRule();
        ProCouponRuleView proCouponRuleView = ruleService.promotionsRuleForType(promotionsRule.getPromotionsType(), promotionsRule.getPromotionsRule());
        switch (promotionsType) {
            case 10:
                itemPre.put("coupon_type", "满赠活动");
                break;
            case 20:
                itemPre.put("coupon_type", "打折活动");
                break;
            case 30:
                itemPre.put("coupon_type", "包邮活动");
                break;
            case 40:
                itemPre.put("coupon_type", "满减活动");
                break;
            case 50:
                itemPre.put("coupon_type", "限价活动");
                break;
            case 60:
                itemPre.put("coupon_type","拼团活动");
                break;
        }
        itemPre.put("amount_money",proCouponRuleView==null?"---":proCouponRuleView.getProruleDetail());
        itemPre.put("rule_name",promotionsRule.getPromotionsName());
        JSONObject timeRule = new JSONObject(time);
        setRuleTime(timeRule,itemPre);
        itemPre.put("create_time", DateUtils.formatDate(LocalDateTime2Date(promotionsRule.getCreateTime()),"yyyy-MM-dd HH:mm:ss"));
        PageHelper.startPage(startNum,pageSize);
        Object no = params.get("no");
        if(no!=null){
            String decryptionCouponNo = couponNoEncodingService.decryptionCouponNo(no.toString());
            params.put("no",decryptionCouponNo);
        }
        List<Map<String, Object>> list = mapper.queryPromotionsDetailListByRuleIdAndSiteId(siteId, ruleId, params);
        PageInfo<Map<String, Object>> info =new PageInfo<Map<String, Object>>(list);
        List<Map<String, Object>> result = info.getList();
        list.stream().forEach(map->{map.putAll(itemPre);
            String num = map.get("coupon_no")==null?"":map.get("coupon_no").toString();
            map.put("coupon_no",couponNoEncodingService.encryptionCouponNo(num));
        });
        if(exportFlag!=null){
            return result;
        }
        Map<String,Object> pages=new HashMap<String,Object>();
        pages.put("rows",result);
        pages.put("pages",info.getPages());
        pages.put("pageNum",info.getPageNum());
        pages.put("total",info.getTotal());
        return pages;

    }

    public void setRuleTime(JSONObject timeRule,Map<String, Object> itemPre)throws  Exception{
        switch (timeRule.getInt("validity_type")) {
            case 1:{
                itemPre.put("valid_period","固定时间："+timeRule.get("startTime")+"-"+timeRule.get("endTime"));break;
            }
            case 2:{
                String month="每月固定日期：";
                String[] assign_rules = timeRule.get("assign_rule").toString().split(",");
                String days="";
                int odd = 0;
                int even = 0;
                boolean flag = true;
                if (assign_rules.length >= 15) {
                    for (int i = 0; i < assign_rules.length; i++) {
                        if (assign_rules[i]!=null) {
                            Integer assgin=Integer.parseInt(assign_rules[i]);
                            int temp=assgin % 2 == 0 ? even++ : odd++;
                        }
                    }
                    if (odd == assign_rules.length) {
                        month += "单号日";
                        flag = false;
                    } else if (even == assign_rules.length) {
                        month += "双号日";
                        flag = false;
                    }
                }
                if (flag) {
                    for (int i = 0; i < assign_rules.length; i++) {
                        days += assign_rules[i] + "日、";
                    }
                    days = days.substring(0, days.length() - 1);
                }
                month+=days;
                if(timeRule.get("lastDayWork")!=null){
                    int lastDayWork = Integer.parseInt(timeRule.get("lastDayWork").toString());
                    month+="当月没有29日、30日、31日时，允许系统自动按每月最后" + lastDayWork +"天计算";
                }
                itemPre.put("valid_period",month);
                break;
            }
            case 3:{
                //按每周
                String week="";
                week += "指定星期：";
                // if(timeRule.assign_rule)
                String[] weeks = {"", "周一", "周二", "周三", "周四","周五","周六","周日"};
                String workdays = "1,2,3,4,5";
                String restdays = "6,7";
                if (timeRule.get("assign_rule") == workdays) {
                    week += "工作日";
                } else if (timeRule.get("assign_rule") == restdays) {
                    week +="双休日";
                } else {
                    String[] assgin = timeRule.get("assign_rule").toString().split(",");
                    for (int i = 0; i < assgin.length; i++) {
                        if (assgin[i]!=null) {
                            Integer index=Integer.parseInt(assgin[i]);
                            week += weeks[index] + "、";
                        }
                    }
                    week = week.substring(0, week.length() - 1);
                }
                itemPre.put("valid_period",week);
                break;
            }

        }
    }

    public Integer getPromotionsCount(Map<String, Object> params) throws Exception{
        //获取前台传递的各种参数
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());
        Object no = params.get("no");
        if(no!=null){
            String decryptionCouponNo = couponNoEncodingService.decryptionCouponNo(no.toString());
            params.put("no",decryptionCouponNo);
        }
        int count = mapper.queryPromotionsDetailCount(siteId, ruleId,params);
        return count;
    }

    public Map getPromotionsStatus(Map<String, Object> params)throws Exception {
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());
        int status = Integer.parseInt(params.get("status").toString());
        Object no = params.get("no");
        if(no!=null){
            String decryptionCouponNo = couponNoEncodingService.decryptionCouponNo(no.toString());
            params.put("no",decryptionCouponNo);
        }
        List<Map<String, Object>> list = mapper.getPromotionsStatus(siteId, ruleId, status, params);
        Map<Object,Object> result =new HashMap<>();
        for (Map<String,Object> temp:list){
            Integer value=Integer.parseInt(temp.get("value").toString());
            result.put(temp.get("t_status"),value);
        }
        return  result;
    }

    public Date LocalDateTime2Date(LocalDateTime local){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = local.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public List<PromotionsDetail> getPromotionsDetails(Integer siteId, @Nonnull Long tradesId) {
        PromotionsDetailSqlParam promotionsDetailSqlParam = new PromotionsDetailSqlParam();
        promotionsDetailSqlParam.setSiteId(siteId);
        promotionsDetailSqlParam.setTradesId(tradesId);
        return mapper.findByParam(promotionsDetailSqlParam);
    }
}
