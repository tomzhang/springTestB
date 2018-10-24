package com.jk51.modules.task.service;

import com.github.pagehelper.PageHelper;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.JKHashMap;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.Stores;
import com.jk51.model.task.*;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.task.domain.*;
import com.jk51.modules.task.domain.dto.QueryBTaskDTO;
import com.jk51.modules.task.mapper.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.jk51.modules.im.service.PushType.*;

/**
 * Created by guosheng on 2017/8/7.
 */
@Service
public class TaskPlanService {
    public static final String REDIS_TIME_KEY = "shop:task:time:start:";

    public static final Logger logger = LoggerFactory.getLogger(TaskPlanService.class);

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    BTaskMapper  bTaskMapper;

    @Autowired
    TQuotaMapper  tQuotaMapper;

    @Autowired
    StoresMapper storesMapper;

    @Autowired
    StoreAdminExtMapper storeAdminExtMapper;

    @Autowired
    TaskService taskService;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    BTaskrewardMapper bTaskrewardMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AppMemberService appMemberService;

    @Autowired
    TaskPlanHelper taskPlanHelper;

    @Autowired
    StoreAdminMapper storeAdminMapper;


//    @Autowired
//    BTaskrewardMapper bTaskrewardMapper;

    public BTaskplan findByid(int id) {
        return bTaskplanMapper.selectByPrimaryKey(id);
    }

    public Map<String, Object> getPlanDetails(BTaskplan bTaskplan) {
        Map<String, Object> map=bTaskplanMapper.getDetails(bTaskplan.getId());
        String[] taskId=map.get("task_ids").toString().split(",");
        String[] joinId=map.get("join_ids").toString().split(",");
        List<Stores> storesList=new ArrayList();
        List<StoreAdminExt> storeAdminExtList=new ArrayList();


        List<String> zhibiaoList=new ArrayList();
        List<BTask> limitList=new ArrayList();

        String siteId=map.get("site_id").toString();
        if(Integer.parseInt(map.get("join_type").toString())==10){
            for(int z=0;z<joinId.length;z++){
                Stores store=storesMapper.getStore(Integer.parseInt(joinId[z]),Integer.parseInt(siteId));
                storesList.add(store);
            }
            map.put("storesList",storesList);
        }else{
            for(int x=0;x<joinId.length;x++){
                StoreAdminExt storeAdminExt=storeAdminExtMapper.selectByStoreAdminKey(Integer.parseInt(siteId),Integer.parseInt(joinId[x]));
                if(Objects.nonNull(storeAdminExt)){
                    storeAdminExtList.add(storeAdminExt);
                }

            }
            map.put("storeAdminExtList",storeAdminExtList);
        }

        for (int i=0;i<taskId.length;i++) {
            BTask bTask=bTaskMapper.selectByPrimaryKey(Integer.valueOf(taskId[i]));
            TQuota tQuota =tQuotaMapper.selectByPrimaryKey(bTask.getTargetId());
            zhibiaoList.add(tQuota.getName());
            limitList.add(bTask);

        }
        map.put("limitList",limitList);
        map.put("zhibiaoList",zhibiaoList);
        map.put("joinLength",joinId.length);

        return map;
    }

    public List<Integer> getAllStart() {
        return bTaskplanMapper.getAllStart();
    }

