package com.jk51.modules.merchant.controller;

import com.alibaba.fastjson.JSONObject;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.merchant.MerchantApply;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.appInterface.util.SendSMS;
import com.jk51.modules.distribution.result.Resultful;
import com.jk51.modules.merchant.mapper.MerchantApplyMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.merchant.service.MerchantApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：商户自助申请开站
 * 作者: XC
 * 创建日期: 2018-09-13 14:21
 * 修改记录:
 **/
@RestController
@RequestMapping("/merchantApply")
public class MerchantApplyController {
    @Autowired
    private MerchantApplyService merchantApplyService;
    @Autowired
    private MerchantApplyMapper merchantApplyMapper;
    @Autowired
    private YbMerchantMapper merchantMapper;
    @Autowired
    private SendSMS sendSMS;


    private Logger logger = LoggerFactory.getLogger(MerchantApplyController.class);

    @PostMapping("")
    public Resultful addMerchantApply(String uphone, String merchant, String merchantExt, String merchantApply, String code) {
        Resultful result = new Resultful();
        try {
            int applicant_id = 0;
            if (uphone != null && !"".equals(uphone)) {
                if (merchantApplyService.getApplicant(uphone) != null) {
                    applicant_id = Integer.parseInt(merchantApplyService.getApplicant(uphone).toString());
                } else {
                    result.setCode(Resultful.FAILED);
                    result.setMsg("用户登录已失效，请重新登录");
                    return result;
                }
            } else {
                result.setCode(Resultful.FAILED);
                result.setMsg("用户登录已失效，请重新登录");
                return result;
            }
            YbMerchant ybMerchant = JSONObject.parseObject(merchant, YbMerchant.class);
            String rcode = merchantApplyService.getApplyCode(ybMerchant.getLegal_mobile());
            if (rcode == null) {
                result.setCode(Resultful.FAILED);
                result.setMsg("验证码已失效，请重新获取");
                return result;
            } else if (!rcode.equals(code)) {
                result.setCode(Resultful.FAILED);
                result.setMsg("验证码错误，请重新输入");
                return result;
            }
            MerchantExtTreat merchantExtTreat = JSONObject.parseObject(merchantExt, MerchantExtTreat.class);
            MerchantApply merchantApply1 = JSONObject.parseObject(merchantApply, MerchantApply.class);
            merchantApplyService.applyMercahnt(applicant_id, ybMerchant, merchantExtTreat, merchantApply1);
            merchantApplyService.SendEmailTo51(ybMerchant.getMerchant_name());
            result.setCode(Resultful.SUCCESS);
            result.setMsg("申请成功");
        } catch (Exception e) {
            result.setCode(Resultful.FAILED);
            result.setMsg("申请失败");
            logger.error("商户申请失败，错误信息：{}", ExceptionUtil.exceptionDetail(e));
        }
        return result;
    }

    @PostMapping("/selectApply")
    public Resultful getAllMerchantApply(@RequestParam(required = false, value = "company_name") String company_name,
                                         @RequestParam(required = false, value = "legal_name") String legal_name,
                                         @RequestParam(required = false, value = "legal_mobile") String legal_mobile,
                                         @RequestParam(required = false, value = "approval_status") String approval_status,
                                         @RequestParam(required = true, defaultValue = "1") int pageNum,
                                         @RequestParam(required = false, defaultValue = "15") int pageSize) {
        Resultful result = new Resultful();
        try {
            result.setData(merchantApplyService.selectApplys(company_name, legal_name, legal_mobile, approval_status, pageNum, pageSize));
            result.setCode(Resultful.SUCCESS);
            result.setMsg("操作成功！");
        } catch (Exception e) {
            result.setCode(Resultful.FAILED);
            result.setMsg("操作失败！");
            logger.error("查询商户申请列表失败，报错信息：{}", ExceptionUtil.exceptionDetail(e));
        }
        return result;
    }

    @GetMapping("/{merchant_id}")
    public Resultful getMerchantApplyByMerchantId(@PathVariable int merchant_id) {
        Resultful result = new Resultful();
        try {
            result.setData(merchantApplyMapper.getByMerchantId(merchant_id));
            result.setCode(Resultful.SUCCESS);
            result.setMsg("操作成功！");
        } catch (Exception e) {
            result.setCode(Resultful.FAILED);
            result.setMsg("操作失败！");
            logger.error("查询商户申请详情失败，报错信息：{}", ExceptionUtil.exceptionDetail(e));
        }
        return result;
    }

    @PostMapping("approvalAllow")
    public Resultful approvalAllow(Integer merchant_id, Integer approval_status, String reason) {
        Resultful result = new Resultful();
        if (approval_status != 0 && approval_status != 2) {
            result.setCode(Resultful.FAILED);
            result.setMsg("操作失败,参数异常！");
            logger.error("参数异常：调用修改商户申请状态approvalAllow方法发生异常：参数approval_status值或者类型不匹配");
            return result;
        }
        try {
            Map<String, String> map = merchantApplyService.approvalAllow(merchant_id, approval_status, reason);
            merchantApplyService.SendEmailToMerchant(merchant_id, map);
            result.setCode(Resultful.SUCCESS);
            result.setMsg("操作成功！");
        } catch (Exception e) {
            result.setCode(Resultful.FAILED);
            result.setMsg("操作失败！");
            logger.error("修改商户申请状态失败，报错信息：{}", ExceptionUtil.exceptionDetail(e));
        }
        return result;
    }

    @PostMapping("getApplyCode")
    public Integer getApplyCode(String phone, String code) {
        return sendSMS.sendSMS(0, phone, code, 180);
    }

    @PostMapping("update")
    public Integer update(MerchantApply merchantApply) {
        int result = 0;
        try {
            merchantApply.setUpdate_time(new Date());
            merchantApplyService.update(merchantApply);
        } catch (Exception e) {
            logger.error("申请材料更新失败,错误信息：{}",ExceptionUtil.exceptionDetail(e));
            result = -1;
        }
        return result;
    }

    @PostMapping("/signContract")
    public Integer signContract(Integer merchant_id, Integer is_sign, String sign_contract){
        int result = 0;
        try {
            merchantApplyService.signContract(merchant_id, is_sign,sign_contract);
        } catch (Exception e) {
            logger.error("签署合同失败,错误信息：{}",ExceptionUtil.exceptionDetail(e));
            result = -1;
        }
        return result;
    }
}
