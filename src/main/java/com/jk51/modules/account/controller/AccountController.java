package com.jk51.modules.account.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateFormatConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.map.MapUtils;
import com.jk51.model.account.requestParams.*;
import com.jk51.model.order.Refund;
import com.jk51.modules.account.mapper.FinancesMapper;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * filename :com.jk51.modules.account.controller.
 * author   :zw
 * date     :2017/3/11
 * Update   :
 */
@RestController
@RequestMapping("account")
public class AccountController {

    private static final Logger logger =  LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;
    @Autowired
    private SettlementDetailMapper settlementDetailMapper;
    @Autowired
    private FinancesMapper financesMapper;
    @Autowired
    private RefundMapper RefundMapper;
    @Autowired
    private  TradesMapper tradesMapper;

    @RequestMapping(value = "/account_detail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> accountDetail(@RequestBody AccountParams accountParams) throws Exception {
        //select_type; //搜索类别 by_order:根据订单 by_store:根据门店 by_user 根据店员
        Map<String,Object> accountParamss= MapUtils.objectToMap(accountParams);
        List<Map<String, Object>> list = null;
        if (accountParams.getSelect_type().equals("by_order")) {
            PageHelper.startPage(accountParams.getPageNum(), accountParams.getPageSize(), accountParams.isCount());//开启分页
            list = settlementDetailAndTradesMapper.getSettlementList(accountParamss);
        } else if (accountParams.getSelect_type().equals("by_store")) {
            PageHelper.startPage(accountParams.getPageNum(), accountParams.getPageSize(), accountParams.isCount());//开启分页
            list = settlementDetailAndTradesMapper.getStoreSettlementListByObjs(accountParamss);
        } else if (accountParams.getSelect_type().equals("by_user")) {
            Page<Object> page = PageHelper.startPage(accountParams.getPageNum(), accountParams.getPageSize(), true);//开启分页
            list = settlementDetailAndTradesMapper.getClerkSettlementListByObjs(accountParamss);
        }
        PageInfo<?> pageInfo = new PageInfo<>(list);
        Map<String, Object> map = new HashedMap();
        List list1 = pageInfo.getList();
        list1.stream().forEach(l -> {
            Map<String, Object> m = (Map<String, Object>) l;
            BigDecimal yingshouxiaoji=null;
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            dfs.setGroupingSeparator(',');
            dfs.setMonetaryDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("###,###.##", dfs);
            String  lastRealPay="---";
            Object trades_id = m.get("trades_id");
            Object daishoujine=m.get("daishoujine");
            Object daituijine=m.get("daituijine");
            Object daishoushouxufei=m.get("daishoushouxufei");
            Object jiaoyiyongjin=m.get("jiaoyiyongjin");
            Object daishoupeisongfei=m.get("daishoupeisongfei");
            Number parse = 0;
            Number parse1 = 0;
            Number parse2 = 0;
            Number parse3 = 0;
            Number parse4 = 0;
            try {
                if (daishoujine!=null &&(!daishoujine.equals("---") )){
                    parse = df.parse(daishoujine+"");
                }
                if (daituijine!=null&&(!daituijine.equals("---"))){
                    parse1 = df.parse(daituijine+"");
                }
                if (daishoushouxufei!=null&&(!daishoushouxufei.equals("---"))){
                    parse2 = df.parse(daishoushouxufei+"");
                }
                if (jiaoyiyongjin!=null&&(!jiaoyiyongjin.equals("---"))){
                    parse3 = df.parse(jiaoyiyongjin+"");
                }
                if (daishoupeisongfei!=null&&(!daishoupeisongfei.equals("---"))){
                    parse4 = df.parse(daishoupeisongfei+"");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Object pay_style=m.get("pay_style");

            //查询退款表退款金额
            Refund refund=RefundMapper.getRefundByTradeId(Integer.parseInt(m.get("site_id").toString()),m.get("trades_id").toString());
            //判断支付类型，线下类型没有退款金额
            if (null==pay_style||"cash".equals(pay_style.toString())||"health_insurance".equals(pay_style.toString())){
                m.put("real_refund_money",0);
            }else {
                m.put("real_refund_money",refund==null?0:refund.getRealRefundMoney());
            }
            if(null!=daishoujine&&StringUtils.equalsIgnoreCase(daishoujine+"","---")){
                lastRealPay="---";
            }

            if(null!=daituijine&&!StringUtils.equalsIgnoreCase(daituijine+"","---")){
                yingshouxiaoji=new BigDecimal(parse.toString()).subtract(new BigDecimal(parse1.toString()));
                lastRealPay=yingshouxiaoji.toString();
            }

            if(null!=daishoushouxufei&&!StringUtils.equalsIgnoreCase(daishoushouxufei+"","---")
                    &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse2.toString()));
                lastRealPay=yingshouxiaoji.toString();
            }

            if(null!=jiaoyiyongjin&&!StringUtils.equalsIgnoreCase(jiaoyiyongjin+"","---")
                    &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse3.toString()));
                lastRealPay=yingshouxiaoji.toString();
            }

            if(null!=daishoupeisongfei&&!StringUtils.equalsIgnoreCase(daishoupeisongfei+"","---")
                    &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal(parse4.toString()));
                lastRealPay=yingshouxiaoji.toString();
            }
            m.put("trades_id", trades_id + "");
            m.put("lastRealPay", lastRealPay + "");

        });
        map.put("items", list1);
        map.put("page", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        //如果不需要统计总行数,总页数不返回参数
        if (accountParams.isCount()) {
            map.put("pages", pageInfo.getPages());
            map.put("total", pageInfo.getTotal());
        }
        return map;
    }
    @RequestMapping(value = "/account_exception", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> accountException(@RequestBody AccountException accountException){
        PageHelper.startPage(accountException.getPageNum(), accountException.getPageSize(), true);//开启分页
        List<AccountException> accountByException = settlementDetailAndTradesMapper.getAccountByException(accountException);
        Map<String, Object> map = new HashedMap();
        PageInfo<?> pageInfo = new PageInfo(accountByException);
        map.put("items",pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("pages", pageInfo.getPages());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @RequestMapping(value = "/account_run", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> accountRun(@RequestBody AccountRun accountRun){
        List<Map<String, Object>> list = null;
        Long total =settlementDetailAndTradesMapper.getAccountRunTotal(accountRun);
        Page<Object> page = PageHelper.startPage(accountRun.getPageNum(), accountRun.getPageSize(), false);//开启分页
        page.setTotal(total);
        list = settlementDetailAndTradesMapper.getAccountRun(accountRun);
        list.stream().forEach(item -> {
           item.put("trades_id",item.get("trades_id").toString()) ;
            Map<String, Object> remitMoney = settlementDetailAndTradesMapper.getRemitMoney(item.get("trades_id").toString());
            if (remitMoney==null){
                    item.put("remit_shouxu_fee",0);
            }else {
                item.put("remit_shouxu_fee",remitMoney.get("remit_shouxu_fee"));
            }
            if (remitMoney==null){
                item.put("remit_refund_fee",0);
            }else {
                item.put("remit_refund_fee",remitMoney.get("remit_refund_fee"));
            }
        });
        ((Page) list).setTotal(total);
        Map<String, Object> map = new HashedMap();
        PageInfo<?> pageInfo = new PageInfo(list);
        map.put("items",pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("pages", pageInfo.getPages());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @RequestMapping(value = "/account_collect", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> accountCollect(@RequestBody AccountCollect accountCollect){
//        PageHelper.startPage(accountCollect.getPageNum(), accountCollect.getPageSize(), true);//开启分页
        int count = settlementDetailAndTradesMapper.getAccountCollectCount(accountCollect);
        int pageNum= count%accountCollect.getPageSize()==0?count/accountCollect.getPageSize():count/accountCollect.getPageSize()+1;
        List<Map<String, Object>> list = settlementDetailAndTradesMapper.getAccountCollect((accountCollect.getPageNum()-1)*accountCollect.getPageSize(), accountCollect.getPageSize(), accountCollect);   //商家汇总
        Map<String, Object> map = new HashedMap();
        PageInfo<?> pageInfo = new PageInfo(list);
        map.put("items",pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("pages",pageNum);
        map.put("total",count);
        return map;
    }

    /*财务划账表分页以及传参数方法*/
    @RequestMapping(value = "/account_remit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> accountRemit(@RequestBody AccountRemit accountRemit) throws Exception{
        PageHelper.startPage(accountRemit.getPageNum(), accountRemit.getPageSize(), true);//开启分页
        Map<String,Object> accountRemitMap= MapUtils.objectToMap(accountRemit);
        List<Map<String, Object>> list = settlementDetailAndTradesMapper.getAccountRemit(accountRemitMap);   //划账明细sql语句
        List<Map<String, Object>> statics = settlementDetailAndTradesMapper.getAccountRemitStatic(accountRemit);   //划账明细的合计
        if (null!=list&&!list.isEmpty()){
            list.get(0).put("static_real_fee",statics.get(0).get("real_pay")); //订单付款金额
            list.get(0).put("static_income_fee",statics.get(0).get("income_amount")); //划账收入金额
            list.get(0).put("static_refundpay_fee",statics.get(0).get("refund_pay_fee")); //划账退款金额
            list.get(0).put("static_shouxu_fee",statics.get(0).get("remit_shouxu_fee")); //划账手续费
            list.get(0).put("static_refund_fee",statics.get(0).get("refund_fee")); //划账退费
            list.get(0).put("static_remit_fee",statics.get(0).get("remit_fee")); //划账金额
        }
        list.stream().forEach(item ->{
            item.put("trades_id",item.get("trades_id")+"");
        });     //遍历 List 中的 trades_id 转换为 string
        Map<String, Object> map = new HashedMap();
        PageInfo<?> pageInfo = new PageInfo<>(list);
        map.put("items",pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("pages", pageInfo.getPages());
        map.put("total", pageInfo.getTotal());
        return map;
    }


    /*财务划账汇总表分页以及传参数方法*/
    @RequestMapping(value = "/account_remitsum", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> accountRemitSum(@RequestBody AccountRemit accountRemit){
        Map<String,Object>  stastic = settlementDetailAndTradesMapper.getAccountRemitStastic(accountRemit);//统计sql语句

        PageHelper.startPage(accountRemit.getPageNum(), accountRemit.getPageSize(), true);//开启分页
        List<Map<String, Object>> list = settlementDetailAndTradesMapper.getAccountRemitSum(accountRemit); //划账汇总sql语句
        Map<String, Object> map = new HashedMap();
        PageInfo<?> pageInfo = new PageInfo<>(list);
        map.put("items",pageInfo.getList());
        map.put("pageNum", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("pages", pageInfo.getPages());
        map.put("total", pageInfo.getTotal());
        map.put("stastic",stastic);
        return map;
    }

    @RequestMapping(value = "/detail/{tradesId}", method = RequestMethod.GET)
    @ResponseBody
    public Object accountDetailById(@PathVariable("tradesId") String tradesId) {

        Map<String, Object> map;
        try {
            map = settlementDetailAndTradesMapper.findSettlementDetailById(tradesId);
        } catch (Exception e) {
            logger.error("查询账单明细详情失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询账单明细详情失败");
        }

        return ReturnDto.buildSuccessReturnDto(map);
    }

    @RequestMapping(value = "/detail/{siteId}/{tradesId}", method = RequestMethod.GET)
    @ResponseBody
    public Object findTradeDetail(@PathVariable("tradesId") String tradesId, @PathVariable("siteId") Integer siteId) {

        if (!StringUtils.isNotBlank(tradesId)) {
            return ReturnDto.buildFailedReturnDto("tradesId为空");
        }

        if (siteId == null) {
            logger.error("siteId为空");
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = settlementDetailMapper.findDetails(tradesId, siteId);
        Object trades_id = map.get("trades_id");
        map.put("trades_id", trades_id + "");
        result.put("map", map);
        result.put("name", settlementDetailMapper.findUserNameById(tradesId, siteId));

        return result;
    }

    /**
     * 查询账单明细详情 51jk后台
     *
     * @param tradesId
     * @param siteId
     * @return
     */
    @RequestMapping(value = "/settlemnet/detail/{siteId}/{tradesId}", method = RequestMethod.GET)
    @ResponseBody
    public Object findTradeDetailByTradeId(@PathVariable("tradesId") String tradesId, @PathVariable("siteId") Integer siteId) throws Exception {

        if (!StringUtils.isNotBlank(tradesId)) {
            return ReturnDto.buildFailedReturnDto("tradesId为空");
        }

        if (siteId == null) {
            logger.error("siteId为空");
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        Map<String, Object> result = financesMapper.findSettlementDetailByTradeId(siteId, tradesId);
        if (result == null) {
            return ReturnDto.buildFailedReturnDto("详情为空为空");
        }
        result.put("realPay",new DecimalFormat().parse(result.get("realPay").toString()).doubleValue());
        result.put("refundMoney",new DecimalFormat().parse(result.get("refundMoney").toString()).doubleValue());
        result.put("shouxufei",new DecimalFormat().parse(result.get("shouxufei").toString()).doubleValue());
        result.put("yongjin",new DecimalFormat().parse(result.get("yongjin").toString()).doubleValue());
        result.put("peisongfei",new DecimalFormat().parse(result.get("peisongfei").toString()).doubleValue());
        result.put("tradesId",result.get("tradesId").toString());
        String str = JacksonUtils.mapToJsonWithNullValues(result);
        return ReturnDto.buildSuccessReturnDto(str);
    }

    @RequestMapping(value = "/officialAccount/{seller_id}", method = RequestMethod.GET)
    @ResponseBody
    public Object officialAccount(@PathVariable("seller_id") String seller_id) {
        if (!StringUtils.isNotBlank(seller_id)) {
            return ReturnDto.buildFailedReturnDto("seller_id为空");
        }
        Map<String,Object> result = financesMapper.getOfficialAccount(seller_id);
        if(result == null){
            return ReturnDto.buildFailedReturnDto("没有找到数据");
        }
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 查询财务余额核对集合
     * @param param
     * @return
     */
    @RequestMapping(value = "/find_finances_balance")
    @ResponseBody
    public PageInfo<?> findFinancesBalance(@RequestBody Map param) {
            if (param.get("startDate")==null){
                param.put("startDate", "0000-00-00");
            }
        if (param.get("endDate")==null){
            param.put("endDate",DateUtils.formatDate(new Date(),"yyyy-MM-dd"));
        }

        logger.info("查询财务余额请求参数====>{}",param);

        if(param.get("pageNum")!=null && param.get("pageSize")!=null) {
            PageHelper.startPage(Integer.parseInt(param.get("pageNum")+""),Integer.parseInt(param.get("pageSize")+""), true);
        }
        List<Map<String,String>> financesBalances= financesMapper.findStatisticFinancesBalance(param);
        List<Map<String,String>> statics1 = financesMapper.getFinancesBalanceStatic(param);   // 财务余额核对的合计
        if (null!=financesBalances&&!financesBalances.isEmpty()){
            financesBalances.get(0).put("static_remit_qichu",statics1.get(0).get("remit_qichu")); // 划账期初余额
            financesBalances.get(0).put("static_remit_daishou",statics1.get(0).get("remit_daishou")); // 代收客户款
            financesBalances.get(0).put("static_finances_daifu",statics1.get(0).get("finances_daifu")); // 代付客户款
            financesBalances.get(0).put("static_finances_qichu",statics1.get(0).get("finances_qichu")); // 账单期初余额
        }
        PageInfo<?> pageInfo = new PageInfo<>(financesBalances);
        return pageInfo;
    }


}