    public Map<String, Object> changeStatus(int[] ids, Byte status){
        String falseResult = "";
        Map<String, Object> result = new HashMap<>();
        if (status.equals( BTaskPlanStatus.DELETE.getValue())){
                for (int i=0; i<ids.length; i++){
                    Integer completeResult = bTaskExecuteMapper.isComplete(ids[i]);
                    if (completeResult != 0){
                        falseResult = falseResult + ids[i] + ",";
                        ids[i] = -1;
                    }
                }
                if (!falseResult.equals("")) falseResult = falseResult.substring(0, falseResult.length()-1);
        }
        int count =  changeStatus(Arrays.stream(ids).boxed().collect(Collectors.toList()), status);
        result.put("count",count);
        if (count == ids.length){
            result.put("message","操作成功");
        }else {
            result.put("message","删除成功" + count + "个，计划" + falseResult + "未完成，无法删除");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(List<Integer> ids, Byte status) {
        BTaskplanExample bTaskplanExample = new BTaskplanExample();
        BTaskplanExample.Criteria criteria = bTaskplanExample.createCriteria();
        criteria.andIdIn(ids);
        BTaskplan bTaskplan = new BTaskplan();
        bTaskplan.setStatus(status);

        int result = bTaskplanMapper.updateByExampleSelective(bTaskplan, bTaskplanExample);

        if (result > 0) {
            if (Objects.equals(BTaskPlanStatus.STARTING.getValue(), status)) {
                // 修改为开始 根据计划内任务生成任务执行记录
                ids.forEach(this::createTaskExecute);
            } else if (Objects.equals(BTaskPlanStatus.STOP.getValue(), status)) {
                // 修改为结束 删除redis所有相关key 处理还没统计的数据
            }
        }

        return result;
    }

    public int release(BTaskplan taskplan) {
        int result = bTaskplanMapper.insertSelective(taskplan);
        if (result > 0 && taskplan.getStartTime().compareTo(new Date()) != 1) {
            //异步处理推送等业务,避免客户端长时间等待,暂不细化到推送节点
            CompletableFuture<Void> future =CompletableFuture.runAsync(() ->{
                changeStatus(new int[]{taskplan.getId()}, BTaskPlanStatus.STARTING.getValue());
                pushMesByPlanIds(taskplan.getId().toString());
            }).exceptionally(t -> {
                logger.info("任务发布异常:{}",t);
                return null;
            });

        }

        return result;
    }

    public List<Map<String,Object>> getTaskPlan(HashMap queryMap, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Map<String,Object>> bTaskplanList=bTaskplanMapper.getTaskPlan(queryMap);

        for(int x=0;x<bTaskplanList.size();x++){
            List<BTask> bTaskList=new ArrayList<>();
            List<String> nameList=new ArrayList<>();
            String[] taskIds=bTaskplanList.get(x).get("task_ids").toString().split(",");
            for(int y=0; y<taskIds.length;y++){
               BTask bTask= bTaskMapper.selectByPrimaryKey(Integer.valueOf(taskIds[y]));
                bTaskList.add(bTask);
                nameList.add(bTask.getName());
            }
            bTaskplanList.get(x).put("bTaskList",bTaskList);
            bTaskplanList.get(x).put("nameList",nameList);
        }


        return bTaskplanList;
    }

    /**
     * 创建任务执行记录
     * @param planId
     */
    public void createTaskExecute(int planId) {
        logger.info("创建任务执行计划 {}", planId);
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(planId);
        if (Objects.isNull(bTaskplan)) {
            throw new IllegalArgumentException("无效的任务计划id" + planId);
        }

        QueryBTaskDTO queryBTaskDTO = new QueryBTaskDTO();
        List<Integer> idList = StringUtil.split(bTaskplan.getTaskIds(), ",", Integer::valueOf);
        queryBTaskDTO.setIdList(idList);
        List<BTask> tasks = taskService.find(queryBTaskDTO);

        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        List<BTaskExecute> bTaskExecutes = new ArrayList<>();

        for (BTask task : tasks) {
            bTaskExecutes.addAll(new TaskExecuteCreator(bTaskplan, task).create());
        }

        if (CollectionUtils.isNotEmpty(bTaskExecutes)) {
            bTaskExecuteMapper.insertList(bTaskExecutes);
        }
    }

    /**
     * 查询未完成的任务执行计划
     * @param date
     * @return
     */
    public List<BTaskExecute> selectUncompleted(Date date) {
        BTaskExecuteExample executeExample = new BTaskExecuteExample();
        executeExample.createCriteria()
                .andCompleteEqualTo(false)
                .andStartDayLessThanOrEqualTo(date);

        return bTaskExecuteMapper.selectByExample(executeExample);
    }

    /**
     * 更新一个任务执行计划到完成状态
     * @param executeId
     * @return
     */
    public boolean updateExecuteComplete(int executeId) {
        BTaskExecute bTaskExecute = new BTaskExecute();
        bTaskExecute.setId(executeId);
        bTaskExecute.setComplete(true);

        boolean result = bTaskExecuteMapper.updateByPrimaryKeySelective(bTaskExecute) != 0;
        if (result) {
            // 删除相关的key
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            try {
                String key = getTimeKey(executeId);
                Charset charset = Charset.forName("UTF-8");
                connection.del(key.getBytes(charset));

                //推送任务完成通知
                Map<String, Object> comple = bTaskExecuteMapper.queryCompleteInfo(executeId);
                if(Objects.nonNull(comple)) {
                    Integer siteId = Integer.valueOf(String.valueOf(comple.get("siteId")));
                    String joinType = String.valueOf(comple.get("joinType"));
                    String taskName = String.valueOf(comple.get("taskName"));
                    if(StringUtil.isEmpty(taskName)) {
                        taskName = "";
                    }
                    String[] joinIds = null;
                    if ("10".equals(joinType)) { //门店
                        joinIds = String.valueOf(comple.get("joinIds")).split(",");
                    } else {  //店员
                        joinIds = String.valueOf(comple.get("joinIds")).split(",");
                    }
                    for (int i = 0; i < joinIds.length; i++) {
                        Integer id = Integer.valueOf(joinIds[i]);
                        try {
                            if ("10".equals(joinType)) { //门店
                                appMemberService.notifyTaskMessage(siteId, null, id, PushType.TASK_FINISH,taskName);
                            } else {
                                appMemberService.notifyTaskMessage(siteId, id, null, PushType.TASK_FINISH,taskName);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            } finally {
                connection.close();
            }

        }

        return result;
    }

    /**
     * 根据传入的计划id推送当天的任务消息
     * @param plainIds
     */
    public void pushMesByPlanIds(String plainIds){
        String[] split = plainIds.split(",");
        List<JKHashMap<String,Object>> msgList = this.queryNotifyMsgByPlanIds(split);
        autoNotifyMess(msgList);
    }

    public void autoNotifyMess(List<JKHashMap<String,Object>> msgList) {
        //当天需要推送的消息列表 0 未完成
        if(Objects.isNull(msgList) || msgList.size() == 0){
            msgList = this.selectNotifyMsg();
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(msgList)) {
            logger.debug("今天没有需要推送的消息");
            return;
        }

        for(JKHashMap<String,Object> map : msgList) {
            //storeId和StoreAdminId只有一种
//                int executeId = Integer.parseInt(String.valueOf(map.get("id")));
            String taskName = map.getString("taskName");
            Integer siteId = map.getInt("siteId");
            String joinType = map.getString("joinType");
            List<Integer> joinIds = StringUtil.split(map.getString("joinIds"), ",", Integer::valueOf);
            int quotaGroup = map.getInt("quota_group");

            for(Integer id : joinIds) {
                try {
                    if("10".equals(joinType)) { //门店
                        appMemberService.notifyTaskMessage(siteId, null, id, getPushType(quotaGroup),taskName);
                    } else {
                        appMemberService.notifyTaskMessage(siteId, id, null, getPushType(quotaGroup),taskName);
                    }
                } catch (Exception e) {
                    logger.error("发送任务通知出错", e);
                }
            }
        }
    }

    public PushType getPushType(int quotaGroup) {
        switch (quotaGroup) {
            case 1:
                return TASK_NEWORDERTASK;
            case 2:
                return TASK_NEWREGISTERTASK;
            case 3:
                return TASK_NEWEXAM;
            default:
                throw new IllegalArgumentException("无效的参数" + quotaGroup);
        }
    }

    /**
     * 安全更新任务执行计划到完成 如果任务还没统计完不会进行更新
     * @param executeId
     * @return
     */
    public boolean safeUpdateExecuteComplete(int executeId) {
        String key = getTimeKey(executeId);
        Charset charset = Charset.forName("UTF-8");
        byte[] keyBytes = key.getBytes(charset);
        // 删除相关的key
        redisTemplate.boundValueOps(key).get();

        return updateExecuteComplete(executeId);
    }

    /**
     * 任务追踪标头
     * @param id
     * @return
     */
    public List<BTask> getTaskIdsList (Integer id){
        String ids = bTaskplanMapper.queryTaskIdsById(id);
        int[] taskIds = Arrays.stream(ids.split(",")).mapToInt( a -> Integer.parseInt(a)).toArray();
        BTaskExample bTaskExample = new BTaskExample();
        BTaskExample.Criteria criteria = bTaskExample.createCriteria();
        criteria.andIdIn(Arrays.stream(taskIds).boxed().collect(Collectors.toList()));
        List<BTask> result = bTaskMapper.selectByExample(bTaskExample);
        return result;
    }

    public Map<String, Object> taskPlanTime(Integer id){
        Map<String, Object> result = bTaskplanMapper.queryTaskPlanTime(id);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String start =String.valueOf(result.get("startTime")).substring(0,10);
        String end =  String.valueOf(result.get("endTime")).substring(0,10);
        Date startTime = null;
        Date endTime = null;
        try {
            startTime = df.parse(start);
            endTime = df.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startDay = df.format(startTime);
        String endDay = df.format(endTime);
        String startMonth = "";
        String endMonth = "";
        if (startDay.charAt(5)=='0'){
            startMonth = startDay.substring(0, 4) + "年第" + startDay.substring(6, 7) + "月";
        }else {
            startMonth = startDay.substring(0, 4) + "年第" + startDay.substring(5, 7) + "月";
        }
        if (endDay.charAt(5)=='0'){
            endMonth = endDay.substring(0, 4) + "年第" + endDay.substring(6, 7) + "月";
        }else {
            endMonth = endDay.substring(0, 4) + "年第" + endDay.substring(5, 7) + "月";
        }
        Calendar ca = Calendar.getInstance();//创建一个日期实例
        ca.setTime(startTime);//实例化一个日期
        String startWeek =startDay.substring(0,4) + "年第" +  (ca.get(Calendar.WEEK_OF_YEAR) -1+ 2) + "周";
        ca.setTime(endTime);//实例化一个日期
        String endWeek =startDay.substring(0,4) + "年第" +  (ca.get(Calendar.WEEK_OF_YEAR)-1+2) + "周";
        result.put("startDay", startDay);
        result.put("endDay", endDay);
        result.put("startMonth", startMonth);
        result.put("endMonth", endMonth);
        result.put("startWeek", startWeek);
        result.put("endWeek",endWeek);
        return result;
    }

    /**
     * 任务追踪
     * @param param
     * @return
     */
    public Map<String, Object> taskParam (Map<String, Object> param){
        //处理时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (!param.containsKey("timeType")){
            Date now = new Date();
            param.put("timeType",df.format(now));
        }
        String timeType = String.valueOf(param.get("timeType"));

        String startDay = "";
        String endDay = "";

        if (timeType.substring(timeType.length()-1, timeType.length()).equals("月")){
            Integer month = Integer.parseInt(timeType.substring(6).replace("月",""));
            Integer year = Integer.parseInt(timeType.substring(0,4));
            startDay = year + "-" + month + "-01";
            if (month == 12){
                month = 1;
                year += 1;
                endDay = year + "-" + month + "-01";
            }else{
                endDay = year + "-" + (month +1) + "-01";
            }
        }else if (timeType.substring(timeType.length()-1, timeType.length()).equals("周")){
            Integer year = Integer.parseInt(timeType.substring(0,4));
            Integer week = Integer.parseInt(timeType.substring(6).replace("周",""));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year); // 2016年
            cal.set(Calendar.WEEK_OF_YEAR, week-1); // 设置为2016年的第10周
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//指定周一为一周的开始
            Date date = cal.getTime();
            startDay = df.format(date);
            endDay = df.format(date.getTime() + 7 * 24 * 60 * 60 * 1000);
        }else {
            try {
                startDay = timeType;
                endDay = df.format(df.parse(timeType).getTime()+ 24 * 60 * 60 * 1000);
            } catch (ParseException e) {
                logger.error("任务追踪日期转换异常");
            }
        }
        param.put("startDay", startDay);
        param.put("endDay", endDay);

        //追踪记录

        List<FollowTask> list = null;
        //Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Integer executeId = bTaskExecuteMapper.queryExecutePrimaryKey(param);
        String objectName = "";
        if (param.containsKey("objectName")){
            objectName = String.valueOf(param.get("objectName"));
        }
        param.put("executeId", executeId);

        return param;
    }

    public Map<String,Object> taskCount(Map<String,Object> param){
        Map<String,Object> result=new HashMap<>();
        final AtomicInteger first = new AtomicInteger();
        final AtomicInteger second = new AtomicInteger();
        final AtomicInteger third = new AtomicInteger();
        final AtomicInteger rewardAll = new AtomicInteger();//绩效支出
        List<FollowTask> list= bTaskrewardMapper.queryRewardFollow(param);
        if(list.size() >0){
            list.stream().forEach(followTask -> {
                rewardAll.addAndGet(followTask.getReward());
                RewardRule rewardRule =new RewardRule();
                try {
                    rewardRule= JacksonUtils.json2pojo(followTask.getRewardDetail(), RewardRule.class);
                    if(Objects.nonNull(rewardRule)){
                        if(rewardRule.getType() == 0){
                            first.addAndGet(list.size());
                        }
                        if(rewardRule.getType() == 10){
                            first.incrementAndGet();
                        }else if(rewardRule.getType() ==20 && StringUtil.isNotEmpty(rewardRule.getLadders().toString()) && StringUtil.isNotEmpty(rewardRule.getLadders()[0].toString()) ){
                            if(rewardRule.getLadders()[0].getCondition() <= followTask.getCountValue()){
                                first.incrementAndGet();
                            }
                            if(rewardRule.getLadders().length >=2 && rewardRule.getLadders()[1].getCondition() <= followTask.getCountValue()){
                                second.incrementAndGet();
                            }
                            if(rewardRule.getLadders().length == 3 && rewardRule.getLadders()[2].getCondition() <= followTask.getCountValue()){
                                third.incrementAndGet();
                            }
                        }else if(rewardRule.getType() ==30 && StringUtil.isNotEmpty(rewardRule.getIntervals().toString().trim()) && StringUtil.isNotEmpty(rewardRule.getIntervals()[0].toString().trim()) ){
                            if(rewardRule.getIntervals()[0].getIntervalMax() != -1 && rewardRule.getIntervals()[0].getIntervalMax() <= followTask.getCountValue()){
                                first.incrementAndGet();
                            }
                            if(rewardRule.getIntervals().length >= 2 && rewardRule.getIntervals()[1].getIntervalMax() != -1 && rewardRule.getIntervals()[1].getIntervalMax() <= followTask.getCountValue()){
                                second.incrementAndGet();
                            }
                            if(rewardRule.getIntervals().length ==3 && rewardRule.getIntervals()[2].getIntervalMax() != -1 && rewardRule.getIntervals()[2].getIntervalMax() <= followTask.getCountValue()){
                                third.incrementAndGet();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            result.put("first",first);
            result.put("second",second);
            result.put("third",third);
            result.put("total",list.size());
            result.put("rewardAll",rewardAll);

        }

        return  result;
    }

    public List<FollowTask> taskFollow(Map<String,Object> param){
        List<FollowTask> list=new ArrayList<>();
        list = bTaskrewardMapper.queryRewardFollow(param);
        if (list != null) {
            if (Objects.nonNull(param.get("object")) && Integer.parseInt(param.get("object").toString()) == 10) {
                list.parallelStream().forEachOrdered(item -> {
                    //门店
                    Integer siteId  = item.getSiteId();
                    Integer storeId = item.getJoinId();
                    param.put("siteId",siteId);
                    param.put("storeId",storeId);
                    Map<String, Object> store= storesMapper.queryStoreAndAdminCount(param);
                    if(Objects.nonNull(store.get("name"))){
                        item.setStoreName(store.get("name").toString());
                        item.setStoresNumber(store.get("storesNumber").toString());
                        item.setAdminCount(Integer.parseInt(store.get("adminCount").toString()));
                    }
                    try {
                        item.setRewardRule(JacksonUtils.json2pojo(item.getRewardDetail(), RewardRule.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            if(Integer.parseInt(param.get("object").toString()) == 20) {
                list.parallelStream().forEachOrdered(item -> {
                    //店员
                    Integer siteId = item.getSiteId();
                    Integer storeadminId = item.getJoinId();
                    param.put("siteId",siteId);
                    param.put("storeadminId",storeadminId);
                    System.out.println("**********************************" + storeadminId + "*****************************");
                    Map<String, Object> admin = storeAdminExtMapper.queryAdminForReward(param);
                    if(Objects.nonNull(admin) && Objects.nonNull(admin.get("adminName"))){
                        item.setStoreName(admin.get("storeName").toString());
                        item.setAdminName(admin.get("adminName").toString());
                        item.setClerkInvitationCode(admin.get("clerkInvitationCode").toString());
                        item.setStoreadminId(Integer.parseInt(admin.get("storeadminId").toString()));
                    }
                    try {
                        item.setRewardRule(JacksonUtils.json2pojo(item.getRewardDetail(), RewardRule.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            for (int i=0; i<list.size(); i++){
                if (list.get(i).getStoreName() == null && list.get(i).getAdminName() == null){
                    list.remove(i);
                    i--;
                }
            }
        }
        return list;
    }

    //查询任务消息列表
    public List<JKHashMap<String,Object>> selectNotifyMsg() {
        return bTaskExecuteMapper.selectNotifyMsg();
    }

    public List<JKHashMap<String,Object>> queryNotifyMsgByPlanIds(String[] plainIds) {
        return bTaskExecuteMapper.queryNotifyMsgByPlanIds(plainIds);
    }

    /**
     * 统计日期是否有效
     * @return
     */
    public boolean isActiveDay(byte activeType, int[] dayList, LocalDateTime dateTime) {
        return TaskPlanHelper.isActiveDay(activeType, dayList, dateTime);
    }

    /**
     * 获取任务执行计划的统计时间Key
     * @param executeId
     * @return
     */
    public String getTimeKey(int executeId) {
        return REDIS_TIME_KEY + executeId;
    }

    /**
     * 获取任务执行计划的统计开始时间
     * @param executeId
     * @return
     */
    public LocalDateTime getStartTime(int executeId) {
        String s = redisTemplate.boundValueOps(getTimeKey(executeId)).get();
        if (Objects.isNull(s)) {
            return null;
        }
        Long l = Long.parseLong(s);
        ZoneId zoneId = ZoneId.systemDefault();

        return Instant.ofEpochMilli(l).atZone(zoneId).toLocalDateTime();
    }

    public boolean updateStartTime(int executeId, long timeMillis) {
        BoundValueOperations<String, String> operations = redisTemplate.boundValueOps(getTimeKey(executeId));
        operations.set(String.valueOf(timeMillis));
        operations.expire(30, TimeUnit.DAYS);

        return true;
    }

    public BTaskplan selectByPrimaryKey(Integer planId) {
        return bTaskplanMapper.selectByPrimaryKey(planId);
    }

    public List<Integer> getRewardIds(Integer siteId, Byte rewardObject, Byte joinType, String joinIds) {
        return taskPlanHelper.getRewardIds(siteId, rewardObject, joinType, joinIds);
    }

    public Map<String, Object> endPlan(int[] ids) {
        Map<String, Object> result = changeStatus(ids, BTaskPlanStatus.STOP.getValue());
        BTaskExecute bTaskExecute = new BTaskExecute();
        bTaskExecute.setComplete(true);
        BTaskExecuteExample bTaskExecuteExample = new BTaskExecuteExample();
        bTaskExecuteExample.createCriteria().andPlanIdIn(Arrays.stream(ids).boxed().collect(Collectors.toList()));

        bTaskExecuteMapper.updateByExampleSelective(bTaskExecute, bTaskExecuteExample);

        return  result;
    }

    public List<BTaskExecute> executeTime(Map<String, Object> param){
        //BTaskExecute bTaskExecute = new BTaskExecute();
        Integer planId = Integer.parseInt(String.valueOf(param.get("planId")));
        Integer taskId = Integer.parseInt(String.valueOf(param.get("taskId")));
//        bTaskExecute.setPlanId(planId);
//        bTaskExecute.setTaskId(taskId);
        BTaskExecuteExample bTaskExecuteExample = new BTaskExecuteExample();
        bTaskExecuteExample.setOrderByClause("start_day ASC");
        bTaskExecuteExample.createCriteria().andPlanIdEqualTo(planId)
            .andTaskIdEqualTo(taskId);
        return bTaskExecuteMapper.selectByExample(bTaskExecuteExample);
    }

    public List<Integer> queryStoreNumbers(Integer siteId){
        return storesMapper.queryStoreNumbers(siteId);
    }

    public List<Integer> queryStoreAdminIds(Integer siteId){
        return storeAdminMapper.queryStoreAdminIds(siteId);
    }
}
