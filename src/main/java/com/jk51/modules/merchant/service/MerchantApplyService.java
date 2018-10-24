package com.jk51.modules.merchant.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.model.merchant.MerchantApply;
import com.jk51.model.merchant.MerchantApplyDto;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.merchant.job.message.*;
import com.jk51.modules.merchant.mapper.BusinessCategoryMapper;
import com.jk51.modules.merchant.mapper.MerchantApplyMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.merchant.utils.MessageForApply;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-13 15:07
 * 修改记录:
 **/
@Service
public class MerchantApplyService {

    @Autowired
    private YbMerchantMapper merchantMapper;

    @Autowired
    private MerchantApplyMapper merchantApplyMapper;

    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MerchantApplySendEmailJob merchantApplySendEmailJob;

    @Autowired
    private BusinessCategoryMapper businessCategoryMapper;

    @Autowired
    private MessageForApply messageForApply;


    private Logger logger = LoggerFactory.getLogger(MerchantApplyService.class);
    public static final Integer SITE_STATUS_SUCCESS = 130;
    public static final Integer SITE_STATUS_FAIL = 110;
    @Transactional
    public void applyMercahnt(int applicant_id,YbMerchant merchant, MerchantExtTreat merchantExt, MerchantApply merchantApply){
        int merchantId = getSiteId();
        merchant.setMerchant_id(merchantId);
        merchant.setMerchant_name(merchant.getCompany_name());
        merchant.setSeller_nick("admin");
        merchant.setSeller_pwd("");
        merchant.setIs_frozen(0);
        merchant.setSite_status(110);
        merchant.setWx_site_status(110);
        merchant.setShop_desc("");
        merchant.setShop_title(merchant.getCompany_name());          //网站名称
        merchant.setShop_url(getShopUrl(merchantId));           //网站域名
        merchant.setShopwx_url(getShopwxUrl(merchantId));
        merchant.setSite_record("");       //网站备案号
        merchant.setCreate_time(new Date());
        merchantMapper.insertSelective(merchant);

        merchantExt.setMerchant_id(merchantId);
        merchantExt.setStore_url(getStoreUrl(merchantId));
        merchantExt.setAllow_refund(1);
        merchantExt.setLogistics_flag_jk(1);
        merchantExt.setCreate_time(new Date());
        merchantExtTreatMapper.insertSelective(merchantExt);

        merchantApply.setMerchant_id(merchantId);
        merchantApply.setApplicant_id(applicant_id);        //申请人id
        merchantApply.setCreate_time(new Date());
        merchantApplyMapper.save(merchantApply);

    }

    private Integer getSiteId() {
        int siteId = merchantMapper.selectMerchantId();
        logger.info("siteId最大值" + siteId);
        return ++siteId;
    }

