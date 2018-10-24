package com.jk51.modules.task.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.SimplePage;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.task.*;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.SelectGoodsService;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.task.domain.BTaskStatus;
import com.jk51.modules.task.domain.RewardRule;
import com.jk51.modules.task.domain.dto.BTaskDTO;
import com.jk51.modules.task.domain.dto.QueryBTaskDTO;
import com.jk51.modules.task.mapper.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    public static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    TCounttypeMapper tCounttypeMapper;

    @Autowired
    TQuotaMapper tQuotaMapper;

    @Autowired
    TCounttypeMapper counttypeMapper;

    @Autowired
    BTaskBlobMapper bTaskBlobMapper;

    @Autowired
    private BTaskcountMapper bTaskcountMapper;

    @Autowired
    private BTaskrewardMapper bTaskrewardMapper;

    @Autowired
    private BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SelectGoodsService selectGoodsService;

    @Autowired
    private StoreAdminMapper adminMapper;

    @Autowired
    private StoresMapper storesMapper;

    @Autowired
    TExaminationMapper tExaminationMapper;


    private static final String[] MONEY_TYPE_TARGET = {"3","4","5","6"};

    public List<BTask> find(QueryBTaskDTO queryCondition) {
        BTaskExample example = new BTaskExample();
        BTaskExample.Criteria criteria = example.createCriteria();
        if (Objects.nonNull(queryCondition.getPageNum()) && Objects.nonNull(queryCondition.getPageSize())) {
            PageHelper.startPage(queryCondition.getPageNum(), queryCondition.getPageSize());
        }

        if (Objects.nonNull(queryCondition.getStatus())) {
            criteria.andStatusEqualTo(queryCondition.getStatus());
        }

        if (Objects.nonNull(queryCondition.getId())) {
            criteria.andIdEqualTo(queryCondition.getId());
        } else if (CollectionUtils.isNotEmpty(queryCondition.getIdList())) {
            criteria.andIdIn(queryCondition.getIdList());
        }

        return bTaskMapper.selectByExample(example);
    }

    public BTask selectByPrimaryKey(Integer id) {
        return bTaskMapper.selectByPrimaryKey(id);
    }

    /**
     * 更改状态（批量）
     * @param id
     * @param status
     * @return
     */
    public Map<String, Object> changeStatus(int[] id, byte status){
        Map<String, Object> result = new HashMap<>();
        int[] ids;
        int count = 0;
        Map<String, Object> checkResult = checkInTaskPlan(id);
        String successId = checkResult.get("succesIds").toString();
        String falseId = checkResult.get("falseIds").toString();
        if (!successId.equals("")) {
            ids = Arrays.stream(successId.split(",")).mapToInt(taskId -> Integer.parseInt(taskId)).toArray();
            BTaskExample bTaskExample = new BTaskExample();
            BTaskExample.Criteria criteria = bTaskExample.createCriteria();
            criteria.andIdIn(Arrays.stream( ids ).boxed().collect(Collectors.toList()));
            BTask bTask = new BTask();
            bTask.setStatus(status);

            count = bTaskMapper.updateByExampleSelective(bTask, bTaskExample);
        }


        result.put("successIds", successId);
        result.put("falseIds", falseId);
        result.put("seccessCount", count);

        return result;
    }


    /**
     * 检查taskPlan中进行的任务中是否有此任务id
     * @param id
     * @return
     */
    public Map<String, Object> checkInTaskPlan(int[] id){
        Map<String, Object> result = new HashMap();
        StringBuffer falseIds = new StringBuffer("");
        StringBuffer successIds = new StringBuffer("");
        for (int i=0; i<id.length; i++){
            if (0 == bTaskplanMapper.taskIsExist(id[i])){
                successIds.append(id[i]);
                successIds.append(",");
            }else {
                falseIds.append(id[i]);
                falseIds.append(",");
            }
        }

        if (!falseIds.toString().equals("")){
        falseIds = falseIds.deleteCharAt(falseIds.length()-1);}
        if (!successIds.toString().equals("")){
        successIds = successIds.deleteCharAt(successIds.length()-1);}

        result.put("falseIds", falseIds);
        result.put("succesIds", successIds);
        return result;
    }

    @Transactional(rollbackFor = BusinessLogicException.class)
    public boolean add(BTaskDTO bTaskDTO) throws BusinessLogicException {
        try {
            ObjectMapper objectMapper = JacksonUtils.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            BTask bTask = objectMapper.convertValue(bTaskDTO, BTask.class);
            bTask.setId(null);

            boolean flag = bTaskMapper.insertSelective(bTask) != 0;

            BTaskBlob bTaskBlob = bTaskDTO.getTaskBlob();
            final int[] NONE_BLOG = {7,8};
            if (Optional.ofNullable(bTaskBlob).isPresent()) {
                bTaskBlob.setTaskId(bTask.getId());
                if (Arrays.binarySearch(NONE_BLOG,bTask.getTargetId()) > -1) {
                    bTaskBlob.setIsAll((byte)2);
                }
                return bTaskBlobMapper.insertSelective(bTaskBlob) != 0 && flag;
            } else {
                return flag;
            }
        } catch (Exception e) {
            throw new BusinessLogicException(e);
        }
    }

    public boolean update(BTaskDTO bTaskDTO) {
        BTask bTask = JacksonUtils.getInstance().convertValue(bTaskDTO, BTask.class);
        return bTaskMapper.updateByPrimaryKeySelective(bTask) != 0;
    }

    /**
     * 根据id列表查找没有被删除的btask
     *
     * @param taskidList
     * @return
     */
    public List<BTask> findBtaskNotDeleteInIds(List<Integer> taskidList) {
        if (CollectionUtils.isEmpty(taskidList)) {
            return null;
        }

        BTaskExample bTaskExample = new BTaskExample();
        BTaskExample.Criteria bTaskCriteria = bTaskExample.createCriteria();
        bTaskCriteria.andIdIn(taskidList);
        bTaskCriteria.andStatusNotEqualTo(BTaskStatus.SOFT_DELETE.getValue());

        return bTaskMapper.selectByExample(bTaskExample);
    }

    public List<TCounttype> findCountTypeList(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        TCounttypeExample tCounttypeExample = new TCounttypeExample();
        TCounttypeExample.Criteria tCounttypeCriteria = tCounttypeExample.createCriteria();
        tCounttypeCriteria.andIdIn(ids);

        return tCounttypeMapper.selectByExample(tCounttypeExample);
    }

    /**
     * 任务回显
     * @param id
     * @return
     */
    public Map<String, Object> taskDetail(Integer id){
        //获取任务详情
        Map<String, Object> result = bTaskMapper.taskDetail(id);
        //处理统计类型名
        StringBuffer typeNames = typeName(result);
        result.put("typeNames", typeNames);

        //奖励规则
        RewardRule rewardRule = rewardRule(result);
        result.put("rewardRule", rewardRule);

//        //惩罚条件（金额类型）
//        if(Optional.ofNullable(result.get("targetId")).isPresent() && Arrays.asList(MONEY_TYPE_TARGET).contains(result.get("targetId").toString()) ){
//            result.put("lowTarget",convertMoney(result.get("lowTarget")));
//        }
//        //奖励类型的处理
//        if(Optional.ofNullable(result.get("rewardType")).isPresent() && result.get("rewardType").toString().equals("人民币")){
//            //奖励总限额
//            result.put("rewardLimit",convertMoney(result.get("rewardLimit")));
//            //惩罚
//            result.put("punish",convertMoney(result.get("punish")));
//        }

        //判断是否有指定商品
        if(Optional.ofNullable(result.get("goodsId")).isPresent() && StringUtil.isNotBlank(result.get("goodsId").toString())){
            String[] goodsIdArray = result.get("goodsId").toString().split(",");
            result.put("goodsNum",goodsIdArray.length);
            result.remove("goodsId");
        }else {
            result.put("goodsNum",0);
        }

        try {
            if(Optional.ofNullable(result.get("examinationId")).isPresent()){
                TExamination tExamination = tExaminationMapper.selectByPrimaryKey(Integer.parseInt(result.get("examinationId").toString()));
                result.put("examinationDetail",new HashMap<String,Object>(){{
                    put("id",tExamination.getId());
                    put("title",tExamination.getTitle());
                    put("secondTotal",tExamination.getSecondTotal());
                    put("minuteTotal",(tExamination.getSecondTotal()-1)/60 + 1);
                    put("questNum",tExamination.getQuestNum());
                }});
            }
        } catch (Exception e) {
            logger.error("试卷详情获取错误examinationId: {} ,{}",result.get("examinationId"),e);
        }

        return result;
    }

    public Map<String,Object> getTaskSpectifyGoods(Integer taskId,final Integer pageSize,Integer pageNum,Integer siteId){

        BTaskBlob taskBlob = bTaskBlobMapper.selectByTaskId(taskId);

        Map<String,Object> result = new HashMap<String,Object>();

        //校验是否存在指定商品
        if(Optional.ofNullable(taskBlob).isPresent()){

            if(taskBlob.getIsAll() == 0 && StringUtil.isNotBlank(taskBlob.getGoodsId())){

                //指定商品id数组
                String[] goodsIds = taskBlob.getGoodsId().split(",");
                //分页的指定商品id数组
                String[] goodsIdsPage = new String[pageSize];

                if((pageSize * (pageNum - 1)) <= goodsIds.length){
                    //获取分页的指定商品id
                    for(int i = 0,j = (pageNum-1) * pageSize; j< pageNum * pageSize && j<goodsIds.length; i++,j++){
                        goodsIdsPage[i] = goodsIds[j];
                    }
                    //获取指定商品详情集合
                    List<Map<String,String>> goodsList  = goodsMapper.getGoodsList(new HashMap<String,Object>(){{
                        put("goodsIds",goodsIdsPage);
                        put("siteId",siteId);
                    }});
                    //商品详情获取图片
                    goodsList.stream().parallel().forEach(map -> {
                            Map<String,Object> params = new HashMap<>();
                            if (!map.containsKey("imgHash") || "".equals(map.get("imgHash"))){
                                params.put("goodsId",map.get("goodsId"));
                                params.put("siteId",siteId);
                                String goodsImg = goodsMapper.getDefaultImg(params);

                                if(goodsImg != "" && goodsImg != null){
                                    System.out.println(goodsImg);
                                    map.put("imgHash",goodsImg);
                                }else{
                                    logger.info("siteId:{},goodsId:{}：没有图片",siteId,map.get("goodsId"));
                                }
                            }
                        }
                    );
                    result.put("isAll",taskBlob.getIsAll());
                    result.put("goodsPage",new HashMap<String,Object>(){{
                        put("list",goodsList);
                        put("total",goodsIds.length);
                        put("pages",(goodsIds.length-1)/pageSize + 1);
                    }});
                }

            }else if( taskBlob.getIsAll() == 1){
                result = selectGoodsService.getGoodsList(new HashMap<String,Object>(){{
                    put("siteId",siteId);
                    put("startRow",pageNum);
                    put("pageSize",pageSize);
                }});
            }

        }
        return result;
    }

    //金额处理
    public Object convertMoney(Object money){

        try {
            if(Optional.ofNullable(money).isPresent()){
                money = Integer.parseInt(money.toString())/100;
            }
        } catch (Exception e) {

        }
        return money;
    }

    //统计类型
    public StringBuffer typeName(Map<String, Object> map){

        if(Optional.ofNullable(map.get("typeIds").toString()).isPresent() && StringUtils.isNotBlank(map.get("typeIds").toString())){
            String typeIds = map.get("typeIds").toString();
            StringBuffer typeNames = new StringBuffer("");

            int[] ids = Arrays.stream(typeIds.split(",")).mapToInt(taskId -> Integer.parseInt(taskId)).toArray();
            List<TCounttype> tCounttypes = findCountTypeList(Arrays.stream( ids ).boxed().collect(Collectors.toList()));
            tCounttypes.parallelStream().forEach( tCounttype -> {
                typeNames.append(tCounttype.getName());
                typeNames.append(",");
            });
            typeNames.deleteCharAt(typeNames.length()-1);

            return typeNames;
        }
        return new StringBuffer("线下数据");

    }

    //奖励规则
    public RewardRule rewardRule(Map<String, Object> map){
        String rewardDetail = map.get("rewardDetail").toString();
        try {
            RewardRule rewardRule = JacksonUtils.json2pojo(rewardDetail, RewardRule.class);

//            //人民币奖励规则处理
//            if(Optional.ofNullable(map.get("rewardType")).isPresent() && map.get("rewardType").toString().equals("人民币")){
//
//                RewardRule.Detail detail = rewardRule.getDetail();
//
//                RewardRule.Detail[] ladders = rewardRule.getLadders();
//
//                //满足条件处理
//                if(Optional.ofNullable(detail).isPresent()){
//                    //条件（金额类型）
//                    if(Optional.ofNullable(map.get("targetId")).isPresent() && Arrays.asList(MONEY_TYPE_TARGET).contains(map.get("targetId").toString()) ){
//                        if(Optional.ofNullable(detail.getCondition()).isPresent()) detail.setCondition(detail.getCondition()/100);
//                    }
//                    //奖励（金额类型）
//                    if(Optional.ofNullable(detail.getReward()).isPresent()) detail.setReward(detail.getReward()/100);
//                    //封顶（金额类型）
//                    if(Optional.ofNullable(detail.getTopLimit()).isPresent()) detail.setTopLimit(detail.getTopLimit()/100);
//                    rewardRule.setDetail(detail);
//                }
//                //阶梯条件处理
//                if(Optional.ofNullable(ladders).isPresent()){
//                    for(int i=0; i<ladders.length; i++){
//                        RewardRule.Detail _detail = ladders[i];
//                        //条件（金额类型）
//                        if(Optional.ofNullable(map.get("targetId")).isPresent() && Arrays.asList(MONEY_TYPE_TARGET).contains(map.get("targetId").toString()) ){
//                            if(Optional.ofNullable(_detail.getCondition()).isPresent()) _detail.setCondition(_detail.getCondition()/100);
//                        }
//                        //封顶（金额类型）
//                        if(Optional.ofNullable(_detail.getReward()).isPresent()) _detail.setReward(_detail.getReward()/100);
//                        ladders[i]=_detail;
//                    }
//                    rewardRule.setLadders(ladders);
//                }
//            }

            return rewardRule;
        } catch (Exception e) {
            logger.error("奖励规则json格式解析错误",e);
        }
        return null;
    }

    /**
     * 任务列表
     * @param param
     * @return
     */
    public List<Map<String, Object>> queryTaskList(Map<String, Object> param){
        Integer pageNum = Integer.parseInt(param.get("pageNum").toString());
        Integer pageSize = Integer.parseInt(param.get("pageSize").toString());
        //查询条件任务时间处理
        if (param.containsKey("startTime")){
            param.put("startTime",param.get("startTime").toString() + " 00:00:00");
        }
        if (param.containsKey("endTime")){
            param.put("endTime",param.get("endTime").toString() + " 23:59:59");
        }
        PageHelper.startPage(pageNum, pageSize);
        //获取任务列表数据
        List<Map<String, Object>> result = bTaskMapper.queryTaskList(param);
        result.parallelStream().forEach(map -> {
            //处理统计类型名
            StringBuffer typeNames = typeName(map);
            map.put("typeNames", typeNames);

            //奖励规则
            RewardRule rewardRule = rewardRule(map);
            map.put("rewardRule", rewardRule);
//            //惩罚条件（金额类型）
//            if(Optional.ofNullable(map.get("targetId")).isPresent() && Arrays.asList(MONEY_TYPE_TARGET).contains(map.get("targetId").toString()) ){
//                map.put("lowTarget",convertMoney(map.get("lowTarget")));
//            }
//            //奖励类型的处理
//            if(Optional.ofNullable(map.get("rewardType")).isPresent() && map.get("rewardType").toString().equals("人民币")){
//                //奖励限额
//                map.put("rewardLimit",convertMoney(map.get("rewardLimit")));
//                //惩罚
//                map.put("punish",convertMoney(map.get("punish")));
//            }

        });
        return result;
    }

    @Transactional
    public Map<String, Object> getQuotaGroupAndCounttype(Map<String,Object> params){
        return new HashMap<String,Object>(){{
            put("groupId", params.get("groupId"));
            put("counttype",tCounttypeMapper.selectTCounttypeByByGroupId(params));
            put("quotaGroup",tQuotaMapper.selectQuotaGroupByGroupId(params));
        }};
    }

