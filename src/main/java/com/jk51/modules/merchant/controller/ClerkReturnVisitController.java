package com.jk51.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Stores;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.clerkvisit.job.VisitSchedule;
import com.jk51.modules.clerkvisit.service.BVisitDescService;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.merchant.service.ClerkReturnVisitService;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 回访列表
 *
 * @auhter zy
 * @create 2017-12-12 15:09
 */
@Controller
@RequestMapping("/visit")
public class ClerkReturnVisitController {

    @Autowired
    ClerkReturnVisitService clerkReturnVisitService;

    @Autowired
    private PromotionsActivityService promotionsActivityService;

    @Autowired
    private CouponProcessUtils couponProcessUtils;

    @Autowired
    BVisitDescService bVisitDescService;

    @Autowired
    VisitSchedule visitSchedule;


    //商户后台查询回访列表
    @PostMapping("/queryVisitList")
    @ResponseBody
    public ReturnDto getClerkList(@RequestBody Map<String,Object> map) {
        Object siteId = map.get("siteId");
        if(Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }

        int page = (map.get("page") == null || "".equals(map.get("page")))?1:Integer.valueOf(map.get("page").toString());
        int pageSize = (map.get("pageSize") == null || "".equals(map.get("pageSize")))?15:Integer.valueOf(map.get("pageSize").toString());
        //开启分页
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> visitList = clerkReturnVisitService.getVisitList(map);
        if(visitList.size() == 0 || Objects.isNull(visitList)) {
            return ReturnDto.buildFailedReturnDto("没有列表记录!");
        }else {
            PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(visitList);
            Map<String,Object> resultMap = new HashedMap();
            resultMap.put("page",mapPageInfo.getPageNum());
            resultMap.put("pageSize",mapPageInfo.getPageSize());
            resultMap.put("items",visitList);
            resultMap.put("totalPages",mapPageInfo.getPages());   //总页数
            resultMap.put("total",mapPageInfo.getTotal());     //总记录数
            return ReturnDto.buildSuccessReturnDto(resultMap);
        }
    }

    /**
     * 修改商户回访状态
     * @param map
     * @return
     */
    @PostMapping("/changeMerchantStatus")
    @ResponseBody
    public ReturnDto changeVisitStatus(@RequestBody Map<String,Object> map) {
        String siteId = String.valueOf(map.get("siteId"));
        String ids = String.valueOf(map.get("idsList"));
        String[] idsList = ids.split(",");
        map.put("idsList",idsList);
        if(StringUtil.isEmpty(siteId)) {
            return ReturnDto.buildFailedReturnDto("商户ID不能为空!");
        }
        int result = clerkReturnVisitService.changeVisitStatus(map);
        clerkReturnVisitService.changeVisitStastatistics(map);
        if(result > 0) {
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("修改回访状态失败!");
        }
    }


