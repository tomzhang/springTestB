package com.jk51.modules.appInterface.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BTrades;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.appInterface.mapper.*;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.appInterface.util.StatisticsDate;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.merchant.mapper.WebPageMapper;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-09
 * 修改记录:
 */
@Service
public class StatisticsService {

    @Autowired
    private UsersService usersService;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;
    @Autowired
    private BMemberInfoMapper bMemberInfoMapper;
    @Autowired
    private BTradesMapper bTradesMapper;
    @Autowired
    private YbTaskHasStoreMapper ybTaskHasStoreMapper;
    @Autowired
    private YbTaskMapper ybTaskMapper;
    @Autowired
    private BMemberMapper bMemberMapper;
    @Autowired
    private BOrdersMapper bOrdersMapper;

    @Autowired
    private WebPageMapper webPageMapper;


    //日销售统计
    public Map<String, Object> todaystatistics(String accessToken) {

        Map<String, Object> result = new HashMap<String, Object>();
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        StoreAdminExt storeAdminex = storeAdminextMapper.selectByPrimaryKey(authToken.getStoreUserId(), authToken.getSiteId());
        if (StringUtil.isEmpty(storeAdminex) || StringUtil.isEmpty(storeAdminex.getClerk_invitation_code())) {
            result.put("status", "ERROR");
            result.put("errorMessage", "店员信息错误！");
            return result;
        }

        String invitation_code = storeAdminex.getClerk_invitation_code().replaceAll("\\d+\\_", "");

        //查询日注册数量
        Integer registe_num = getRegisteNum(invitation_code, DateUtils.getToday(), authToken.getSiteId());

        //查询每天的销售额
        //Integer trades_num = getTradesNum(authToken.getStoreAdminId(), DateUtils.getToday(),authToken.getSiteId());
        Integer trades_num = bTradesMapper.todaySalesByStores(authToken.getSiteId(), authToken.getStoreId());//门店今日销售笔数 --> 送货上门：待收货，已完成；   门店自提：已自提；   门店直购：已完成。

        //查询每天的下单数量
        Integer orders_number = getOrdersNumber(authToken.getStoreId(), authToken.getStoreAdminId(), DateUtils.getToday(), authToken.getSiteId());//送货上门订单量

        //查询每天的门店直购数量
        Integer other_number = getOtherNumber(authToken.getSiteId(), authToken.getStoreId(), DateUtils.getToday());

        //查询每天的门店自提数量
        Integer pick_number = getPickNumber(authToken.getStoreId(), DateUtils.getToday(), authToken.getSiteId());

        //任务数量
        Integer tasks_number = getTasksNumber(authToken.getStoreId(), authToken.getSiteId());

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("registerNumber", StringUtil.isEmpty(registe_num) ? 0 : registe_num);
        results.put("tradesNumber", StringUtil.isEmpty(trades_num) ? 0 : trades_num);
        results.put("ordersNumber", StringUtil.isEmpty(orders_number) ? 0 : orders_number);
        results.put("otherNumber", StringUtil.isEmpty(other_number) ? 0 : other_number);
        results.put("pickNumber", StringUtil.isEmpty(pick_number) ? 0 : pick_number);
        results.put("tasksNumber", StringUtil.isEmpty(tasks_number) ? 0 : tasks_number);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }

