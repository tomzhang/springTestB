package com.jk51.modules.offline.service;

import com.jk51.annotation.TimeRequired;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.erpDataConfig.baodao.DataSourceConfig_BaoDao;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.offline.mapper.BErpOrderLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-12-06
 * 修改记录:
 */
@Service
public class BaoDaoOfflineService {
    private static final Logger logger = LoggerFactory.getLogger(BaoDaoOfflineService.class);
    @Resource
    private DataSourceConfig_BaoDao config_baoDao;
    @Autowired
    private BMemberMapper memberMapper;

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
            return responseParams;
        } else if (Integer.parseInt(result.get("code").toString()) == 0) { //，从商户获取信息成功且没有同步过erp
            Map<String, String> memInfoFromERP = (Map<String, String>) result.get("info");
            updateonLineMemberInfo(siteId, mobile, Integer.parseInt(member.get("buyerId").toString()), memInfoFromERP.get("name"), memInfoFromERP.get("sex"), memInfoFromERP.get("birthday"),
                memInfoFromERP.get("address"), memInfoFromERP.get("idcard_number"));//第一次同步erp，将商户信息同步到51后台
            memberMapper.updateFirstErp(siteId, mobile);
            responseParams.put("code", 200);
            responseParams.put("msg", "宝岛同步会员信息成功");
            return responseParams;
        }
        responseParams.put("code", 404);
        responseParams.put("msg", "宝岛会员信息获取失败");
        return responseParams;
    }


    //更新会员信息