    /**
     * 获取门店店员列表
     * @return
     */
    @RequestMapping(value = "/clerk/list",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getClerksList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(parameterMap.get("siteId"));
        if(StringUtil.isEmpty(siteId)) {
            return ReturnDto.buildFailedReturnDto("商户ID不能为空!");
        }
        List<Map<String,Object>> clerkList = clerkReturnVisitService.getMerchantClerkList(parameterMap);
        Map<String,Object> map = new HashedMap();
        map.put("items",clerkList);
        map.put("total",clerkList.size());
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 商户回访批量调配
     * @param map
     * @return
     */
    @RequestMapping(value = "/change/clerk",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto changeMerchantClerk(@RequestBody Map<String,Object> map) {
        String siteId = String.valueOf(map.get("siteId"));
        if(StringUtil.isEmpty(siteId)) {
            return ReturnDto.buildFailedReturnDto("商户ID不能为空!");
        }
        String[] clerkInfos = String.valueOf(map.get("clerkInfo")).split(",");
        String clerkId = clerkInfos[0];//店员ID
        String clerkName = clerkInfos[1];//店员姓名
        String storeId = clerkInfos[2];//门店ID
        String storeName = clerkInfos[3];//门店名称
        String[] userIdss = String.valueOf(map.get("userIds")).split(",");
        map.put("userIds",userIdss);
        map.put("clerkId",clerkId);
        map.put("clerkName",clerkName);
        map.put("storeId",storeId);
        map.put("storeName",storeName);
        Boolean result = clerkReturnVisitService.changeClerk(map);
        if(result) {
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("调配失败!");
        }
    }

    /**
     * 查询门店列表
     * @param request
     * @return
     */
    @PostMapping("/getMerchantStoreList")
    @ResponseBody
    public ReturnDto getStoreList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = parameterMap.get("siteId").toString();
        if(StringUtil.isEmpty(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        List<Map<String,Object>> storeList = clerkReturnVisitService.getStoreList(Integer.valueOf(siteId));
        if(Objects.nonNull(storeList) && storeList.size() > 0) {
            return ReturnDto.buildSuccessReturnDto(storeList);
        }else {
            return ReturnDto.buildFailedReturnDto("没有查询到门店列表!");
        }
    }

    /**
     * 创建回访任务
     * 获取商户后台所有 发布中(已开始) 发布中(未开始)的活动信息
     * 没传activityId就查所有活动名称
     * 传了就查当前活动的所有信息
     * @param map   siteId activityId
     * @return
     */
    @RequestMapping(value = "/getActivityInfo",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getMerchantAllActivitys(@RequestBody Map<String,Object> map) {
        Object siteId = map.get("siteId");
        if (Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        Object activityId = map.get("activityId");
        Integer param = null;
        if (Objects.nonNull(activityId)) {
            param = Integer.valueOf(activityId.toString());
        }
        //活动列表包含rule
        List<PromotionsActivity> releasePomotionsActivity = promotionsActivityService.findReleasePomotionsActivity(Integer.valueOf(siteId.toString()), param);
        //处理结果集
        if (Objects.nonNull(releasePomotionsActivity) && releasePomotionsActivity.size() > 0) {
            if (Objects.isNull(activityId)) {
                //活动名称和活动ID的集合
                List<Map<String, Object>> results = new ArrayList<>();
                releasePomotionsActivity.stream().forEach(pa -> {
                    Map<String, Object> params = new HashedMap();
                    params.put("activityName", pa.getTitle());
                    params.put("activityId", pa.getId());
                    results.add(params);
                });
                Map<String,Object> actList = new HashedMap();
                actList.put("items",results);
                actList.put("total",results.size());
                return ReturnDto.buildSuccessReturnDto(actList);
            }else {
                PromotionsActivity promotionsActivity = releasePomotionsActivity.get(0);
                Map<String, Object> results = new HashedMap();
                results.put("activityName", promotionsActivity.getTitle());//活动名称
                SignMembers signMembers = JSON.parseObject(promotionsActivity.getUseObject(), SignMembers.class);
                int type = signMembers.getType();
                //参与对象 类型和会员ID存储到隐藏域
                //0全部会员  1指定标签组会员 2 指定会员 3 指定标签会员
                switch (type) {
                    case 0: //0全部会员
                        results.put("memberType",0);
                        results.put("membersId","");//会员id
                        results.put("partInObject","全部会员");//参与对象
                        results.put("partInNumber","");//参与人数
                        break;
                    case 1: //指定标签组会员
                        Set<String> result = new HashSet<String>();
                        //查询会员标签集合并过滤不包含用户ID的对象
                        List<MemberLabel> labels = clerkReturnVisitService.queryMemberLabels(Integer.valueOf(siteId.toString()),signMembers.getPromotion_members().split(",")).stream().filter(memberLabel -> StringUtil.isNotEmpty(memberLabel.getScene())).collect(Collectors.toList());
                        Map<String, Object> stringMap = null;
//                        StringBuffer sb = new StringBuffer();
                        String userIds = "";
                        for (MemberLabel memberLabel : labels) {
                            stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                            userIds = (String) stringMap.get("userIds");
//                            sb.append(userIds+",");
                            result.addAll(Arrays.asList(userIds.split(",")));
                        }
                        results.put("memberType",1);
//                        results.put("membersId",result);
//                        results.put("membersId",sb.toString());
                        results.put("membersId",userIds);
                        results.put("partInObject",labels);//指定标签会员
                        results.put("partInNumber",result.size());
                        break;
                    case 2: //指定会员
                        String[] membersId = signMembers.getPromotion_members().split(",");
                        results.put("memberType",2);
//                        results.put("membersId",Arrays.asList(membersId));
                        results.put("membersId",signMembers.getPromotion_members());
                        results.put("partInNumber",membersId.length);
                        results.put("partInObject", "");//指定会员
                        break;
                    case 3://指定标签会员
                        String[] memberId = signMembers.getPromotion_members().split(",");
                        List<String> members = clerkReturnVisitService.queryMemberInfoById(Integer.valueOf(siteId.toString()),memberId);
                        String join = String.join(",", members);
                        results.put("memberType",3);
//                        results.put("membersId",Arrays.asList(membersId));
                        results.put("membersId",join);
                        results.put("partInNumber",members.size());
                        results.put("partInObject", "");
                        break;
                }
                //参与商品
                PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                Map<String, String> goodsIds = PromotionsRuleService.getGoodsIds(promotionsRule);
                String goodsIds1 = goodsIds.get("goodsIds");
                //goodsIdsType 0全部商品参加 1指定商品参加 2指定商品不参加
                int goodsIdsType = Integer.parseInt(goodsIds.get("goodsIdsType"));
                Map<String, Object> partInGoods = new HashedMap();
                switch (goodsIdsType) {
                    case 0:
                        partInGoods.put("goodsType",0);
                        partInGoods.put("goodsIds",goodsIds1);  //用于指定商品列表
                        partInGoods.put("goodsList",null);//全部商品不获取商品列表
                        partInGoods.put("total",0);//参加商品数量
                        results.put("goodsInfo",partInGoods);
                        break;
                    case 1:
                        String[] split1 = goodsIds1.split(",");
                        //根据id查询商品名称和现价
                        List<Map<String,Object>> goodsInfo1 = clerkReturnVisitService.queryGoodsInfoById(Integer.valueOf(siteId.toString()),split1,goodsIdsType);
                        partInGoods.put("goodsType",1);
                        partInGoods.put("goodsIds",goodsIds1);//参加商品ID
                        partInGoods.put("goodsList",goodsInfo1);//参加商品名称和现价
                        partInGoods.put("total",goodsInfo1.size());//参加商品数量
                        results.put("goodsInfo",partInGoods);
                        break;
                    case 2://指定商品不参加
                        String[] split2 = goodsIds1.split(",");
                        //结果是参与的商品
                        List<Map<String,Object>> goodsInfo2 = clerkReturnVisitService.queryGoodsInfoById(Integer.valueOf(siteId.toString()),split2,goodsIdsType);
                        partInGoods.put("goodsType",2);
                        partInGoods.put("goodsIds",goodsIds1);
                        partInGoods.put("goodsList",goodsInfo2);
                        partInGoods.put("total",goodsInfo2.size());//参加商品数量
                        results.put("goodsInfo",partInGoods);
                        break;
                }
                //适用门店
                String useStore = promotionsRule.getUseStore(); //-1全部 1具体门店 2指定区域
                if ("-1".equals(useStore)) {//全部门店
                    results.put("applyStoresType", -1);
                    results.put("applyStoresIds",promotionsRule.getUseArea());
                    results.put("applyStores", null);
                } else if ("1".equals(useStore)) {//具体门店
                    results.put("applyStoresType", 1);
                    String useArea = promotionsRule.getUseArea();
                    String[] split = useArea.split(",");
                    //获取门店编号,名称,支持服务
                    List<Map<String, Object>> storesInfo = clerkReturnVisitService.queryStoreInfo(Integer.valueOf(siteId.toString()), split);
                    results.put("applyStoresIds",useArea);
                    results.put("applyStores", storesInfo);
                } else if ("2".equals(useStore)) {//指定区域
                    results.put("applyStoresType", 2);
                    //获取cityId,根据cityId查询门店列表
                    String[] split = promotionsRule.getUseArea().split(",");
                    List<Stores> storesList = clerkReturnVisitService.queryStoreListByCityId(Integer.valueOf(siteId.toString()), split);
                    StringBuffer sb = new StringBuffer();
                    results.put("applyStoresIds",promotionsRule.getUseArea());
                    results.put("applyStores", storesList);
                }
                //活动有效时间
//                TimeRuleForPromotionsRule timeRuleForPromotionsRule = JSON.parseObject(promotionsRule.getTimeRule(), TimeRuleForPromotionsRule.class);
//                results.put("",promotionsActivity);//有效期
                results.put("activityStartTime",promotionsActivity.getStartTime());//活动时间开始
                results.put("activityEndTime",promotionsActivity.getEndTime());//活动时间结束
                return ReturnDto.buildSuccessReturnDto(results);
            }
        } else {
            return ReturnDto.buildFailedReturnDto("没有查询到活动记录!");
        }
    }

    /**
     * 查询回访任务活动列表
     * @param request
     * @return
     */
    @PostMapping("/getActivityList")
    @ResponseBody
    public ReturnDto getActivityList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String siteId = parameterMap.get("siteId").toString();
        Integer pageSize = Integer.valueOf(parameterMap.get("pageSize").toString());
        Integer page = Integer.valueOf(parameterMap.get("pageno").toString());

        //开启分页
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> activityList = clerkReturnVisitService.getActivityList(parameterMap);

        if(Objects.nonNull(activityList) && activityList.size() > 0) {
            PageInfo pageInfo = new PageInfo<>(activityList);
            return ReturnDto.buildSuccessReturnDto(pageInfo);
        }else {
            return ReturnDto.buildFailedReturnDto("没有查询到任务活动列表!");
        }

    }

    /**
     * 根据商品ID获取商品详情
     * 参与商品
     * @param request
     * @return
     */
    @PostMapping("/getPartInGoodsInfo")
    @ResponseBody
    public ReturnDto getPartInGoodsInfo(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        Object goodsIds = parameterMap.get("goodsIds");
        Object goodsType = parameterMap.get("goodsType");
        if(Objects.isNull(siteId) || Objects.isNull(goodsType)) {
            return ReturnDto.buildFailedReturnDto("商户ID或商品ID或商品类型不能为空!");
        }
        //如果是全部商品,goodsIds为all
        String [] gIds = null;
        if (!"0".equals(goodsType.toString())) {
            gIds = String.valueOf(goodsIds).split(",");
        }

        List<Map<String,Object>> goodsInfo = clerkReturnVisitService.queryGoodsInfoById(Integer.valueOf(siteId.toString()),gIds,Integer.parseInt(goodsType.toString()));
        Map<String,Object> map = new HashedMap();
        map.put("total",goodsInfo.size());
        map.put("goodsList",goodsInfo);
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 根据ID获取门店列表
     * 适用门店
     * @param request
     * @return
     */
    @RequestMapping("/getPIStoresList")
    @ResponseBody
    @SuppressWarnings("all")
    public ReturnDto getPartInStoresList(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        Object partInStoresType = parameterMap.get("partInStoresType");
        Object partInStoresIds = parameterMap.get("partInStoresIds");
        if(Objects.isNull(siteId) || Objects.isNull(partInStoresIds) || Objects.isNull(partInStoresType)) {
            return ReturnDto.buildFailedReturnDto("商户ID或门店或城市ID不也能为空!");
        }
        int i = Integer.parseInt(partInStoresType.toString());
        String[] split1 = partInStoresIds.toString().split(",");
        Map<String,Object> map = new HashedMap();
        switch (i) {
            case 1:
                //获取门店编号,名称,支持服务
                List<Map<String, Object>> storesInfo = clerkReturnVisitService.queryStoreInfo(Integer.valueOf(siteId.toString()), split1);
                storesInfo.stream().forEach(store -> {
                    Object service_support = store.get("service_support");
                    if(Objects.nonNull(service_support) && StringUtil.isNotEmpty(String.valueOf(service_support))) {
                        String[] split = service_support.toString().split(",");
                        StringBuffer sb = new StringBuffer();
                        for (int a = 0; a < split.length; a++ ) {
                            String s = split[a];
                            if("150".equals(s)) {
                                sb.append("送货上门");
                                sb.append(" , ");
                            }else if ("160".equals(s)) {
                                sb.append("门店自提");
                                sb.append(" , ");
                            }
                        }
                        sb.delete(sb.length()-3,sb.length());
                        store.put("service_support",sb.toString());
                    }
                });
                map.put("StoresList",storesInfo);
                map.put("total",storesInfo.size());
                break;
            case 2:
                //根据cityId获取门店列表
//                List<Stores> storesList = clerkReturnVisitService.queryStoreListByCityId(Integer.valueOf(siteId.toString()), split1);
                List<Map<String,Object>> storesList = clerkReturnVisitService.queryStoresByCityId(Integer.valueOf(siteId.toString()), split1);
                storesList.stream().forEach(store -> {
                    String service_support = store.get("service_support").toString();
                    if (StringUtil.isNotEmpty(service_support)) {
                        StringBuffer sb = new StringBuffer();
                        String[] split = service_support.split(",");
                        for (int a = 0; a < split.length; a++) {
                            String s = split[a];
                            if("150".equals(s)) {
                                sb.append("送货上门");
                                sb.append(" , ");
                            }else if ("160".equals(s)) {
                                sb.append("门店自提");
                                sb.append(" , ");
                            }
                        }
                        sb.delete(sb.length()-3,sb.length());
                        store.put("service_support",sb.toString());
                    }
                });
                map.put("StoresList",storesList);
                map.put("total",storesList.size());
                break;
            default:
                return ReturnDto.buildFailedReturnDto("门店ID类型不正确!");
        }
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 根据商品ID和购买天数查询符合的回访数
     * @param request
     * @return
     */
    @PostMapping("/queryAccordMember")
    @ResponseBody
    public ReturnDto getAccordCustomer(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        Object passDays = parameterMap.get("passDays");
        Object goodsIds = parameterMap.get("goodsIds");
        Object goodsType = parameterMap.get("goodsType");
        Object memberId = parameterMap.get("memberId");//参与对象ID
        if(Objects.isNull(siteId) || Objects.isNull(passDays) || Objects.isNull(goodsType)) {
            return ReturnDto.buildFailedReturnDto("参数不能为空!");
        }
        Map<String,Object> map = new HashedMap();
        //查询商品数量
        //goodsType 0全部商品参加 1指定商品参加 2指定商品不参加
        int i = Integer.parseInt(goodsType.toString());
        Integer goodsNum = 0;
        //0全部商品参加 1指定商品参加 2指定商品不参加
        switch (i) {
            case 0:
                //查询商户下商品数
                goodsNum = clerkReturnVisitService.queryAllGoodsNum(Integer.valueOf(siteId.toString()));
                break;
            case 1:
                if(Objects.nonNull(goodsIds)) {
                    goodsNum = goodsIds.toString().split(",").length;
                }
                break;
            case 2:
                if(Objects.nonNull(goodsIds)) {
                    goodsNum = clerkReturnVisitService.queryLeftGoodsNum(Integer.valueOf(siteId.toString()), goodsIds.toString().split(","));
                }
                break;
        }
        String[] goodsId = null;
        if (Objects.nonNull(goodsIds)) {
            goodsId = goodsIds.toString().split(",");
        }
        String[] memberIds = null;
        if(Objects.nonNull(memberId)) {
            memberIds = memberId.toString().split(",");
        }
        Integer accordNum = clerkReturnVisitService.queryCoincideCustomer(Integer.valueOf(siteId.toString()),goodsId , Integer.valueOf(passDays.toString()),Integer.valueOf(goodsType.toString()),memberIds);
        map.put("goodsNum",goodsNum);//选择商品的数量
        map.put("returnVisitNum",accordNum);
        return ReturnDto.buildSuccessReturnDto(map);
    }


    /**
     * 根据指定的商品ID和购买天数查询在指定对象范围内可创建的回访数
     * @param request
     * @return
     */
    @PostMapping("/queryAccordVisitNum")
    @ResponseBody
    public ReturnDto getAccordReturnVisitTask(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Object siteId = map.get("siteId");
        Object passDays = map.get("passDays");
        Object goodsIds = map.get("goodsIds");
        Object memberId = map.get("memberId");//参与对象ID 空表示全部会员
        if(Objects.isNull(siteId) || Objects.isNull(passDays) || Objects.isNull(goodsIds)) {
            return ReturnDto.buildFailedReturnDto("参数不能为空!");
        }

        Map<String,Object> resultMap = new HashedMap();
        //查询商品数量
        int goodsNum = goodsIds.toString().split(",").length;

        String[] goodsId = goodsIds.toString().split(",");
        String[] memberIds = null;
        if(Objects.nonNull(memberId)) {
            memberIds = memberId.toString().split(",");
        }
        Integer accordNum = clerkReturnVisitService.queryCoincideCustomerNum(Integer.valueOf(siteId.toString()),goodsId,Integer.valueOf(passDays.toString()),memberIds);
        map.put("goodsNum",goodsNum);//选择商品的数量
        map.put("returnVisitNum",accordNum);
        return ReturnDto.buildSuccessReturnDto(map);







    }

    /**
     * 查询所有会员数
     * @param request
     * @return
     */
    @PostMapping("/queryVisitNum")
    @ResponseBody
    public ReturnDto queryAccordMember(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        if(Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        Integer num = clerkReturnVisitService.queryMemberNum(Integer.valueOf(siteId.toString()));
        Map<String,Object> map = new HashedMap();
        map.put("returnVisitNum",num);
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 根据商品ID 分页获取列表
     * @param request
     * @return
     */
    @PostMapping("/getPIGoodsList")
    @ResponseBody
    @SuppressWarnings("all")
    public ReturnDto getPIGoodsList(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Object siteId = map.get("siteId");
        int pageNum = (map.get("curPage") == null || "".equals(map.get("curPage")))?1:Integer.valueOf(map.get("curPage").toString());
        int pageSize = (map.get("pageLength") == null || "".equals(map.get("pageLength")))?15:Integer.valueOf(map.get("pageLength").toString());
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> goodsList = clerkReturnVisitService.queryPIGoodsList(map);
        if(goodsList.size() == 0 || Objects.isNull(goodsList)) {
            return ReturnDto.buildFailedReturnDto("没有列表记录!");
        }else {
            PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(goodsList);
            Map<String,Object> resultMap = new HashedMap();
            resultMap.put("page",mapPageInfo.getPageNum());
            resultMap.put("pageSize",mapPageInfo.getPageSize());
            resultMap.put("items",goodsList);
            resultMap.put("totalPages",mapPageInfo.getPages());   //总页数
            resultMap.put("total",mapPageInfo.getTotal());     //总记录数
            return ReturnDto.buildSuccessReturnDto(resultMap);
        }
    }

    /**
     * 回访列表页面查看效果
     * @param request
     * @return
     */
    @PostMapping("/checkVisitResult")
    @ResponseBody
    public ReturnDto checkVisitResult(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);


        Map<String,Object> resultMap = bVisitDescService.checkVisitResult(parameterMap);

        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    /**
     * 根据siteId查询门店店员列表
     * @param request
     * @return
     */
    @PostMapping("/getMerchantStoreAdmins")
    @ResponseBody
    public ReturnDto getStores(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Object siteId = map.get("siteId");
        if(Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        int pageNum = (map.get("pageNum") == null || "".equals(map.get("pageNum")))?1:Integer.valueOf(map.get("pageNum").toString());
        int pageSize = (map.get("pageSize") == null || "".equals(map.get("pageSize")))?15:Integer.valueOf(map.get("pageSize").toString());
        String storeCode = String.valueOf(map.get("storeCode"));
        String clerkName = String.valueOf(map.get("clerkName"));
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> storeList = clerkReturnVisitService.getStoresBySiteId(Integer.valueOf(siteId.toString()),storeCode,clerkName);
        if(storeList.size() == 0 || Objects.isNull(storeList)) {
            return ReturnDto.buildFailedReturnDto("没有查询到店员列表记录!");
        }else {
            PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(storeList);
            Map<String,Object> resultMap = new HashedMap();
            resultMap.put("page",mapPageInfo.getPageNum());
            resultMap.put("pageSize",mapPageInfo.getPageSize());
            resultMap.put("items",storeList);
            resultMap.put("totalPages",mapPageInfo.getPages());   //总页数
            resultMap.put("total",mapPageInfo.getTotal());     //总记录数
            return ReturnDto.buildSuccessReturnDto(resultMap);
        }
    }


    /**
     * 创建回访任务
     * @param request
     * @return
     */
    @PostMapping("/createVisitTask")
    @ResponseBody
    public ReturnDto createReturnVisitTask(HttpServletRequest request) throws ParseException {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        if(Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空!");
        }
        /**
         * 参与回访的会员信息
         */
//        int partInType = Integer.parseInt(parameterMap.get("partInType").toString());
        //会员信息
        List<Map<String,Object>> memberIds = new ArrayList<>();
        Object memberId = parameterMap.get("memberId");//参与会员ID
        if("0".equals(memberId.toString())) {//所有会员
            memberIds = clerkReturnVisitService.queryMemberInfoBySiteId(Integer.valueOf(siteId.toString()),null);

        }else {
            //根据ID查询会员信息
            String[] split = memberId.toString().split(",");
            memberIds = clerkReturnVisitService.queryMemberInfoBySiteId(Integer.valueOf(siteId.toString()),split);
        }
/*        switch (partInType) {
            case 0://全部参与对象
//                int memberType = Integer.parseInt(pIObj.get("memberType").toString());

                break;
            case 1://过滤对象
                Object membId = parameterMap.get("memberId");
                Object passDays = parameterMap.get("passDays");
                Object goodsIds = parameterMap.get("goodsIdsStr");
                Object goodsType = parameterMap.get("goodsType");
                String[] membersId = null;
                if(Objects.nonNull(membId)) {
                    membersId = membId.toString().split(",");
                }
                if (Objects.isNull(goodsType)) {//指定的商品
                    String[] split = goodsIds.toString().split(",");
                    //查询符合条件的会员列表集合
                    memberIds = clerkReturnVisitService.queryAccordMember(Integer.valueOf(siteId.toString()),split , Integer.valueOf(passDays.toString()),1,membersId);
                }else {//活动商品
                    if("all".equals(goodsIds)) {//全部商品
                        memberIds = clerkReturnVisitService.queryAccordMember(Integer.valueOf(siteId.toString()),null , Integer.valueOf(passDays.toString()),Integer.valueOf(goodsType.toString()),membersId);
                    }else {
                        String[] split = goodsIds.toString().split(",");
                        memberIds = clerkReturnVisitService.queryAccordMember(Integer.valueOf(siteId.toString()),split , Integer.valueOf(passDays.toString()),Integer.valueOf(goodsType.toString()),membersId);
                    }

                }
                break;
        }*/

        /**
         * 回访任务时间
         */
        //回访时间类型
        int timeType = Integer.parseInt(parameterMap.get("timeType").toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //查询b_clerk_visit 待回访的
        List<BClerkVisit> visitList =  clerkReturnVisitService.queryVisitBySiteId(Integer.valueOf(siteId.toString()));
        Date da = null;
        switch (timeType) {
            case 0://活动开始前一天
                String returnVisitTime0 = String.valueOf(parameterMap.get("returnVisitTime"));
                //2018-1-10--2018-1-10
                String[] split = returnVisitTime0.split("--");
                String splitTime = split[0];
                //活动时间
                Date parse = dateFormat.parse(splitTime);
                Calendar ca = Calendar.getInstance();
                ca.setTime(parse);
                ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
                da = ca.getTime();
                String format2 = dateFormat.format(ca.getTime());
                visitList = visitList.stream().filter(bClerkVisit -> dateFormat.format(bClerkVisit.getVisitTime()).equals(format2) ).collect(Collectors.toList());
                break;
            case 1://创建后立即回访
                //获取当前时间
                Date d = new Date();
                da = d;
                String format1 = dateFormat.format(d);
                visitList = visitList.stream().filter(bClerkVisit -> dateFormat.format(bClerkVisit.getVisitTime()).equals(format1) ).collect(Collectors.toList());
                break;
            case 2://自定义时间
                String returnVisitTime2 = String.valueOf(parameterMap.get("returnVisitTime"));
                da = dateFormat.parse(returnVisitTime2);
                visitList = visitList.stream().filter(bClerkVisit -> dateFormat.format(bClerkVisit.getVisitTime()).equals(returnVisitTime2) ).collect(Collectors.toList());
                break;
        }

        /**
         * 店员信息
         */
        Object object = parameterMap.get("obj");
        List<Map<String,Object>> clerkList = JSON.parseObject(object.toString(), List.class);
//        List<Map<String,Object>>  clerkList = (List<Map<String,Object>>)object;
        Set<String> storeNum = new HashSet<>();//门店编号
        Set<String> clerkNum = new HashSet<>();//店员编号
        //店员和顾客对应列表
        List<Map<String,Object>> distributeTask = new ArrayList<>();
        Integer start = 0;//截取开始
        Integer end = 0;//截取结束
        for (Map<String,Object> map : clerkList) {
            Map<String,Object> info = new HashedMap();//存储店员和会员对应信息
            //storeAdminId+","+storeAdminName+","+storeName+","+storesNumber+","+mobile+","+storeId
            String value = String.valueOf(map.get("value"));
            String[] split = value.split(",");
            storeNum.add(split[3]);//门店编号
            clerkNum.add(split[0]);//店员ID
            //店员任务数
            Integer distributeNum = Integer.valueOf(map.get("distributeNum").toString());
            if (distributeNum != 0) {
                end = end + distributeNum;
                //分配会员ID到当前店员
                map.put("storeAminId",split[0]);
                map.put("storeAdminName",split[1]);
                map.put("storeId",split[5]);
                map.put("storeName",split[2]);
                map.put("memberId",memberIds.subList(start,end));
                map.put("adminMobile",split[4]);
                distributeTask.add(map);
                start += distributeNum;
//            end = end + distributeNum;
            }

        }

        //门店数
        Integer stores = storeNum.size();
        Integer clerks = clerkNum.size();

        //活动ID
        Object impotentIds = parameterMap.get("impotentIds");
        if(Objects.isNull(impotentIds)){
            impotentIds="0";
        }
        Object activityIds = parameterMap.get("activityIds");
        //回访名称
        Object actName = parameterMap.get("visitName");
        Object activityInfos = parameterMap.get("activityInfos");
        Object memberSource = parameterMap.get("memberSource");
        Object sumPerson = parameterMap.get("sumPerson");
        Object userName = parameterMap.get("userName");
        //总回访数
        //指定的商品ID
        String goodsIds = String.valueOf(parameterMap.get("goodsIds"));

/*        if(Objects.isNull(goodsType)) {//指定的商品ID
            goodsIds = goodsIdsStr1;
        }else {//全部商品ID
            //根据类型查询参与活动商品Id
            String[] gdsIds = null;
            if(Objects.nonNull(goodsIdsStr1) && !"all".equals(goodsIdsStr1)) {
                gdsIds = goodsIdsStr1.split(",");
            }
            List<String> goodsId = clerkReturnVisitService.queryGoodsIdsByType(Integer.valueOf(siteId.toString()),gdsIds,Integer.valueOf(goodsType.toString()));
            goodsIds = String.join(",", goodsId);
        }*/


        //新回访任务集合
        List<Map<String,Object>> tasks = new ArrayList<>();

        for (Map<String,Object> map : distributeTask) {//店员会员
            Object storeAminId = map.get("storeAminId");
            Object storeAdminName = map.get("storeAdminName");
            Object storeId = map.get("storeId");
            Object storeName = map.get("storeName");
            Object adminMobile = map.get("adminMobile");
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("memberId");
            if(Objects.nonNull(list) && list.size() > 0) {
                for(Map<String,Object> member : list) {
                    Map<String,Object> log = new HashedMap();
                    log.put("siteId",Integer.valueOf(siteId.toString()));
                    log.put("adminName",storeAdminName);
                    log.put("storeAdminId",storeAminId);
                    log.put("storeId",storeId);
                    log.put("storeName",storeName);
                    log.put("visitTime",da);
                    log.put("activityIds",activityIds);
                    //buyer_id buyerId, member_id memberId, mobile,name
                    log.put("buyerId",member.get("buyerId"));
                    log.put("buyerMobile",member.get("mobile"));
                    log.put("buyerName",member.get("name"));
                    log.put("adminMobile",adminMobile);
                    //根据顾客id查询符合条件的商品集合
//                    List<String> goodsId = clerkReturnVisitService.queryGoodsIds(Integer.valueOf(siteId.toString()),Integer.valueOf(member.get("buyerId").toString()),goodsIds.split(","),Integer.valueOf(parameterMap.get("passDays").toString()));
                    log.put("goodsIds",goodsIds);
                    tasks.add(log);
                }
            }

        }
        Map<String,Object> bvstatistics = new HashedMap();
        bvstatistics.put("siteId",siteId);
        bvstatistics.put("activityIds",activityIds);
        bvstatistics.put("impotentIds",impotentIds);
        bvstatistics.put("visitName",actName);
        bvstatistics.put("memberNum",memberIds.size());//应访会员数
        bvstatistics.put("storeNum",stores);
        bvstatistics.put("clerkNum",clerks);
        bvstatistics.put("memberSource",memberSource);
        bvstatistics.put("activityInfos",activityInfos);
        bvstatistics.put("memberSourceNum",sumPerson);
        bvstatistics.put("userName",userName);


        return clerkReturnVisitService.updateOrInsert(tasks,visitList,bvstatistics,timeType);
    }

    /**
     * 根据siteId获取门店列表
     * @return
     */
    @PostMapping("/getAllStoresBySiteId")
    @ResponseBody
    public ReturnDto getAllStoresBySiteId(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteId = parameterMap.get("siteId");
        if(Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        List<Map<String,Object>> stores = clerkReturnVisitService.getAllStoresBySiteId(Integer.valueOf(siteId.toString()));
        Map<String,Object> map = new HashedMap();
        map.put("items",stores);
        map.put("total",stores.size());
        return ReturnDto.buildSuccessReturnDto(map);
    }



}
