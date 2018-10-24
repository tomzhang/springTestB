package com.jk51.modules.offline.service;

import com.jk51.annotation.TimeRequired;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.erpDataConfig.guanji.DataSoureConfig;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.appInterface.mapper.YbAreaMapper;
import com.jk51.modules.offline.mapper.BErpOrderLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-07-24
 * 修改记录:
 */
@Service
public class GuangJiOfflineService {
    private static final Logger logger = LoggerFactory.getLogger(GuangJiOfflineService.class);
    @Resource
    private DataSoureConfig dataSoureConfig;
    @Autowired
    private BMemberMapper memberMapper;

    @Autowired
    private YbAreaMapper ybAreaMapper;
    @Autowired
    private BMemberInfoMapper memberInfoMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private BErpOrderLogMapper erpOrderLogMapper;


    //对接erp获取会员信息
//    @TimeRequired
    public Map<String, Object> getUser(Integer siteId, String mobile, Map<String, Object> member) {
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> result = selectMemberInfo(siteId, mobile, member);//默认从商户erp中获取到信息
        erpOrderLogMapper.insertSelectErpLog(siteId, "", mobile, JacksonUtils.mapToJson(result));
        if (Integer.parseInt(result.get("code").toString()) == -1) {//从商户获取erp信息失败，从51后台获取用户信息返回前端
            responseParams.put("code", 400);
            responseParams.put("msg", "从51后台获取会员信息");
            return responseParams;
        } else if (Integer.parseInt(result.get("code").toString()) == 0) { //，从商户获取信息成功且没有同步过erp
            Map<String, String> memInfoFromERP = (Map<String, String>) result.get("info");
            updateonLineMemberInfo(siteId, mobile, Integer.parseInt(member.get("buyerId").toString()), memInfoFromERP.get("name"), memInfoFromERP.get("sex"), memInfoFromERP.get("birthday"),
                memInfoFromERP.get("address"), memInfoFromERP.get("card_no"));//第一次同步erp，将商户信息同步到51后台
            memberMapper.updateFirstErp(siteId, mobile);
            responseParams.put("code", 200);
            responseParams.put("msg", "广济同步会员信息成功");
            return responseParams;
        }
        responseParams.put("code", 404);
        responseParams.put("msg", "广济会员信息获取失败");
        return responseParams;
    }

   /* public Map<String, Object> getUser2(Integer siteId, String mobile, String inviteCode) {
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);
        Map<String, Object> responseParams = new HashMap<>();
        Map<String, Object> result = selectMemberInfo(siteId, mobile, inviteCode);//默认从商户erp中获取到信息
        if (result.get("code").equals("-1")) {//从商户获取erp信息失败，从51后台获取用户信息返回前端
            Map<String, Object> memberInfoFromJk = memberMapper.selectMemberBySiteIdAndMobile(siteId, mobile).get(0);
            memberInfoFromJk.put("address", queryMemberAddress(memberInfoFromJk.get("province"), memberInfoFromJk.get("city"),
                memberInfoFromJk.get("area"), memberInfoFromJk.get("address")));
            memberInfoFromJk.put("card_no", mobile);//若数据库链接失败，默认使用当前用户的手机号码作为会员卡号
            memberInfoMapper.updateCardNo(siteId, mobile, mobile, Integer.parseInt(member.get("buyerId").toString()));
            responseParams.put("code", 0);
            responseParams.put("info", memberInfoFromJk);
            responseParams.put("msg", "从51后台获取会员信息");
            return responseParams;
        } else if (result.get("code").equals("0") && member.get("first_erp").equals("0")) { //，从商户获取信息成功且没有同步过erp
            Map<String, String> memInfoFromERP = (Map<String, String>) result.get("info");
            int i = updateonLineMemberInfo(siteId, mobile, Integer.parseInt(member.get("buyer_id").toString()), memInfoFromERP.get("name"), memInfoFromERP.get("sex"), memInfoFromERP.get("birthday"),
                memInfoFromERP.get("address"), memInfoFromERP.get("card_no"));//第一次同步erp，将商户信息同步到51后台
            if (i != 0) {
                memberMapper.updateFirstErp(siteId, mobile);
            }
            memInfoFromERP.put("card_no", mobile);
            return result;
        }
        return result;
    }*/