//    public List<Integer> queryPlanIdsByCompleteStatus(Map<String,Object> map){
    public Map<String,List> queryPlanIdsByCompleteStatus(Map<String,Object> map){

        Map<String,List> result = new HashMap<>();

        logger.info("mapsssss:{}",map);
        List<Map<String,Object>> list = bTaskExecuteMapper.selectTaskListByJoinId(map);  //当前店员和门店的正在进行中的所有任务

        list.stream().forEach(task->{
            System.out.println(task);
            task.put("completeStatus",0);
            try{
                map.put("executeId",task.get("execut_id"));
                logger.info("queryPlanIdsByCompleteStatus.countValue参数:execut_id:{}，joinType:{}，joinId:{}，storeId:{}",task.get("execut_id"),task.get("object"),map.get("joinId"),map.get("storeId"));
                map.put("joinType",task.get("object"));
                int countValue = queryTaskCountByExecuteId(map);

                if(task.containsKey("reward_detail") && !"".equals(task.get("reward_detail"))){
                    Map rewardDetail = JacksonUtils.json2map(task.get("reward_detail").toString());  //奖励模
                    Map detail = JacksonUtils.json2map(JacksonUtils.obj2json(rewardDetail.get("detail")));
                    List<Map> ladders = JacksonUtils.json2list(JacksonUtils.obj2json(rewardDetail.get("ladders")),Map.class);

                    if(detail.containsKey("condition")){  //按比例

                        int get = (int)Math.floor(countValue/Integer.valueOf(detail.get("condition").toString()));
                        if(get > 0){
                            task.put("completeStatus",1);
                        }

                    }else if(ladders.size() > 0){  //阶梯

                        OptionalInt maxTag =ladders.stream().parallel().filter(x->Integer.valueOf(x.get("condition").toString())<=countValue).mapToInt(x->Integer.valueOf(x.get("reward").toString())).max();
                        if(maxTag.isPresent()){
                            task.put("completeStatus",1);  //标记这个任务已经完成
                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        List<Integer>executeIds = new LinkedList<>();
        int complete = Integer.valueOf(map.get("complete").toString());
        List<Integer>planIds =  list.parallelStream().filter(task->Integer.valueOf(task.get("completeStatus").toString()) == complete).map(task->Integer.valueOf(task.get("plan_id").toString())).distinct().collect(Collectors.toList());

        if(complete == 0){
            executeIds = list.parallelStream().filter(task->Integer.valueOf(task.get("completeStatus").toString()) == 1).map(task->Integer.valueOf(task.get("execut_id").toString())).distinct().collect(Collectors.toList());
        }else{
            executeIds = list.parallelStream().filter(task->Integer.valueOf(task.get("completeStatus").toString()) == 0).map(task->Integer.valueOf(task.get("execut_id").toString())).distinct().collect(Collectors.toList());
        }

        map.remove("executeId");
        map.remove("joinType");

        result.put("executeIds",executeIds);
        result.put("planIds",planIds);
        logger.info("idsL:{}",result);
        return result;
    }

    public List<Map<String,Object>> queryTaskListByJoinId(Map<String,Object>param){
        List<Map<String,Object>> list = new LinkedList<>();
        logger.info("参数1：{}",param);

        if(param.containsKey("complete")){
            Map<String,List> ids = queryPlanIdsByCompleteStatus(param);

            List<Integer> planIds = ids.get("planIds");
            List<Integer> executeIds = ids.get("executeIds");
            if(CollectionUtils.isEmpty(planIds)) {
                return list;
            }

            if(executeIds.size() > 0){
                param.put("executeIds",executeIds);
            }
            param.put("planIds",planIds);
        }

        logger.info("参数2：{}",param);
        int pageNum = NumberUtils.toInt(String.valueOf(param.get("pageNum")), 1);
        int pageSize = NumberUtils.toInt(String.valueOf(param.get("pageSize")), 5);
        SimplePage.startPage(pageNum, pageSize);
        list = bTaskExecuteMapper.selectTaskListByJoinId(param);  //当前店员和门店的正在进行中的所有任务
        final List<Integer> targetList = Arrays.asList(3,4,5,6);
        list.stream().forEach(task->{
            System.out.println(task);
            float getReward = 0.f;  //完成任务量获得的奖励
            Map map = new HashMap();
            map.put("siteId",param.get("siteId"));
            map.put("executeId",task.get("execut_id"));
            if(NumberUtils.toInt(task.get("object").toString()) == 10){
                map.put("joinId",param.get("storeId"));
            }else{
                map.put("joinId",param.get("joinId"));
            }

            Map<String,Integer> rewardMap = bTaskrewardMapper.queryTaskRewardByJoinId(map);
            task.put("getReward", 0);

            task.put("isEmpty", 0);

            if(!StringUtil.isEmpty(rewardMap)){
                task.put("getReward",  rewardMap.get("reward"));
            }
            String max = "+∞";
            String lowDesc = "";
            String desc = "";
            String limitDesc = "任务总奖励额上不封顶";
            task.put("completeStatus",0);
            task.put("joinType",task.get("object"));
            task.put("complete",0);
            try{
                param.put("executeId",task.get("execut_id"));
                if(!StringUtil.isEmpty(task.get("type_ids"))){
                    String typeId = task.get("type_ids").toString();
                    String[] typeIds = typeId.split(",");
                    List<String> typeIdList = Arrays.asList(typeIds);
                    List<Integer> ids = typeIdList.parallelStream().map(x->Integer.valueOf(x)).collect(Collectors.toList());
                    String countType = tCounttypeMapper.conactTypeName(ids);
                    task.put("countType",countType);

                }
                else{
                    task.put("countType","线下数据");
                }
                int rewardType = Integer.valueOf(task.get("reward_type").toString());
                logger.info("countValue参数：execut_id:{}，joinType:{}，joinId:{}，storeId:{}",task.get("execut_id"),task.get("object"),param.get("joinId"),param.get("storeId"));
                param.put("joinType",task.get("object"));
                int countValue = queryTaskCountByExecuteId(param);
                int completeCount = Arrays.asList(MONEY_TYPE_TARGET).contains(task.get("target_id").toString())?countValue/100:countValue;
                param.remove("joinType");
                task.put("completeCount",completeCount);
                task.put("complete",countValue);
                task.put("type",0);
                if(task.containsKey("reward_detail") && !"".equals(task.get("reward_detail"))){
                    Map rewardDetail = JacksonUtils.json2map(task.get("reward_detail").toString());  //奖励模板
                    if(rewardDetail.containsKey("type") && Integer.valueOf(rewardDetail.get("type").toString()) > 0){
                        int type = Integer.valueOf(rewardDetail.get("type").toString());
                        task.put("type",type);
                    }

                    int rewardLimit = Integer.valueOf(task.get("reward_limit").toString());
                    int low = Integer.valueOf(task.get("low_target").toString());
                    String pont = Integer.valueOf(task.get("reward_type").toString()) == 10?"元":"绩效";
                    if(low > 0 && low >= countValue){
                        lowDesc = "不满"+(Integer.valueOf(task.get("reward_type").toString()) == 10?low/100:low)+pont+"罚"+task.get("punish");
                    }

                    Map detail = JacksonUtils.json2map(JacksonUtils.obj2json(rewardDetail.get("detail")));
                    List<Map> ladders = JacksonUtils.json2list(JacksonUtils.obj2json(rewardDetail.get("ladders")),Map.class);


                    task.put("topLimit",0);
                    if(detail.containsKey("condition")){  //按比例
                        int condition = NumberUtils.toInt(detail.get("condition").toString());
                         if(condition > 0){
                             int get = (int)Math.floor(countValue/condition);
                             if(targetList.contains(Integer.valueOf(task.get("target_id").toString()))){
                                 detail.put("condition",Integer.valueOf(detail.get("condition").toString())/100);
                             }
                             if(rewardType == 10){
                                 detail.put("reward",Integer.valueOf(detail.get("reward").toString())/100);
                                 if(detail.containsKey("topLimit")){
                                     detail.put("topLimit",Integer.valueOf(detail.get("topLimit").toString())/100);
                                 }
                             }
                             List<Map> details = Arrays.asList(detail);
                             task.put("detail",details);

                             int reward = Integer.valueOf(detail.get("reward").toString());

                             int limit = 0;
                             desc = "每满"+detail.get("condition").toString()+"奖"+reward+pont;

                             if(detail.containsKey("topLimit")){
                                 max = detail.get("topLimit").toString();
                                 limit = Integer.valueOf(max);
                                 limitDesc = "任务总奖励额"+max+pont;
                                 task.put("topLimit",detail.get("topLimit").toString());
                             }

                             if(get > 0){
                                 task.put("completeStatus",1);
                             }
                         }

                    }else if(ladders.size() > 0){  //阶梯
                        ladders.stream().forEach(lad->{
                            if(targetList.contains(Integer.valueOf(task.get("target_id").toString()))){
                                lad.put("condition",Integer.valueOf(lad.get("condition").toString())/100);
                            }
                            if(rewardType == 10){
                                lad.put("reward",Integer.valueOf(lad.get("reward").toString())/100);
                                if(lad.containsKey("topLimit")){
                                    lad.put("topLimit",Integer.valueOf(lad.get("topLimit").toString())/100);
                                }
                            }
                        });
                        task.put("detail",ladders);
                        for (Map ladder:ladders) {
                            String reward = ladder.get("reward").toString();
//                            desc += "满"+ladder.get("condition")+"奖励"++pont+",";
                            desc += "满"+ladder.get("condition")+"奖励"+(Integer.valueOf(task.get("reward_type").toString()) == 10?Integer.valueOf(reward)/100:reward )+pont+",";
                        }
                        OptionalInt maxTag =ladders.stream().parallel().filter(x->Integer.valueOf(x.get("condition").toString())<=countValue).mapToInt(x->Integer.valueOf(x.get("reward").toString())).max();
                        if(maxTag.isPresent()){
                            task.put("completeStatus",1);  //标记这个任务已经完成
                        }

                        Optional<Integer> mm = ladders.parallelStream().map(x->Integer.valueOf(x.get("reward").toString())).reduce(Math::max);

                        if(mm.isPresent()){
                            max = mm.get().toString();
                            limitDesc = "任务总奖励额"+(Integer.valueOf(task.get("reward_type").toString()) == 10?Integer.valueOf(max)/100:max )+pont;
                        }
                    }
                }
                String day_num = task.get("day_num").toString().replaceAll(","," ");
                task.put("day_num",day_num);
                task.put("max",max);
                task.put("desc",desc);
                task.put("lowDesc",lowDesc);
                task.put("limitDesc",limitDesc);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        return list;
    }

    public int queryTaskCountByExecuteId(Map<String,Object>param) {
        return bTaskcountMapper.queryTaskCountByExecuteId(param);
    }
    //查询我的奖励
    public List<Map<String,Object>> queryMayRewards(Map map) {
        return bTaskMapper.queryMyRewards(map);

    }

    public int queryTaskCountByJoinId(Map<String,Object>param){
        return bTaskExecuteMapper.queryTaskCountByJoinId(param);
    }

    public List<Map<String,Object>> queryTaskRewardList(Map<String,Object>param){
        return bTaskrewardMapper.queryTaskRewardList(param);
    }
     public List<Integer> queryPlanIdsByJoin(Map<String,Object>param){
        return bTaskplanMapper.queryPlanIdsByJoin(param);
    }

    public Map<String,Object> queryCounter(Map<String,Object>param){
        Map<String,Object> result = new HashMap<>();
        int joinId = Integer.valueOf(param.get("joinId").toString());
        param.put("type",20);
        param.put("isTopCount",1);
        param.remove("joinId");
        List<Map<String,Object>> list = taskCountList(param);

        List<Map> currentList = list.stream().filter(m->Integer.valueOf(m.get("joinId").toString()) == joinId).collect(Collectors.toList());
        if(currentList.size() > 0){
            //当前用户
            Map current = list.stream().filter(m->Integer.valueOf(m.get("joinId").toString()) == joinId).collect(Collectors.toList()).get(0);
            System.out.println(current);
            List<Integer> completeCountList = list.parallelStream().map(m->Integer.valueOf(m.get("completeCount").toString())).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            List<Double> moneyRewardList = list.parallelStream().map(m->Double.valueOf(m.get("moneyReward").toString())).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            List<Integer> performanceRewardList = list.parallelStream().map(m->Integer.valueOf(m.get("performanceReward").toString())).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            System.out.println(current.get("completeCount") );
            System.out.println(completeCountList);
            int sorted = completeCountList.indexOf(Integer.valueOf(current.get("completeCount").toString()))+1;
            int sortedM = moneyRewardList.indexOf(Double.valueOf(current.get("moneyReward").toString()))+1;
            int sortedP = performanceRewardList.indexOf(Integer.valueOf(current.get("performanceReward").toString()))+1;
            Map<String,Object> task = new HashMap(){{put("total",current.get("total"));put("sorted",sorted);put("complete",current.get("completeCount").toString());}};
            Map<String,Object> money = new HashMap(){{put("total",current.get("moneyRewardTotal"));put("sorted",sortedM);put("complete",current.get("moneyReward"));}};
            Map<String,Object> performance = new HashMap(){{put("total",current.get("performanceRewardTotal"));put("sorted",sortedP);put("complete",current.get("performanceReward").toString());}};
            result.put("task",task);
            result.put("money",money);
            result.put("performance",performance);

        }else{
            Map<String,Object> task = new HashMap(){{put("total",0);put("sorted",0);put("complete",0);}};
            Map<String,Object> money = new HashMap(){{put("total",0);put("sorted",0);put("complete",0);}};
            Map<String,Object> performance = new HashMap(){{put("total",0);put("sorted",0);put("complete",0);}};
            result.put("task",task);
            result.put("money",money);
            result.put("performance",performance);
        }
        return result;
    }

    public Map<String,Object> sortList(Map<String,Object>param){
        Map<String,Object> result = new HashMap<>();

        int joinType = Integer.valueOf(param.get("joinType").toString());

        int joinId = joinType == 20 ? Integer.valueOf(param.get("joinId").toString()):Integer.valueOf(param.get("storeId").toString());

        List<Map<String,Object>> list = taskCountList(param);

        List<Map<String,Object>> listDesc = list.parallelStream().sorted((p1,p2)->Integer.valueOf(p2.get("complete").toString()).compareTo(Integer.valueOf(p1.get("complete").toString()))).collect(Collectors.toList());
        result.put("items",listDesc.stream().limit(3).collect(Collectors.toList()));
        List<Map> currentList = listDesc.stream().filter(m->Integer.valueOf(m.get("joinId").toString()) == joinId).collect(Collectors.toList());

        Map<String,Object>map = new HashMap<>();
        if(currentList.size()>0){
            map = currentList.get(0);
            map.put("sorted",listDesc.indexOf(map)+1);
        }
        result.put("current",map);

        return  result;
    }

    public List<Map<String,Object>> taskConpleteById(Map<String,Object>param){
        List<Map<String,Object>> list = new LinkedList<>();
        return list;
    }

    public List<Map<String,Object>> taskCountList(Map<String,Object>param){
        List<Map<String,Object>> list = new LinkedList<>();

        List<Map<String,Object>> adminList = new LinkedList<>();

        if(param.containsKey("joinType") && Integer.valueOf(param.get("joinType").toString()) == 10){//门店任务
            int siteId = Integer.valueOf(param.get("siteId").toString());
            adminList = storesMapper.queryStoresBySiteId(siteId);

        }else{//头部统计或店员任务
            adminList = adminMapper.queryAdminInfoAllByStore(Integer.valueOf(param.get("siteId").toString()),Integer.valueOf(param.get("storeId").toString()));
        }

        adminList.stream().forEach(admin->{
            Map<String,Object> map = new HashMap<>();
            if(admin.containsKey("stores_number")){
                param.put("storeId",admin.get("id"));
            }else{
                param.put("joinId",admin.get("id"));
            }
            List<Integer> planIds = queryPlanIdsByJoin(param);
            if(planIds.size() > 0){
                if(param.containsKey("start")){
                    param.put("activeType",20);
                }else{
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    Date start = new Date();
                    Date end = new Date(start.getTime()+24*60*60*1000);
                    param.put("start",sf.format(start));
                    param.put("end",sf.format(start));
                }

                param.put("planIds",planIds);

                List<Map<String,Object>> taskList = queryTaskListByJoinId(param);
                int total = taskList.size();
                if(param.containsKey("isTopCount")){
                    taskList = taskList.stream().filter(task->Integer.valueOf(task.get("object").toString()) == 20).collect(Collectors.toList());
                }
                if(taskList.size() > 0){
                    //已经完成的任务数量
                    long completeCount = taskList.parallelStream().filter(task->Integer.valueOf(task.get("completeStatus").toString())== 1).count();
                    long complete = 0;
                    if(param.containsKey("taskId")){
                        int taskId = Integer.valueOf(param.get("taskId").toString());
                        //已经完成的数量
                        taskList.parallelStream().forEach(task->{

                        });
                        complete = taskList.parallelStream().filter(task->Integer.valueOf(task.get("id").toString())== taskId).map(task->Integer.valueOf(task.get("completeCount").toString())).reduce(0,Integer::sum);
                    }
                    map.put("complete",complete);

                    //人民币奖励  reward_type 10
                    double moneyReward = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString()) > 0).filter(task->Integer.valueOf(task.get("completeStatus").toString())== 1).filter(task->Integer.valueOf(task.get("reward_type").toString())==10).mapToDouble(task->Double.valueOf(task.get("getReward").toString())).reduce(0,Double::sum);
                    //人民币阶梯
                    Optional tag = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString()) > 0).filter(task->Integer.valueOf(task.get("reward_type").toString())==10).filter(task->Integer.valueOf(task.get("type").toString())==10).filter(task->Integer.valueOf(task.get("topLimit").toString())==0).findFirst();
                    if(tag.isPresent()){
                        map.put("moneyRewardTotal","+∞");
                    }else{
                        long moneyRewardTotal = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString()) > 0).filter(task->Integer.valueOf(task.get("reward_type").toString())==10).mapToInt(task-> NumberUtils.toInt(task.get("max").toString())).reduce(0,Integer::sum);
                        map.put("moneyRewardTotal",moneyRewardTotal);
                    }
//
                    //按绩效
                    long performanceReward = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString())>0).filter(task->Integer.valueOf(task.get("completeStatus").toString())== 1).filter(task->Integer.valueOf(task.get("reward_type").toString())==20).mapToInt(task->Integer.valueOf(task.get("getReward").toString())).reduce(0,Integer::sum);

                    //是否存在阶梯
                    Optional tagx = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString())>0).filter(task->Integer.valueOf(task.get("reward_type").toString())==20).filter(task->Integer.valueOf(task.get("type").toString())==10).filter(task->Integer.valueOf(task.get("topLimit").toString())==0).findFirst();

                    if(tagx.isPresent()){
                        map.put("performanceRewardTotal","+∞");
                    }else{
                        long   performanceRewardTotal = taskList.parallelStream().filter(task->Integer.valueOf(task.get("type").toString())>0).filter(task->Integer.valueOf(task.get("reward_type").toString())==20).mapToInt(task->NumberUtils.toInt(task.get("max").toString())).reduce(0,Integer::sum);
                        map.put("performanceRewardTotal",String.valueOf(performanceRewardTotal));
                    }

                    map.put("total",String.valueOf(taskList.size()));
                    map.put("joinId",admin.get("id"));
                    map.put("avatar",admin.get("avatar"));
                    map.put("mobile",admin.get("mobile"));
                    map.put("user_name",admin.get("user_name"));
                    map.put("completeCount",String.valueOf(completeCount));
                    map.put("moneyReward",String.valueOf(moneyReward));
                    map.put("performanceReward",String.valueOf(performanceReward));
                    list.add(map);
                }
            }
        });

        return list;
    }

    public List<Map<String,Object>> queryTaskListByIds(Map<String,Object>param){
        return bTaskMapper.queryTaskListByIds(param);
    }

    public List<Map<String, Object>> getTaskGoods(Map<String, Object> parameterMap) {
        String siteId = parameterMap.get("siteId").toString();
        String taskId = parameterMap.get("taskId").toString();
        BTaskBlob bTaskBlob=null;
        if(taskId!=null){
             bTaskBlob=bTaskBlobMapper.selectByTaskId(Integer.valueOf(taskId));
        }

        List<Map<String,Object>> taskGoods=null;
        if(bTaskBlob!=null){
            String[] goodIds=bTaskBlob.getGoodsId().split(",");
           taskGoods=goodsMapper.getTaskGoods(Integer.valueOf(siteId),goodIds);
        }

        return taskGoods;
    }

    public Map<String,Object> queryAdminHead(Integer siteId, Integer joinId) {
        return bTaskMapper.queryAdminHead(siteId,joinId);
    }
}
