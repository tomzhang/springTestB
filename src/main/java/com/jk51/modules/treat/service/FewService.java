package com.jk51.modules.treat.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.account.models.SettlementdayConfig;
import com.jk51.model.account.requestParams.DealTimeParam;
import com.jk51.model.balance.BaseFeeSet;
import com.jk51.model.balance.SmsFeeRule;
import com.jk51.model.balance.SmsFeeSet;
import com.jk51.model.order.Meta;
import com.jk51.model.order.SMeta;
import com.jk51.model.treat.*;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.account.mapper.PayPlatformMapper;
import com.jk51.modules.account.mapper.SettlementdayConfigMapper;
import com.jk51.modules.account.service.SettingDealTimeService;
import com.jk51.modules.balance.mapper.BaseFeeMapper;
import com.jk51.modules.balance.mapper.SmsFeeMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.meta.service.MetaService;
import com.jk51.modules.treat.mapper.FewMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import com.jk51.modules.treat.mapper.MerchantMapper;
import com.jk51.modules.treat.mapper.YbManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

@Service
public class FewService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;

    @Autowired
    private YbManagerMapper ybManagerMapper;

    @Autowired
    private FewMapper fewMapper;

    @Autowired
    private AccountCommissionRateMapper accountCommissionRateMapper;

    @Autowired
    private PayPlatformMapper payPlatformMapper;

    @Autowired
    private SettlementdayConfigMapper settlementdayConfigMapper;

    @Autowired
    private SettingDealTimeService settingDealTimeService;

    @Autowired
    private YbMerchantMapper ybMerchantMapper;
    @Autowired
    private BaseFeeMapper baseFeeMapper;
    @Autowired
    private SmsFeeMapper smsFeeMapper;
    @Autowired
    private MetaService metaService;

    public List<MerchantVO> selectMerchantList(String name, Integer merchantId, Integer status, Date from, Date to, Integer set_type, Date thelast_time, Integer cyclefrom, Integer cycleto){
        return merchantMapper.selectSeletive(name, merchantId, status, from, to, set_type,thelast_time, cyclefrom, cycleto);
    }

    public List<YBManager> selectAllYbManager(String username, String realname, Integer isActive) {
        return ybManagerMapper.selectAll(username, realname, isActive);
    }

    public PageInfo<YBManager> selectAllYbManagerPage(String username, String realname, Integer isActive, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<YBManager> managers = ybManagerMapper.selectAll(username, realname, isActive);
        PageInfo page = new PageInfo<>(managers);
        return page;
    }

    public IconLib insertAndGet(IconLib record){
        fewMapper.insertAndGet(record);
        return record;
    }


    /**
     * 新增商户
     *
     */
    @Transactional
    public String addShop(Map param) throws Exception {

            MerchantTreat merchant = JacksonUtils.json2pojo(param.get("merchant")+"",MerchantTreat.class);
            MerchantExtTreat merchantExt = JacksonUtils.json2pojo(param.get("merchantExt")+"",MerchantExtTreat.class);

            MerchantTreat isM = merchantMapper.selectByMerchantId(merchant.getMerchant_id());
            if (isM != null) {
                return "服务器提了一个问题，请刷新页面重新编辑";
            }
            merchant.setSeller_nick("admin");
            String pwd = merchant.getSeller_pwd();
            merchant.setSeller_pwd(sha2(pwd));
            merchantMapper.insertSelective(merchant);
            merchantExt.setMerchant_id(merchant.getMerchant_id());

            //wx_appid去空格
            merchantExt.setWx_appid(merchantExt.getWx_appid().trim());
            merchantExtTreatMapper.insertSelective(merchantExt);

            initMerchantDate(merchant.getMerchant_id());

            //生成默认数据基础费用设置
            BaseFeeSet record = new BaseFeeSet();
            //存储格式==退单规则0（退单不返还）；1-0 or 1-1（退单返还-全额返；退单返还-比例返还）
            record.setRefuseRule("1-1");
            record.setFeeRate(1f);
            record.setFeeRule(1);
            record.setFeeType(0);
            record.setPayType("100,110,120,130,140,200,210,220,300,310");
            record.setDeliveryType("150,160");
            record.setScene("110,120,150,160");
            record.setName("佣金");
            record.setMark("默认数据");
            record.setSiteId(merchant.getMerchant_id());
            baseFeeMapper.addBaseFee(record);

            //新开的网站会出现没数据的情况，所以要新增一条主题数据以及草稿
            SMeta meta = new SMeta();
            meta.setThemeId(0);
            meta.setSiteId(merchant.getMerchant_id());
            meta.setMetaStatus(1);
            meta.setMetaVal("");
            meta.setMetaType("merchant_page");
            meta.setMetaKey("merchant_context");
            Integer x = metaService.addmeta(meta);
            if(x==1){
                //再生成草稿
                meta.setMetaType("merchant_draft_page");
                meta.setMetaKey("merchant_draft_context");
                x = metaService.addmeta(meta);
            }

            //生成短信收费设置默认数据
            SmsFeeSet smsFeeSet = new SmsFeeSet();
            smsFeeSet.setCode("100,110,120,130,140,150,160,170,300,400,500,600,700,800,810,820,900");
            smsFeeSet.setSiteId(merchant.getMerchant_id());
            smsFeeMapper.addSmsFeeSet(smsFeeSet);

            SmsFeeRule smsFeeRule = new SmsFeeRule();
            smsFeeRule.setFee(5);
            smsFeeRule.setSmlNum(1);
            smsFeeRule.setBigNum(100000);
            smsFeeRule.setSiteId(merchant.getMerchant_id());
            smsFeeMapper.addSmsFeeRule(smsFeeRule);


            return "200";
    }

    @Async
    public void initMerchantDate(Integer siteId) {
        SettlementdayConfig item = new SettlementdayConfig();
        item.setSite_id(siteId);
        //设置类型（0按日结 1按周结 2按月结)
        item.setSet_type(2);
        Calendar c = Calendar.getInstance();
        int dateNum = c.get(Calendar.DATE);
        dateNum = dateNum > 28 ? 28 : dateNum;
        item.setSet_value(dateNum + "");
        item.setThelast_time(DateUtils.getBeforeOrAfterDate(new Date(), -1));
        settlementdayConfigMapper.addSettlementdayConfig(item);

        AccountCommissionRate rate = new AccountCommissionRate();
        rate.setSite_id(siteId);
        rate.setDirect_purchase_rate(1);
        rate.setDistributor_rate(3);
        rate.setShipping_fee_rate(5);
        accountCommissionRateMapper.addAccount(rate);

        List<PayPlatform> list = initPayPlatformDate(siteId);
        for (PayPlatform payItem : list) {
            payPlatformMapper.addPayPlatform(payItem);
        }

        fewMapper.initCategory(siteId);

        DealTimeParam dealTimeParam = new DealTimeParam();
        dealTimeParam.setSite_id(siteId);
        dealTimeParam.setTrades_auto_close_time(3);
        dealTimeParam.setTrades_auto_confirm_time(7);
        dealTimeParam.setTrades_allow_refund_time(3);
        settingDealTimeService.settingDealTime(dealTimeParam);

    }

    public List<PayPlatform> initPayPlatformDate(Integer siteId){
        List<PayPlatform> list = new ArrayList<>();
        PayPlatform wx = new PayPlatform();
        wx.setSite_id(siteId);
        wx.setPay_type(AccountConstants.PAY_TYPE_WX);
        wx.setProcedure_fee(1.0);
        list.add(wx);

        PayPlatform ali = new PayPlatform();
        ali.setSite_id(siteId);
        ali.setPay_type(AccountConstants.PAY_TYPE_ALI);
        ali.setProcedure_fee(1.0);
        list.add(ali);

        PayPlatform cash = new PayPlatform();
        cash.setSite_id(siteId);
        cash.setPay_type(AccountConstants.PAY_TYPE_CASH);
        cash.setProcedure_fee(0.0);
        list.add(cash);

        PayPlatform insurance = new PayPlatform();
        insurance.setSite_id(siteId);
        insurance.setPay_type(AccountConstants.PAY_TYPE_HEALTH_INSURANCE);
        insurance.setProcedure_fee(0.0);
        list.add(insurance);

        PayPlatform union = new PayPlatform();
        union.setSite_id(siteId);
        union.setPay_type(AccountConstants.PAY_TYPE_UNION_PAY);
        union.setProcedure_fee(1.0);
        list.add(union);
        return list;
    }

    public String sha2(String pwd) {
        try {
            return EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(pwd));
        } catch (Exception e) {
            return "0";
        }
    }

    public Map queryDefaulto2oPro(String siteId) {
        Map result = new HashMap();
        try {
            Map status = merchantExtTreatMapper.queryLogisticsFlag(siteId);
            Map meta = ybMerchantMapper.queryDefaulto2o(siteId);
            Integer i = merchantExtTreatMapper.getIsService(siteId);
            if (StringUtil.isEmpty(i)){
                i = 1;
            }
            result.put("is_service",i);
            result.put("status", status);
            result.put("meta", meta);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    @Transactional
    public void updateBDMember(Integer siteId, String buyerId, String name,String idcard_number,Integer sex,Integer offline_integral,
                               String address,String tag, String birth, String case_history,String allergies) throws ParseException {
        Date date = DateUtils.parse(birth,"yyyy-MM-dd");
        fewMapper.updateBDMember(siteId,buyerId, name, idcard_number, sex, offline_integral );
        fewMapper.updateBDMemberInfo(siteId, buyerId,address, tag, date, case_history, allergies);
    }

}