    //更新会员信息
//    @TimeRequired
    @Transactional
    public Map<String, Object> updateUser(Integer siteId, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> resultParam = new HashMap<>();
        String sql = String.format("update u_memcard_reg set cardholder=?,sex=?,cardaddress=?,birthday=? where handset=?");
        Integer result = dataSoureConfig.getGuanjiJDBCTemplate().update(sql, new Object[]{name, sex, address, birthday, mobile});
        if (result > 0) {//修改先下信息操作成功，将信息保存到线上
            resultParam.put("code", 200);
            resultParam.put("msg", "广济修改会员信息成功");
            return resultParam;
        } else {
            resultParam.put("code", 400);
            resultParam.put("msg", "广济更新会员信息失败");
            return resultParam;
        }
    }
 /*   public Map<String, Object> updateUser2(Integer siteId, String mobile, String name, String sex, String
        birthday, String address) {
        Map<String, Object> responseParams = new HashMap<>();
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);
        Map<String, Object> resultParam = new HashMap<>();
        Integer i = updateonLineMemberInfo(siteId, mobile, Integer.parseInt(member.get("buyer_id").toString()), name, sex, birthday,
            address, null);//更新51后台用户信息
        Integer result = -1;
        try {
            String sql = String.format("update u_memcard_reg set cardholder=?,sex=?,cardaddress=?,birthday=? where handset=?");
            logger.info("更新线下会员信息SQL语句:{},姓名{},性别{},地址{},生日{},手机号码{}", sql, name, sex, address, birthday, mobile);
            result = dataSoureConfig.getGuanjiJDBCTemplate().update(sql, new Object[]{name, sex, address, birthday, mobile});
        } catch (Exception e) {
            logger.debug("更新商户线下会员信息失败{}", e);
            responseParams.put("code", -1);
            responseParams.put("msg", "更新失败");
            return responseParams;
        }
        if (result > 0) {//广济药房线下用户信息更新成功
            responseParams.put("code", 0);
            responseParams.put("msg", "更新成功");
        } else {
            responseParams.put("code", -1);
            responseParams.put("msg", "更新失败");
        }
        return responseParams;
    }*/

