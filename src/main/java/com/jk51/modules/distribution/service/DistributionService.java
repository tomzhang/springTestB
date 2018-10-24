package com.jk51.modules.distribution.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.distribute.*;
import com.jk51.modules.distribution.mapper.*;
import com.jk51.modules.distribution.request.WithdrawRecordAdd;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * Created by Administrator on 2017/2/8.
 */
@Service
public class DistributionService {

    @Autowired
    private DistributorMapper distributorMapper;

    @Autowired
    private OperationRecodMapper operationRecodMapper;

    @Autowired
    private DWithdrawAccountMapper withdrawAccountMapper;

    @Autowired
    private DWithdrawRecordMapper withdrawRecordMapper;

    @Autowired
    private DistributeMoneyRecordMapper distributeMoneyRecordMapper;
    @Autowired
    private DistributorExtMapper distributorExtMapper;

    @Autowired
    private RefereeListMapper refereeListMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private RecruitMapper recruitMapper;
    @Autowired
    private DDisappconfigMapper disappconfigMapper;

    /**
     * 查询所有的分销商
     *
     */
    public List<Distributor> getAllDistributor(Map<String,Object>params){
        List<Distributor> list = distributorMapper.getList(params);
        return list;
    }

    public Map<String, Object> getDistributorByID(int id,int siteId){
        return distributorMapper.getDistributorByID(id, siteId);
    }

    public Map<String, Object> getDistributorBySiteId(int siteId){
        return distributorMapper.getDistributorBySiteId(siteId);
    }

    public List<Map<String,Object>> selectBindUsers(){
        return distributorMapper.selectBindUsers();
    }

    public Integer setDistributorStore(int siteId, int isOpen){
        if(getDistributorBySiteId(siteId)!=null){
            return distributorMapper.setDistributorStore(siteId, isOpen);
        }else{
            return distributorMapper.createDistributorStore(siteId, isOpen);
        }

    }

    public Map<String,String> updateDistributor(int id, int siteId, int status, String note){

        Map<String, Object> distributor = distributorMapper.getDistributorInfoByID(id,siteId);

        int result = distributorMapper.updateDistributor(id, siteId, status,note);

        String msg = "";

        if(result > 0){

            msg = "success";

            String distributorJson = JacksonUtils.mapToJson(distributor);

            saveDistributorChangeRecode(id,status,note,distributorJson);
        }else{
            msg = "数据更新失败";
        }
        Map<String,String> resultMap = new HashMap<String,String>();

        resultMap.put("msg",msg);

        return resultMap;
    }

    private void saveDistributorChangeRecode(int id, int status,String note,String distributorJson){
        OperationRecond record = new OperationRecond();
        record.setD_id(id);
        record.setAutding_status(status);
        record.setRemark(note);
        record.setSnapshot(distributorJson);
        record.setUpdate_time(new Timestamp(System.currentTimeMillis()));
        operationRecodMapper.insert(record);
    }


    public List<OperationRecond> getDistributorChangeRecord(int d_id, int siteId) {
        return  operationRecodMapper.getDistributorChangeRecord(d_id,siteId);
    }

    public Map<String,Object> getDistributor(Map<String,Object > param){
        //String buyerId=String.valueOf(param.get("buyerId"));
//        int realPay=tradesMapper.getPayMoney(Integer.parseInt(buyerId));
        Map<String,Object> dis=distributorMapper.getDistributor(param);
        return dis;
    }

    public  int activateDistributor(int id,int siteId ){
        return this.distributorMapper.updateDistributor(id ,siteId,1,null);
    }

    @Transactional
    public int createDistributor(Map<String,Object> param){
        //查询当前分销商
        Map<String,Object > distribution=distributorMapper.getDistributor(param);
        //根据邀请码获取分销商扩展表信息
        DistributorExt distributorExt=distributorExtMapper.selectByCode(param.get("code").toString());
        //验证邀请码是否有效
        int result=0;
        if(null != distributorExt){
            //为当前用户绑定上级，生成邀请码
            Map<String,Object> ext=new HashMap();
            ext.put("did",distribution.get("id").toString());
            ext.put("parentId",distributorExt.getDid().toString());
            //ext.put("invitation_code",createVerificationCode(6));
            result=distributorExtMapper.updateByDisSelective(ext);
            if(result >0 ){
                //获取上级分销root
                Map<String,Object > distributor=distributorMapper.getDistributorInfoByID(distributorExt.getDid(),Integer.parseInt(distribution.get("owner").toString()));
                int root =Integer.parseInt(distributor.get("is_root").toString());
                distributorMapper.updateDistributorRoot(root+1,Integer.parseInt(distribution.get("id").toString()),Integer.parseInt(distribution.get("owner").toString()));
            }
        }

        return  result;
    }

