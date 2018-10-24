package com.jk51.modules.distribution.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.distribution.service.RecommendRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 18:10
 * 修改记录:
 */
@Controller
@RequestMapping("/recommend")
public class RecommendRewardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendRewardController.class);

    @Autowired
    private RecommendRewardService distributeMoneyRecordService;

    /**
     * 查询推荐人奖励列表页面数据
     * @param map
     * @return
     */
    @RequestMapping("/distributorMoneyList")
    @ResponseBody
    public Result getDistributorMoneyList(@RequestParam Map<String,Object> map){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getDistributorMoneyList(map));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+map.get("siteId")+"的分销商奖励分页数据查询失败",e);
            return new Result(Result.FAIL, 500,"查询失败", null);
        }

    }

    /**
     * 查询推荐人奖励列表页面数据
     * @param map
     * @return
     */
    @RequestMapping("/distributorMoneyDetailList")
    @ResponseBody
    public Result getDistributorMoneyDetailList(@RequestParam Map<String,Object> map){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getDistributorMoneyDetailList(map));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+map.get("siteId")+"的分销商奖励详情分页数据查询失败",e);
            return new Result(Result.FAIL, 500,"查询失败", null);

        }

    }

    /**
     * 查询推荐人奖励列表页面数据（未确认）
     * @param map
     * @return
     */
    @RequestMapping("/distributorMoneyDetailListUnconfirmed")
    @ResponseBody
    public Result getDistributorMoneyDetailListUnconfirmed(@RequestParam Map<String,Object> map){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getDistributorMoneyDetailListUnconfirmed(map));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+map.get("siteId")+"的分销商奖励详情分页数据查询失败",e);
            return new Result(Result.FAIL, 500,"查询失败", null);

        }

    }

    /**
     * 查询分销商开户行信息
     * @param siteId
     * @param distributorId
     * @return
     */
    @RequestMapping("/withdrawAccount")
    @ResponseBody
    public Result getWithdrawAccount(Long recordId, Integer siteId, Integer distributorId){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getWithdrawAccount(recordId,siteId,distributorId));
        }catch (NullPointerException e) {
            LOGGER.error("分销商金融账户查询失败，siteId:" + siteId +", distributorId:"+distributorId +", recordId:"+ recordId,e);
            return new Result(Result.FAIL, 500,"未匹配到分销商账户信息", e);
        }catch (Exception e) {
            LOGGER.error("分销商金融账户查询失败，siteId:" + siteId +",distributorId:"+distributorId  +", recordId:"+ recordId,e);
            return new Result(Result.FAIL, 500,"查询失败", e);
        }

    }

    /**
     * 财务更新处理状态
     * @param id
     * @param siteId
     * @param status
     * @return
     */
    @RequestMapping("/financeOperation")
    @ResponseBody
    public Result financeOperation(Long id,Integer siteId,Integer status,String remark){

        try {
            return new Result(Result.SUCCESS, 200,"处理成功", distributeMoneyRecordService.financeOperation(id,siteId,status,remark));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的财务处理分销商 （奖励，提现等）申请失败,recordId:"+id,e);
            return new Result(Result.FAIL, 500,"处理失败", null);
        }

    }

    /**
     * 根据siteId查询最小提现金额
     * @param siteId
     * @return
     */
    @RequestMapping("/getWithdrawMinMoney")
    @ResponseBody
    public Result getWithdrawMinMoney (Integer siteId){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getWithdrawMinMoney(siteId));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的提现额度查询失败",e);
            return new Result(Result.FAIL, 500,"查询失败", null);
        }

    }

    /**
     * 修改最小金额
     * @param siteId
     * @param minMoney
     * @return
     */
    @RequestMapping("/setWithdrawMinMoney")
    @ResponseBody
    public Result setWithdrawMinMoney (Integer siteId,Integer minMoney){
        try {
            return new Result(Result.SUCCESS, 200,"处理成功", distributeMoneyRecordService.setWithdrawMinMoney(siteId,minMoney));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的提现额度设置失败",e);
            return new Result(Result.FAIL, 500,"处理失败", null);
        }
    }

    /**
     * 确认订单奖励状态
     * @param siteId
     * @param id
     * @param rewardStatus
     * @return
     */
    @ResponseBody
    @RequestMapping("/confirmOrderRewardStatus")
    public Result confirmOrderRewardStatus(Integer siteId ,Integer distributorId, Integer id, Integer rewardStatus){

        try {
            return new Result(Result.SUCCESS, 200,"确认奖励成功", distributeMoneyRecordService.confirmOrderRewardStatus(siteId,distributorId,id,rewardStatus));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的确认奖励失败，id:"+ id,e);
            return new Result(Result.FAIL, 500,"确认奖励失败: " + e.getMessage(), null);
        }

    }

    /**
     * 批量确认订单奖励状态
     * @param siteId
     * @param ids
     * @param rewardStatus
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchConfirmOrderRewardStatus")
    public Result batchConfirmOrderRewardStatus(Integer siteId , String ids, Integer rewardStatus){
        try {
            return new Result(Result.SUCCESS, 200,"确认奖励成功", distributeMoneyRecordService.batchConfirmOrderRewardStatus(siteId,ids,rewardStatus));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的确认奖励失败，id:"+ ids,e);
            return new Result(Result.FAIL, 500,"确认奖励失败: " + e.getMessage(), null);
        }

    }

    /**
     * 查询分销商id
     * @param siteId
     * @param distributorName
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDistritorIdByUsername")
    public Result getDistritorIdByUsername(Integer siteId ,String distributorName){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getDistritorIdByUsername(siteId,distributorName));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的分销商id查询失败，distributorName:"+ distributorName,e);
            return new Result(Result.FAIL, 500,"查询失败", null);
        }

    }

    /**
     * 查询分销商账户总计
     * @param siteId
     * @param distributorId
     * @return
     */
    @ResponseBody
    @RequestMapping("/getReferrerAccountTotal")
    public Result getReferrerAccountTotal(Integer siteId ,Integer distributorId){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getReferrerAccountTotal(siteId,distributorId));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+siteId+"的分销商id账户总计查询失败，distributorId:"+ distributorId,e);
            return new Result(Result.FAIL, 500,"查询失败", e);
        }

    }

    /**
     * 获取分销商推荐订单
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRecommendOrderList")
    public Result getRecommendOrderList(@RequestParam Map<String,Object> param){

        try {
            return new Result(Result.SUCCESS, 200,"查询成功", distributeMoneyRecordService.getRecommendOrderDetailList(param));
        } catch (Exception e) {
            LOGGER.error("商户siteId:"+param.get("siteId")+"的分销商订单查询失败，distributorId:"+ param.get("distributorId"),e);
            return new Result(Result.FAIL, 500,"查询失败", null);
        }

    }
    
    @PostMapping("/myReward")
    @ResponseBody
    public ReturnDto getMyReward(HttpServletRequest request){
        Map<String,Object> paramMap = ParameterUtil.getParameterMap(request);
        Object siteId = paramMap.get("siteId");
        Object distributorId = paramMap.get("id");
        if(siteId ==null || "".equals(siteId)){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if(distributorId ==null || "".equals(distributorId)){
            return ReturnDto.buildFailedReturnDto("id为空");
        }
        
        Map<String,Object> map = distributeMoneyRecordService.getMyReward(Integer.parseInt(siteId.toString()),Integer.parseInt(distributorId.toString()));
        
        return ReturnDto.buildSuccessReturnDto(map);
    }

}