//    @TimeRequired
    @Transactional
    public Map<String, Object> updateUser(Integer siteId, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> resultParam = new HashMap<>();
        try {
            String sql = String.format("update querymember set name=?,sex=?,address=?,birth=? where mobile=?");
            Integer sexNum = StringUtil.isEmpty(sex) ? 3 : sex.equals("男") ? 1 : sex.equals("女") ? 0 : 3;
            Integer result = config_baoDao.getBaoDaoJDBCTemplate().update(sql, new Object[]{name, sexNum, address, DateUtils.convert(birthday, "yyyy-MM-dd"), mobile});
            if (result > 0) {//修改先下信息操作成功，将信息保存到线上
                resultParam.put("code", 200);
                resultParam.put("msg", "宝岛修改会员信息成功");
                return resultParam;
            } else {
                resultParam.put("code", 400);
                resultParam.put("msg", "宝岛更新会员信息失败");
                return resultParam;
            }
        } catch (ParseException e) {
            logger.info("宝岛日期类型转换异常" + e);
            resultParam.put("code", 404);
            resultParam.put("msg", "宝岛更新会员信息失败");
            return resultParam;
        }

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
    public Map<String, Object> selectMemberInfo(Integer siteId, String mobile, Map<String, Object> getMemStoreInfo) {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String sql = String.format("select name as name ,mobile as mobile,sex as sex,address as address,birth as birthday," +
                "idcard_number as idcard_number,case_history as case_history,allergies as allergies,deptcode51 as depno," +
                "registration as ctime from querymember where mobile =?");
            logger.info("查询宝岛会员信息SQL语句:{}", sql);
            result = config_baoDao.getBaoDaoJDBCTemplate().queryForList(sql, new Object[]{mobile});
        } catch (Exception e) {
            logger.info("宝岛数据库连接失败{}", e);
            responseparams.put("code", -1);
            responseparams.put("msg", "查询线下会员信息失败");
            return responseparams;
        }
        logger.info("线下会员信息{}", result.toString());
        if (result.size() > 0) {//线下会员会员存在
            if (StringUtil.isEmpty(result.get(0).get("ctime"))) {//线下会员未填写线上注册时间
                try {
                    if (StringUtil.isEmpty(getMemStoreInfo.get("uid"))) {//会员线上注册时未填写店员邀请码,没有找到对应门店编码,只填写线上注册时间
                        String insertSql = String.format("update querymember set registration=? where mobile=?");
                        config_baoDao.getBaoDaoJDBCTemplate().update(insertSql, new Object[]{getMemStoreInfo.get("ctime"), mobile});
                    } else {//会员线上注册时填写了店员邀请码，将对应门店的信息传递给线下erp
                        String insertSql = String.format("update querymember set deptcode51=?, registration=? where mobile=?");
                        config_baoDao.getBaoDaoJDBCTemplate().update(insertSql, new Object[]{getMemStoreInfo.get("uid"), getMemStoreInfo.get("ctime"), mobile});
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
                if (StringUtil.isEmpty(getMemStoreInfo.get("uid"))) {//会员线上注册时未填写店员邀请码,只登记线上注册时间，线上注册门店自动填写默认值
                    String insertSql = String.format("insert into querymember (mobile,status,registration) values (?,'success',?)");
                    config_baoDao.getBaoDaoJDBCTemplate().update(insertSql, new Object[]{mobile, getMemStoreInfo.get("ctime")});
                } else {//会员线上注册时填写了店员邀请码，将对应店员的信息传递给线下erp
                    String insertSql = String.format("insert into querymember (mobile,status,deptcode51,registration) values (?,'success',?,?)");
                    config_baoDao.getBaoDaoJDBCTemplate().update(insertSql, new Object[]{mobile, getMemStoreInfo.get("uid"), getMemStoreInfo.get("ctime")});
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
            String sql = String.format("select name as name ,mobile as mobile,sex as sex,address as address," +
                "birth as birthday,idcard_number as idcard_number,case_history as case_history,allergies as allergies from querymember where mobile =%s", mobile);
            reresult = config_baoDao.getBaoDaoJDBCTemplate().queryForList(sql);
        } catch (Exception e) {
            logger.info("获取新建信息{}", e);
            responseparams.put("code", -1);
            responseparams.put("msg", "查询会员信息失败");
            return responseparams;
        }
        if (reresult.size() < 0) {
            responseparams.put("code", -1);
            responseparams.put("msg", "宝岛未查询到会员相关信息");
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
                                          Object birthday, String address, String idcard_number) {
        int i = 0;
        if (name != null || sex != null || idcard_number != null) {
            Map memberMap = new HashMap();  //更新b_member 表信息
            memberMap.put("name", name);
            memberMap.put("sex", sex);
            memberMap.put("site_id", siteId);
            memberMap.put("mobile", mobile);
            memberMap.put("idcard_number", idcard_number);
            i += memberMapper.updateMember(memberMap);//更新member表用户信息
        }
        Map memberInfoMap = new HashMap();
        memberInfoMap.put("address", address);//详细地址
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            memberInfoMap.put("card_no", mobile);//会员卡号以手机号为准
            if (!StringUtil.isEmpty(birthday)) {
                Date date = sdf.parse(birthday.toString());
                memberInfoMap.put("birthday", date);
            }
        } catch (ParseException e) {
            memberInfoMap.put("birthday", "1970-01-01");
        }
        memberInfoMap.put("site_id", siteId);
        memberInfoMap.put("member_id", buyerId);
        i += memberInfoMapper.updateMemberInfo(memberInfoMap);
        return i;
    }


    public Map<String, Object> getStorageList(String goodsNo, String uid) throws Exception {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> storageList = new ArrayList<>();
        StringBuffer coderequest = new StringBuffer("");
        String codeStrings = "";
        StringBuffer uidRequest = new StringBuffer("");
        String uidStrings = "";
        if (!StringUtil.isEmpty(goodsNo)) {
            for (String s : goodsNo.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                coderequest.append("'" + s + "'" + ",");
            }
            codeStrings = coderequest.substring(0, coderequest.length() - 1);
        }
        if (!StringUtil.isEmpty(uid)) {
            for (String s : uid.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                uidRequest.append("'" + s + "'" + ",");
            }
            uidStrings = uidRequest.substring(0, uidRequest.length() - 1);
        }
        if (StringUtil.isEmpty(codeStrings) || StringUtil.isEmpty(uidStrings)) {
            responseparams.put("code", 1);
            responseparams.put("info", storageList);
            return responseparams;
        }
        logger.info("查询门店编码为{},商品编码为：{}.", uid, coderequest);
        String sql = String.format("select DEPTNO as sno ,ARTICODE as GOODSNO,KYKC as kcqty from V_STKLOC_JKKC where ARTICODE in(%s) and DEPTNO in(%s)", codeStrings, uidStrings);
        logger.info("宝岛的库存查询语句" + sql);
        storageList = config_baoDao.getBaoDaoJDBCTemplate().queryForList(sql);
        if (storageList.size() == 3) {
            logger.info("门店编码为{},商品编码为：{},存在库存", uid, coderequest);
        }
        for (Map<String, Object> storageMap : storageList) {
            storageMap.put("UID", storageMap.get("sno"));
            storageMap.put("kcqty", storageMap.get("kcqty"));
        }
        responseparams.put("code", 1);
        responseparams.put("info", storageList);
        return responseparams;
    }

}