    public Map<String,String> getDistributorInfoByMobile(int siteId, String mobile){
        return distributorMapper.getDistributorInfoByMobile(siteId, mobile);
    }

    public Integer withdrawAccountAdd(Integer distributorId,int owner,String name, String type, String account,String bankName){
        DWithdrawAccount DWAccount = new DWithdrawAccount();
        DWAccount.setDistributorId(distributorId);
        DWAccount.setOwner(owner);
        DWAccount.setName(name);
        DWAccount.setType(type);
        DWAccount.setAccount(account);
        DWAccount.setBandName(bankName);
        return withdrawAccountMapper.withdrawAccountAdd(DWAccount);
    }

    public List<DWithdrawAccount> withdrawAccountList(int siteId,int distributorId){
        return withdrawAccountMapper.getAccountList(siteId,distributorId);
    }

    //    public Integer withdrawRecordAdd(Integer distributorId,Integer siteId,Integer amount,Integer remainingMoney,){
    public Integer withdrawRecordAdd(WithdrawRecordAdd recordAdd){
        System.out.println(recordAdd.getTradesId());
        System.out.println(recordAdd.getRemainingMoney());
        DdistributorMoneyRecord ddistributorMoneyRecord = new DdistributorMoneyRecord();
        ddistributorMoneyRecord.setDistributorId(Long.valueOf(recordAdd.getDistributorId()));
        ddistributorMoneyRecord.setOwner(Long.valueOf(recordAdd.getOwner()));
        ddistributorMoneyRecord.setChangeMoney(Long.valueOf(recordAdd.getAmount()));
        ddistributorMoneyRecord.setRemainingMoney(Long.valueOf(recordAdd.getRemainingMoney()));
        ddistributorMoneyRecord.setType(2);
        ddistributorMoneyRecord.setFromDid(Long.valueOf(0));
        ddistributorMoneyRecord.setStatus(1);

        if(distributeMoneyRecordMapper.insertDistributorMoneyRecord(ddistributorMoneyRecord) > 0){
            DWithdrawRecord dWithdrawRecord = new DWithdrawRecord();
            dWithdrawRecord.setDistributorId(recordAdd.getDistributorId());
            dWithdrawRecord.setTradesId(recordAdd.getTradesId());
            dWithdrawRecord.setAccount(recordAdd.getAccount());
            dWithdrawRecord.setOwner(recordAdd.getOwner());
            dWithdrawRecord.setType(1);
            dWithdrawRecord.setMoneyRecordId(ddistributorMoneyRecord.getId().intValue());
            dWithdrawRecord.setAmount(recordAdd.getAmount());
            dWithdrawRecord.setWithdrawStyle(recordAdd.getWithdrawStyle());
            dWithdrawRecord.setWithdrawFee(recordAdd.getWithdrawFee());
            dWithdrawRecord.setTotalFee(recordAdd.getWithdrawFee());
            dWithdrawRecord.setPayStatus(2);
            dWithdrawRecord.setSettlementStatus(recordAdd.getSettlementStatus());
            dWithdrawRecord.setCheckingStatus(recordAdd.getCheckingStatus());
            return withdrawRecordMapper.withdrawRecordAdd(dWithdrawRecord);
        }else{
            return 0;
        }

    }

    public List<Map<String,Object>> getMyTeam(Map<String,Object>params){
        return distributorMapper.getMyTeam(params);
    }

    public List<Integer> getDistributorId(Map<String,Object>params){
        return distributorMapper.getDistributorId(params);
    }

    public  String createVerificationCode(int verificationCodeLength)
    {
        String[] verificationCodeArrary={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                "A","B","C","D","E","F","G","H","I","J", "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
        };
        String verificationCode = "";
        Random random = new Random();
        for(int i=0;i<verificationCodeLength;i++){verificationCode += verificationCodeArrary[random.nextInt(verificationCodeArrary.length)];}
        return verificationCode;
    }

    public DDisappconfig findConfig(Integer siteId) {
        return disappconfigMapper.findBySiteId(siteId);
    }
}