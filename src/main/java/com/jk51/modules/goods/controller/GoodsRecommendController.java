package com.jk51.modules.goods.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.goods.mapper.GoodsRecommendMapper;
import com.jk51.modules.goods.service.GoodsRecommendService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Administrator on 2018/6/29.
 */
@RestController
@RequestMapping("/merchant")
public class GoodsRecommendController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GoodsRecommendService goodsRecommendService;
    @Autowired
    private GoodsRecommendMapper goodsRecommendMapper;

    //增加需求，推荐商品关联下单商品ids------start
    @RequestMapping("/goods/updateRecommend")
    public ReturnDto updateRecommend(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object recommendIdObj = paramMap.get("recommendId");
        if (Objects.isNull(recommendIdObj) || StringUtils.isBlank(recommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("关联商品推荐id不能为空！");
        Object orderGoodsIdsObj = paramMap.get("orderGoodsIds");
        /*if (Objects.isNull(orderGoodsIdsObj) || StringUtils.isBlank(orderGoodsIdsObj.toString()))
            return ReturnDto.buildFailedReturnDto("关联下单商品ids不能为空！");*/
        Integer flag = goodsRecommendMapper.updateRecommend(paramMap);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("flag", flag);
        if (0 < flag){
            resultMap.put("orderGoodsIds", orderGoodsIdsObj);
        }else {
            resultMap.put("orderGoodsIds", null);
            resultMap.put("message", "添加关联下单商品失败！");
        }
        return ReturnDto.buildSuccessReturnDto(resultMap);
    }
    //增加需求，推荐商品关联下单商品ids------end
    /**
     * 增加关联推荐商品------
     * 若已经增加的关联推荐商品中，有某个推荐商品的状态是关闭的状态下，
     * 这个时候可以再次新增推荐商品中状态为关闭的商品进行推荐，
     * 但是，再次新增成功之后且状态为启用，这个时候，在修改之前状态为关闭的推荐商品为开启状态时，
     * 是不允许修改的，因为关联推荐商品，在启用状态下，有且只有一个唯一的关联推荐商品，不允许重复推荐关联推荐商品
     * @param request
     * @return
     */
    @RequestMapping("/goods/addGoodsRecommend")
    public ReturnDto addGoodRecommend(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object goodsRecommendIdObj = paramMap.get("goodsRecommendId");
        if (Objects.isNull(goodsRecommendIdObj) || StringUtils.isBlank(goodsRecommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品id不能为空！");
        Object sceneObj = paramMap.get("scene");
        if (Objects.isNull(sceneObj) || StringUtils.isBlank(sceneObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐场景scene不能为空！");
        Object weightObj = paramMap.get("weight");
        if (Objects.isNull(weightObj) || StringUtils.isBlank(weightObj.toString())){
            return ReturnDto.buildFailedReturnDto("权重设置weight不能为空！");
        }
        Object goodsRecommendHashObj = paramMap.get("goodsRecommendHash");
        if (Objects.isNull(goodsRecommendHashObj) || StringUtils.isBlank(goodsRecommendHashObj.toString())){
            return ReturnDto.buildFailedReturnDto("推荐图片goodsRecommendHash不能为空！");
        }
        Object statusObj = paramMap.get("status");
        if (Objects.isNull(statusObj) || StringUtils.isBlank(statusObj.toString())){
            return ReturnDto.buildFailedReturnDto("推荐商品状态status不能为空！");
        }
        //添加推荐商品之前，先判断是否已存在该推荐商品且该推荐商品是启用状态下的
        Map<String,Object> countMap = goodsRecommendMapper.getGoodsRecommendByGoodsId(paramMap);
        Object countNumObj = countMap.get("countNum");
        if (Objects.isNull(countNumObj) || 0 == Integer.parseInt(countNumObj.toString()) || "0".equals(countNumObj.toString())){
            Integer flag = goodsRecommendMapper.addGoodsRecommend(paramMap);
            resultMap.put("flag", flag);
            if (flag > 0)
                resultMap.put("message", "操作成功！");
            else
                resultMap.put("message", "操作失败！");
        }else {
            resultMap.put("flag", 0);
            resultMap.put("message", "该推荐商品已存在，且状态为启用状态！请推荐其他商品！");
        }
        return ReturnDto.buildSuccessReturnDto(resultMap);
    }
    /**
     * 获取关联推荐商品列表
     * @param request
     * @return
     */
    @RequestMapping("/goods/getGoodsRecommendList")
    @ResponseBody
    public ReturnDto getGoodsRecommendList(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Map<String, Object> resultMap = goodsRecommendService.getGoodsRecommendList(paramMap);
        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    /**
     * flag=1:关闭推荐是商品(即更新b_goods_recommend表的status字段值为0)，
     * flag为空：查看推荐商品详情
     * @param request
     * @return
     */
    @RequestMapping("/goods/getGoodsRecommendDetail")
    public ReturnDto getGoodsRecommendDetail(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object goodsRecommendIdObj = paramMap.get("goodsId");
        if (Objects.isNull(goodsRecommendIdObj) || StringUtils.isBlank(goodsRecommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("查看推荐商品id不能为空！");
        Object flag = paramMap.get("flag");
        if (Objects.nonNull(flag) && 1 == Integer.parseInt(flag.toString())){//推荐商品 列表页面 关闭 推荐商品状态
            Integer countNum = goodsRecommendMapper.updateGoodsRecommend(paramMap);
            return ReturnDto.buildSuccessReturnDto(countNum);
        }else {
            Map<String, Object> resultMap = goodsRecommendMapper.getGoodsRecommendDetail(paramMap);
            return ReturnDto.buildSuccessReturnDto(resultMap);
        }
    }

    //推荐商品 某个推荐商品的详情页面数据
    //修改详情之前，先判断其状态是否为启用状态
    //若为启用状态，则可以进行更新
    //若为关闭状态，则查找并判断是否已经再次新增该推荐商品且状态为开启的状态，
    //若查找有，则不允许更新详情，
    //若查找没有，则允许更新详情
    /**
     * 查看 关联推荐商品 是 修改 关联推荐商品操作
     * 若之前推荐商品状态为关闭状态，且又已新添加了该推荐商品记录，
     * 则这时，修改该推荐商品的状态为启用状态时，是不允许修改的，因为该推荐商品已经被再次添加新纪录了，且状态为启用状态
     * @param request
     * @return
     */
    @RequestMapping("/goods/updateGoodsRecommend")
    public ReturnDto updateGoodsRecommend(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
//        Map<String, Object> resultMap = new HashMap<String, Object>();
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object goodsRecommendIdObj = paramMap.get("goodsRecommendId");
        if (Objects.isNull(goodsRecommendIdObj) || StringUtils.isBlank(goodsRecommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品id不能为空！");
        Object sceneObj = paramMap.get("scene");
        if (Objects.isNull(sceneObj) || StringUtils.isBlank(sceneObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐场景scene不能为空！");
        Map<String, Object> resultMap = goodsRecommendService.updateGoodsRecommendDetail(paramMap);

        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    /**
     * 删除关联推荐商品图片
     * @param request
     * @return
     */
    @RequestMapping("/goods/deleteGoodsRecommendImg")
    public ReturnDto deleteGoodsRecommendImg(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object goodsIdObj = paramMap.get("goodsId");
        if (Objects.isNull(goodsIdObj) || StringUtils.isBlank(goodsIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品id不能为空！");
        Object imgHashObj = paramMap.get("imgHash");
        if (Objects.isNull(imgHashObj) || StringUtils.isBlank(imgHashObj.toString()))
            return ReturnDto.buildFailedReturnDto("图片hash值不能为空！");
        Integer flag = goodsRecommendMapper.deleteGoodsRecommendImg(paramMap);
        resultMap.put("flag", flag);
        return ReturnDto.buildSuccessReturnDto(resultMap);
    }
    //APP下单页
    // 记录店员信息（店员id或店员手机号），会员信息（会员id或会员手机号）
    //推荐商品是针对会员的手机号
    // id, site_id, shop_id, shop_mobile, member_id, member_mobile, goods_id,
    //跟据b_goods_recommend表中的weight权重字段进行排序，并推荐weight权重字段值最高的商品进行推荐，若权重值相等则随机推荐
    //判断推荐商品中，是否包含下单的商品，若不包含，则直接推荐，若包含，则重新推荐另一个商品
    /**
     * APP下单页，关联商品推荐
     * 增加需求（8-16），若关联下单商品下，存在关联的推荐商品，则优先推荐该下单商品下的推荐商品
     * @param request
     * @return
     */
    @RequestMapping("/goods/getAppGoodsRecommend")
    public ReturnDto getAppGoodsRecommend(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("APP下单页，siteId不能为空！");
        Object goodsIdsObj = paramMap.get("goodsIds");
        if (Objects.isNull(goodsIdsObj) || StringUtils.isBlank(goodsIdsObj.toString()))
            return ReturnDto.buildFailedReturnDto("APP下单页，下单商品goodsIds不能为空！");
        //判断会员当天是否已经被推荐过该商品了（会员手机号和会员id不能同时为空）
        Object mobileObj = paramMap.get("mobile");
        Object userIdObj = paramMap.get("userId");
        if ((Objects.isNull(mobileObj) || StringUtils.isBlank(mobileObj.toString())) && (Objects.isNull(userIdObj) || StringUtils.isBlank(userIdObj.toString())))
            return ReturnDto.buildFailedReturnDto("APP下单页，会员手机号和会员id不能同时为空！");
        ReturnDto startOfGoodsRecommendList = goodsRecommendService.getStartOfGoodsRecommendList2(paramMap);
//        ReturnDto startGoodsRecommend = goodsRecommendService.getStartOfGoodsRecommendList(paramMap);
        return startOfGoodsRecommendList;
    }
    //关于浏览量的问题，另写一个接口，点击推荐商品，跳转商品详情页时，调此接口
    //根据推荐商品的id，进行浏览量加一更新操作，日浏览量，判断当前日期是否大于更新的日期，若不大于，则日浏览量加一，若大于，则日浏览量清零并加一
    /**
     * APP下单页，关联商品浏览量增加接口
     * @param request
     * @return
     */
    @RequestMapping("/goods/updateDailyBrowseOfAppGoodsRecommend")
    public ReturnDto updateDailyBrowseOfAppGoodsRecommend(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object recommendIdObj = paramMap.get("recommendId");
        if (Objects.isNull(recommendIdObj) || StringUtils.isBlank(recommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品recommendId不能为空！");
        LocalDate localDate = LocalDate.now();
        paramMap.put("localDate", localDate.toString());
        Integer flag = goodsRecommendMapper.updateDailyBrowseOfAppGoodsRecommend(paramMap);
        if (0 < flag)
            return ReturnDto.buildSuccessReturnDto(flag);
        return ReturnDto.buildFailedReturnDto("推荐商品日浏览量更新失败！");
    }
    //下单后，生成推荐商品的相应记录
    //该记录包括，店员信息、会员信息、商品信息
    /**
     * APP下单页，关联推荐商品记录接口
     * @param request
     * @return
     */
    @RequestMapping("/goods/insertGoodsRecommendRecords")
    public ReturnDto insertGoodsRecommendRecords(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object goodsRecommendIdObj = paramMap.get("goodsRecommendId");
        if (Objects.isNull(goodsRecommendIdObj) || StringUtils.isBlank(goodsRecommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品goodsRecommendId不能为空！");
        Object userIdObj = paramMap.get("userId");
        if (Objects.isNull(userIdObj) || StringUtils.isBlank(userIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("会员userId不能为空！");
        Object storeAdminIdObj = paramMap.get("storeAdminId");
        if (Objects.isNull(storeAdminIdObj) || StringUtils.isBlank(storeAdminIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("店员storeAdminId不能为空！");
        Object storeIdObj = paramMap.get("storeId");
        if (Objects.isNull(storeIdObj) || StringUtils.isBlank(storeIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("门店storeId不能为空！");
        Object recommendIdObj = paramMap.get("recommendId");
        if (Objects.isNull(recommendIdObj) || StringUtils.isBlank(recommendIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("商品推荐recommendId不能为空！");
        Object storeAdminphoneObj = paramMap.get("storeAdminphone");
        if (Objects.isNull(storeAdminphoneObj))
            paramMap.put("storeAdminphone", "");
        Object mobileObj = paramMap.get("mobile");
        if (Objects.isNull(mobileObj))
            paramMap.put("mobile", "");
        //根据信息进行插入操作
        Integer flag = goodsRecommendMapper.insertGoodsRecommendRecords(paramMap);
        if (0 < flag)
            return ReturnDto.buildSuccessReturnDto(flag);
        return ReturnDto.buildFailedReturnDto("生成推荐商品相关记录失败！");
    }
    //APP下单后，若用户购买了推荐的商品，这调用此接口
    @RequestMapping("/goods/updateBTradesBySiteIdAndTradesId")
    public ReturnDto updateBTradesBySiteIdAndTradesId(HttpServletRequest request){
        Map<String, Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = paramMap.get("siteId");
        if (Objects.isNull(siteIdObj) || StringUtils.isBlank(siteIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        Object tradesIdObj = paramMap.get("tradesId");
        if (Objects.isNull(tradesIdObj) || StringUtils.isBlank(tradesIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("tradesId不能为空！");
        Object goodsIdObj = paramMap.get("goodsId");
        if (Objects.isNull(goodsIdObj) || StringUtils.isBlank(goodsIdObj.toString()))
            return ReturnDto.buildFailedReturnDto("推荐商品id不能为空！");
        Integer flag = goodsRecommendMapper.updateBTradesBySiteIdAndTradesId(paramMap);
        if (0 < flag)
            return ReturnDto.buildSuccessReturnDto(flag);
        logger.info("更新订单相关记录失败！");
        return ReturnDto.buildFailedReturnDto("更新订单相关记录失败！");
    }



}
