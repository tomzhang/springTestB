package com.jk51.modules.account.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.models.Finances;
import com.jk51.model.account.requestParams.QueryOrderBill;
import com.jk51.model.account.requestParams.QuerySettlementLog;
import com.jk51.model.account.requestParams.QueryStatement;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.mapper.FinancesMapper;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.controller.
 * author   :zw
 * date     :2017/2/14
 * Update   :
 */
@Controller
@RequestMapping("/settle_import_data")
public class SettlementController {

    private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);

    @Autowired
    private SettlementDetailService settlementDetailService;
    @Autowired
    private FinancesMapper financesMapper;

    @Autowired
    private TradesMapper tradesMapper;

    /**
     * 自动对账入口
     */
    public void batchAccountChecking() {
        settlementDetailService.batchAccountChecking(null);
    }

    /**
     * 微信银行明细
     *
     * @param
     * @param
     */
  /*  @RequestMapping(value = "/wechat_bank_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto weichat_bank_upload(@RequestParam MultipartFile file) {
        try {
            settlementDetailService.parseWechatBankFile(file);
        } catch (IOException e) {

            logger.info("解析错误" + e);
        } catch (Exception e) {
            ReturnDto.buildSystemErrorReturnDto();
            logger.info("系统出错" + e);
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }*/


  /*  @RequestMapping(value = "/ali_bank_upload", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto ali_bank_upload(@RequestParam MultipartFile file) {
        try {
            settlementDetailService.parseAliBankFile(file);
            System.out.println("==================");
        } catch (IOException e) {
            logger.info("解析错误" + e);
        } catch (Exception e) {
            ReturnDto.buildSystemErrorReturnDto();
            logger.info("系统出错" + e);
        }
        return ReturnDto.buildSuccessReturnDto("导入成功");
    }
*/
    /*@RequestMapping(value = "/toupload")
    public ModelAndView upload() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("account/testupload");
        return mav;

    }*/

    /**
     * 查询对账单详情
     */
    @RequestMapping(value="/query", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto query(HttpServletRequest request) {

        String siteId = request.getParameter("siteId");
        String financeNo = request.getParameter("financeNo");

        if (!StringUtils.isNotBlank(siteId)){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if (!StringUtils.isNotBlank(financeNo)){
            return ReturnDto.buildFailedReturnDto("financeNo为空");
        }

        Map<String,Object> map;
        try {
            map = financesMapper.findByFinancesNo(Integer.parseInt(siteId),financeNo);
            if (map == null){
                return ReturnDto.buildFailedReturnDto("对账单详情不存在");
            }
            Map<String,Object> data = financesMapper.findBeforeData(Integer.parseInt(siteId),map.get("create_time")+"");
            if (data == null){
                map.put("shangqijiezhuan",0);
            }else {
                map.put("shangqijiezhuan",Double.parseDouble(data.get("need_pay").toString())- Double.parseDouble(data.get("real_pay").toString()));
            }
            map.put("list",financesMapper.findStatisticById(Integer.parseInt(siteId),financeNo));
        } catch (Exception e) {
            logger.error("获取详情失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询详情出错");
        }
        return ReturnDto.buildSuccessReturnDto(map);
    }

    /**
     * 查询对账单
     */
    @RequestMapping(value="/queryDetail", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryDetail(QueryStatement queryStatement) {

        PageInfo<?> pageInfo;
        try {
            pageInfo = this.settlementDetailService.querySettlementDetail(queryStatement);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 更新账单状态
     */
    @RequestMapping(value="/updateChargeOff", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto updateChargeOff(QueryStatement queryStatement) {

        PageInfo<?> pageInfo;
        try {
            pageInfo = this.settlementDetailService.updateChargeOff(queryStatement);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }


    /**
     * 商家查询对账单
     */
    @RequestMapping(value="/queryDetailMerchant", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryDetailMerchant(QueryStatement queryStatement) {

        PageInfo<?> pageInfo;
        try {
            pageInfo = this.settlementDetailService.querySettlementDetailMerchant(queryStatement);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 查询订单对账单情况
     */
    @RequestMapping(value="/queryList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryList(@RequestBody QueryOrderBill queryOrderBill) {

        PageInfo<?> pageInfo;
        try {
            pageInfo = settlementDetailService.querySettlementList(queryOrderBill);
            pageInfo.getList();
        }catch (Exception e){
            logger.error("获取对账单情况失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单情况出错");
        }

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 结算记录
     */
    @RequestMapping(value="/queryLog", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto querySettlementLog(@RequestBody QuerySettlementLog log) {

        PageInfo<?> pageInfo;
        try {
            pageInfo = settlementDetailService.querySettlementLog(log);
        }catch (Exception e){
            logger.error("获取结算日志失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询结算日志出错");
        }

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 结算记录
     */
    @RequestMapping(value="/queryOrderSettleDetail", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryOrderSettleDetail(@RequestBody Map<String,Object> params) {
        PageInfo<?> pageInfo;
        try {
            pageInfo = settlementDetailService.queryOrderSettleDetail(params);
        }catch (Exception e){
            logger.error("获取结算日志失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询结算日志出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 根据finance_no修改对账单
     */
    @RequestMapping(value="/update", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto updateFinance(@RequestBody Finances finances) throws UnsupportedEncodingException {
        logger.info("aaaa修改对账状态，账单编号===>"+finances.getFinance_no());
        if (StringUtils.isBlank(finances.getFinance_no())){
            return ReturnDto.buildFailedReturnDto("finance_no 为空");
        }

        try {
            settlementDetailService.updateFinance(finances);
        }catch (Exception e){
            logger.error("修改失败,错误是:",e);
            return ReturnDto.buildFailedReturnDto("修改失败");
        }
        return ReturnDto.buildSuccessReturnDto("修改成功");
    }

    /**
     * 查询订单信息
     */
    @RequestMapping(value="/get_trades_list", method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<?> get_trades_list(@RequestBody Trades trades) {
        List<Map<String, Object>> list=null;
        try {
            PageHelper.startPage(trades.getPageNum(),trades.getPageSize());
            list = settlementDetailService.get_trades_list(trades);
            list.stream().forEach(l->{
                l.put("trades_id",l.get("trades_id")+"");
            });
        }catch (Exception e){
            logger.error("获取订单信息失败,错误是:",e);
            return null;
        }
        return new PageInfo<>(list);
    }

}