    public String sha2(String pwd) {
        try {
            return EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(pwd));
        } catch (Exception e) {
            return "0";
        }
    }


    public Object getApplicant(String uphone){
        return  redisTemplate.opsForHash().get(uphone + "_ApplyUser", "uid");
    }

    public String getApplyCode(String phone){
        if(redisTemplate.opsForValue().get(phone + "_applyCode")!=null){
            return redisTemplate.opsForValue().get(phone + "_applyCode").toString();
        }
        return null;
    }

    public String getInitPassword(){
        Random random = new Random();
        String pwd = "";
        for(int i=0;i<8;i++){
            pwd = pwd + random.nextInt(10);
        }
        return pwd;
    }

    private String getShopUrl(Integer merchantId){
        return merchantId +".shop-run.51jk.com";
    }

    private String getShopwxUrl(Integer merchantId){
        return merchantId + ".weixin-run.51jk.com";
    }

    private String getStoreUrl(Integer merchantId){
        return merchantId +".store-run.51jk.com";
    }

    public void SendEmailTo51(String merchant_name){
        List<MerchantApplyDto> applys = transformBusinessCategory(merchantMapper.getAllApplyList());
        MerchantApplySendEmailJobMessage sendEmailJobMessage = new MerchantApplySendEmailJobMessage();
        SendMailTo51Dto sendMailTo51Dto = new SendMailTo51Dto();
        sendMailTo51Dto.setMerchant_name(merchant_name);
        sendMailTo51Dto.setMerchantApplyDtos(applys);
        sendEmailJobMessage.setSmd(sendMailTo51Dto);
        merchantApplySendEmailJob.sendMail(sendEmailJobMessage);
    }

    public void SendEmailToMerchant(Integer merchant_id,Map<String,String> map){
        YbMerchant ybMerchant = merchantMapper.selectByMerchantId(merchant_id);
        MerchantApply merchantApply = merchantApplyMapper.getByMerchantId(merchant_id);
        MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(merchant_id);
        String seller_pwd = map.get("seller_pwd");
        if("".equals(seller_pwd)){
            messageForApply.sendFailMessageToMerchant(ybMerchant,seller_pwd);
            MerchantApplySendEmailJobMessage sendEmailJobMessage = new MerchantApplySendEmailJobMessage();
            SendFailedMailToMerchantDto failedMailToMerchantDto = new SendFailedMailToMerchantDto();
            failedMailToMerchantDto.setLegal_name(ybMerchant.getLegal_name());
            failedMailToMerchantDto.setReason(map.get("reason"));
            failedMailToMerchantDto.setAccepter(merchantApply.getDocking_email());
            sendEmailJobMessage.setSfmmd(failedMailToMerchantDto);
            merchantApplySendEmailJob.sendMail(sendEmailJobMessage);
        }else {
            messageForApply.sendSuccessMessageToMerchant(ybMerchant,merchantExt,seller_pwd);
            MerchantApplySendEmailJobMessage sendEmailJobMessage = new MerchantApplySendEmailJobMessage();
            SendSuccessMailToMerchantDto successMailToMerchantDto = new SendSuccessMailToMerchantDto();
            successMailToMerchantDto.setAccepter(merchantApply.getDocking_email());
            successMailToMerchantDto.setMerchant(ybMerchant);
            successMailToMerchantDto.setMerchantExt(merchantExt);
            successMailToMerchantDto.setSeller_pwd(seller_pwd);
            sendEmailJobMessage.setSsmmd(successMailToMerchantDto);
            merchantApplySendEmailJob.sendMail(sendEmailJobMessage);
        }
    }

    public PageInfo<MerchantApplyDto> selectApplys(String company_name,String legal_name,String legal_mobile,String approval_status,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<MerchantApplyDto> applys = merchantMapper.selectApplys(company_name, legal_name, legal_mobile, approval_status);
        List<MerchantApplyDto> merchantApplyDtos = transformBusinessCategory(applys);
        return new PageInfo<>(merchantApplyDtos);
    }

    private List<MerchantApplyDto> transformBusinessCategory(List<MerchantApplyDto> applys){
        for(int j=0;j<applys.size();j++){
            String[] split = applys.get(j).getBusiness_category().split(",");
            String business_category = "";
            for (int i=0;i< split.length;i++){
                business_category = business_category + businessCategoryMapper.getById(Integer.parseInt(split[i])).getCategory_name() + " ";
            }
            applys.get(j).setBusiness_category(business_category);
        }
        return applys;
    }

    public Map<String,String> approvalAllow(Integer merchant_id, Integer approval_status,String reason){
        Map<String,String> map = new HashMap<String,String>();
        String seller_pwd = "";
        Integer site_status = SITE_STATUS_FAIL;
        if(approval_status == 0){
            seller_pwd = getInitPassword();
            site_status = SITE_STATUS_SUCCESS;
        }
        merchantMapper.approvalAllow(merchant_id,approval_status,sha2(seller_pwd),site_status);
        map.put("seller_pwd",seller_pwd);
        map.put("reason",reason);
        return map;
    }

    public void update(MerchantApply merchantApply){
        merchantApplyMapper.update(merchantApply);
    }

    public void signContract(Integer merchant_id, Integer is_sign ,String sign_contract){
        merchantMapper.signContract(merchant_id,is_sign,sign_contract);
    }


}
