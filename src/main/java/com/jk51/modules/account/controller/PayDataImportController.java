package com.jk51.modules.account.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.requestParams.PayDataImportParams;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.service.PayDataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 上传文件，Excel解析存入数据库
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/16-02-16
 * 修改记录 :
 * 现在导入的数据需要转成2007以上的excel版本 否则取读失败
 */
@RestController
@RequestMapping("/import_data")
public class PayDataImportController {

    private static final Logger logger = LoggerFactory.getLogger(PayDataImportController.class);

    @Autowired
    private PayDataImportService payDataImportService;

    @Autowired
    private PayDataImportMapper payDataImportMapper;

    /**
     * 支付宝的收款表
     * @param file
     * @param
     * @return
     */
    @RequestMapping(value = "/ali_pay_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto ali_pay_upload(@RequestParam MultipartFile file, Integer siteId) {
        try {
            payDataImportService.parsePayFile(file,siteId);
        } catch (IOException e) {

            logger.error("解析错误" + e);
            return ReturnDto.buildFailedReturnDto("解析失败,请检查导入数据是否正确，请确认是否为支付宝付款数据！");
        } catch (Exception e){
            logger.error("系统出错" + e);
            return ReturnDto.buildFailedReturnDto("系统出错");
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }
    /**
     * 支付宝退款表
     * @param file
     * @param
     * @return
     */
    @RequestMapping(value = "/ali_refund_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto ali_refund_upload(@RequestParam MultipartFile file, Integer siteId ) {
        try {
            payDataImportService.parseAliRefundFile(file,siteId);
        } catch (IOException e) {
            logger.error("解析错误" + e);
            return ReturnDto.buildFailedReturnDto("解析失败,请检查导入数据是否正确，请确认是否为支付宝退款数据！");
        } catch (Exception e){

            logger.error("系统出错" + e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }


    /**
     * 微信的收款表
     * @param file
     * @param
     * @return
     */
    @RequestMapping(value = "/wechat_pay_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto wechat_pay_upload(@RequestParam MultipartFile file, Integer siteId) {
        try{
            payDataImportService.parseWechatPayFile(file,siteId);
        }catch (IOException e) {
            logger.error("解析错误" + e);
            return ReturnDto.buildFailedReturnDto("解析失败,请检查导入数据是否正确，请确认是否为微信付款数据！");
        }catch (Exception e){

            logger.error("系统出错" + e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }
    /**
     * 微信的退款表
     * @param
     * @param
     */
    @RequestMapping(value = "/wechat_refund_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto weichat_refund_upload(@RequestParam MultipartFile file, Integer siteId) {
        try {
            payDataImportService.parseWechatRefundFile(file,siteId);
        } catch (IOException e) {
            logger.error("解析错误" + e);
            return ReturnDto.buildFailedReturnDto("解析失败,请检查导入数据是否正确，请确认是否为微信退款数据！");
        } catch (Exception e){
            logger.error("系统出错" + e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }

    /**
     * 支付宝付款划账
     * @param
     * @param
     */
    @RequestMapping(value = "/is_account_uploads", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto is_account_upload(@RequestParam MultipartFile file) {
        try {
            payDataImportService.parseIsAccountile(file);
        } catch (IOException e) {
            logger.error("解析错误" , e);
            return ReturnDto.buildFailedReturnDto("解析失败");
        } catch (Exception e){
            logger.error("系统出错" , e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }

    /**
     * 微信付款划账
     * @param file
     * @return
     */
    @RequestMapping(value="/wechat_account_bank",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto wechat_account_bank(@RequestParam MultipartFile file){
        try {
            payDataImportService.paresWechatAccountBank(file);
        } catch (IOException e) {
            logger.error("解析错误" + e);
            return ReturnDto.buildFailedReturnDto("解析失败");
        } catch (Exception e){
            logger.error("系统出错" + e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }

    @RequestMapping(value = "/toupload")
    public ModelAndView toupload() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("account/testupload");
        return mav;
    }

    /**
     * 查询支付记录的数据
     * @return
     */
    @RequestMapping(value = "/get_pay_data",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> queryPayList(PayDataImportParams payDataImport,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
        Map<String, Object> map = null;
        Page pageInfo = PageHelper.startPage(page, pageSize);//开启分页
        List<PayDataImport> list = payDataImportMapper.getPayLogList(payDataImport);
        map.put("items",list);
        map.put("page",pageInfo.toPageInfo());
        return map;
    }

    /**
     * 查询财务划账表的数据
     * @return
     */
//    @RequestMapping(value = "*",method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> queryRemitAcountList(
//                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
//                                                    @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
//        Map<String, Object> map = null;
//        Page pageInfo = PageHelper.startPage(page, pageSize);//开启分页
//        List<PayDataImport> list = payDataImportMapper.queryRemitAcountList(RemitAcountParams);
//        map.put("*",list);
//        map.put("*",pageInfo.toPageInfo());
//        return map;
//    }

    /**
     * 结算记录
     */
    @RequestMapping(value="/queryPayDataImport", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryPayDataImport(@RequestBody Map<String,Object> params) {
        PageInfo<?> pageInfo;
        try {
            pageInfo = payDataImportService.queryPayDataImport(params);
        }catch (Exception e){
            logger.error("查询倒入数据失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询倒入数据失败");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }
    /**
     * 修改对账状态
     */
    @RequestMapping(value="/update_check_status", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto update_check_status(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        try {

          //修改对账表状态
            int i=payDataImportService.updateCheckStatus(param);

          if (i==1)
              return ReturnDto.buildSuccessReturnDto("success!");
        }catch (Exception e){
            logger.error("修改对账状态失败,错误是",e);
            return ReturnDto.buildFailedReturnDto("查询倒入数据失败");
        }
        return ReturnDto.buildFailedReturnDto("failed!");
    }

}
