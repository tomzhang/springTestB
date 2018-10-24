package com.jk51.modules.coupon.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.account.models.Finances;
import com.jk51.model.coupon.mqParams.SendCouponMq;
import com.jk51.modules.account.job.SchedulerTask;
import com.jk51.modules.account.service.ChargeOffService;
import com.jk51.modules.coupon.event.CouponEvent;
import com.jk51.modules.coupon.job.OldConvertNewCouponTask;
import com.jk51.modules.coupon.service.CouponNoEncodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.controller.
 * author   :zw
 * date     :2017/4/27
 * Update   :
 */
@RestController
@RequestMapping("couponTask")
public class CouponTaskController {

    @Autowired
    private SchedulerTask schedulerTask;

    @Autowired
    private ChargeOffService chargeOffService;

    @Autowired
    private OldConvertNewCouponTask oldConvertNewCouponTask;
    /*@Autowired
    private AccountCouponMq accountCouponMq;*/

    private static final Logger logger = LoggerFactory.getLogger(CouponTaskController.class);

    @RequestMapping(value = "/runTask/{siteId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto revampCouponRuleStatus(@PathVariable("siteId") Integer siteId) {

        if (siteId == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        oldConvertNewCouponTask.run(siteId);
        return ReturnDto.buildSuccessReturnDto("导入成功，详情见日志");
    }

    @RequestMapping(value = "/runRedis/{siteId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto runRedis(@PathVariable("siteId") Integer siteId) {

        if (siteId == null) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        oldConvertNewCouponTask.runRedis(siteId);
        return ReturnDto.buildSuccessReturnDto("导入成功，详情见日志");
    }

    @RequestMapping(value = "/testConnRedis", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto testConnRedis() {
        oldConvertNewCouponTask.testConnRedis();
        return ReturnDto.buildSuccessReturnDto("导入成功，详情见日志");
    }

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/testMq", method = RequestMethod.GET)
    @ResponseBody

    public ReturnDto testMq() {
 
        SendCouponMq sendCouponMq = new SendCouponMq();
        sendCouponMq.setActivityId(8);
        sendCouponMq.setSiteId(100073);
        sendCouponMq.setType(2);
        CouponEvent couponEvent = new CouponEvent(this, sendCouponMq);
     
        applicationContext.publishEvent(couponEvent);

 
       /* accountCouponMq.setSiteId(100073);
        accountCouponMq.setType(2);
        AccountEvent accountEvent = new AccountEvent(this, accountCouponMq);*/
       
        return ReturnDto.buildSuccessReturnDto("---------");
    }

    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    @RequestMapping(value = "/convert/{couponNo}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto convertCouponNo(@PathVariable("couponNo") long couponNo) {

       
        return ReturnDto.buildSuccessReturnDto(couponNo + "解密为：" + couponNoEncodingService.decryptionCouponNo(String.valueOf(couponNo)));
    }

    @RequestMapping(value = "/decode/{couponNo}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto convertDecodeCouponNo(@PathVariable("couponNo") long couponNo) {


        return ReturnDto.buildSuccessReturnDto(couponNo + "加密为：" + couponNoEncodingService.encryptionCouponNo(String.valueOf(couponNo)));
    }

    /**
     * 预出账
     * @param request
     * @return
     */
    @RequestMapping("/setDate")
    @ResponseBody
    public ReturnDto setDate(HttpServletRequest request) {

        logger.info("***********开始手工出账单*************");
//        Timestamp startTime=null;
        Timestamp endTime=null;
        Integer siteId=null;
        String operator=null;
        try{
            Map<String,Object> param = ParameterUtil.getParameterMap(request);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//            startTime=Timestamp.valueOf(param.get("startTime")+" 00:00:00");
            endTime=Timestamp.valueOf(param.get("endTime")+" 23:59:59");

            operator=param.get("operator")+"";


            siteId=Integer.parseInt(param.get("siteId").toString());
            return chargeOffService.classifiedAccount(siteId,endTime,operator);
            /*schedulerTask.generateBillingJob(date,siteId);*/
        } catch (Exception e) {
            logger.info("出账错误，错误信息：", e);
            return ReturnDto.buildFailedReturnDto("出账错误，请确认账单周期和商家编号是否正确！");
        }

    }

    /**
     * 重新核算账单
     * @param request
     * @return
     */
    @RequestMapping("/financesRecalculate")
    @ResponseBody
    public ReturnDto financesRecalculate(HttpServletRequest request) {

        logger.info("*****开始重新核算账单********");
        String operator=null;
        try{
            Map<String,Object> param = ParameterUtil.getParameterMap(request);
            String financesNo=param.get("financesNo")+"";
            if (null==financesNo||"".equals(financesNo)){
                return ReturnDto.buildFailedReturnDto("账单编号不能为空！");
            }
            Finances finances=chargeOffService.financesRecalculate(financesNo);
            if (null==finances){
                return ReturnDto.buildFailedReturnDto("账单编号不存在，请核对！");
            }
            return ReturnDto.buildSuccessReturnDto(finances);
        } catch (Exception e) {
            logger.info("核算账单错误，错误信息：", e);
            return ReturnDto.buildFailedReturnDto("核算账单错误，请确认账单编号是否正确！");
        }

    }

    /**
     * 重新核算账单
     * @param request
     * @return
     */
    @RequestMapping("/updateFinances")
    @ResponseBody
    public ReturnDto updateFinances(HttpServletRequest request) {

        String operator=null;
        try{
            Map<String,Object> param = ParameterUtil.getParameterMap(request);
            String financesNo=param.get("financesNo")+"";
            Finances finances=chargeOffService.updateFinances(financesNo);
            return ReturnDto.buildSuccessReturnDto(finances);
        } catch (Exception e) {
            logger.info("修改账单错误，错误信息：", e);
            return ReturnDto.buildFailedReturnDto("修改账单失败！");
        }

    }

}
