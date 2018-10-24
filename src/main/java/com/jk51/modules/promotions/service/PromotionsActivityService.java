package com.jk51.modules.promotions.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.model.Goods;
import com.jk51.model.coupon.requestParams.ActiveStatus;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.exception.ParameterErrorException;
import com.jk51.model.goods.PageData;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.order.Member;
import com.jk51.model.order.Trades;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.RuleAndActivityParams;
import com.jk51.model.promotions.activity.*;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import com.jk51.modules.persistence.mapper.MemberLabelMapper;
import com.jk51.modules.persistence.mapper.RelationLabelMapper;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProActivityDto;
import com.jk51.modules.promotions.request.ProActivityDtoForPage;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * 活动发放相关的功能
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@Service
public class PromotionsActivityService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsActivityMapper mapper;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private PromotionsRuleService promotionsRuleService;
    @Autowired
    private VerifyActivityService verifyActivityService;
    @Autowired
    private CouponActiveForMemberService couponActiveForMemberService;
    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MemberLabelMapper memberLabelMapper;
    @Autowired
    private CouponProcessUtils couponProcessUtils;
    @Autowired
    private RelationLabelMapper relationLabelMapper;


    public ReturnDto create(PromotionsActivity promotionsActivity) {
        ReturnDto checkResult = isValidForCreate(promotionsActivity);
        if (checkResult != null) {
            return checkResult;
        }
        if (promotionsActivity.getActive_link() != null && promotionsActivity.getActive_link() != "") {
            promotionsActivity.setPromotionsId(-1);
            if (promotionsActivity.getActive_link().indexOf("http://") == -1) {
                promotionsActivity.setActive_link("http://" + promotionsActivity.getActive_link());
            }
        }

        transformFromThemaActivity(promotionsActivity);

        promotionsActivity.setCreateTime(LocalDateTime.now());
        promotionsActivity.setUpdateTime(promotionsActivity.getCreateTime());

        try {
            int i = mapper.create(promotionsActivity);
            if (i != 1) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            logger.error("创建活动发放失败", e);
            return ReturnDto.buildFailedReturnDto("创建活动发放失败");
        }

        return ReturnDto.buildSuccessReturnDto();
    }

    public ReturnDto update(PromotionsActivity promotionsActivity) {
        ReturnDto checkResult = isValidForCreate(promotionsActivity);
        if (checkResult != null) {
            return checkResult;
        }

        transformFromThemaActivity(promotionsActivity);

        if (promotionsActivity.getCreateTime() == null)
            promotionsActivity.setCreateTime(LocalDateTime.now());
        promotionsActivity.setUpdateTime(LocalDateTime.now());

        try {
            int i = mapper.update(promotionsActivity);
            if (i != 1) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            logger.error("更新活动发放失败", e);
            return ReturnDto.buildFailedReturnDto("更新活动发放失败");
        }

        return ReturnDto.buildSuccessReturnDto();
    }

    //主题活动覆盖方法
    public void transformFromThemaActivity(PromotionsActivity promotionsActivity) {
        //先判断该活动是否为主题活动
        boolean flag = false;
        String showRule = promotionsActivity.getShowRule();
        if (showRule.contains("\"forcePopup\":\"1\"")) {
            flag = true;
        }
        if (flag) {
            //是主题活动，进行覆盖其他主题活动
            List<PromotionsActivity> list = mapper.getThemaActivityList(promotionsActivity.getSiteId());
            for (PromotionsActivity activity : list) {
                String sr = activity.getShowRule();
                try {
                    Map<String, Object> map = JacksonUtils.json2map(sr);
                    String isShow = (String) map.get("isShow");
                    String result = "{\"isShow\":\"" + isShow + "\",\"forcePopup\":\"0\",\"popupAtHomePage\":\"0\",\"popupWhenLogin\":0,\"isTransformFromThemaActivity\":1}";
                    activity.setShowRule(result);
                    mapper.update(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ReturnDto isValidForCreate(PromotionsActivity promotionsActivity) {
        if (promotionsActivity.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        if (promotionsActivity.getIntro() == null) {
            return ReturnDto.buildFailedReturnDto("活动描述不能为空");
        }
        if (promotionsActivity.getTemplatePic() == null) {
            return ReturnDto.buildFailedReturnDto("活动模版不能为空");
        }
        if (promotionsActivity.getStartTime() == null || promotionsActivity.getEndTime() == null) {
            return ReturnDto.buildFailedReturnDto("活动时间不能为空");
        }
        if (promotionsActivity.getUseObject() == null) {
            return ReturnDto.buildFailedReturnDto("参与对象不能为空");
        }
        if (promotionsActivity.getShowRule() == null) {
            return ReturnDto.buildFailedReturnDto("显示规则不能为空");
        }

        return null;
    }

    /**
     * 根据活动发放id和siteId，自动变更活动发放状态
     *
     * @param siteId
     * @param promotionsActivityId
     * @return
     */
    public boolean autoChangeStatus(Integer siteId, Integer promotionsActivityId) {
        PromotionsActivity promotionsActivity = mapper.getPromotionsActivityDetail(siteId, promotionsActivityId);

        promotionsRuleService.onlyChangeRuleStatus(siteId, promotionsActivity.getPromotionsId());

        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsActivity.getPromotionsId());

        switch (promotionsActivity.getStatus()) {
            case PROMOTIONS_ACTIVITY_STATUS_DRAFT:
            case PROMOTIONS_ACTIVITY_STATUS_STOP:
                return true; // 上述状态为不可逆或手动修改的
            case PROMOTIONS_ACTIVITY_STATUS_WILL_REALEASE:
            case PROMOTIONS_ACTIVITY_STATUS_HAS_REALEASE:
            case PROMOTIONS_ACTIVITY_STATUS_OVERDUE:
            case PROMOTIONS_ACTIVITY_STATUS_NO_INVENTORY:
                return autoChangeStatusByAmountAndTime(promotionsActivity, promotionsRule);
            default:
                logger.error("promotionsActivity.status 不可能出现的错误之数据库保存了没有定义的状态值");
        }

        return false;
    }

    private boolean autoChangeStatusByAmountAndTime(PromotionsActivity promotionsActivity, PromotionsRule promotionsRule) {
        switch (promotionsRule.getStatus()) {
            case PROMOTIONS_RULE_STATUS_DRAFT:
                if (verifyActivityService.verifyEndtime(promotionsActivity))
                    return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_OVERDUE);
                break;
            case PROMOTIONS_RULE_STATUS_RELEASE:
                if (verifyActivityService.verifyEndtime(promotionsActivity))
                    return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_OVERDUE);
                else if (verifyActivityService.verifyStarTime(promotionsActivity))
                    return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_HAS_REALEASE);
                else
                    return true;
            case PROMOTIONS_RULE_STATUS_NO_INVENTORY:
                if (verifyActivityService.verifyEndtime(promotionsActivity))
                    return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_OVERDUE);
                else
                    return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_NO_INVENTORY);
            case PROMOTIONS_RULE_STATUS_OVERDUE:
                return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_OVERDUE);
            case PROMOTIONS_RULE_STATUS_STOP:
                return ifNotThenChangeForStatus(promotionsActivity, PROMOTIONS_ACTIVITY_STATUS_STOP);
        }
        return false;
    }

    /**
     * 如果状态不符合预期，则改变它
     *
     * @param promotionsActivity
     * @param status
     * @return
     */
    private boolean ifNotThenChangeForStatus(PromotionsActivity promotionsActivity, int status) {
        if (promotionsActivity.getStatus() == status) {
            return true;
        } else {
            int i = mapper.updateStatusByIdAndSiteId(promotionsActivity.getId(), promotionsActivity.getSiteId(), status);
            return i == 1;
        }
    }

    public ReturnDto changeStatus(Integer siteId, Integer promotionsActivityId, Integer status) {
        try {
            int i = mapper.updateStatusByIdAndSiteId(promotionsActivityId, siteId, status);
            if (i == 1)
                return ReturnDto.buildSuccessReturnDto();
            else
                return ReturnDto.buildFailedReturnDto("因找不到数据导致更新活动发放状态失败");
        } catch (Exception e) {
            logger.error("更新活动发放状态失败", e);
            return ReturnDto.buildFailedReturnDto("因更新操作失败导致更新活动发放状态失败");
        }
    }

    public PageInfo<?> proActivityList(ProActivityDtoForPage param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.proActivityList(param);
        return new PageInfo<>(list);

    }


    public PageInfo<?> proActivityListNew(ProActivityDtoForPage param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.proActivityListNew(param);
        return new PageInfo<>(list);

    }

    public PageInfo<?> couponActivityList(ProActivityDtoForPage param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = mapper.couponActivityList(param);
        return new PageInfo<>(list);
    }

    public PageInfo<?> promotionsActivityList(ProActivityDtoForPage param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        //外部链接活动判断
        List<PageData> list = new ArrayList<PageData>();
        if (param.getPromotionsRuleType() != null && param.getPromotionsRuleType() == 100) {
            list = mapper.promotionsActivityList2(param);
        } else {
            list = mapper.promotionsActivityList(param);
        }
        return new PageInfo<>(list);
    }


    public List<Map<String, Object>> centerOfProActivityList(ProActivityDto proActivityDto) {
        return mapper.centerOfProActivityList(proActivityDto).stream().
            filter(map -> checkShowRule(map))
            .filter(map -> checkActivityTime(map))
            /*.filter(map-> promotionsRuleService.checkproRuleTimeRule((map.get("time_rule").toString())))*/.collect(Collectors.toList());
    }

    private boolean checkShowRule(Map<String, Object> map) {
        try {
            ShowRule showRule = JSON.parseObject(map.get("show_rule").toString(), ShowRule.class);
            if (null != showRule && showRule.getIsShow() == 1)
                return true;
            else
                return false;
        } catch (Exception e) {

            return false;
        }

    }

    private boolean checkActivityTime(Map<String, Object> map) {
        try {
            String promotionsTimeRule = map.get("time_rule") != null ? map.get("time_rule").toString() : "";
            TimeRuleForPromotionsRule timeRuleForPromotionsRule = null;
            if (!StringUtil.isEmpty(promotionsTimeRule))
                timeRuleForPromotionsRule = JSON.parseObject(promotionsTimeRule, TimeRuleForPromotionsRule.class);

            String format = "yyyy-MM-dd hh:mm:ss";

            String startTime = "";
            if (map.get("start_time") == null)
                return false;

            startTime = map.get("start_time").toString();
            Date startDate = DateUtils.parseDate(startTime, format);


            String endTime = "";
            if (map.get("end_time") == null)
                return false;

            endTime = map.get("end_time").toString();
            Date endDate = DateUtils.parseDate(endTime, format);

            if (null != timeRuleForPromotionsRule && timeRuleForPromotionsRule.getValidity_type() == 1) {
                Date startTimen = DateUtils.parseDate(timeRuleForPromotionsRule.getStartTime(), format);
                Date endTimen = DateUtils.parseDate(timeRuleForPromotionsRule.getEndTime(), format);
                if (endTimen.before(startDate))
                    return false;
                if (endDate.before(startTimen))
                    return false;

                if (startTimen.before(startDate) && endTimen.after(startDate) && endTimen.before(endDate)) {
                    if (!(new Date().after(startDate) && new Date().before(endTimen)))
                        return false;
                }

                if (endTimen.after(endDate) && startTimen.after(startDate) && endTimen.before(endDate)) {
                    if (!(new Date().after(startDate) && new Date().before(endDate)))
                        return false;
                }

                if (startTimen.after(startDate) && endTimen.before(endDate)) {
                    if (!(new Date().after(startDate) && new Date().before(endTimen)))
                        return false;
                }


                if (startTimen.before(startDate) && endTimen.after(endDate)) {
                    if (!(new Date().after(startDate) && new Date().before(endDate)))
                        return false;
                }

                if (startTimen.equals(startDate) && endTimen.equals(endDate)) {
                    if (!(new Date().after(startTimen) && new Date().before(endTimen)))
                        return false;
                }

                if (startTimen.equals(startDate) && endTimen.before(endDate)) {
                    if (!(new Date().after(startTimen) && new Date().before(endTimen)))
                        return false;
                }

                if (startTimen.equals(startDate) && endTimen.after(endDate)) {
                    if (!(new Date().after(startTimen) && new Date().before(endDate)))
                        return false;
                }

                if (endTimen.equals(endDate) && !startTimen.equals(startDate)) {
                    if (!(new Date().after(startDate) && new Date().before(endDate)))
                        return false;
                }
                /*if (!(startTimen.before(new Date()) && endTimen.after(new Date()) && (endTimen.after(startDate))))
                    return false;*/
            }


            if (endDate.before(new Date()))
                return false;

            return true;
        } catch (Exception e) {
            return false;
        }

    }


    private boolean checkActivityUseTime(Map<String, Object> map) {
        try {
            String format = "yyyy-MM-dd hh:mm:ss";
            String endTime = "";
            if (map.get("end_time") == null)
                return false;

            endTime = map.get("end_time").toString();
            Date endDate = DateUtils.parseDate(endTime, format);

            String startTime = "";
            if (map.get("start_time") == null)
                return false;

            startTime = map.get("start_time").toString();
            Date startDate = DateUtils.parseDate(startTime, format);


            if ((startDate.before(new Date()) && endDate.after(new Date())))
                return true;

            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public Map<String, Object> getPromotionsActivityMap(ProActivityDto proActivityDto) {
        Map<String, Object> resultMap = mapper.getPromotionsActivityMap(proActivityDto);
        if (Objects.nonNull(resultMap)) {
            if(Objects.isNull(resultMap.get("promotions_type"))||Objects.isNull(resultMap.get("promotions_rule"))){
                logger.error("查询出现空值：{}",resultMap);
                return null;
            }
            resultMap.put("proCouponRuleView", promotionsRuleService.promotionsRuleForType(Integer.parseInt(resultMap.get("promotions_type").toString()), resultMap.get("promotions_type").toString()));

            resultMap.put("useMap", getRealUseTime(resultMap.get("time_rule").toString(), resultMap.get("start_time").toString(), resultMap.get("end_time").toString()));

            if (StringUtil.equalsIgnoreCase(resultMap.get("status").toString(), "11")) {
                resultMap.put("useStatus", -1);
                return resultMap;
            }

            if (!(checkActivityUseTime(resultMap) && promotionsRuleService.checkproRuleTimeRule(resultMap.get("time_rule").toString()))) {
                resultMap.put("useStatus", 2);
                return resultMap;
            }

            boolean useStatus = couponActiveForMemberService.checkProActivity(proActivityDto.getSiteId(), Integer.parseInt(resultMap.get("id").toString()), proActivityDto.getMemberId());
            if (useStatus)
                resultMap.put("useStatus", 1);
            else
                resultMap.put("useStatus", 0);
        }

        return resultMap;
    }


    public Map<String, Object> getRealUseTime(String promotionsTimeRule, String proActivityStartTime, String proActivityEndTime) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String result = "";
        try {
            TimeRuleForPromotionsRule timeRuleForPromotionsRule = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(promotionsTimeRule, TimeRuleForPromotionsRule.class);
            String format = "yyyy-MM-dd hh:mm:ss";
            Date startTime = DateUtils.parseDate(proActivityStartTime, format);
            Date endTime = DateUtils.parseDate(proActivityEndTime, format);
            switch (timeRuleForPromotionsRule.getValidity_type()) {
                case 1:
                    Date proruleStartTime = DateUtils.parseDate(timeRuleForPromotionsRule.getStartTime(), format);
                    Date proruleEndTime = DateUtils.parseDate(timeRuleForPromotionsRule.getEndTime(), format);
                    String resultstart = startTime.before(proruleStartTime) ? timeRuleForPromotionsRule.getStartTime() : proActivityStartTime;
                    resultstart = DateUtils.parseDate(resultstart, format).before(endTime) ? resultstart : proActivityStartTime;

                    String resultend = endTime.before(proruleEndTime) ? proActivityEndTime : timeRuleForPromotionsRule.getEndTime();
                    result = resultstart.substring(0, 16) + "~" + resultend.substring(0, 16);
                    resultMap.put("time", result);
                    return resultMap;
                case 2:
                    result = "活动期间";
                    if (null != timeRuleForPromotionsRule.getLastDayWork() && timeRuleForPromotionsRule.getLastDayWork() > 0)
                        result += "每月最后" + timeRuleForPromotionsRule.getLastDayWork() + "天，以及";
                    String singleMonth = "1,3,5,7,8,10,12";
                    String doubleMonth = "4,6,9,11";

                    //31天月份
                    if (new HashSet<String>(Arrays.asList(singleMonth.split(","))).contains(LocalDateTime.now().getMonthValue() + "")) {
                        if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), "1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31"))
                            result += "逢单号日可享受优惠";
                        else if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), "2,4,6,8,10,12,14,16,18,20,22,24,26,28,30"))
                            result += "逢双号日可享受优惠";
                        else {
                            String[] assign_rule_days = timeRuleForPromotionsRule.getAssign_rule().split(",");
                            List<String> set_days = new ArrayList<String>(Arrays.asList(assign_rule_days));
                            result += "逢";
                            for (String s : set_days) {
                                result += s + ",";
                            }
                            result = result.substring(0, result.length() - 1) + "日可享受优惠";
                        }

                    } else if (new HashSet<String>(Arrays.asList(doubleMonth.split(","))).contains(LocalDateTime.now().getMonthValue() + "")) {
                        if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), "1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31"))
                            result += "逢单号日可享受优惠";
                        else if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), "2,4,6,8,10,12,14,16,18,20,22,24,26,28,30"))
                            result += "逢双号日可享受优惠";
                        else {
                            String[] assign_rule_days = timeRuleForPromotionsRule.getAssign_rule().split(",");
                            List<String> set_days = new ArrayList<String>(Arrays.asList(assign_rule_days));
                            result += "逢";
                            for (String s : set_days) {
                                result += s + ",";
                            }
                            result = result.substring(0, result.length() - 1) + "日可享受优惠";
                        }

                    } else if (LocalDateTime.now().getMonthValue() == 2) {
                        Calendar cale = Calendar.getInstance();
                        cale.set(Calendar.MONTH, LocalDateTime.now().getMonthValue());
                        cale.set(Calendar.DAY_OF_MONTH, 0);
                        Integer lastday = cale.get(Calendar.DAY_OF_MONTH);

                        String mons = "";
                        if (lastday == 29) {
                            mons = "1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31";
                        } else if (lastday == 28)
                            mons = "1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31";

                        if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), mons))
                            result += "逢单号日可享受优惠";
                        else if (StringUtil.equalsIgnoreCase(timeRuleForPromotionsRule.getAssign_rule(), "2,4,6,8,10,12,14,16,18,20,22,24,26,28,30"))
                            result += "逢双号日可享受优惠";
                        else {
                            String[] assign_rule_days = timeRuleForPromotionsRule.getAssign_rule().split(",");
                            List<String> set_days = new ArrayList<String>(Arrays.asList(assign_rule_days));
                            result += "逢";
                            for (String s : set_days) {
                                result += s + ",";
                            }
                            result = result.substring(0, result.length() - 1) + "日可享受优惠";
                        }

                    }
                    resultMap.put("time", proActivityStartTime.substring(0, 16) + "~" + proActivityEndTime.substring(0, 16));
                    resultMap.put("msg", result);
                    return resultMap;
                case 3:
                    result = "活动期间逢";
                    String[] assign_rule_weeks = timeRuleForPromotionsRule.getAssign_rule().split(",");
                    List<String> set_week = new ArrayList<String>(Arrays.asList(assign_rule_weeks));
                    for (String s : set_week) {
                        if (StringUtil.equalsIgnoreCase(s, "1"))
                            result += "周一,";
                        if (StringUtil.equalsIgnoreCase(s, "2"))
                            result += "周二,";
                        if (StringUtil.equalsIgnoreCase(s, "3"))
                            result += "周三,";
                        if (StringUtil.equalsIgnoreCase(s, "4"))
                            result += "周四,";
                        if (StringUtil.equalsIgnoreCase(s, "5"))
                            result += "周五,";
                        if (StringUtil.equalsIgnoreCase(s, "6"))
                            result += "周六,";
                        if (StringUtil.equalsIgnoreCase(s, "7"))
                            result += "周日,";
                    }
                    result = result.substring(0, result.length() - 1) + "可享受优惠";
                    resultMap.put("time", proActivityStartTime.substring(0, 16) + "~" + proActivityEndTime.substring(0, 16));
                    resultMap.put("msg", result);
                    return resultMap;
            }
        } catch (Exception e) {
            logger.info("活动详情获取实际使用时间异常");
            return null;
        }

        return null;
    }

    public ReturnDto updateActiveStatus(ActiveStatus activeStatus) {


        PromotionsActivity activity = mapper.getPromotionsActivityDetail(activeStatus.getSiteId(), activeStatus.getActiveId());


        if (null == activity) {
            return ReturnDto.buildFailedReturnDto("没有查到相关活动信息");
        }

        if (!Objects.equals(activeStatus.getPreStatus(), activity.getStatus())) {
            return ReturnDto.buildFailedReturnDto("该活动状态已修改，请刷新后确认");
        }

        int result = mapper.updateStatusByIdAndSiteId(activeStatus.getActiveId(), activeStatus.getSiteId(), activeStatus.getToUpdateStatus());

        if (result == 1) {
            return ReturnDto.buildSuccessReturnDto("修改活动成功");
        } else {
            return ReturnDto.buildFailedReturnDto("修改活动失败");
        }
    }

    //根据活动获取用户列表
    public ReturnDto getAllMembersForActivity(Integer activityId, Integer siteId) {
        PromotionsActivity activity = mapper.getPromotionsActivityDetail(siteId, activityId);
        if (activity == null)
            return ReturnDto.buildFailedReturnDto("没查到对应的活动信息");
        SignMembers members = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(activity.getUseObject(), SignMembers.class);
        List<Member> memberList = memberMapper.queryMemberListForCouponActive(siteId, new HashSet<String>(Arrays.asList(members.getPromotion_members().split(","))));

        return ReturnDto.buildSuccessReturnDto(memberList);

    }

    public ReturnDto getForcePopupCounts(Integer siteId) {
        int i = 0;
        try {
            i = mapper.getForcePopupCounts(siteId);
        } catch (Exception e) {
            logger.error("查找表中主题活动个数失败", e);
            return ReturnDto.buildFailedReturnDto("查找表中主题活动个数失败");
        }
        return ReturnDto.buildSuccessReturnDto(i);
    }

    //查询弹框数据
    public ReturnDto getproActivityBomb(ProActivityBomb proActivityBomb) {
        List<Map<String, Object>> resultMap = mapper.getproActivityBomb(proActivityBomb).stream().filter(map -> checkMap(map)).collect(toList());
        if (!resultMap.isEmpty())
            return ReturnDto.buildSuccessReturnDto(resultMap.get(0));
        else
            return ReturnDto.buildFailedReturnDto("暂无数据");
    }

    //查询拼团活动下拼团总人数与已经付款的人数
    public Map<String, Object> getGroupSumPeopleAndPayingPeople(GroupPeopleNum groupPeopleNum) {
        // 2017/11/20 根据实体类中的tradesId 查询出订单信息 2 根据订单信息中的service_type 判断是否为拼团订单(50)
        // 如果是拼团订单，则查询b_group_purchase 表 获取团长与团员的数据  根据status 判断此订单的状态 和团员的付款状态
        // 该活动的参团人数上限需要查询b_promotions_activity 表 中的字段 groupMemberNum
        Trades trades = tradesMapper.getTradesByTradesId(groupPeopleNum.getTradesId());
        if (trades.getServceTpye() != null && trades.getServceTpye() == 50) {
            GroupPurchaseParam groupPurchaseParam = new GroupPurchaseParam();
            groupPurchaseParam.setSiteId(groupPeopleNum.getSiteId());
            groupPurchaseParam.setTradesId(String.valueOf(groupPeopleNum.getTradesId()));
            List<GroupPurchase> groupPurchaseList = groupPurChaseMapper.getGroupPurchaseList(groupPurchaseParam);
            int sum = 0;
            int payingPeople = 1;
            Map<String, Object> map = new HashMap<String, Object>();
            LocalDateTime groupbeginTime = null;
            for (GroupPurchase groupPurchase : groupPurchaseList) {
                //团长订单
                if (groupPurchase.getParentId() == null) {
                    if (trades.getTradesStatus() == 110 || trades.getTradesStatus() == 170) {
                        payingPeople = 0;
                    }
                    GroupPurchaseParam groupPurchaseParam2 = new GroupPurchaseParam();
                    groupPurchaseParam2.setParentId(groupPurchase.getId());
                    GroupPurchase info = groupPurChaseMapper.findInfo(groupPurchase.getSiteId(), groupPurchase.getId());
                    List<GroupPurchase> sonGroupList = groupPurChaseMapper.getGroupPurchaseList(groupPurchaseParam2);
                    for (GroupPurchase groupPurchase1 : sonGroupList) {
                        if (groupPurchase1.getStatus() == 2) {
                            payingPeople += 1;
                        }
                    }
                    groupbeginTime = info.getGroupbeginTime();
                    map.put("role", 1);
                    map.put("beginTime", info.getGroupbeginTime());
                    map.put("groupStatus", groupPurchase.getStatus().toString());
                    if (trades.getIsRefund() == 120 || trades.getTradesStatus() == 180) {
                        map.put("groupStatus", 4);
                    }
                    map.put("goodsId", info.getGoodsId());
                    map.put("fightGroupId", info.getId());
                } else {
                    //团员订单
                    payingPeople = 1;
                    GroupPurchaseParam groupPurchaseParam2 = new GroupPurchaseParam();
                    groupPurchaseParam2.setId(groupPurchase.getParentId());
                    groupPurchaseParam2.setSiteId(groupPurchase.getSiteId());
                    GroupPurchase info = groupPurChaseMapper.findInfo(groupPurchase.getSiteId(), groupPurchase.getParentId());
                    List<GroupPurchase> leaderList = groupPurChaseMapper.getGroupPurchaseList(groupPurchaseParam2);
                    GroupPurchaseParam groupPurchaseParam3 = new GroupPurchaseParam();
                    GroupPurchase groupPurchase2 = leaderList.get(0);
                    groupPurchaseParam3.setParentId(groupPurchase2.getId());
                    List<GroupPurchase> sonGroupList = groupPurChaseMapper.getGroupPurchaseList(groupPurchaseParam3);
                    for (GroupPurchase groupPurchase1 : sonGroupList) {
                        if (groupPurchase1.getStatus() == 2) {
                            payingPeople += 1;
                        }
                        if (trades.getIsRefund() == 120 || trades.getTradesStatus() == 180) {
                            map.put("groupStatus", 4);
                        }
                        if (groupPurchase1.getStatus() == 4 && groupPurchase1.getTradesId().equals(trades.getTradesId().toString())) {
                            map.put("groupStatus", groupPurchase1.getStatus());
                        }
                    }
                    groupbeginTime = info.getGroupbeginTime();
                    map.put("role", 2);
                    map.put("beginTime", info.getGroupbeginTime());
                    if (map.get("groupStatus") == null) {
                        map.put("groupStatus", groupPurchase2.getStatus().toString());
                    }
                    map.put("goodsId", info.getGoodsId());
                    map.put("fightGroupId", info.getId());
                }
                //查询开团的总人数
                PromotionsActivity promotions = mapper.getPromotionsActivityDetail(groupPurchase.getSiteId(), groupPurchase.getProActivityId());
                if (promotions != null) {
                    map.put("proActivityId", promotions.getId());
                    ProActivityDto proActivityDto = new ProActivityDto();
                    proActivityDto.setId(promotions.getId());
                    proActivityDto.setSiteId(promotions.getSiteId());
                    Map<String, Object> promotionsActivityMap = mapper.getPromotionsActivityMap(proActivityDto);
                    String promotions_rule = promotionsActivityMap.get("promotions_rule").toString();
                    GroupBookingRule groupBookingRule = JSON.parseObject(promotions_rule, GroupBookingRule.class);
                    map.put("liveTime", groupBookingRule.getGroupLiveTime());
                    LocalDateTime aftergroupLiveTime = groupbeginTime.minusHours(0 - groupBookingRule.getGroupLiveTime());
                    Duration duration = Duration.between(LocalDateTime.now(), aftergroupLiveTime);
                    groupPurchase.setLastTime(duration.toMillis());
                    map.put("aftergroupLiveTime", aftergroupLiveTime);
                    if (groupBookingRule.getRuleType() == 2 || groupBookingRule.getRuleType() == 4) {
                        //这两种类型是拼团活动的按照商品设置拼团价格（非统一价）
                        List<Map<String, Integer>> rules = groupBookingRule.getRules();
                        Integer goodsId = groupPurchase.getGoodsId();
                        for (Map<String, Integer> rulesMap : rules) {
                            if (Integer.parseInt(rulesMap.get("goodsId").toString()) == goodsId) {
                                sum = rulesMap.get("groupMemberNum");
                            }
                        }
                    } else {
                        List<Map<String, Integer>> rules = groupBookingRule.getRules();
                        for (Map<String, Integer> rulesMap : rules) {
                            sum = rulesMap.get("groupMemberNum");
                        }
                    }
                }
                map.put("payingPeople", payingPeople);
                map.put("sum", sum);
                map.put("remainPeople", sum - payingPeople);
            }
            return map;
        }
        return null;
    }

    /**
     * 根据promotionsActivityId查询对应的promotionsActivity带promotionsRule，两者状态都要求为0
     *
     * @param siteId
     * @param promotionsActivityId
     * @return
     */
    public Optional<PromotionsActivity> getActivePromotionsActivityWithPromotionsRule(@Nonnull Integer siteId,
                                                                                      @Nonnull Integer promotionsActivityId) {
        PromotionsActivity promotionsActivity = mapper.getPromotionsActivityDetail(siteId, promotionsActivityId);
        if (promotionsActivity == null || promotionsActivity.getStatus() != 0)
            return Optional.empty();

        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsActivity.getPromotionsId());
        if (promotionsRule == null || promotionsRule.getStatus() != 0)
            return Optional.empty();

        promotionsActivity.setPromotionsRule(promotionsRule);

        return Optional.of(promotionsActivity);
    }

    public PromotionsActivity getPromotionsActivityById(Integer siteId, Integer activityId) {
        return mapper.getPromotionsActivityDetail(siteId, activityId);
    }

    public boolean checkRuleIsReleaseAtLeastOnce(Integer siteId, Integer promotionsRuleId) {
        return mapper.countRuleReleaseNum(siteId, promotionsRuleId) > 0;
    }

    public boolean checkMap(Map<String, Object> map) {
        if (map.get("active_link") != null)
            return true;
        else
            return checkActivityTime(map) && promotionsRuleService.checkproRuleTimeRule((map.get("time_rule").toString()));
    }

    @Transactional
    public void createReleaseRuleAndDraftActivity(RuleAndActivityParams ruleAndActivity) throws ParameterErrorException {
        if (ruleAndActivity.getSiteId() == null) throw new NullPointerException("无法获取到siteId");
        ruleAndActivity.getPromotionsRule().setSiteId(ruleAndActivity.getSiteId());
        ruleAndActivity.getPromotionsActivity().setSiteId(ruleAndActivity.getSiteId());

        Integer promotionsRuleId = createAndReturnId(ruleAndActivity.getPromotionsRule());

        if (promotionsRuleId != null) {
            PromotionsActivity promotionsActivity = ruleAndActivity.getPromotionsActivity();
            promotionsActivity.setPromotionsId(promotionsRuleId);

            create(promotionsActivity);
        } else {
            throw new NullPointerException("无法获取到活动规则id");
        }
    }

    @Transactional
    public void editReleaseRuleAndDraftActivity(RuleAndActivityParams ruleAndActivity) throws ParameterErrorException {
        promotionsRuleService.edit(ruleAndActivity.getPromotionsRule());
        update(ruleAndActivity.getPromotionsActivity());
    }

    public Integer createAndReturnId(PromotionsRule promotionsRule) throws ParameterErrorException {
        isValidForCreateAndGetId(promotionsRule);

        promotionsRule.setCreateTime(LocalDateTime.now());
        promotionsRule.setUpdateTime(promotionsRule.getCreateTime());
        promotionsRule.setStatus(0);

        if (promotionsRule.getPromotionsType().equals(10)) {
            GiftRule giftRule = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON
                .parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
            giftRule.getSendGifts().stream()
                .forEach(sendGifts -> sendGifts.setTotal(sendGifts.getSendNum()));
            promotionsRule.setPromotionsRule(com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON
                .toJSONString(giftRule));
        }

        promotionsRuleMapper.createAndGetId(promotionsRule);

        return promotionsRule.getId();
    }

    private void isValidForCreateAndGetId(PromotionsRule promotionsRule) throws ParameterErrorException {
        if (promotionsRule.getSiteId() == null) {
            throw new ParameterErrorException("siteId不能为空");
        }
        if (promotionsRule.getPromotionsType() == null) {
            throw new ParameterErrorException("promotionsType不能为空");
        }
        if (promotionsRule.getAmount() == null || promotionsRule.getTotal() == null) {
            throw new ParameterErrorException("数量不能为空");
        }
        if (promotionsRule.getLabel() == null) {
            throw new ParameterErrorException("标签不能为空");
        }
        if (promotionsRule.getPromotionsRule() == null || promotionsRule.getPromotionsType() == null) {
            throw new ParameterErrorException("规则不能为空");
        }
        if (promotionsRule.getIsFirstOrder() == null) {
            throw new ParameterErrorException("是否首单不能为空");
        }
    }

    public List<PromotionsActivity> findReleasePomotionsActivity(Integer siteId, Integer activityId) {
        return promotionsActivityMapper.findAllReleasePromotionsActivity(siteId, activityId)
            .stream()
            .filter(pa -> {
                PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(pa.getSiteId(), pa.getPromotionsId());
                if (promotionsRule == null) {
                    return false;
                } else {
                    pa.setPromotionsRule(promotionsRule);
                    return true;
                }
            })
            .collect(toList());
    }

    /**
     * 查询所有状态为0的活动并查询对应的rule封装到活动里面去
     * 根据活动创建时间排序
     *
     * @param siteId
     * @return
     */
    public Optional<List<PromotionsActivity>> getPromotionsActivitiesReleasedAndSorted(Integer siteId) {
        // 查找状态为0的PromotionsActivity，并且已根据时间排序
        List<PromotionsActivity> promotionsActivities = mapper.getPromotionsActivitiesByStatusAndSiteId(checkNotNull(siteId));

        Set<Integer> promotionsRuleIds = promotionsActivities.stream()
            .map(PromotionsActivity::getPromotionsId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (promotionsRuleIds.size() == 0)
            return Optional.empty();

        // 给PromotionsActivity填充对应id的PromotionsRule
        promotionsActivities = setPromotionsRuleForPromotionsActivity(siteId, promotionsActivities, promotionsRuleIds, true);

        if (promotionsActivities.size() == 0)
            return Optional.empty();
        else
            return Optional.of(promotionsActivities);
    }


    /**
     * 查询所有状态为0的活动并查询对应的rule封装到活动里面去
     * 根据活动创建时间排序
     *
     * @param siteId
     * @param promotionsActivityIds
     * @return
     */
    public Optional<List<PromotionsActivity>> getPromotionsActivitiesReleasedAndSorted(Integer siteId, List<Integer> promotionsActivityIds) {
        // 查找状态为0的PromotionsActivity
        if (promotionsActivityIds == null || promotionsActivityIds.isEmpty())
            throw new ParamErrorException();

        List<PromotionsActivity> promotionsActivities = mapper.getPromotionsActivitiesBySiteIdAndIds(checkNotNull(siteId), promotionsActivityIds, true);

        Set<Integer> promotionsRuleIds = promotionsActivities.stream()
            .map(PromotionsActivity::getPromotionsId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (promotionsRuleIds.size() == 0)
            return Optional.empty();

        // 给PromotionsActivity填充对应id的PromotionsRule
        promotionsActivities = setPromotionsRuleForPromotionsActivity(siteId, promotionsActivities, promotionsRuleIds, true);

        if (promotionsActivities.size() == 0)
            return Optional.empty();
        else
            return Optional.of(promotionsActivities);
    }


    /**
     * 根据promotionsActivityId查询对应的promotionsActivity带promotionsRule，两者状态都要求为0
     *
     * @param siteId
     * @param promotionsActivityId
     * @return
     */
    public Optional<PromotionsActivity> getPromotionsActivityWithPromotionsRule(@Nonnull Integer siteId,
                                                                                @Nonnull Integer promotionsActivityId) {
        PromotionsActivity promotionsActivity = mapper.getPromotionsActivityDetail(siteId, promotionsActivityId);
        if (promotionsActivity == null || promotionsActivity.getStatus() != 0)
            return Optional.empty();

        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsActivity.getPromotionsId());
        if (promotionsRule == null || promotionsRule.getStatus() != 0)
            return Optional.empty();

        promotionsActivity.setPromotionsRule(promotionsRule);

        return Optional.of(promotionsActivity);
    }

    /**
     * 根据活动ids查找带promotionsRule的promotionsActivity，对状态无要求
     *
     * @param siteId
     * @param promotionsActivityIds
     * @return
     */
    public Optional<List<PromotionsActivity>>
    getProActivityWithProRuleByProActivityIds(@Nonnull Integer siteId, @Nonnull List<Integer> promotionsActivityIds) {
        if (promotionsActivityIds.isEmpty())
            throw new ParamErrorException();

        List<PromotionsActivity> promotionsActivities = mapper.getPromotionsActivitiesBySiteIdAndIds(checkNotNull(siteId), promotionsActivityIds, false);

        Set<Integer> promotionsRuleIds = promotionsActivities.stream()
            .map(PromotionsActivity::getPromotionsId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        promotionsActivities = setPromotionsRuleForPromotionsActivity(siteId, promotionsActivities, promotionsRuleIds, false);

        if (promotionsRuleIds.size() == 0)
            return Optional.empty();

        return Optional.of(promotionsActivities);
    }

    private List<PromotionsActivity>
    setPromotionsRuleForPromotionsActivity(Integer siteId,
                                           List<PromotionsActivity> promotionsActivities,
                                           Set<Integer> promotionsRuleIds,
                                           boolean ruleIsReleased) {
        List<PromotionsRule> promotionsRules = promotionsRuleMapper.getPromotionsRuleByIdsAndSiteId2(siteId, promotionsRuleIds);
        Map<Integer, PromotionsRule> map = new HashMap<>();
        promotionsRules.stream()
            .filter(promotionsRule -> promotionsRule.getStatus() == 0 || !ruleIsReleased)
            .forEach(promotionsRule -> map.put(promotionsRule.getId(), promotionsRule));

        promotionsActivities.forEach(promotionsActivity ->
            promotionsActivity.setPromotionsRule(map.get(promotionsActivity.getPromotionsId())));

        // 过滤PromotionsRule状态不为0的PromotionsActivity
        promotionsActivities = promotionsActivities.stream()
            .filter(promotionsActivity -> promotionsActivity.getPromotionsRule() != null)
            .collect(Collectors.toList());
        return promotionsActivities;
    }

    /**
     * @param siteId
     * @param activityId
     * @param type       1:所有活动状态 2:进行中的活动
     * @return
     */
    public List<PromotionsActivity> findAllReleasePromotionsActivityForBuyer(Integer siteId, Integer activityId, Integer type) {
        List<PromotionsActivity> list = new ArrayList<>();
        if (type == 1) {
            list = promotionsActivityMapper.findAllReleasePromotionsActivityForBuyer(siteId, activityId);
        } else {
            list = promotionsActivityMapper.findAllReleasePromotionsActiviting(siteId, activityId);
        }
        return list
            .stream()
            .filter(pa -> {
                PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(pa.getSiteId(), pa.getPromotionsId());
                if (promotionsRule == null) {
                    return false;
                } else {
                    pa.setPromotionsRule(promotionsRule);
                    return true;
                }
            })
            .collect(toList());
    }

    /**
     * 回访--同时包含回访会员和回访商品，处于未开始或进行中状态的活动
     * 1 根据商品Id查出包含该商品的活动
     * 2 判断该活动内参与对象是否有userIds中的元素
     * 3 如果没有，剔除该活动
     * 4 如果有，查询该活动指定的所有商品，计数有多少个指定商品包含于回访商品内
     * 5 查询该活动指定的会员范围（包括标签，标签组等），计数有多少个指定会员包含于回访会员内
     * 6 活动的数据对前端友好性的转换
     * 7 根据特定规则对返回的List数据进行排序
     *
     * @param goodIds 一组商品id
     * @param userId  一组用户id
     * @return
     */

    public Map<String, Object> searchContainGoodsIdAndUserIdPromotions(String goodIds, String userId, Integer siteId) throws Exception {

        String[] goodStrings;
        String[] uidStrings;
        StringBuffer goodsIdBuffer = new StringBuffer();
        List<Integer> joinGoodsIdList = new ArrayList<>();
        if ("0".equals(goodIds)) {
            List<String> goodsIdsList = goodsMapper.queryGoodsIds(siteId);
            goodStrings = goodsIdsList.toArray(new String[0]);
        } else {
            //去重
            goodStrings = removeDuplicate(goodIds);
        }
        if ("0".equals(userId)) {
            List<String> memberIdList = memberMapper.queryMemberId(siteId);
            uidStrings = memberIdList.toArray(new String[0]);
        } else {
            uidStrings = removeDuplicate(userId);
        }

        //返回参数列表
        Map<String, Object> returnMap = new HashMap<>();
        List<VisitTask> visitTasksList = new ArrayList<>();
        //活动map
        Map<Integer, PromotionsActivity> promotionsActivityMap = new HashMap<>();
        //活动与商品参与方式的关系Map
        Map<Integer, TempDateModel> goodsJoinMethod = new HashMap<>();
        //总回访人数Map
        Map<Integer, Member> totalMemberMap = new HashMap<>();
        //实际参与会员Map
        Map<Integer, Member> realMap = new HashMap<>();
        List<String> goodsIdList = Arrays.asList(goodStrings);
        goodsIdList.parallelStream().forEach(goodsId -> {
            List<PromotionsActivity> promotionsActivities = queryActList(StringUtil.join(goodStrings, ","), goodsId, siteId, userId, goodsJoinMethod);
            for (PromotionsActivity promotionsActivity : promotionsActivities) {
                Integer count1 = 0;
                Integer count2 = 0;
                //商品map
                Map<Integer, Goods> goodsMap = new HashMap<>();
                //会员map
                Map<Integer, Member> memberMap = new HashMap<>();


                if (!promotionsActivityMap.containsKey(promotionsActivity.getId())) {
                    promotionsActivityMap.put(promotionsActivity.getId(), promotionsActivity);
                    memberMap = statisticsMemberCount(promotionsActivity, memberMap);
                    if (memberMap != null && memberMap.size() > 0) {
                        for (String uid : uidStrings) {
                            if (memberMap.containsKey(Integer.parseInt(uid))) {
                                realMap.put(Integer.parseInt(uid), memberMap.get(Integer.parseInt(uid)));
                                count1++;
                            }
                        }
                    }
                    goodsMap = statisticsGoodsCount(promotionsActivity, goodsMap, goodsJoinMethod, StringUtil.join(goodStrings, ","));
                    if (goodsMap != null && goodsMap.size() > 0) {
                        for (String gid : goodStrings) {
                            if (goodsMap.containsKey(Integer.parseInt(gid))) {
                                if (!joinGoodsIdList.contains(Integer.parseInt(gid))) {
                                    joinGoodsIdList.add(Integer.parseInt(gid));
                                }
                                count2++;
                            }
                        }
                    }
                    totalMemberMap.putAll(realMap);
                    String promotionsGoods = resolver1(goodsJoinMethod.get(promotionsActivity.getId()).getGoodsIdType());
                    String promotionType = resolver2(goodsJoinMethod.get(promotionsActivity.getId()).getPromotionType());
                    String joinObject = null;
                    try {
                        joinObject = resolver3(JacksonUtils.json2pojo(promotionsActivity.getUseObject(), SignMembers.class).getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    VisitTask visitTask = buildReturnData(promotionsActivity, count1, count2, joinObject, promotionsGoods, promotionType, promotionsActivity.getStartTime());
                    if (null != visitTask) {
                        visitTasksList.add(visitTask);
                    }
                }
            }
        });
        logger.info("活动匹配：共匹配到{}个活动", visitTasksList.size());
        returnMap.put("list", visitTasksList);
        returnMap.put("total", realMap.size());
        if (joinGoodsIdList.size() > 0) {
            joinGoodsIdList.stream().forEach(id -> {
                goodsIdBuffer.append(id + ",");
            });
            returnMap.put("goodsIds", goodsIdBuffer.deleteCharAt(goodsIdBuffer.length() - 1));
        }
        visitTasksList.sort(new Comparator<VisitTask>() {
            @Override
            public int compare(VisitTask o1, VisitTask o2) {
                //先比较指定商品方式
                if (o1.getPromotionGoods().length() < o2.getPromotionGoods().length()) {
                    //比较参与对象方式
                    if (o1.getJoinObject().length() < o2.getJoinObject().length()) {
                        return 1;
                    } else if (o1.getJoinObject().length() == o2.getJoinObject().length()) {
                        //比较时间大小
                        if (o1.getDateTime().compareTo(o2.getDateTime()) > 0) {
                            return -1;
                        } else if (o1.getDateTime().compareTo(o2.getDateTime()) < 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        return -1;
                    }
                } else if (o1.getPromotionGoods().length() > o2.getPromotionGoods().length()) {

                    if (o1.getJoinObject().length() > o2.getJoinObject().length()) {
                        return -1;
                    } else if (o1.getJoinObject().length() == o2.getJoinObject().length()) {
                        //比较时间大小
                        if (o1.getDateTime().compareTo(o2.getDateTime()) > 0) {
                            return -1;
                        } else if (o1.getDateTime().compareTo(o2.getDateTime()) < 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        return 1;
                    }
                } else if (o1.getPromotionGoods().length() == o2.getPromotionGoods().length()) {
                    if (o1.getJoinObject().length() == o2.getJoinObject().length())
                        if (o1.getDateTime().compareTo(o2.getDateTime()) > 0) {
                            return -1;
                        } else if (o1.getDateTime().compareTo(o2.getDateTime()) < 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                }
                return 0;
            }

        });
        //visitTasksList.sort(Comparator.comparing(VisitTask::getDateTime).reversed());
        return returnMap;
    }

    /**
     * 根据商品Id查出包含该商品并且会员存在于参与对象的的活动列表
     *
     * @return
     */
    public List<PromotionsActivity> queryActList(String goodIds, String goodId, Integer siteId, String userId, Map<Integer, TempDateModel> goodsJoinMethod) {
        List<PromotionsActivity> allReleasePromotionsActivity2 = promotionsActivityMapper.findAllReleasePromotionsActivity2(siteId);
        List<PromotionsActivity> containGoodsList = allReleasePromotionsActivity2.stream().filter(list -> isContainGoods(list, goodIds, goodId, goodsJoinMethod)).collect(Collectors.toList());
        return containGoodsList.stream().filter(ms -> isContainUser(ms, userId, siteId)).collect(toList());
    }


    public Boolean isContainGoods(PromotionsActivity promotionsActivity, String goodIds, String goodId, Map<Integer, TempDateModel> goodsJoinMethod) {
        PromotionsRule promotionRule = promotionsRuleMapper.getPromotionsRuleBySiteIdAndActivityId(promotionsActivity.getSiteId(), promotionsActivity.getId());
        if (Objects.nonNull(promotionRule)) {
            Integer promotionsType = promotionRule.getPromotionsType();
            switch (promotionsType) {
                //赠品(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT:
                    GiftRule giftRule = JSON.parseObject(promotionRule.getPromotionsRule(), GiftRule.class);
                    if (Arrays.asList(giftRule.getGoodsIds().split(",")).contains(goodId)) {
                        goodsJoinMethod.put(promotionsActivity.getId(), new TempDateModel(1, giftRule.getGoodsIds(), promotionsType));
                        return true;
                    }
                    break;
                //打折(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_DISCOUNT:
                    DiscountRule discountRule = JSON.parseObject(promotionRule.getPromotionsRule(), DiscountRule.class);
                    return isContainRange(goodIds, promotionsType, Arrays.asList(discountRule.getGoodsIds().split(",")),
                        discountRule.getGoodsIdsType(), goodId, discountRule.getGoodsIds(), goodsJoinMethod, promotionsActivity);

                //包邮(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST:
                    FreePostageRule freePostageRule = JSON.parseObject(promotionRule.getPromotionsRule(), FreePostageRule.class);
                    return isContainRange(goodIds, promotionsType, Arrays.asList(freePostageRule.getGoodsIds().split(",")),
                        freePostageRule.getGoodsIdsType(), goodId, freePostageRule.getGoodsIds(), goodsJoinMethod, promotionsActivity);
                //满减(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_MONEY_OFF:
                    ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionRule.getPromotionsRule(), ReduceMoneyRule.class);
                    return isContainRange(goodIds, promotionsType, Arrays.asList(reduceMoneyRule.getGoodsIds().split(",")),
                        reduceMoneyRule.getGoodsIdsType(), goodId, reduceMoneyRule.getGoodsIds(), goodsJoinMethod, promotionsActivity);
                //限价(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                    FixedPriceRule fixedPriceRule = JSON.parseObject(promotionRule.getPromotionsRule(), FixedPriceRule.class);
                    return isContainRange(goodIds, promotionsType, Arrays.asList(fixedPriceRule.getGoodsIds().split(",")),
                        fixedPriceRule.getGoodsIdsType(), goodId, fixedPriceRule.getGoodsIds(), goodsJoinMethod, promotionsActivity);
                //拼团(1)
                case PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                    GroupBookingRule groupBookingRule = JSON.parseObject(promotionRule.getPromotionsRule(), GroupBookingRule.class);
                    return isContainRange(goodIds, promotionsType, Arrays.asList(groupBookingRule.getGoodsIds().split(",")),
                        groupBookingRule.getGoodsIdsType(), goodId, groupBookingRule.getGoodsIds(), goodsJoinMethod, promotionsActivity);
            }
        }
        return false;
    }

    public Boolean isContainRange(String goodsIds, Integer promotionsType, List<String> strings, Integer goodIdType, String goodIds, String promotionGoodIds,
                                  Map<Integer, TempDateModel> goodsJoinMethod, PromotionsActivity promotionsActivity) {
        switch (goodIdType) {
            //指定参加
            case 1:
                if (strings.contains(goodIds)) {
                    goodsJoinMethod.put(promotionsActivity.getId(), new TempDateModel(goodIdType, promotionGoodIds, promotionsType));
                    return true;
                } else {
                    return false;
                }
                //指定不参加
            case 2:
                if (!strings.contains(goodIds)) {
                    goodsJoinMethod.put(promotionsActivity.getId(), new TempDateModel(goodIdType, promotionGoodIds, promotionsType));
                    return true;
                } else {
                    return false;
                }
            case 0:
                goodsJoinMethod.put(promotionsActivity.getId(), new TempDateModel(goodIdType, goodsIds, promotionsType));
                return true;
            default:
                throw new RuntimeException("未知的goodIdType类型");
        }
    }

    public Boolean isContainUser(PromotionsActivity promotionsActivity, String userIds, Integer siteId) {
        for (String id : userIds.split(",")) {
            if (couponActiveForMemberService.checkProActivity(siteId, promotionsActivity.getId(), Integer.parseInt(id))) {
                return true;
            }
        }
        return false;
    }

    //计数会员数量
    public Map<Integer, Member> statisticsMemberCount(PromotionsActivity promotionsActivity, Map<Integer, Member> map) {
        PromotionsActivity proActivityDtoactivity = promotionsActivityMapper.getPromotionsActivityDetail(promotionsActivity.getSiteId(), promotionsActivity.getId());
        if (null == proActivityDtoactivity)
            return null;
        String user_Obj = proActivityDtoactivity.getUseObject();
        SignMembers members = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(user_Obj, SignMembers.class);
        if (members.getType() == 0) {
            List<Member> allMember = memberMapper.findAllMember(promotionsActivity.getSiteId());
            map = allMember.stream().collect(Collectors.toMap(Member::getMemberId, member -> member));
        }
        if (members.getType() == 1) {
            String[] sign_members = members.getPromotion_members().split(",");
            List<MemberLabel> memberLabelList = memberLabelMapper.getLabelAllForCouponActive(proActivityDtoactivity.getSiteId(), sign_members).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getScene())).collect(Collectors.toList());
            Map<String, Object> stringMap = null;
            for (MemberLabel memberLabel : memberLabelList) {
                stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                String userIds = (String) stringMap.get("userIds");
                for (String id : userIds.split(",")) {
                    Member member = memberMapper.selectByMemberIdAndSiteId(id, promotionsActivity.getSiteId());
                    if (Objects.nonNull(member)) {
                        map.put(member.getMemberId(), member);
                    }
                }
            }

        } else if (members.getType() == 2) {
            List<String> strings = Arrays.asList(members.getPromotion_members().split(","));
            for (String id : strings) {
                Member member = memberMapper.selectByMemberIdAndSiteId(id, promotionsActivity.getSiteId());
                if (Objects.nonNull(member)) {
                    map.put(member.getMemberId(), member);
                }
            }
        } else if (members.getType() == 3) {
            String[] labelNameArr = (com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(proActivityDtoactivity.getUseObject(), SignMembers.class).getPromotion_members().split(","));
            List<String> strings = relationLabelMapper.getCustomMemberIdAll2(proActivityDtoactivity.getSiteId(), labelNameArr);
            for (String id : strings) {
                Member member = memberMapper.selectByMemberIdAndSiteId(id, promotionsActivity.getSiteId());
                if (Objects.nonNull(member)) {
                    map.put(member.getMemberId(), member);
                }
            }
        }
        return map;
    }

    //计数商品数量
    public Map<Integer, Goods> statisticsGoodsCount(PromotionsActivity promotionsActivity, Map<Integer, Goods> map,
                                                    Map<Integer, TempDateModel> goodsJoinMethod, String goodIds) {
        if (goodsJoinMethod.containsKey(promotionsActivity.getId())) {
            Integer goodsIdType = goodsJoinMethod.get(promotionsActivity.getId()).getGoodsIdType();
            String goodsIds = goodsJoinMethod.get(promotionsActivity.getId()).getGoodsIds();
            List<String> strings = Arrays.asList(goodsIds.split(","));
            for (String s : goodsIds.split(",")) {
                switch (goodsIdType) {
                    //指定参加
                    case 1:
                        if (strings.contains(s)) {
                            Goods goods = goodsMapper.getBySiteIdAndGoodsId(Integer.parseInt(s), promotionsActivity.getSiteId());
                            if (Objects.nonNull(goods)) {
                                map.put(Integer.parseInt(s), goods);
                            }
                        } else {
                            return null;
                        }
                        break;
                    //指定不参加
                    case 2:
                        if (!strings.contains(goodIds)) {
                            Goods goods = goodsMapper.getBySiteIdAndGoodsId(Integer.parseInt(s), promotionsActivity.getSiteId());
                            if (Objects.nonNull(goods)) {
                                map.put(Integer.parseInt(s), goods);
                            }
                        } else {
                            return null;
                        }
                        break;
                    case 0:
                        Goods goods = goodsMapper.getBySiteIdAndGoodsId(Integer.parseInt(s), promotionsActivity.getSiteId());
                        if (Objects.nonNull(goods)) {
                            map.put(Integer.parseInt(s), goods);
                        }
                        break;
                    default:
                        throw new RuntimeException("未知的goodIdType类型");
                }
            }
        }
        return map;
    }

    public String resolver1(Integer goodsIdsType) {
        switch (goodsIdsType) {
            case 1:
                return "指定商品参加";
            case 2:
                return "指定商品不参加";
            case 0:
                return "全部商品参加此次活动";
            default:
                throw new RuntimeException("未知的goodIdsType");
        }
    }

    public String resolver2(Integer promotionType) {
        switch (promotionType) {
            case 10:
                return "满赠活动";
            case 20:
                return "打折活动";
            case 30:
                return "包邮活动";
            case 40:
                return "满减活动";
            case 50:
                return "限价活动";
            case 60:
                return "拼团活动";
            default:
                throw new RuntimeException("未知的promotionsType类型");
        }
    }

    public String resolver3(Integer type) {
        switch (type) {
            case 0:
                return "全部会员参加此次活动";
            case 1:
                return "指定标签组会员";
            case 2:
                return "指定会员";
            case 3:
                return "指定标签会员";
            default:
                throw new RuntimeException("未知的type类型");
        }
    }


    public VisitTask buildReturnData(PromotionsActivity promotionsActivity, Integer memberCount,
                                     Integer goodsCount, String joinObject, String promotionGoods, String promotionsType, LocalDateTime createTime) {
        VisitTask visitTask = new VisitTask();
        if (goodsCount == 0 || memberCount == 0) {
            visitTask = null;
        } else {
            visitTask.setPromotionName(promotionsActivity.getTitle());
            visitTask.setContainVisitGoodsNum(goodsCount);
            visitTask.setContainVisitMemberNum(memberCount);
            visitTask.setJoinObject(joinObject);
            visitTask.setPromotionGoods(promotionGoods);
            visitTask.setPromotionType(promotionsType);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            visitTask.setDateTime(dateTimeFormatter.format(createTime));
            visitTask.setPromotionId(promotionsActivity.getId());
        }
        return visitTask;
    }

    public String[] removeDuplicate(String s) {
        String[] arr = s.split(",");
        List list = Arrays.asList(arr);
        Set set = new HashSet(list);
        return (String[]) set.toArray(new String[0]);
    }

    public String resolveTime(LocalDateTime end, LocalDateTime now) {
        long hours = ChronoUnit.HOURS.between(now, end);
        if (hours < 24 && hours > 0) {
            return hours + "小时后结束";
        } else if (hours >= 24) {
            long days = ChronoUnit.DAYS.between(now, end);
            return days + "天后结束";
        }
        if (hours == 0) {
            long minutes = ChronoUnit.MINUTES.between(now, end);
            return minutes + "分钟后结束";
        }
        return null;
    }
}