    //获取线下积分总额，并更新至线下
    public Map<String, Object> queryScore(Integer siteId, String mobile_no) {
        Map<String, Object> responseParams = new HashMap<>();
        String code = "0";
        String msg = "";
        String sql = String.format("select top 1 integral as gold_score from u_memcard_reg where handset =? ORDER BY cardtype,integral desc");
        List<Map<String, Object>> result = dataSoureConfig.getGuanjiJDBCTemplate().queryForList(sql, new Object[]{mobile_no});
        if (result.size() == 0) {
            code = "-1";
            msg = "获取失败";
            responseParams.put("code", code);
            responseParams.put("msg", msg);
            return responseParams;
        }
        code = "0";
        responseParams.put("info", result);
        Object score = result.get(0).get("gold_score");
        Double d = Double.parseDouble(score.toString());
        Integer olIntegral = Integer.valueOf(d.intValue());
        // BigInteger olIntegral = new BigInteger(result.get(0).get("gold_score").toString());
        logger.info("offlineScore:{}", olIntegral.toString());
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile_no);
        int i = memberMapper.updateOfflineIntegral(map);
        if (i != 0) {
            code = "0";
            msg = "积分同步成功";
        } else {
            code = "-1";
            msg = "积分同步失败";
        }
        responseParams.put("code", code);
        responseParams.put("msg", msg);
        return responseParams;
    }

    /**
     * 获取线下积分获得列表
     *
     * @param mobile
     * @return
     */
    public Map<String, Object> getScoreList(String mobile, Integer pageNum, Integer pageSize) {
        Map<String, Object> responseParams = new HashMap<>();
        try {
            String sql = String.format(" select CONVERT(varchar,b.accdate, 120 ) as create_time,a.memcardno as memcardno,a.cardholder as cardholder,b.busno as busno,b.saleno as saleno,b.realamount as realamount,b.integral as integral,b.billcode + '送积分' as billcode from v_memcard_in_out b ,u_memcard_reg a where a.memcardno = b.memcardno and a.handset=? order BY create_time desc", mobile);
            logger.info("广济查询积分列表sql语句" + sql);
            List<Map<String, Object>> result = dataSoureConfig.getGuanjiJDBCTemplate().queryForList(sql, new Object[]{mobile});
            responseParams.put("code", 0);
            responseParams.put("msg", "查询积分列表成功");
            responseParams.put("info", result);
        } catch (Exception e) {
            logger.info("查询积分列表失败{}", e);
            responseParams.put("code", -1);
            responseParams.put("msg", "查询积分列表失败");
        }
        return responseParams;
    }

    /**
     * 查询线下erp会员信息
     * 1:如果存在，返回线下会员信息，如果不存在，新建会员信息并将会员信息返回
     * 记录线下会员在线上的注册时间，并传体给线下erp相关信息
     * 会员门店归属逻辑
     * 1.如果线上新会员没有线下注册，扫码注册，会员归属扫码门店
     * 2.如果线上新会员没有线下注册，未扫码注册，会员归属总部
     * 3.如果线上新会员有线下注册过，扫码注册，读取ERP后台信息，优先归属扫码门店
     * 4.如果线上新会员有线下注册过，未扫码注册，读取ERP后台信息，归属ERP中登记的门店
     */
    @Transactional
    public Map<String, Object> selectMemberInfo(Integer siteId, String mobile, Map<String, Object> getMemberInfo) {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String sql = String.format("select top 1 cardholder as name ,handset as mobile,sex as sex,cardaddress as address,CONVERT(varchar,birthday, 120 ) birthday,memcardno as card_no,erp_createtime as online_time from u_memcard_reg where handset =? ORDER BY cardtype,integral desc");
            logger.info("查询广济会员信息SQL语句:{}", sql);
            result = dataSoureConfig.getGuanjiJDBCTemplate().queryForList(sql, new Object[]{mobile});
        } catch (Exception e) {
            logger.info("广济数据库连接失败{}", e);
            responseparams.put("code", -1);
            responseparams.put("msg", "查询线下会员信息失败");
            return responseparams;
        }
        logger.info("线下会员信息{}", result.toString());
        if (result.size() > 0) {//线下会员会员存在
            if (StringUtil.isEmpty(result.get(0).get("erp_store"))) {//线下会员未填写线上注册时间
                try {
                    if (StringUtil.isEmpty(getMemberInfo.get("clerkName"))) {//会员线上注册时未填写店员邀请码,只登记线上注册时间，注册门店填写总部
                        String insertSql = String.format("update u_memcard_reg set erp_store=? , erp_storeAmin=? ,erp_storename=? , erp_createtime=? where handset=?");
                        dataSoureConfig.getGuanjiJDBCTemplate().update(insertSql, new Object[]{"001", "1014", "广济总部", getMemberInfo.get("ctime"), mobile});
                    } else {//会员线上注册时填写了店员邀请码，将对应店员的信息传递给线下erp
                        String insertSql = String.format("update u_memcard_reg set erp_store=? , erp_storeAmin=? ,erp_storename=? ,erp_createtime=? where handset=?");
                        dataSoureConfig.getGuanjiJDBCTemplate().update(insertSql, new Object[]{getMemberInfo.get("uid"), getMemberInfo.get("clerkName"), getMemberInfo.get("storeName"), getMemberInfo.get("ctime"), mobile});
                    }
                } catch (Exception e) {
                    logger.debug("更新会员线上注册信息" + e);
                    responseparams.put("code", -1);
                    responseparams.put("msg", "更新会员线上注册信息");
                    return responseparams;
                }
            }
            responseparams.put("code", 0);
            responseparams.put("msg", "查询成功");
            responseparams.put("info", result.get(0));
            return responseparams;
        } else {//线下会员不存在，注册该会员信息，同时将线上注册时的对应店员信息传递到线下erp
            try {
                if (StringUtil.isEmpty(getMemberInfo.get("clerkName"))) {//会员线上注册时未填写店员邀请码,只登记线上注册时间，线上注册门店写总部
                    String insertSql = String.format("insert into u_memcard_reg (handset,memcardno,busno,cardtype,cardlevel,cardstatus,saleamount,realamount,puramount,integral,createuser,createtime,erp_store,erp_storeAmin,erp_storename,erp_createtime) values (?,?,'001',1,2,1,0,0,0,0,'1014',?,?,?,?,?)");
                    dataSoureConfig.getGuanjiJDBCTemplate().update(insertSql, new Object[]{mobile, mobile, DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), "001", "1014", "广济总部", getMemberInfo.get("ctime")});
                } else {//会员线上注册时填写了店员邀请码，将对应店员的信息传递给线下erp
                    String insertSql = String.format("insert into u_memcard_reg (handset,memcardno,busno,cardtype,cardlevel,cardstatus,saleamount,realamount,puramount,integral,createuser,createtime,erp_store,erp_storeAmin,erp_storename,erp_createtime) values (?,?,'001',1,2,1,0,0,0,0,'1014',?,?,?,?,?)");
                    dataSoureConfig.getGuanjiJDBCTemplate().update(insertSql, new Object[]{mobile, mobile, DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), getMemberInfo.get("uid"), getMemberInfo.get("clerkName"), getMemberInfo.get("storeName"), getMemberInfo.get("ctime")});
                }
            } catch (Exception e) {
                logger.debug("插入会员注册信息" + e);
                responseparams.put("code", -1);
                responseparams.put("msg", "插入会员注册信息");
                return responseparams;
            }
        }
        List<Map<String, Object>> reresult = new ArrayList<>();
        try {
            String sql = String.format("select top 1 cardholder as name ,handset as mobile,sex as sex,cardaddress as address,CONVERT(varchar,birthday, 120 )  birthday,memcardno as card_no,erp_createtime as online_time from u_memcard_reg where handset ='%s' ORDER BY cardtype,integral desc", mobile);
            reresult = dataSoureConfig.getGuanjiJDBCTemplate().queryForList(sql);
        } catch (Exception e) {
            logger.info("获取新建信息{}", e);
            responseparams.put("code", -1);
            responseparams.put("msg", "查询会员信息失败");
            return responseparams;
        }
        if (reresult.size() < 0) {
            responseparams.put("code", -1);
            responseparams.put("msg", "未查询到会员相关信息");
        } else {
            responseparams.put("code", 0);
            responseparams.put("msg", "查询成功");
            responseparams.put("info", reresult.get(0));
        }
        return responseparams;
    }


    /**
     * 用户通过微信端更改个人数据
     *
     * @param siteId
     * @param mobile
     * @param name
     * @param sex
     * @param birthday
     * @param address
     * @return
     */

    public Integer updateonLineMemberInfo(Integer siteId, String mobile, Integer buyerId, String name, String sex,
                                          String birthday, String address, String cardNo) {
        int i = 0;
        Map memberMap = new HashMap();  //更新b_member 表信息
        memberMap.put("name", name);
        Integer sexNum = 3;
        if (!StringUtil.isEmpty(sex)) {
            sexNum = sex.equals("男") ? 1 : 0;
            memberMap.put("sex", sexNum);
        } else {
            memberMap.put("sex", 3);
        }
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        if (name != null || sex != null) {
            i += memberMapper.updateMember(memberMap);//更新member表用户信息
        }
        if (i != 0) {//更新b_member_info表信息
            Map memberInfoMap = new HashMap();
            memberInfoMap.put("address", address);//详细地址
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (!StringUtil.isEmpty(birthday)) {
                    Date date = sdf.parse(birthday);
                    memberInfoMap.put("birthday", date);
                }
            } catch (ParseException e) {
                memberInfoMap.put("birthday", "1970-01-01");
            }
            memberInfoMap.put("site_id", siteId);
            memberInfoMap.put("member_id", buyerId);
            memberInfoMap.put("card_no", cardNo);
            if (address != null || birthday != null || cardNo != null) {
                i += memberInfoMapper.updateMemberInfo(memberInfoMap);
            }
        }
        return i;
    }


    public String queryMemberAddress(Object province, Object city, Object area, Object address) {
        String result = null;
        if (StringUtil.isEmpty(area)) {//用户没有填写省市区地址信息
            if (!StringUtil.isEmpty(address)) {
                return address.toString();
            }
        } else if (!StringUtil.isEmpty(area) && area.toString().equals("0")) {//用户没有填写个人地址信息
            if (!StringUtil.isEmpty(address)) {
                return address.toString();
            }
        } else {
            try {
                String pname = ybAreaMapper.queryAreaByAreaId(Integer.parseInt(province.toString())).get("name").toString();
                String cname = ybAreaMapper.queryAreaByAreaId(Integer.parseInt(city.toString())).get("name").toString();
                String aname = ybAreaMapper.queryAreaByAreaId(Integer.parseInt(area.toString())).get("name").toString();
                if (!StringUtil.isEmpty(address)) {
                    result = pname + "," + cname + "," + aname + "|" + address;
                } else {
                    result = pname + "," + cname + "," + aname + "|";
                }
            } catch (Exception e) {
                logger.info("广济地址转换异常" + e);
                return result;
            }
        }
        return result;
    }

    /**
     * 定时任务，将线上已注册用户信息和线下用户信息进行对比，实时更新，只更新姓名，性别，地址，生日
     */

    public void memberByTimer() {
        List<Map<String, Object>> memberList = memberMapper.selectMemberBySiteIdAndMobile(100204, null);
        dataSoureConfig.getGuanjiJDBCTemplate().batchUpdate(" update u_memcard_reg set cardholder=?,sex=?,cardaddress=?,birthday=? where handset=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String name = null;
                if (!StringUtil.isEmpty(memberList.get(i).get("name"))) {
                    name = memberList.get(i).get("name").toString();
                }
                String sex = null;
                if (!StringUtil.isEmpty(memberList.get(i).get("sex"))) {
                    if (!memberList.get(i).get("sex").toString().equals("3")) {
                        sex = Integer.parseInt(memberList.get(i).get("sex").toString()) == 1 ? "男" : "女";
                    }
                }
                String address = null;
                if (!StringUtil.isEmpty(memberList.get(i).get("area"))) {
                    if (!memberList.get(i).get("area").toString().equals("0")) {
                        address = queryMemberAddress(memberList.get(i).get("province"), memberList.get(i).get("city"), memberList.get(i).get("area"), memberList.get(i).get("address"));
                    }
                }
                String birthday = null;
                if (!StringUtil.isEmpty(memberList.get(i).get("birthday"))) {
                    birthday = memberList.get(i).get("birthday").toString();
                }
                ps.setString(1, name);
                ps.setString(2, sex);
                ps.setString(3, address);
                ps.setString(4, birthday);
                ps.setString(5, memberList.get(i).get("mobile").toString());
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        });
    }
}



