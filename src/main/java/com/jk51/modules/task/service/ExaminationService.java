package com.jk51.modules.task.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.pricetransform.DoubleTransformUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.JKHashMap;
import com.jk51.model.StoreAdmin;
import com.jk51.model.goods.YbCategory;
import com.jk51.model.task.*;
import com.jk51.modules.goods.mapper.YbCategoryMapper;
import com.jk51.modules.goods.service.YbCategoryService;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.task.domain.*;
import com.jk51.modules.task.domain.count.Counts;
import com.jk51.modules.task.domain.count.ExamCounter;
import com.jk51.modules.task.domain.dto.TExaminationQueryDTO;
import com.jk51.modules.task.domain.exception.ExaminationNotFoundException;
import com.jk51.modules.task.domain.reward.RewardResult;
import com.jk51.modules.task.mapper.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ExaminationService {
    private static final Logger logger = LoggerFactory.getLogger(ExaminationService.class);

    @Autowired
    TExaminationMapper tExaminationMapper;

    @Autowired
    TQuestionMapper tQuestionMapper;

    @Autowired
    TAnswerMapper tAnswerMapper;

    @Autowired
    TDiseaseMapper tDiseaseMapper;

    @Autowired
    YbCategoryMapper ybCategoryMapper;

    @Autowired
    BTaskMapper bTaskMapper;

    @Autowired
    YbCategoryService ybCategoryService;

    @Autowired
    TTrainedMapper tTrainedMapper;

    @Autowired
    BTaskBlobMapper bTaskBlobMapper;

    @Autowired
    TExaminationExtMapper tExaminationExtMapper;

    @Autowired
    BTaskplanMapper bTaskplanMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    @Autowired
    TaskPlanService taskPlanService;

    @Autowired
    StoreAdminMapper storeAdminMapper;

    @Autowired
    BExamAnswerlogMapper bExamAnswerlogMapper;

    @Autowired
    YbJkexcrecordMapper ybJkexcrecordMapper;

    @Autowired
    BTaskrewardMapper bTaskrewardMapper;

    public List<TExamination> select(TExaminationQueryDTO queryDTO, Byte adminType,Integer storeId) {

        List<Integer> adminIds = null;
        try{
            if(Objects.nonNull(storeId)){
                adminIds = storeAdminMapper.selectStoreAdminIdsByStoreId(queryDTO.getSiteId(),storeId);
                if(adminIds.size() < 1){
                    adminIds = null;
                }
            }
        }catch (Exception e){

        }

        if (Objects.nonNull(queryDTO.getEndTime())){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                String end =df.format(queryDTO.getEndTime()) + " 23:59:59";
                queryDTO.setEndTime(df2.parse(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        // 开启分页 页数可以不传 默认第一页
        PageHelper.startPage(queryDTO.getPageNo(1), queryDTO.getPageSize(15));
        TExaminationExample tExaminationExample = queryDTO.buildExample(adminType,adminIds);
        List<TExamination> tExamination = tExaminationMapper.selectByExample(tExaminationExample);

        tExamination.parallelStream().forEach( exam -> {
            int[] id = {exam.getId()};
            Map<String, Object> result = checkExaminationInTask(id);
            if (!String.valueOf(result.get("falseIds")).equals(""))
            exam.setStatus((byte) 20);
        });

        return tExamination;
    }

    /**
     * 添加试卷
     * @param examinationInfo
     * @return
     * @throws BusinessLogicException
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public boolean addExamination(ExaminationInfo examinationInfo) throws BusinessLogicException {
        try {
            updateExam(examinationInfo);
            TExamination examination = examinationInfo.getExamination();
            List<ExaminationInfo.QuestionAnswer> questionAnswers = examinationInfo.getQuestionAnswers();

            if (questionAnswers.size() == 0) {
                throw new BusinessLogicException("试卷题目数量不能为0");
            }

            tExaminationMapper.insertSelective(examination);
            int examId = examination.getId();
            TExaminationExt tExaminationExt = new TExaminationExt();
            tExaminationExt.setExamId(examId);
            tExaminationExt.setContent(Objects.toString(examinationInfo.getContent(), ""));
            tExaminationExtMapper.insertSelective(tExaminationExt);

            addQuestionAnswer(questionAnswers, examId);

            return true;
        } catch (Exception e) {
            logger.error("添加试卷出错 {} {} {}", examinationInfo, e.getMessage(), e);
            throw new BusinessLogicException(e.getMessage());
        }
    }

    /**
     * 更新试卷
     * @param examinationInfo
     * @return
     * @throws BusinessLogicException
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public boolean updateExamination(ExaminationInfo examinationInfo) throws BusinessLogicException {
        try {
            Objects.requireNonNull(examinationInfo.getExamination().getId(), "试卷id不能为空");

            updateExam(examinationInfo);
            TExamination examination = examinationInfo.getExamination();
            List<ExaminationInfo.QuestionAnswer> questionAnswers = examinationInfo.getQuestionAnswers();

            if (questionAnswers.size() == 0) {
                throw new BusinessLogicException("试卷题目数量不能为0");
            }

            tExaminationMapper.updateByPrimaryKeySelective(examination);
            int examId = examination.getId();
            TExaminationExt tExaminationExt = new TExaminationExt();
            tExaminationExt.setExamId(examId);
            tExaminationExt.setContent(Objects.toString(examinationInfo.getContent(), ""));
            tExaminationExtMapper.insertSelective(tExaminationExt);

            // 更新之前的题目为无效
            updateQuestionStatus(examId, (byte)20);
            addQuestionAnswer(questionAnswers, examId);

            return true;

        } catch (Exception e) {
            logger.error("更新试卷出错 {} {} {}", examinationInfo, e.getMessage(), e);
            throw new BusinessLogicException(e);
        }
    }

    /**
     * 更新试卷信息  分类名、题目数量、题目类型
     * @param examinationInfo
     */
    public void updateExam(ExaminationInfo examinationInfo) throws BusinessLogicException {
        examinationInfo.updateQuestion();
        String cateName = ybCategoryService.selectCateName(examinationInfo.getExamination().getDrugCategory());

        if (StringUtil.isEmpty(cateName)) {
            throw new BusinessLogicException("药品分类错误 分类不存在");
        }
        TExamination examination = examinationInfo.getExamination();
        examination.setCategoryName(cateName);
    }
    /**
     * 添加试卷问题和答案
     */
    public void addQuestionAnswer(final List<ExaminationInfo.QuestionAnswer> questionAnswers, final int examId) {
        List<TAnswer> answers = new ArrayList<>(4 * questionAnswers.size());
        questionAnswers.stream().forEach(questionAnswer -> {
            TQuestion question = questionAnswer.getQuestion();
            // 将题目和试卷关联
            question.setExamId(examId);
            tQuestionMapper.insertSelective(question);

            // 将答案选项和问题关联
            questionAnswer.getAnswers().forEach(t -> t.setQuestId(question.getId()));
            answers.addAll(questionAnswer.getAnswers());
            if (CollectionUtils.isEmpty(answers)) {
                throw new RuntimeException("问题答案选项不能为空");
            }
        });

        tAnswerMapper.batchInsert(answers);
    }

    /**
     * 将之前试卷的题目状态到指定状态
     * @param examId
     * @param status
     * @return
     */
    public boolean updateQuestionStatus(int examId, byte status) {
        Objects.requireNonNull(examId);

        TQuestion tQuestion = new TQuestion();
        tQuestion.setStatus(status);
        TQuestionExample tQuestionExample = new TQuestionExample();
        tQuestionExample.createCriteria().andExamIdEqualTo(examId);

        return tQuestionMapper.updateByExampleSelective(tQuestion, tQuestionExample) > 0;
    }

    /**
     * 获取试卷信息
     * @param examId 试卷id
     * @return
     */
    public ExaminationInfo getExaminationInfo(Integer examId) {
        Objects.requireNonNull(examId);

        TExamination tExamination = tExaminationMapper.selectByPrimaryKey(examId);
        if (Objects.isNull(tExamination)) {
            throw new ExaminationNotFoundException(examId);
        }

        TExaminationExt tExaminationExt = tExaminationExtMapper.selectByExamId(examId);
        if (!Optional.ofNullable(tExaminationExt).isPresent()){
            throw new ExaminationNotFoundException(examId);
        }

        ExaminationInfo examinationInfo = new ExaminationInfo();
        examinationInfo.setExamination(tExamination);
        examinationInfo.setContent(tExaminationExt.getContent());
        List<TQuestion> tQuestions = selectQuestionByExamid(examId);
        List<Integer> questionIds = tQuestions.stream().map(TQuestion::getId).collect(toList());
        // 根据问题id分组
        Map<Integer, List<TAnswer>> answerOfQuestId = selectAnswerByQuestionIdsGroup(questionIds);
        List<ExaminationInfo.QuestionAnswer> questionAnswers = tQuestions.stream()
            .map(t -> new ExaminationInfo.QuestionAnswer(t, answerOfQuestId.get(t.getId())))
            .collect(toList());
        examinationInfo.setQuestionAnswers(questionAnswers);

        return examinationInfo;
    }

    /**
     * 获取试卷题目
     * @param examId
     * @return
     */
    public List<TQuestion> selectQuestionByExamid(Integer examId) {
        Objects.requireNonNull(examId);

        TQuestionExample tQuestionExample = new TQuestionExample();
        tQuestionExample.createCriteria().andExamIdEqualTo(examId).andStatusEqualTo((byte)10);
        // 获取题目
        List<TQuestion> tQuestions = tQuestionMapper.selectByExample(tQuestionExample);

        if (CollectionUtils.isEmpty(tQuestions)) {
            return Collections.emptyList();
        }

        return tQuestions;
    }

    /**
     * 根据题目获取答案选项
     * @param questionIds
     * @return
     */
    public List<TAnswer> selectAnswerByQuestionIds(List<Integer> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }

        TAnswerExample tAnswerExample = new TAnswerExample();
        tAnswerExample.createCriteria().andQuestIdIn(questionIds);

        List<TAnswer> tAnswers = tAnswerMapper.selectByExample(tAnswerExample);

        if (CollectionUtils.isEmpty(tAnswers)) {
            return Collections.emptyList();
        }

        return tAnswers;
    }

    /**
     * 题目答案选项根据题目分组
     * @param questionIds
     * @return
     */
    public Map<Integer, List<TAnswer>> selectAnswerByQuestionIdsGroup(List<Integer> questionIds) {
        return selectAnswerByQuestionIds(questionIds).stream().collect(groupingBy(TAnswer::getQuestId));
    }

    /**
     * 疾病类别
     * @return
     */
    public List<TDisease> queryDisease(){
        return tDiseaseMapper.queryDisease();
    }

    /**
     * 检查试卷是否在任务中
     * @param ids
     * @return
     */
    public Map<String, Object> checkExaminationInTask(int[] ids){
        List<Integer> examinationExist = bTaskMapper.checkExaminationInTask();
        StringBuffer falseIds = new StringBuffer("");
        StringBuffer successIds = new StringBuffer("");
        Map<String, Object> result = new HashMap<>();

        for (int id : ids){
            int flag = 0;
            for ( Integer examination : examinationExist) {
                if (id == examination){
                    flag = 1;
                    falseIds.append(id + ",");
                    break;
                }
            }
            if (flag == 0){
                successIds.append(id + ",");
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

    /**
     * 更改试卷状态
     * @param id
     * @param status
     * @return
     */
    public Map<String, Object> changeStatus(int[] id, byte status){
        Map<String, Object> result = new HashMap<>();
        int[] ids;
        int count = 0;
        Map<String, Object> checkResult = checkExaminationInTask(id);
        String successId = checkResult.get("succesIds").toString();
        String falseId = checkResult.get("falseIds").toString();
        if (!successId.equals("")) {
            ids = Arrays.stream(successId.split(",")).mapToInt(taskId -> Integer.parseInt(taskId)).toArray();
            TExaminationExample tExaminationExample = new TExaminationExample();
            TExaminationExample.Criteria criteria = tExaminationExample.createCriteria();
            criteria.andIdIn(Arrays.stream( ids ).boxed().collect(Collectors.toList()));
            TExamination tExamination = new TExamination();
            tExamination.setStatus(status);

            count = tExaminationMapper.updateByExampleSelective(tExamination, tExaminationExample);
        }


        result.put("successIds", successId);
        result.put("falseIds", falseId);
        result.put("seccessCount", count);

        return result;
    }

    /**
     * 药品类别
     * @return
     */
    public List<YbCategory> queryCategory(){
        return ybCategoryMapper.getParentCategory();
    }

    /**
     * 培训类别
     * @return
     */
    public List<Trains> queryTrains(){
        return tTrainedMapper.queryTrained();
    }

    /**
     * 查找任务中试卷id
     * @param id
     * @return
     */
    public BTaskBlob selectTaskBlobByTaskId(Integer id){
        BTaskBlob bTaskBlob = bTaskBlobMapper.selectByTaskId(id);
        if (bTaskBlob!=null){
            return bTaskBlob;
        }else {
            logger.error("查找试卷：taskId不存在");
            return null;
        }
    }

    public Integer examReward (Byte count, RewardRule rewardRule){
        //RewardRule rewardRule = rewardRule(task);
        Integer rewardTotal = 0;

        if (rewardRule.getType() == 10){
            //每满多少奖多少
            if (rewardRule.getDetail().getTopLimit()==0) {
                Integer condition = rewardRule.getDetail().getCondition();
                Integer reward = rewardRule.getDetail().getReward();
                rewardTotal = count / condition * reward;
            }else {
                rewardTotal = rewardRule.getDetail().getTopLimit();
            }
        }else if (rewardRule.getType() == 20){
            //满奖取最高
            rewardTotal = rewardRule.getLadders()[rewardRule.getLadders().length-1].getReward();
        }
        return rewardTotal;
    }

    //奖励规则
    public RewardRule rewardRule(Map<String, Object> map){
        String rewardDetail = map.get("rewardDetail").toString();
        try {
            RewardRule rewardRule = JacksonUtils.json2pojo(rewardDetail, RewardRule.class);
            return rewardRule;
        } catch (Exception e) {
            logger.error("奖励规则json格式解析错误",e);
        }
        return null;
    }

    public TExaminationExt examContant(Integer examId){
        return tExaminationExtMapper.selectByExamId(examId);
    }

    /**
     * 答题计分统计
     * @param exam
     * @return
     */
    public JKHashMap<String, Object> commitExam(JKHashMap<String, Object> exam) throws BusinessLogicException {
        Objects.requireNonNull(exam, "答题信息不能为空");
        int executeId = exam.getInt("executeId");
        int storeAdminId = exam.getInt("storeAdminId");

        BTaskExecute bTaskExecute = bTaskExecuteMapper.selectByPrimaryKey(executeId);
        if (Objects.isNull(bTaskExecute)) {
            throw new BusinessLogicException("任务不存在");
        }

        BTask bTask = bTaskMapper.selectByPrimaryKey(bTaskExecute.getTaskId());
        BTaskplan bTaskplan = bTaskplanMapper.selectByPrimaryKey(bTaskExecute.getPlanId());

        if (!(bTaskplan.getActiveType()==10)) {
            int[] dayList = Arrays.stream(bTaskplan.getDayNum().split(",")).mapToInt(i -> Integer.parseInt(i)).toArray();
            if (!taskPlanService.isActiveDay(bTask.getTimeType(), dayList, LocalDateTime.now())) {
                throw new BusinessLogicException("今天不是任务有效日期，不能答题");
            }
        }

        int siteId = exam.getInt("siteId");
        // 店员信息
        JKHashMap<String, Object> storeAdminInfo = storeAdminMapper.selectSimpleInfo(siteId, storeAdminId);
        Objects.requireNonNull(storeAdminInfo, "店员不存在");

        int examId = exam.getInt("examId");

        List<JKHashMap> answers = exam.getType("answers", new TypeReference<List<JKHashMap>>() {});
        ExaminationInfo examinationInfo = getExaminationInfo(examId);
        // 答题id对应的正确答案列表
        Map<Integer, List<Character>> questionTrueAnswerMap = examinationInfo.getQuestionTrueAnswerMap();

        Map<Integer, TQuestion> questionMap = examinationInfo.getQuestionMap();

        int success = 0;
        boolean flag;
        int questionId;
        List<TQuestion> errorQuestions = new LinkedList<>();

        for (JKHashMap<String, Object> answer : answers) {
            questionId = answer.getInt("questionId");
            List<Character> select = answer.getType("select", new TypeReference<List<Character>>() {});
            List<Character> characters = questionTrueAnswerMap.get(questionId);
            if (Objects.isNull(characters)) {
                throw new BusinessLogicException("答题信息错误 题号" + questionId + "不存在");
            }

            flag = Objects.nonNull(select) && Arrays.equals(select.toArray(), characters.toArray());

            if (flag) {
                success++;
            } else {
                // 添加到错题列表
                errorQuestions.add(questionMap.get(questionId));
            }
        }

        logger.info("答题正确数 {}", success);
        // 奖励统计
        Counts counts = new Counts(bTaskplan, bTask, bTaskExecute);
        ExamCounter examCounter = new ExamCounter(success, storeAdminId);
        counts.count(examCounter);

        TaskPlanHelper taskPlanHelper = new TaskPlanHelper();
        RewardRule rewardRule = JSONObject.parseObject(bTask.getRewardDetail(), RewardRule.class);
        RewardResult rewardResult = taskPlanHelper.calcReward(success, rewardRule);

        JKHashMap<String, Object> result = new JKHashMap();

        // 添加答题记录
        BExamAnswerlog bExamAnswerlog = new BExamAnswerlog();
        bExamAnswerlog.setExamId(examId);
        bExamAnswerlog.setPlanId(bTaskExecute.getPlanId());
        bExamAnswerlog.setTaskId(bTaskExecute.getTaskId());
        bExamAnswerlog.setExecuteId(bTaskExecute.getId());
        bExamAnswerlog.setName(storeAdminInfo.getString("user_name"));
        bExamAnswerlog.setClerkInvitationCode(storeAdminInfo.getString("clerk_invitation_code"));
        bExamAnswerlog.setStoreName(storeAdminInfo.getString("name"));
        bExamAnswerlog.setSiteId(siteId);
        bExamAnswerlog.setStoreAdminId(storeAdminId);
        bExamAnswerlog.setStoreId(storeAdminInfo.getInt("store_id"));
        bExamAnswerlog.setTotal((byte)questionMap.size());
        bExamAnswerlog.setNum((byte)success);
        bExamAnswerlog.setRemark("答题记录");
        bExamAnswerlog.setReward(rewardResult.getReward());
        bExamAnswerlog.setSnapshot(JSONObject.toJSONString(answers));
        bExamAnswerlog.setRewardType(bTask.getRewardType());
        bExamAnswerlogMapper.insertSelective(bExamAnswerlog);

        // 答题正确数
        result.put("success", success);
        // 回答错误的问题
        result.put("errorQuestions", errorQuestions);
        // 总共多少题
        result.put("totalQuestion", questionMap.size());
        // 奖励多少
        result.put("rewardValue", rewardResult.getReward());
        // 奖励目标级别
        result.put("targetLevel", rewardResult.getTargetLevel());
        // 奖励什么
        result.put("rewardType", bTask.getRewardType());

        return result;
    }

    /**
     * 试卷跟踪
     * @param param
     * @return
     */
    public List<BExamAnswerlog> examFollow(Map<String, Object> param){
        Integer examId = Integer.parseInt(param.get("examId").toString());
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        String startTime = null;
        String endTime = null;
        Date start = null;
        Date end = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            if (param.containsKey("startTime") && param.containsKey("endTime")){
                //有开始时间和结束时间
                startTime = param.get("startTime").toString()+ " 00:00:00";
                endTime = param.get("endTime").toString() + " 23:59:59";
                start = df2.parse(startTime);
                end = df2.parse(endTime);
            }else if (param.containsKey("startTime") && !param.containsKey("endTime")){
                //有开始时间没有结束时间，从开始时间到半年之后
                startTime = param.get("startTime").toString()+ " 00:00:00";
                start = df2.parse(startTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                calendar.add(Calendar.DATE, +183);
                end = calendar.getTime();
            }else if (!param.containsKey("startTime") && param.containsKey("endTime")){
                //有结束时间没有开始时间，结束时间到半年前
                endTime = param.get("endTime").toString() + " 23:59:59";
                end = df2.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                // add方法中的第二个参数n中，正数表示该日期后n天，负数表示该日期的前n天
                calendar.add(Calendar.DATE, -183);
                start = calendar.getTime();
            }else {
                //没有开始时间和结束时间，当前时间到半年前
                endTime = df.format(new Date()) + " 23:59:59";
                end = df2.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                // add方法中的第二个参数n中，正数表示该日期后n天，负数表示该日期的前n天
                calendar.add(Calendar.DATE, -183);
                start = calendar.getTime();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Date> maxTime = bExamAnswerlogMapper.allMaxTime();
        if (siteId == 0){
            BExamAnswerlogExample bExamAnswerlogExample= new BExamAnswerlogExample();
            BExamAnswerlogExample.Criteria bExamAnswerlogCriteria = bExamAnswerlogExample.createCriteria();
            bExamAnswerlogCriteria.andCreateTimeBetween(start, end).andExamIdEqualTo(examId).andCreateTimeIn(maxTime);
            return bExamAnswerlogMapper.selectByExample(bExamAnswerlogExample);
        }else {
            BExamAnswerlogExample bExamAnswerlogExample= new BExamAnswerlogExample();
            BExamAnswerlogExample.Criteria bExamAnswerlogCriteria = bExamAnswerlogExample.createCriteria();
            bExamAnswerlogCriteria.andCreateTimeBetween(start, end).andExamIdEqualTo(examId).andSiteIdEqualTo(siteId).andCreateTimeIn(maxTime);
            return bExamAnswerlogMapper.selectByExample(bExamAnswerlogExample);
        }
    }

    /**
     * 健康豆查询
     * @param param
     * @return
     */
    public Map<String, Object> beans(Map<String, Object> param){
        Map<String, Object> result = new HashMap<>();
        Integer surplusBeans;
        Integer totalBeans;
        if (!param.containsKey("siteId" ) || !param.containsKey("storeId") || !param.containsKey("storeAdminId")){
            logger.error("查询用户健康豆缺少参数");
            result.put("error","系统异常");
            return result;
        }
        Integer siteId = Integer.parseInt(param.get("siteId").toString());
        Integer storeId = Integer.parseInt(param.get("storeId").toString());
        Integer storeAdminId = Integer.parseInt(param.get("storeAdminId").toString());
//        List<Date> maxTime = bExamAnswerlogMapper.maxTime();

        //答题中的健康豆
//        BExamAnswerlogExample bExamAnswerlogExample = new BExamAnswerlogExample();
//        BExamAnswerlogExample.Criteria bExamAnswerlogCriteria = bExamAnswerlogExample.createCriteria();
//        bExamAnswerlogCriteria.andCreateTimeIn(maxTime).andSiteIdEqualTo(siteId).andStoreIdEqualTo(storeId).andStoreAdminIdEqualTo(storeAdminId).andRewardTypeEqualTo((byte) 30);
//        List<BExamAnswerlog> bExamAnswerlog = bExamAnswerlogMapper.selectByExample(bExamAnswerlogExample);
//        if (bExamAnswerlog !=null) {
//            for (BExamAnswerlog log : bExamAnswerlog) {
//                totalBeans += log.getReward();
//            }
//        }
//        BTaskrewardExample bTaskrewardExample = new BTaskrewardExample();
//        BTaskrewardExample.Criteria bTaskrewardCriteria = bTaskrewardExample.createCriteria();
//        bTaskrewardCriteria.andSiteIdEqualTo(siteId).andJoinTypeEqualTo((byte)20).andJoinIdEqualTo(storeAdminId).andRewardTypeEqualTo((byte)30);
//        List<BTaskreward> bTaskrewards = bTaskrewardMapper.selectByExample(bTaskrewardExample);
//        if (bTaskrewards !=null) {
//            for (BTaskreward taskreward : bTaskrewards) {
//                totalBeans += taskreward.getReward();
//            }
//        }

        totalBeans = bTaskrewardMapper.beansReward(siteId,storeAdminId);
        Integer useBeans = ybJkexcrecordMapper.surplusBeans(siteId, storeId, storeAdminId);
        if (Objects.isNull(useBeans)) {
            surplusBeans = totalBeans;
        }else {
            surplusBeans = totalBeans-useBeans;
        }
        result.put("totalBeans", totalBeans);
        result.put("surplusBeans", surplusBeans);
        return result;
    }

    /**
     * 兑换健康豆
     * @param ybJkexcrecord
     * @return
     */
    public Map<String, Object> takeOutBeans (YbJkexcrecord ybJkexcrecord){
        Map<String, Object> result = new HashMap<>();
        if (Objects.isNull(ybJkexcrecord.getSiteId()) || Objects.isNull(ybJkexcrecord.getStoreId()) || Objects.isNull(ybJkexcrecord.getStoreAdminId())){
            logger.error("增加健康豆提现记录缺少参数");
            result.put("error","系统异常");
            return result;
        }
        if (Objects.isNull(ybJkexcrecord.getMoney()) || Objects.isNull(ybJkexcrecord.getJkd())){
            logger.error("健康豆兑换数量不明");
            result.put("error","请选择兑换数量");
            return result;
        }
        //查询健康豆是否足够
        Map<String, Object> param = new HashMap<>();
        param.put("siteId", ybJkexcrecord.getSiteId());
        param.put("storeId", ybJkexcrecord.getStoreId());
        param.put("storeAdminId", ybJkexcrecord.getStoreAdminId());
        Map<String, Object> beans = beans(param);
        Integer bean = Integer.parseInt(beans.get("surplusBeans").toString());
        if (bean < Integer.parseInt(ybJkexcrecord.getJkd())){
            logger.error("健康豆兑换数量不足无法兑换，剩余健康豆数量：" + bean);
            result.put("error","健康豆数量不足无法兑换");
            return result;
        }

        long excId = createExcId(ybJkexcrecord.getStoreAdminId());
        ybJkexcrecord.setExcId(excId);
//        double money =  Double.valueOf(ybJkexcrecord.getMoney())*100;
        double money = DoubleTransformUtil.multiplystr(ybJkexcrecord.getMoney(),100).doubleValue();
        ybJkexcrecord.setMoney(String.valueOf((int) money));

        int status = ybJkexcrecordMapper.insertSelective(ybJkexcrecord);
        if (status == 1){
            result.put("money", ybJkexcrecord.getMoney());
            result.put("jkd", ybJkexcrecord.getJkd());
        }else {
            result.put("error", "兑换失败");
        }
        return result;
    }

    public long createExcId(Integer storeAdminId){
        String excId = String.valueOf(storeAdminId) + String.valueOf(System.currentTimeMillis());
        return Long.parseLong(excId);
    }

    /**
     * 提现记录
     * @param storeId
     * @return
     */
    public List<YbJkexcrecord> queryTakeOutList(Integer pageNum, Integer pageSize, Integer siteId, Integer storeId, Integer storeAdminId) {

        // 开启分页 页数可以不传 默认第一页
        PageHelper.startPage(pageNum, pageSize);
        YbJkexcrecordExample ybJkexcrecordExample = new YbJkexcrecordExample();
        ybJkexcrecordExample.setOrderByClause("create_time DESC");
        YbJkexcrecordExample.Criteria ybJkexcrecordCriteria = ybJkexcrecordExample.createCriteria();
        ybJkexcrecordCriteria.andSiteIdEqualTo(siteId).andStoreIdEqualTo(storeId).andStoreAdminIdEqualTo(storeAdminId);

        return ybJkexcrecordMapper.selectByExample(ybJkexcrecordExample);
    }
}