    /**
     * 日销售统计 v2
     *
     * @param accessToken
     * @return
     */
    public Map<String, Object> todaystatistics2(String accessToken) {

        Map<String, Object> result = new HashMap<String, Object>();
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        StoreAdminExt storeAdminex = storeAdminextMapper.selectByPrimaryKey(authToken.getStoreUserId(), authToken.getSiteId());
        if (StringUtil.isEmpty(storeAdminex) || StringUtil.isEmpty(storeAdminex.getClerk_invitation_code())) {
            result.put("status", "ERROR");
            result.put("errorMessage", "店员信息错误！");
            return result;
        }

        String invitation_code = storeAdminex.getClerk_invitation_code().replaceAll("\\d+\\_", "");

        //待办事项
        //我的待送货x笔 --> 指定店员的待送货上门的订单数 --> 待收货
        Integer orders_number = getOrdersNumber(authToken.getStoreId(), authToken.getStoreAdminId(), DateUtils.getToday(), authToken.getSiteId());
        //门店待付款x笔 --> 门店范围内，所有通过门店助手下的处于代付款状态的直购订单数。 --> 待付款
        Integer other_number = getOtherNumber(authToken.getSiteId(), authToken.getStoreId(), DateUtils.getToday());
        //门店待自提x笔 --> 门店范围内所有待自提的订单数量 --> 待自提
        Integer pick_number = getPickNumber(authToken.getStoreId(), DateUtils.getToday(), authToken.getSiteId());


        //统计
        //门店今日销售笔数 --> 送货上门：待收货，已完成；   门店自提：已自提；   门店直购：已完成。
        Integer trades_num = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, "date");
        //我的今日销售笔数 --> 使用我的邀请码 下的 送货上门订单：待收货，已完成；    使用我的邀请码下的门店自提：已自提；    使用我的邀请码下的门店直购：已完成。
        Integer tasks_number = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, "date");
        //我的今日注册x人 --> 店员个人今日注册的会员数量，包含以下场景：1，在微商城注册时填写店员邀请码注册成功的会员 2，在门店助手注册成功的会员。3.通过门店后台/商户后台使用店员邀请码注册的会员。
        Integer registe_num = getRegisteNum(invitation_code, DateUtils.getToday(), authToken.getSiteId());


        Map<String, Object> results = new HashMap<String, Object>();
        results.put("registerNumber", StringUtil.isEmpty(registe_num) ? 0 : registe_num);
        results.put("tradesNumber", StringUtil.isEmpty(trades_num) ? 0 : trades_num);
        results.put("ordersNumber", StringUtil.isEmpty(orders_number) ? 0 : orders_number);
        results.put("otherNumber", StringUtil.isEmpty(other_number) ? 0 : other_number);
        results.put("pickNumber", StringUtil.isEmpty(pick_number) ? 0 : pick_number);
        results.put("tasksNumber", StringUtil.isEmpty(tasks_number) ? 0 : tasks_number);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }


    //任务数量
    private Integer getTasksNumber(int storeId, int siteId) {

        List<Integer> taskIdList = ybTaskHasStoreMapper.getTaskId(storeId, siteId);
        if (StringUtil.isEmpty(taskIdList) || taskIdList.size() == 0) {
            return 0;
        }
        return ybTaskMapper.CountTasksNumber(taskIdList);
    }

    //门店自提数量
    private Integer getPickNumber(int storeId, String time, int siteId) {
        return bTradesMapper.getCountPickNumber(storeId, time, siteId);
    }

    //门店直购数量
    private Integer getOtherNumber(int siteId, int storeId, String time) {
        return bTradesMapper.getCountOtherNumber(siteId, storeId, time);
    }

    //下单数量 送货员 送货订单
    private Integer getOrdersNumber(int storeId, int storeUserId, String time, int siteId) {
        return bTradesMapper.getCountOrderNum(storeId, storeUserId, time, siteId);
    }

    //销售额(单位为分)
    private Integer getTradesNum(int storeUserId, String time, int siteId) {
        return bTradesMapper.getSumRealPay(storeUserId, time, siteId);
    }

    //注册数量
    public Integer getRegisteNum(String invitation_code, String time, int siteId) {
        return bMemberInfoMapper.findCountRegistNum(invitation_code, time, siteId);
    }

    //查询所有的注册数量
    //time为null时查询所有时间的注册统计
    public Map<String, Object> registercount(String accessToken, String time) {

        Map<String, Object> result = new HashMap<String, Object>();

        //获取InvitationCode,如果status存在并且等于"ERROR",返回错误信息
        Map<String, Object> msp = getInvitationCode(accessToken);
        if (!StringUtil.isEmpty(msp) && !StringUtil.isEmpty(msp.get("status")) && msp.get("status").equals("ERROR")) {
            return msp;
        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        String invitation_code = (String) msp.get("invitation_code");

        //查询所有注册数量
        int totalNumber = getRegisteNum(invitation_code, time, authToken.getSiteId());
        int monthNumber = getRegisteNum(invitation_code, DateUtils.getMonth(), authToken.getSiteId());
        int dayNumber = getRegisteNum(invitation_code, DateUtils.getToday(), authToken.getSiteId());

        Map<String, Object> results = new HashMap<String, Object>();
        Map<String, Object> registe_num = new HashMap<String, Object>();
        registe_num.put("totalNumber", StringUtil.isEmpty(totalNumber) ? 0 : totalNumber);
        registe_num.put("monthNumber", StringUtil.isEmpty(monthNumber) ? 0 : monthNumber);
        registe_num.put("dayNumber", StringUtil.isEmpty(dayNumber) ? 0 : dayNumber);

        results.put("registerNumber", registe_num);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }


    /**
     * 注册列表(给定时间的)
     */
    public Map<String, Object> registerdaylist(String accessToken, String date) {

        Map<String, Object> result = new HashMap<String, Object>();
        if (!StringUtil.checkDateFormat(date, "(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", date + " 格式不符合\"yyyy-MM-dd\"！");
            return result;
        }

        //获取InvitationCode,如果status存在并且等于"ERROR",返回错误信息
        Map<String, Object> msp = getInvitationCode(accessToken);
        if (!StringUtil.isEmpty(msp) && !StringUtil.isEmpty(msp.get("status")) && msp.get("status").equals("ERROR")) {
            return msp;
        } else {

        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }


        Map<String, Object> results = new HashMap<String, Object>();
        List<Map<String, Object>> bmembers = getRegisterList((String) msp.get("invitation_code"), date, authToken.getSiteId());
        results.put("bmembers", bmembers);
        result.put("status", "OK");
        result.put("results", results);
        return result;
    }

    //查询注册信息
    private List<Map<String, Object>> getRegisterList(String invitation_code, String date, int siteId) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        List<SBMemberInfo> bMemberInfoList = bMemberInfoMapper.findMemberInfoList(invitation_code, date, siteId);
        if (StringUtil.isEmpty(bMemberInfoList) || bMemberInfoList.size() == 0) {
            Map<String, Object> bmember = new HashMap<String, Object>();
            bmember.put("mobile", "");
            bmember.put("createTime", "");
            result.add(bmember);
            return result;
        }

        for (SBMemberInfo info : bMemberInfoList) {
            List<Map<String, Object>> registerInfoList = bMemberMapper.findRegisterInfo(info.getMember_id(), siteId);
            if (!StringUtil.isEmpty(registerInfoList) && registerInfoList.size() > 0) {

                Map<String, Object> registerInfo = registerInfoList.get(0);
                Timestamp timestamp = (Timestamp) registerInfo.get("create_time");
                String createTime = DateUtils.formatDate(timestamp, "yyyy-MM-dd HH:mm:ss");
                registerInfo.put("createTime", createTime);
                result.add(registerInfo);
            }
        }

        return result;
    }

    public Map<String, Object> tradescount(String accessToken) {

        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //日销售数量
        //Integer todaySaleNum = getSaleNum(authToken.getStoreAdminId(), DateUtils.getToday(),authToken.getSiteId());
        Integer todaySaleNum = bTradesMapper.todaySalesByStores(authToken.getSiteId(), authToken.getStoreId());//门店今日销售笔数 --> 送货上门：待收货，已完成；   门店自提：已自提；   门店直购：已完成。

        //日销售额
        //Integer todaySaleCount = getSaleAmount(authToken.getStoreAdminId(), DateUtils.getToday(),authToken.getSiteId());
        Integer todaySaleCount = bTradesMapper.todaySalesRealPayByStores(authToken.getSiteId(), authToken.getStoreId());

        //月销售数量
        //Integer monthSaleNum = getSaleNum(authToken.getStoreAdminId(), DateUtils.getMonth(),authToken.getSiteId());
        Integer monthSaleNum = bTradesMapper.monthSalesByStores(authToken.getSiteId(), authToken.getStoreId());

        //月销售额
        //Integer monthSaleAmount = getSaleAmount(authToken.getStoreAdminId(), DateUtils.getMonth(),authToken.getSiteId());
        Integer monthSaleAmount = bTradesMapper.monthSalesRealPayByStores(authToken.getSiteId(), authToken.getStoreId());

        //累计销售数量
        //Integer saleCount = getSaleNum(authToken.getStoreAdminId(),null,authToken.getSiteId());
        Integer saleCount = bTradesMapper.salesByStores(authToken.getSiteId(), authToken.getStoreId());

        //累计销售额
        //Integer amount = getSaleAmount(authToken.getStoreAdminId(),null,authToken.getSiteId());
        Integer amount = bTradesMapper.salesRealPayByStores(authToken.getSiteId(), authToken.getStoreId());

       /* //ios要的数据结构

        results.put("todaySaleNum",StringUtil.isEmpty(todaySaleNum)?0:todaySaleNum);
        results.put("todaySaleCount",StringUtil.isEmpty(todaySaleCount)?0:todaySaleCount);
        results.put("monthSaleNum",StringUtil.isEmpty(monthSaleNum)?0:monthSaleNum);
        results.put("monthSaleCount",StringUtil.isEmpty(monthSaleAmount)?0:monthSaleAmount);
        results.put("saleCount",StringUtil.isEmpty(saleCount)?0:saleCount );
        results.put("amount",StringUtil.isEmpty(amount)?0:amount );*/

        //安卓要的数据结构
        Map<String, Object> results = new HashMap<String, Object>();
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("todaySaleNum", StringUtil.isEmpty(todaySaleNum) ? 0 : todaySaleNum);
        stats.put("todaySaleCount", StringUtil.isEmpty(todaySaleCount) ? 0 : todaySaleCount);
        stats.put("monthSaleNum", StringUtil.isEmpty(monthSaleNum) ? 0 : monthSaleNum);
        stats.put("monthSaleCount", StringUtil.isEmpty(monthSaleAmount) ? 0 : monthSaleAmount);
        stats.put("saleCount", StringUtil.isEmpty(saleCount) ? 0 : saleCount);
        stats.put("amount", StringUtil.isEmpty(amount) ? 0 : amount);

        results.put("stats", stats);
        result.put("status", "OK");
        result.put("results", results);

        return result;

    }

    /**
     * 门店 销售订单 统计
     *
     * @param accessToken
     * @return
     */
    public Map<String, Object> tradescount2(String accessToken) {
        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //日销售数量
        Integer todaySaleNum = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, "date");
        //日销售额
        Integer todaySaleCount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, "date");

        //月销售数量
        Integer monthSaleNum = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, "month");
        //月销售额
        Integer monthSaleAmount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, "month");

        //累计销售数量
        Integer saleCount = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, null);
        //累计销售额
        Integer amount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), null, null, null);


        //安卓要的数据结构
        Map<String, Object> results = new HashMap<String, Object>();
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("todaySaleNum", StringUtil.isEmpty(todaySaleNum) ? 0 : todaySaleNum);
        stats.put("todaySaleCount", StringUtil.isEmpty(todaySaleCount) ? 0 : todaySaleCount);
        stats.put("monthSaleNum", StringUtil.isEmpty(monthSaleNum) ? 0 : monthSaleNum);
        stats.put("monthSaleCount", StringUtil.isEmpty(monthSaleAmount) ? 0 : monthSaleAmount);
        stats.put("saleCount", StringUtil.isEmpty(saleCount) ? 0 : saleCount);
        stats.put("amount", StringUtil.isEmpty(amount) ? 0 : amount);

        results.put("stats", stats);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }

    /**
     * 店员 销售订单 统计
     *
     * @param accessToken
     * @return
     */
    public Map<String, Object> tradescountclerk2(String accessToken) {
        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //日销售数量
        Integer todaySaleNum = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, "date");
        //日销售额
        Integer todaySaleCount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, "date");

        //月销售数量
        Integer monthSaleNum = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, "month");
        //月销售额
        Integer monthSaleAmount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, "month");

        //累计销售数量
        Integer saleCount = bTradesMapper.todaySalesByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, null);
        //累计销售额
        Integer amount = bTradesMapper.todaySalesRealPayByStoresClerk(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, null);


        //安卓要的数据结构
        Map<String, Object> results = new HashMap<String, Object>();
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("todaySaleNum", StringUtil.isEmpty(todaySaleNum) ? 0 : todaySaleNum);
        stats.put("todaySaleCount", StringUtil.isEmpty(todaySaleCount) ? 0 : todaySaleCount);
        stats.put("monthSaleNum", StringUtil.isEmpty(monthSaleNum) ? 0 : monthSaleNum);
        stats.put("monthSaleCount", StringUtil.isEmpty(monthSaleAmount) ? 0 : monthSaleAmount);
        stats.put("saleCount", StringUtil.isEmpty(saleCount) ? 0 : saleCount);
        stats.put("amount", StringUtil.isEmpty(amount) ? 0 : amount);

        results.put("stats", stats);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }


    //查询销售额
    private Integer getSaleAmount(int storeUserId, String time, int siteId) {
        return bTradesMapper.getSaleAmount(storeUserId, time, siteId);
    }

    //查询销售数量
    private Integer getSaleNum(int storeUserId, String time, int siteId) {
        return bTradesMapper.getSaleNum(storeUserId, time, siteId);
    }

    //销售列表
    public Map<String, Object> tradesdaylist(String accessToken, String date) {


        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(date, "(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", date + " 与要求yyyy-MM-dd格式不符！");
            return result;
        }

        //List<BTrades> bTradesList = getBTradesList(authToken.getStoreAdminId(),date,authToken.getSiteId());
        List<BTrades> bTradesList = getBTradesListByDate(authToken.getSiteId(), authToken.getStoreId(), date);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("trades", bTradesList);
        result.put("status", "OK");
        result.put("results", results);

        return result;

    }

    //销售列表  门店
    public Map<String, Object> tradesdaylist2(String accessToken, String date) {
        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(date, "(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", date + " 与要求yyyy-MM-dd格式不符！");
            return result;
        }

        List<BTrades> bTradesList = getBTradesListByDate2(authToken.getSiteId(), authToken.getStoreId(), null, null, date);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("trades", bTradesList);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }

    //销售列表  店员
    public Map<String, Object> tradesdaylistclerk2(String accessToken, String date) {
        Map<String, Object> result = new HashMap<String, Object>();

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(date, "(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", date + " 与要求yyyy-MM-dd格式不符！");
            return result;
        }

        List<BTrades> bTradesList = getBTradesListByDate2(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, date);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("trades", bTradesList);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }


    //查询订单
    private List<BTrades> getBTradesList(int storeUserId, String date, int siteId) {

        List<BTrades> bTradesList = bTradesMapper.findBTradesList(storeUserId, date, siteId);
        if (StringUtil.isEmpty(bTradesList) || bTradesList.size() == 0) {
            return new ArrayList();
        }

        for (BTrades trades : bTradesList) {
            //查询子订单信息
            List<Map<String, Object>> orderInfoList = bOrdersMapper.getOrderInfo(trades.getTradesId(), siteId);
            for (Map<String, Object> map : orderInfoList) {
                String goods_imgurl = (String) map.get("goods_imgurl");
                Map<String, String> goodsImgurl = new HashMap<String, String>();
                goodsImgurl.put("imageId", goods_imgurl);
                map.put("goodsImgurl", goodsImgurl);

            }
            trades.setOrder_list(orderInfoList);
        }

        return bTradesList;
    }

    //查询订单    某一天的 所有门店 销售 订单
    private List<BTrades> getBTradesListByDate(int siteId, int storeId, String date) {

        //List<BTrades> bTradesList = bTradesMapper.findBTradesList(storeUserId,date,siteId);
        List<BTrades> bTradesList = bTradesMapper.findBTradesListByDate(siteId, storeId, date);
        if (StringUtil.isEmpty(bTradesList) || bTradesList.size() == 0) {
            return new ArrayList();
        }

        for(BTrades trades:bTradesList){
            //查询子订单信息
            List<Map<String,Object>> orderInfoList = bOrdersMapper.getOrderInfo(trades.getTradesId(),siteId);
            for(Map<String,Object> map:orderInfoList){
                String goods_imgurl = (String) map.get("goods_imgurl");
                Map<String,String> goodsImgurl = new HashMap<String,String>();
                goodsImgurl.put("imageId",goods_imgurl);
                map.put("goodsImgurl",goodsImgurl);

            }
            trades.setOrder_list(orderInfoList);
        }
        return bTradesList;
    }

    //查询订单    某一天的 所有门店 销售 订单
    private List<BTrades> getBTradesListByDate2(int siteId, int storeId, Integer storeUserId, Integer storeShippingClerkId, String date) {

        List<BTrades> bTradesList = bTradesMapper.findBTradesListByDateClerk(siteId, storeId, storeUserId, storeShippingClerkId, date);
        if(StringUtil.isEmpty(bTradesList)||bTradesList.size()==0){
            return new ArrayList();
        }

        for(BTrades trades:bTradesList){
            //查询子订单信息
            List<Map<String,Object>> orderInfoList = bOrdersMapper.getOrderInfo(trades.getTradesId(),siteId);
            for(Map<String,Object> map:orderInfoList){
                String goods_imgurl = (String) map.get("goods_imgurl");
                Map<String,String> goodsImgurl = new HashMap<String,String>();
                goodsImgurl.put("imageId",goods_imgurl);
                map.put("goodsImgurl",goodsImgurl);

            }
            trades.setOrder_list(orderInfoList);
        }
        return bTradesList;
    }


    //月注册统计
    public Map<String,Object> registermonth(String accessToken, String month) {

        Map<String,Object> result = new HashMap<String,Object>();

        if(!StringUtil.checkDateFormat(month,"(\\d{4})-(0\\d{1}|1[0-2])")){
            result.put("status","ERROR");
            result.put("errorMessage",month+ " 与要求的时间格式不符(yyyy-MM)");
            return result;
        }

        //获取InvitationCode,如果status存在并且等于"ERROR",返回错误信息
        Map<String,Object> msp = getInvitationCode(accessToken);
        if(!StringUtil.isEmpty(msp)&&!StringUtil.isEmpty(msp.get("status"))&&msp.get("status").equals("ERROR")){
            return msp;
        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        //查询
        Map<String,Object> registermonth = new HashMap<String,Object>();
        List<Map<String,Object>> dayArray =  getRegisteDateNum((String)msp.get("invitation_code"),month,authToken.getSiteId());
        registermonth.put("registerMonth",dayArray);
        result.put("status","OK");
        result.put("results",registermonth);

        return result;
    }


    //查询每一天的注册数量,没有注册的日期赋0
    private List<Map<String,Object>> getRegisteDateNum(String invitation_code, String month, int siteId){

        Map<String,Integer> dayMap = new HashMap<String,Integer>();
        List<Date> regitDateList = bMemberInfoMapper.getRegisteDate(invitation_code,month,siteId);

         if(!StringUtil.isEmpty(regitDateList)||regitDateList.size()!=0){
            //遍历时间数组，统计每天的注册数量
            for (Date date : regitDateList) {
                String dayString = DateUtils.formatDate(date, "yyyy-MM-dd");

                Integer num = dayMap.get(dayString);
                if (StringUtil.isEmpty(num)) {
                    dayMap.put(dayString, 1);
                } else {
                    dayMap.put(dayString, dayMap.get(dayString) + 1);
                }
            }
        }


         //查询月份的每一天
        List<String> dayList = getMonthDay(month);

        List<Map<String,Object>> dayArray = new ArrayList<Map<String,Object>>();

        for(String day:dayList){

            Map<String,Object> registeDateNum = new LinkedHashMap<>();

            //只遍历比当前时间小的日期
            if(DateUtils.compareNow(day)!=1){
                if(StringUtil.isEmpty(dayMap.get(day))){
                   // registeDateNum.put(day,0);
                    registeDateNum.put("key",day);
                    registeDateNum.put("value",0);
                }else{
                    //registeDateNum.put(day,dayMap.get(day));
                    registeDateNum.put("key",day);
                    registeDateNum.put("value",dayMap.get(day));
                }

                dayArray.add(registeDateNum);
            }


        }

        return dayArray;
    }

    //查询月份的每一天日期
    public List<String> getMonthDay(String month){
        String year = month.substring(0,month.indexOf("-"));
        String monthStr =month.substring(month.indexOf("-")+1,month.length());
        List<String> dayList = DateUtils.getDayListOfMonth(Integer.valueOf(year),Integer.valueOf(monthStr),0);
        return dayList;
    }

    //获取InvitationCode
    private Map<String,Object> getInvitationCode(String accessToken){
        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        StoreAdminExt storeAdminex = storeAdminextMapper.selectByPrimaryKey(authToken.getStoreUserId(), authToken.getSiteId());
        if (StringUtil.isEmpty(storeAdminex) || StringUtil.isEmpty(storeAdminex.getClerk_invitation_code())) {
            result.put("status", "ERROR");
            result.put("errorMessage", "店员信息错误！");
            return result;
        }


        String invitation_code = "";
        if (!StringUtil.isEmpty(storeAdminex.getClerk_invitation_code())) {
            invitation_code = storeAdminex.getClerk_invitation_code().replaceAll("\\d+\\_", "");
        }

        result.put("invitation_code", invitation_code);

        return result;
    }

    //查询每月每天的销售数量
    public Map<String, Object> tradesmonth(String accessToken, String month) {

        Map<String, Object> result = new HashMap<String, Object>();

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(month, "(\\d{4})-(0\\d{1}|1[0-2])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", month + " 与要求yyyy-MM格式不符！");
            return result;
        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //查询没每天下的订单数量
        Map<String, Object> results = new HashMap<String, Object>();
        //List<Map<String,Object>> tradesmonthNum = getTradesmonthNum(authToken.getStoreAdminId(),month,authToken.getSiteId());
        List<Map<String, Object>> tradesmonthNum = getTradesmonthNum(authToken.getStoreId(), month, authToken.getSiteId());

        results.put("tradesMonth", tradesmonthNum);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }

    //查询每月每天的销售数量   门店
    public Map<String, Object> tradesmonth2(String accessToken, String month) {
        Map<String, Object> result = new HashMap<String, Object>();

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(month, "(\\d{4})-(0\\d{1}|1[0-2])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", month + " 与要求yyyy-MM格式不符！");
            return result;
        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //查询没每天下的订单数量
        Map<String, Object> results = new HashMap<String, Object>();
        List<Map<String, Object>> tradesmonthNum = getTradesmonthNum2(authToken.getSiteId(), authToken.getStoreId(), null, null, month);

        results.put("tradesMonth", tradesmonthNum);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }

    //查询每月每天的销售数量   店员
    public Map<String, Object> tradesmonthclerk2(String accessToken, String month) {
        Map<String, Object> result = new HashMap<String, Object>();

        //验证month的格式，只接受  yyyy-MM
        if (!StringUtil.checkDateFormat(month, "(\\d{4})-(0\\d{1}|1[0-2])")) {
            result.put("status", "ERROR");
            result.put("errorMessage", month + " 与要求yyyy-MM格式不符！");
            return result;
        }

        AuthToken authToken = usersService.parseAccessToken(accessToken);
        if (StringUtil.isEmpty(authToken)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "accessToken解析失败！");
            return result;
        }

        //查询没每天下的订单数量
        Map<String, Object> results = new HashMap<String, Object>();
        List<Map<String, Object>> tradesmonthNum = getTradesmonthNum2(authToken.getSiteId(), authToken.getStoreId(), authToken.getStoreAdminId(), null, month);

        results.put("tradesMonth", tradesmonthNum);
        result.put("status", "OK");
        result.put("results", results);

        return result;
    }


    //查询每天下的订单数量
    private List<Map<String, Object>> getTradesmonthNum(int storeId, String month, int siteId) {

        Map<String, Integer> result = new HashMap<String, Integer>();
        //List<Date> dateList = bTradesMapper.getBTradesDate(storeUserId,month,siteId);
        List<Map<String, Object>> dateList = bTradesMapper.everydayMonthSalesByStores(siteId, storeId, month);

        //遍历时间数组，统计每天的注册数量
        if (!StringUtil.isEmpty(dateList) && dateList.size() != 0) {
            for (Map<String, Object> date : dateList) {
                //String dayString = DateUtils.formatDate(date, "yyyy-MM-dd");
//                Integer num = result.get(dayString);
//                if (StringUtil.isEmpty(num)) {
//                    result.put(dayString, 1);
//                } else {
//                    result.put(dayString, result.get(dayString) + 1);
//                }
                result.put(DateUtils.formatDate((Date) date.get("key"), "yyyy-MM-dd"), Integer.parseInt(String.valueOf(date.get("value"))));
            }
        }


        List<Map<String, Object>> dayArray = new ArrayList<Map<String, Object>>();


        //查询月份的每一天
        List<String> dayList = getMonthDay(month);

        for (String day : dayList) {

            //只遍历比当前时间小的日期
            if (DateUtils.compareNow(day) != 1) {

                Map<String, Object> orderlyMap = new LinkedHashMap<>();

                if (StringUtil.isEmpty(result.get(day))) {
                    //orderlyMap.put(day,0);
                    orderlyMap.put("key", day);
                    orderlyMap.put("value", 0);
                } else {
                    //orderlyMap.put(day,result.get(day));
                    orderlyMap.put("key", day);
                    orderlyMap.put("value", result.get(day));
                }

                dayArray.add(orderlyMap);
            }

        }

        return dayArray;

    }

    //查询每天下的订单数量
    private List<Map<String, Object>> getTradesmonthNum2(int siteId, int storeId, Integer storeUserId, Integer storeShippingClerkId, String month) {
        Map<String, Integer> result = new HashMap<String, Integer>();

        List<Map<String, Object>> dateList = bTradesMapper.everydayMonthSalesByStoresClerk(siteId, storeId, storeUserId, storeShippingClerkId, month);

        if (!StringUtil.isEmpty(dateList) && dateList.size() != 0) {
            for (Map<String, Object> date : dateList) {
                result.put(DateUtils.formatDate((Date) date.get("key"), "yyyy-MM-dd"), Integer.parseInt(String.valueOf(date.get("value"))));
            }
        }

        List<Map<String, Object>> dayArray = new ArrayList<Map<String, Object>>();
        //查询月份的每一天
        List<String> dayList = getMonthDay(month);
        for (String day : dayList) {
            //只遍历比当前时间小的日期
            if (DateUtils.compareNow(day) != 1) {
                Map<String, Object> orderlyMap = new LinkedHashMap<>();
                if (StringUtil.isEmpty(result.get(day))) {
                    orderlyMap.put("key", day);
                    orderlyMap.put("value", 0);
                } else {
                    orderlyMap.put("key", day);
                    orderlyMap.put("value", result.get(day));
                }
                dayArray.add(orderlyMap);
            }
        }
        return dayArray;
    }



    /**
     * 查询当前七日内的订单数据to门店助手
     *
     * @param siteId
     * @param storeId
     * @param userId
     * @param nowDay
     * @return
     */
    public List<Map<String, Object>> appselectTradespay(Integer siteId, Integer storeId, Integer userId, String nowDay) {
        String eTime = nowDay + " 23:59:59";
        Date endDate = DateUtils.parseDate(eTime, "yyyy-MM-dd");
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -6);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = bTradesMapper.appselectTradespay(siteId, storeId, userId,1, sTime, eTime);
        return rearragement(currentResult, startDate, nowDay);
    }

    /**
     * 查询七日内门店订单数量
     *
     * @param siteId
     * @param storeId
     * @param userId
     * @param nowDay
     * @return
     */
    public List<Map<String, Object>> appselectTradenum(Integer siteId, Integer storeId, Integer userId, String nowDay) {
        String eTime = nowDay + " 23:59:59";
        Date endDate = DateUtils.parseDate(eTime, "yyyy-MM-dd");
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -6);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = bTradesMapper.appselectTradenum(siteId, storeId, userId,1, sTime, eTime);
        return rearragement(currentResult, startDate, nowDay);
    }

    /**
     * 该门店所有注册会员数量统计
     *
     * @param siteId
     * @param storeId
     * @param userId
     * @param nowDay
     * @return
     */
    public List<Map<String, Object>> appselectmembernum(Integer siteId, Integer storeId, Integer userId, String nowDay) {
        String eTime = nowDay + " 23:59:59";
        Date endDate = DateUtils.parseDate(eTime, "yyyy-MM-dd");
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -6);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = bMemberInfoMapper.appselectmembernum2(siteId, storeId, userId, sTime, eTime);
        return rearragement(currentResult, startDate, nowDay);
    }

    public List<Map<String, Object>> rearragement(List<Map<String, Object>> currentResult, Date startDate, String endTime) {
        List<Map<String, Object>> resultParmas = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        //遍历数组，统计每天的积极店员数量
        if (!StringUtil.isEmpty(currentResult) && currentResult.size() != 0) {
            for (Map<String, Object> date : currentResult) {
                result.put(DateUtils.formatDate((Date) date.get("query_time"), "yyyy-MM-dd"), String.valueOf(date.get("value")));
            }
        }
        List<String> dates = DateUtils.getContinuousDayStr(DateUtils.formatDate(startDate, "yyyy-MM-dd"), endTime);
        for (int i = 0; i < dates.size(); i++) {
            Map<String, Object> clerkNum = new HashMap<>();
            clerkNum.put("query_time", dates.get(i));
            if (result.containsKey(dates.get(i))) {
                if ("null".equals(result.get(dates.get(i)).toString())) {
                    clerkNum.put("value", 0);
                } else {
                    clerkNum.put("value", result.get(dates.get(i)));
                }
            } else {
                clerkNum.put("value", 0);
            }
            resultParmas.add(clerkNum);
        }
        return resultParmas;
    }

    public Map<String, Object> selectMaxValue(List<Map<String, Object>> currentParams) {
        Map<String, Object> objectMap = new HashMap<>();
        Float num = 0f;
        for (Map<String, Object> cmap : currentParams) {
            Float cnum = Float.valueOf(cmap.get("value").toString());
            if (num <= cnum) {
                num = cnum;
            } else {
                continue;
            }
        }
        objectMap.put("maxvalue", num);
        return objectMap;
    }

}
