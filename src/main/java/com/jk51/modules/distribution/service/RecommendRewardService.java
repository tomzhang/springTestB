package com.jk51.modules.distribution.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.distribute.DdistributorMoneyRecord;
import com.jk51.model.distribute.Reward;
import com.jk51.modules.distribution.constants.DistributionConstants;
import com.jk51.modules.distribution.mapper.*;
import com.jk51.modules.distribution.result.*;
import com.jk51.modules.distribution.util.PageFormat;
import com.jk51.modules.distribution.util.ParamFormat;
import com.jk51.modules.order.mapper.DistributeOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 16:06
 * 修改记录:
 */
@Service
public class RecommendRewardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendRewardService.class);

    @Autowired
    private DistributeMoneyRecordMapper distributeMoneyRecordMapper;
    @Autowired
    private DWithdrawAccountMapper dWithdrawAccountMapper;
    @Autowired
    private DWithdrawCashSettingMapper dWithdrawCashSettingMapper;
    @Autowired
    private RewardMapper rewardMapper;
    @Autowired
    private DistributorMapper distributorMapper;
    @Autowired
    private DistributeOrderMapper distributeOrderMapper;
    @Autowired
    private RefereeListMapper refereeListMapper;
    @Autowired
    private DWithdrawRecordMapper dWithdrawRecordMapper;

    /**
     * 查询推荐人奖励列表页面数据
     * @param map
     * @return
     */
    public Map<String, Object> getDistributorMoneyList(Map<String,Object> map){

        map.put("siteId", ParamFormat.formatToInteger(map.get("siteId")));
        //参数转换
        map = PageFormat.pageFormat(map);

        Map<String,Object> resultMap = new HashMap();

        Page page = null;
        DistributorReward rewardTotal= null;

        //判断是否有查询条件(旧版查询记录表)
//        if(map.get("mobile") == null  || StringUtil.isBlank((String)map.get("mobile"))){
//
//            page = getDistributorMoneyPageList(map);
//            rewardTotal = getDistributorMoneyTotal((Integer)map.get("siteId"));
//
//        }else{
//
//            page = getDistributorMoneyPageList(map);
//
//            //获取商户奖励总计
//            if(page.getData() != null && ((List<DistributorReward>)page.getData()).size()>0 ){
//                rewardTotal = ((List<DistributorReward>)page.getData()).get(0);
//            }
//
//        }

        //生成分页数据
        page = getDistributorMoneyPageList(map);
        //奖励总计
        rewardTotal = refereeListMapper.selectRefereeListTotal(map);

        resultMap.put("page",page);
        resultMap.put("rewardTotal",rewardTotal);

        LOGGER.info("商户siteId:"+map.get("siteId")+"的分销商奖励分页数据查询成功");

        return resultMap;

    }

    /**
     * 查询推荐人奖励分页列表
     * @param map
     * @return
     */
    public Page getDistributorMoneyPageList(Map<String,Object> map){

        Page page = new Page();

//        //查询当前页（旧版查询记录表）
//        List<DistributorReward> distributorRewardList = distributeMoneyRecordMapper.selectDistributorMoneyList(map);
//
//        //查询总数（旧版查询记录表）
//        Long distributorRewardCount = getDistributorMoneyPageCount(map);

        //查询当前页
        List<DistributorReward> distributorRewardList = refereeListMapper.selectRefereeLists(map);

        //查询总数
        Long distributorRewardCount = refereeListMapper.selectRefereeListCount(map);

        return PageFormat.createPage(distributorRewardCount,map,distributorRewardList);

    }

    /**
     * 查询推荐人奖励总数
     * @param map
     * @return
     */
    public Long getDistributorMoneyPageCount(Map<String,Object> map){
        return distributeMoneyRecordMapper.selectDistributorMoneyListCount(map);
    }

    /**
     * 查询商户下推荐人奖励总计
     * @param siteId
     * @return
     */
    public DistributorReward getDistributorMoneyTotal(Integer siteId){
        return distributeMoneyRecordMapper.selectDistributorMoneyTotal(siteId);
    }

    /**
     * 查询推荐人奖励列表页面数据
     * @param map
     * @return
     */
    public Map<String, Object> getDistributorMoneyDetailList(Map<String,Object> map){

        map.put("siteId", ParamFormat.formatToInteger(map.get("siteId")));

        //参数转换
        map = PageFormat.pageFormat(map);

        final Map<String,Object> map1 = map;

        Map<String,Object> resultMap = new HashMap();

        //获取奖励列表
        List<DistributorRewardDetail> distributorMoneyDetailList = getDistributorMoneyDetailPageList(map);

        if(map.get("mobile") != null){

            //获取分销商id
            Integer distributorId = distributorMapper.selectDistributorIdByUsername((Integer) map.get("siteId"),(String) map.get("mobile"));
            //获取订单二级分佣的分销商
            List<Integer> distributor2Array = distributorMapper.selectCommissionLevelDistributorIdList(Arrays.asList(ParamFormat.formatToInteger(distributorId)));
            //获取订单三级分佣的分销商
            List<Integer> distributor3Array = distributor2Array == null || distributor2Array.size() < 1 ? new ArrayList<>() : distributorMapper.selectCommissionLevelDistributorIdList(distributor2Array);

            distributorMoneyDetailList.forEach(distributorMoneyDetail -> {

                if(distributorMoneyDetail.getType() == 1){
                    Map<String,Object> disMap= distributorMapper.selectDistributorById((Integer) map1.get("siteId"),distributorMoneyDetail.getFromDid());//.get("mobile");
                    if ((Objects.nonNull(disMap) && Objects.nonNull(disMap.get("mobile")))) {
                        distributorMoneyDetail.setOrderUsername((String) (disMap.get("mobile")));
                    }
                    //判断来自订单分佣的几级奖励
                    if(distributor2Array.contains(distributorMoneyDetail.getFromDid())){
                        distributorMoneyDetail.setCommissionLevel("二级");
                    }else if(distributor3Array.contains(distributorMoneyDetail.getFromDid())){
                        distributorMoneyDetail.setCommissionLevel("三级");
                    }else {
                        distributorMoneyDetail.setCommissionLevel("一级");
                    }

                }

            });
        }

        //获取奖励总个数
        Long distributorMoneyDetailCount = getDistributorMoneyDetailListCount(map);

        if(map.get("type") != null && ParamFormat.formatToInteger(map.get("type")) == 2 && ParamFormat.formatToInteger(map.get("status")) == null){
            map.put("status",2);
        }

        //获取奖励总数
        Long distributorMoneyDetailTotal =getDistributorMoneyDetailListTotal(map);

        //创建分页数据
        Page page = PageFormat.createPage(distributorMoneyDetailCount,map,distributorMoneyDetailList);

        resultMap.put("totalMoney",distributorMoneyDetailTotal);
        resultMap.put("page",page);

        LOGGER.info("商户siteId:"+map.get("siteId")+"的分销商奖励详情分页数据查询成功");

        return resultMap;

    }

    /**
     * 查询推荐人奖励列表页面数据
     * @param map
     * @return
     */
    public Map<String, Object> getDistributorMoneyDetailListUnconfirmed(Map<String,Object> map){

        map.put("siteId", ParamFormat.formatToInteger(map.get("siteId")));

        //参数转换
        map = PageFormat.pageFormat(map);

        final Map<String,Object> map1 = map;

        Map<String,Object> resultMap = new HashMap();

        //获取分销商id
        Integer distributorId = distributorMapper.selectDistributorIdByUsername((Integer) map.get("siteId"),(String) map.get("mobile"));
        //获取订单二级分佣的分销商
        List<Integer> distributor2Array = distributorMapper.selectCommissionLevelDistributorIdList(Arrays.asList(ParamFormat.formatToInteger(distributorId)));
        //获取订单三级分佣的分销商
        List<Integer> distributor3Array = distributor2Array == null || distributor2Array.size() < 1 ? new ArrayList<>() : distributorMapper.selectCommissionLevelDistributorIdList(distributor2Array);

        List<Integer> distributors = new LinkedList<Integer>();
        distributors.add(distributorId);
        if(distributor2Array != null) distributors.addAll(distributor2Array);
        if(distributor3Array != null) distributors.addAll(distributor3Array);

        map.put("distributorIdList", distributors);

        //获取奖励列表
        List<DistributorRewardDetail> distributorMoneyDetailList = distributeMoneyRecordMapper.selectDistributorMoneyDetailUnconfirmed(map);

        distributorMoneyDetailList.forEach(distributorMoneyDetail -> {

            distributorMoneyDetail.setMobile((String) map1.get("mobile"));

            //判断来自订单分佣的几级奖励
            if(distributor2Array.contains(distributorMoneyDetail.getDistributorId())){
                distributorMoneyDetail.setCommissionLevel("二级");
                distributorMoneyDetail.setChangeMoney(distributorMoneyDetail.getAwardTwo());
            }else if(distributor3Array.contains(distributorMoneyDetail.getDistributorId())){
                distributorMoneyDetail.setCommissionLevel("三级");
                distributorMoneyDetail.setChangeMoney(distributorMoneyDetail.getAwardThree());
            }else {
                distributorMoneyDetail.setCommissionLevel("一级");
                distributorMoneyDetail.setChangeMoney(distributorMoneyDetail.getAwardOne());
            }

        });

        //获取奖励总数
        //Long distributorMoneyDetailTotal = distributeMoneyRecordMapper.selectDistributorMoneyDetailListTotalUnconfirmed(map);
        Long distributorMoneyDetailTotal=Long.valueOf(distributorMoneyDetailList.stream().mapToInt(DistributorRewardDetail::getChangeMoney).sum());

        //创建分页数据
        Page page = PageFormat.createPage(Long.valueOf(distributorMoneyDetailList.size()),map,distributorMoneyDetailList);

        resultMap.put("totalMoney",distributorMoneyDetailTotal);
        resultMap.put("page",page);

        LOGGER.info("商户siteId:"+map.get("siteId")+"的分销商奖励详情未确认奖励分页数据查询成功");

        return resultMap;

    }

    /**
     * 查询推荐人奖励详情分页列表
     * @param map
     * @return
     */
    public List<DistributorRewardDetail> getDistributorMoneyDetailPageList(Map<String,Object> map){
        return distributeMoneyRecordMapper.selectDistributorMoneyDetailList(map);
    }

    /**
     * 查询推荐人奖励详情总数
     * @param map
     * @return
     */
    public Long getDistributorMoneyDetailListCount(Map<String,Object> map){
        return distributeMoneyRecordMapper.selectDistributorMoneyDetailListCount(map);
    }

    public Long getDistributorMoneyDetailListTotal(Map<String,Object> map){
        return distributeMoneyRecordMapper.selectDistributorMoneyDetailListTotal(map);
    }

    /**
     * 获取分销商提现账户信息
     * @param siteId
     * @param distributorId
     * @return
     * @throws Exception
     */
    public DWithdrawAccount getWithdrawAccount(Long recordId,Integer siteId, Integer distributorId)throws Exception{

        DWithdrawAccount dWithdrawAccount = dWithdrawAccountMapper.selectWithdrawAccountByRecordIdAndDistributorId(recordId,siteId,distributorId);

        if(dWithdrawAccount == null){
            throw new NullPointerException("该用户账户异常");
        }

        return dWithdrawAccount;
    }

    /**
     * 财务处理
     * @param id
     * @param siteId
     * @param status 待确认1，确认提现2，不确认提现3
     * @return
     * @throws Exception
     */
    @Transactional
    public Integer financeOperation(Long id,Integer siteId,Integer status,String remark) throws Exception{

        DistributorRewardDetail distributorRewardDetail = distributeMoneyRecordMapper.selectDistributorRewardDetailById(id,siteId);

        Long accountBalance = null;

        if(distributorRewardDetail.getType() == DistributionConstants.WITHDRAW_TYPE){

            //判断奖励明细状态，处理奖励明细
            if(DistributionConstants.SUCCESS_STATUS == status){
                //type 提现
                accountBalance = updateRefereeList(siteId,Long.valueOf(distributorRewardDetail.getDistributorId()),Long.valueOf(distributorRewardDetail.getChangeMoney()), DistributionConstants.WITHDRAW_TYPE);
            }else if(DistributionConstants.FAIL_STATUS == status){

                dWithdrawRecordMapper.updateWithdrawRecordPayStatus(id,siteId,DistributionConstants.PAY_STATUS_FAIL);

            }else if( DistributionConstants.WAIT_STATUS == status ) {
                LOGGER.error("奖励提现记录不能输入待确认状态，不能确认, id:" + id + " , status:" + status + " , remark:" + remark);
                return 2;
            }else {
                LOGGER.error("输入的奖励提现记录确认状态不符合规则, id:" + id + ", status:" + status + " , remark:" + remark);
                return 3;
            }

        }else{
            LOGGER.error("奖励提现记录类型不正确，不能确认, id:" + id + " , status:" + status + " , remark:" + remark);
            return 4;
        }

        LOGGER.info("商户siteId:"+siteId+"的财务处理成功,id:"+ id +", status:"+status);

        return distributeMoneyRecordMapper.updateDistributorMoneyDetailById(id,siteId,status,remark,accountBalance);

    }

    /**
     * 查询最小提现金额
     * @param siteId
     * @return
     */
    public Integer getWithdrawMinMoney(Integer siteId){
        return dWithdrawCashSettingMapper.selectWithdrawMinMoneyBySiteId(siteId);
    }

    /**
     * 修改最小提现金额
     * @param siteId
     * @param minMoney
     * @return
     */
    public Integer setWithdrawMinMoney(Integer siteId,Integer minMoney){
        return dWithdrawCashSettingMapper.updateWithdrawMinMoneyBySiteId(siteId,minMoney);
    }

    /**
     * 确认奖励操作
     * @param siteId
     * @param distributorId
     * @param id
     * @param rewardStatus 奖励状态：0-待确认 1-已确认
     * @return
     * @throws Exception
     */
    @Transactional
    public Integer confirmOrderRewardStatus(Integer siteId , Integer distributorId, Integer id, Integer rewardStatus) throws Exception{

        Reward reward = rewardMapper.selectRewardById(siteId,distributorId,id);
        if(Objects.isNull(reward)){
            LOGGER.error("订单奖励不能输入待确认状态,id:" + id + " ,reward_status:" + rewardStatus);
            return 2;
        }

/*        //判断订单奖励的确认状态
        if( DistributionConstants.REWARD_WATI_STATUS == rewardStatus){
            LOGGER.error("订单奖励不能输入待确认状态,id:" + id + " ,reward_status:" + rewardStatus);
            return 2;
        }else if( DistributionConstants.REWARD_OK_STATUS == rewardStatus){

        }else {
            LOGGER.error("输入的订单奖励确认状态不符合规则,id:" + id + " ,reward_status:" + rewardStatus);
            return 3;
        }
        //如果奖励状态不为0，oldRewardStatus（页面原状态也不为零）不进行奖励结算
        if( DistributionConstants.REWARD_WATI_STATUS != reward.getReward_status()){
            LOGGER.error("订单奖励信息异常，不能确认,id:" + id + " ,reward_status:" + rewardStatus);
            return 4;
        }*/

//        Map<String,Object> distributor1 =  distributorMapper.selectDistributorById(siteId,distributorId);
//        Map<String,Object> distributor2 = null;
//        Map<String,Object> distributor3 = null;
//
//        DdistributorMoneyRecord ddistributorMoneyRecord1 = null;
//        DdistributorMoneyRecord ddistributorMoneyRecord2 = null;
//        DdistributorMoneyRecord ddistributorMoneyRecord3 = null;
//
//        String mobile1  = distributor1.get("mobile").toString();
//        Map<String,Object> map1 = new HashMap<>();
//        map1.put("siteId", siteId);
//        map1.put("mobile", mobile1);
//
//        Integer remainingCount1 = 0;
//        try {
//            remainingCount1 = distributeMoneyRecordMapper.selectDistributorMoneyList(map1).get(0).getRemainingCount();
//        } catch (Exception e) {
//
//        }
//
//        ddistributorMoneyRecord1 = packageDdistributorMoneyRecord(reward,distributor1,(Long) distributor1.get("distributorId"));
//        ddistributorMoneyRecord1.setChangeMoney(reward.getLevel_1_award());
//        ddistributorMoneyRecord1.setRemainingMoney(reward.getLevel_1_award()+remainingCount1);
//
//        if((Integer)distributor1.get("isRoot") != 0){
//
//            distributor2 =  distributorMapper.selectDistributorById(siteId,distributor1.get("parentId"));
//
//            String mobile2  = distributor2.get("mobile").toString();
//            Map<String,Object> map2 = new HashMap<>();
//            map2.put("siteId", siteId);
//            map2.put("mobile", mobile2);
//
//            Integer remainingCount2 = 0;
//            try {
//                remainingCount2 = distributeMoneyRecordMapper.selectDistributorMoneyList(map2).get(0).getRemainingCount();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            ddistributorMoneyRecord2 = packageDdistributorMoneyRecord(reward,distributor2,(Long) distributor1.get("distributorId"));
//            ddistributorMoneyRecord2.setChangeMoney(reward.getLevel_2_award());
//            ddistributorMoneyRecord2.setRemainingMoney(reward.getLevel_2_award()+remainingCount2);
//            ddistributorMoneyRecord2.setStatus(2);
//
//            if((Integer)distributor2.get("isRoot") != 0){
//
//                distributor3 = distributorMapper.selectDistributorById(siteId,distributor2.get("parentId"));
//
//                String mobile3  = distributor3.get("mobile").toString();
//                Map<String,Object> map3 = new HashMap<>();
//                map3.put("siteId", siteId);
//                map3.put("mobile", mobile3);
//                Integer remainingCount3 = 0;
//                try {
//                    remainingCount3 = distributeMoneyRecordMapper.selectDistributorMoneyList(map3).get(0).getRemainingCount();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                ddistributorMoneyRecord3 = packageDdistributorMoneyRecord(reward,distributor3,(Long) distributor1.get("distributorId"));
//                ddistributorMoneyRecord3.setChangeMoney(reward.getLevel_3_award());
//                ddistributorMoneyRecord3.setRemainingMoney(reward.getLevel_3_award()+remainingCount3);
//                ddistributorMoneyRecord3.setStatus(2);
//
//
//            }
//
//        }

        //进行分佣-创建奖励记录
        if(createDdistributorMoneyRecord(siteId,id,rewardStatus,createDdistributorMoneyRecordArray(siteId,Long.valueOf(distributorId),Long.valueOf(distributorId),reward,1,new ArrayList<DdistributorMoneyRecord>()).toArray(new DdistributorMoneyRecord[3])) == 1){
            LOGGER.error("订单奖励确认成功,id:" + id + " ,rewardStatus:" + rewardStatus +" ,distributorId:" + distributorId);
            return 1;
        }else{
            throw new Exception();
        }

    }


    /**
     * 确认奖励操作
     * @param siteId
     * @param ids
     * @param rewardStatus 奖励状态：0-待确认 1-已确认
     * @return
     * @throws Exception
     */
    @Transactional
    public Integer batchConfirmOrderRewardStatus(Integer siteId , String  ids, Integer rewardStatus) throws Exception{

        int count=0;
        if ((StringUtil.isNotEmpty(ids))) {
            String [] rids=ids.split(",");
            for (String rid : rids) {
                Integer id=Integer.valueOf(rid);
                Reward reward = rewardMapper.selectRewardById(siteId,null,id);
               if(Objects.nonNull(reward)){
                   //如果奖励状态不为0，oldRewardStatus（页面原状态也不为零）不进行奖励结算
                   if( DistributionConstants.REWARD_WATI_STATUS != reward.getReward_status()){
                       LOGGER.error("订单奖励信息异常，不能确认,id:" + id + " ,reward_status:" + rewardStatus);
                       return 4;
                   }

                   //进行分佣-创建奖励记录
                   if(createDdistributorMoneyRecord(siteId,id,rewardStatus,createDdistributorMoneyRecordArray(siteId,Long.valueOf(reward.getDistributor_id()),Long.valueOf(reward.getDistributor_id()),reward,1,new ArrayList<DdistributorMoneyRecord>()).toArray(new DdistributorMoneyRecord[3])) == 1){
                       LOGGER.error("订单奖励确认成功,id:" + id + " ,rewardStatus:" + rewardStatus +" ,distributorId:" + reward.getDistributor_id());
                       count++ ;
                   }else{
                       throw new Exception();
                   }
               }
            }
        }
        return  count;


    }

    /**
     * 生成奖励记录实例
     * @param siteId
     * @param distributorId
     * @param fromDid
     * @param reward
     * @param distributorLevel 1-当前分销商，2-上一级分销商，3上上一级分销商（分佣关系等级）
     * @param ddistributorMoneyRecords
     * @return
     */
    private List<DdistributorMoneyRecord> createDdistributorMoneyRecordArray(Integer siteId ,Long distributorId,Long fromDid,Reward reward,int distributorLevel,List<DdistributorMoneyRecord> ddistributorMoneyRecords) throws Exception {

        Map<String,Object> distributor =  distributorMapper.selectDistributorById(siteId,distributorId);
//        Map<String,Object> map = new HashMap<>();
//        map.put("siteId", siteId);
//        map.put("mobile", distributor.get("mobile").toString());
//
//        Integer remainingCount = 0;
//        try {
//            remainingCount = distributeMoneyRecordMapper.selectDistributorMoneyList(map).get(0).getRemainingCount();
//        } catch (Exception e) {
//
//        }

        if(!Optional.ofNullable(distributor).isPresent()){
            throw new RuntimeException("根据该订单所属的分销商ID查不到相应的分销商信息。");
        }

        //封装奖励记录
        DdistributorMoneyRecord ddistributorMoneyRecord = packageDdistributorMoneyRecord(reward,distributor,fromDid);
        //判断获得几级分佣的奖励金额
        ddistributorMoneyRecord.setChangeMoney(DistributionConstants.DISTRIBUTOR_LEVEL_1 == distributorLevel ? reward.getLevel_1_award() : DistributionConstants.DISTRIBUTOR_LEVEL_2 == distributorLevel ? reward.getLevel_2_award() : reward.getLevel_3_award() );
        //计算该奖励的分销商账户余额
        ddistributorMoneyRecord.setRemainingMoney(updateRefereeList(siteId,distributorId,ddistributorMoneyRecord.getChangeMoney(),DistributionConstants.REWARD_TYPE));

        //加入奖励记录的集合
        ddistributorMoneyRecords.add(ddistributorMoneyRecord);

        //判断是否是顶级分销商，并控制只分佣3层
        if( DistributionConstants.ROOT_DISTRIBUTOR_CODE != ParamFormat.formatToInteger(distributor.get("isRoot"))  && distributorLevel < 3){
            //分佣层级计数
            distributorLevel++;
            //递归调用，生成奖励录
            return createDdistributorMoneyRecordArray(siteId,ParamFormat.formatToLong(distributor.get("parentId")),fromDid,reward,distributorLevel,ddistributorMoneyRecords);
        }else {
            return ddistributorMoneyRecords;
        }
    }

    /**
     *
     * @param siteId
     * @param distributorId
     * @param changeMoney
     * @param type 操作类型  1：奖励  2：提现
     * @return
     */
    private Long updateRefereeList(Integer siteId,Long distributorId,Long changeMoney,int type){

        List<RefereeList> refereeLists = refereeListMapper.selectRefereeList(siteId,distributorId);
        RefereeList refereeList=new RefereeList();
        if(refereeLists.size() >0 ){
            refereeList  = refereeLists.get(0);
            //计算分销商账户余额（奖励）
            refereeList.setAccountBalance( DistributionConstants.REWARD_TYPE == type ?  refereeList.getAccountBalance() + changeMoney : refereeList.getAccountBalance() - changeMoney);
            //计算分销商支出金额（奖励）
            refereeList.setTotalExpenditure(DistributionConstants.WITHDRAW_TYPE == type ? refereeList.getTotalExpenditure() + changeMoney : refereeList.getTotalExpenditure());
            //计算分销商收入金额（奖励）
            refereeList.setTotalIncomeAmount(DistributionConstants.REWARD_TYPE == type ? refereeList.getTotalIncomeAmount() + changeMoney : refereeList.getTotalIncomeAmount());

            refereeListMapper.updateRefereeList(refereeList);

            return refereeList.getAccountBalance();
        }
        return  null;




    }

    /**
     * 生成奖励记录同步更新奖励状态
     * @param ddistributorMoneyRecords
     * @return
     */
    @Transactional
    public Integer createDdistributorMoneyRecord(Integer siteId, Integer id,Integer rewardStatus,DdistributorMoneyRecord... ddistributorMoneyRecords){

        //插入奖励记录
        Arrays.asList(ddistributorMoneyRecords).forEach(ddistributorMoneyRecord -> {

            if(ddistributorMoneyRecord != null){
                distributeMoneyRecordMapper.insertDistributorMoneyRecord(ddistributorMoneyRecord);
            }

        });

        //同步更新奖励状态
        return rewardMapper.updateRewardStatus(siteId,id,rewardStatus);

    }

    /**
     * 用于封装奖励记录(订单确认分佣时使用)
     * @param reward
     * @param distributor
     * @param fromDid
     * @return
     */
    private DdistributorMoneyRecord packageDdistributorMoneyRecord(Reward reward,Map<String,Object> distributor,Long fromDid){

        DdistributorMoneyRecord ddistributorMoneyRecord = new DdistributorMoneyRecord();

        ddistributorMoneyRecord.setDistributorId((Long) distributor.get("distributorId"));
        ddistributorMoneyRecord.setRewardId(reward.getId());
        ddistributorMoneyRecord.setFromDid(fromDid);
        ddistributorMoneyRecord.setOwner(reward.getOwner());
        ddistributorMoneyRecord.setType(DistributionConstants.REWARD_TYPE);
        ddistributorMoneyRecord.setStatus(DistributionConstants.SUCCESS_STATUS);
        ddistributorMoneyRecord.setOrderStatus(reward.getOrder_status());
        ddistributorMoneyRecord.setTradeId(reward.getOrder_id());

        return ddistributorMoneyRecord;

    }

    /**
     * 获取分销商id
     * @param siteId
     * @param distributorName
     * @return
     */
    public Integer getDistritorIdByUsername(Integer siteId , String distributorName){

        return distributorMapper.selectDistributorIdByUsername(siteId,distributorName);

    }

    /**
     * 获取分销商账户总计
     * @param siteId
     * @param distributorId
     * @return
     */
    public List<Map<String,Object>> getReferrerAccountTotal(Integer siteId , Integer distributorId) throws Exception{

        if(distributorId == null || distributorId <= 0){

            throw new Exception("distributorId不存在，查询失败");

        }

        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>() ;//distributeMoneyRecordMapper.selectReferrerAccountTotal(siteId,distributorId);

        Map<String,Object> map = new HashMap<>();

        map.put("siteId",siteId);
        map.put("distributorId",distributorId);
        map.put("rewardStatus",0);

        int total_no = distributeMoneyRecordMapper.selectRewardTotal(map);
        LOGGER.info("map 奖励总额 {}", total_no);

        //未确认奖励
        Map<String,Object> wait_reward_map = new HashMap<>();

        wait_reward_map.put("owner",siteId);
        wait_reward_map.put("distributorId",distributorId);
        wait_reward_map.put("changeMoneyTotal", total_no);
        wait_reward_map.put("type",1);
        wait_reward_map.put("status",1);

        result.add(wait_reward_map);

        //未确认提现
        Map<String,Object> withdrawMap = distributeMoneyRecordMapper.selectReferrerTotal(siteId,distributorId,DistributionConstants.WITHDRAW_TYPE,DistributionConstants.WAIT_STATUS);

        if(!StringUtil.isEmpty(withdrawMap)){
            result.add(withdrawMap);
        }

        List<RefereeList> refereeLists = refereeListMapper.selectRefereeList(siteId,Long.valueOf(distributorId));


        if(refereeLists != null && refereeLists.size() == 1){

            RefereeList refereeList = refereeLists.get(0);

            //已确认奖励
            if(refereeList.getTotalIncomeAmount() != null && refereeList.getTotalIncomeAmount() > 0){

                Map<String,Object> map2 = new HashMap<>();

                map2.put("owner",siteId);
                map2.put("distributorId",distributorId);
                map2.put("changeMoneyTotal",refereeList.getTotalIncomeAmount());
                map2.put("type",1);
                map2.put("status",2);

                result.add(map2);

            }
            //未确认奖励
            if(refereeList.getTotalExpenditure() != null && refereeList.getTotalExpenditure() > 0){

                Map<String,Object> map3 = new HashMap<>();

                map3.put("owner",siteId);
                map3.put("distributorId",distributorId);
                map3.put("changeMoneyTotal",refereeList.getTotalExpenditure());
                map3.put("type",2);
                map3.put("status",2);

                result.add(map3);

            }

        }

        return result;

    }

    /**
     * 获取分销商账户总计
     * @param map
     * @return
     * @throws Exception
     */
    public Map<String,Object> getRecommendOrderDetailList(Map<String,Object> map) throws Exception{

        map.put("siteId", ParamFormat.formatToInteger(map.get("siteId")));
        //参数转换
        map = PageFormat.pageFormat(map);

        Map<String,Object> resultMap = new HashMap();

        //获取分销商id
        Integer distributorId = ParamFormat.formatToInteger(map.get("distributorId"));
        //获取它的下级分销商们id
        List<Integer> distributorId2Array = distributorMapper.selectCommissionLevelDistributorIdList(Arrays.asList(distributorId));
        //获取它的下下分销商们id
        List<Integer> distributorId3Array = distributorId2Array == null || distributorId2Array.size() < 1 ? new ArrayList<>() : distributorMapper.selectCommissionLevelDistributorIdList(distributorId2Array);

        List<Integer> distributorIdAll = new ArrayList();

        distributorIdAll.add(distributorId);

        distributorIdAll.addAll(distributorId2Array);
        distributorIdAll.addAll(distributorId3Array);

        map.put("distributorIdList",distributorIdAll);

        //分页数据
        List<Map<String,Object>> distributorMoneyDetailList = getRecommendOrderList(map);

        distributorMoneyDetailList.forEach(item -> item.put("orderId",item.get("orderId").toString()));

        //金额总计
        Long RecommendOrderCount = getRecommendOrderListCount(map);

        //创建分页数据
        Page page = PageFormat.createPage(RecommendOrderCount,map,distributorMoneyDetailList);

        resultMap.put("page",page);

        LOGGER.info("商户siteId:"+map.get("siteId")+"的分销商奖励订单详情分页数据查询成功");

        return resultMap;

    }

    public List<Map<String,Object>> getRecommendOrderList(Map<String,Object> map){
        return  distributeOrderMapper.selectRecommendOrderList(map);
    }

    public Long getRecommendOrderListCount(Map<String,Object> map){
        return  distributeOrderMapper.selectRecommendOrderListCount(map);
    }
    
    public Map<String,Object> getMyReward(Integer siteId, Integer distributorId){
        Map<String,Object> map = new HashMap<>();
        System.out.println(distributorId);
        int total_reward = distributeMoneyRecordMapper.getMyTotalReward(siteId,distributorId, -1);
//        Integer unconfirmed_reward = distributeMoneyRecordMapper.getMyTotalReward(siteId,distributorId,1);
        Integer withdraw_reward = distributeMoneyRecordMapper.getWithdraw(siteId,distributorId,2);
        map.put("total_reward", total_reward);
        map.put("unconfirmed_reward",total_reward-withdraw_reward);
        map.put("withdraw_reward",withdraw_reward);
//        
        return map;
    }

}
