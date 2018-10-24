package com.jk51.modules.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.JKHashMap;
import com.jk51.model.goods.YbCategory;
import com.jk51.model.task.*;
import com.jk51.modules.task.domain.*;
import com.jk51.modules.task.domain.dto.ExaminationDTO;
import com.jk51.modules.task.domain.dto.QuestionDTO;
import com.jk51.modules.task.domain.dto.TExaminationQueryDTO;
import com.jk51.modules.task.service.ExaminationService;
import com.jk51.modules.task.service.TaskService;
import com.jk51.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/task/examination")
@Slf4j
public class ExaminationController {
    @Autowired
    ExaminationService examinationService;

    @Autowired
    TaskService taskService;

    private static final Logger log = LoggerFactory.getLogger(ExaminationController.class);

    @PostMapping("/find")
    @ResponseBody
    public ReturnDto find( TExaminationQueryDTO queryDTO,Byte adminType,Integer storeId) throws BusinessLogicException {
        try {

            List<TExamination> examinations = examinationService.select(queryDTO, adminType,storeId);
            PageInfo pageInfo = new PageInfo<>(examinations);

            return ReturnDto.buildSuccessReturnDto(pageInfo);
        } catch (Exception e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }

    /**
     * 删除试卷
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/delete")
    public ReturnDto delete (HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String ids = param.get("ids").toString();
        int[] id = Arrays.stream(ids.split(",")).mapToInt(eId -> Integer.parseInt(eId)).toArray();

        Map<String, Object> result = examinationService.changeStatus(id, (byte) 20);

        Integer count = Integer.parseInt(result.get("seccessCount").toString());
        if (id.length == count){
            return ReturnDto.buildSuccessReturnDto(result);
        }

        return new ReturnDto("101", "成功删除"+count+"个试卷，试卷"+result.get("falseIds")+"在任务中，无法删除", result);
    }

    @PostMapping("/save")
    public ReturnDto save(@RequestBody ExaminationInfo examinationInfo) throws BusinessLogicException {
        try {
            validateExam(examinationInfo, AddGroup.class);

            boolean flag = examinationService.addExamination(examinationInfo);
            if (flag) {
                return ReturnDto.buildSuccessReturnDto();
            }

            return ReturnDto.buildFailedReturnDto("保存失败");
        } catch (Exception e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ReturnDto update(@RequestBody ExaminationInfo examinationInfo) throws BusinessLogicException {
        try {
            validateExam(examinationInfo, UpdateGroup.class);

            boolean flag = examinationService.updateExamination(examinationInfo);
            if (flag) {
                return ReturnDto.buildSuccessReturnDto();
            }

            return ReturnDto.buildFailedReturnDto("保存失败");
        } catch (Exception e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }

    public void validateExam(ExaminationInfo examinationInfo, Class<?>... groups) throws BusinessLogicException {
        ObjectMapper objectMapper = new ObjectMapper();
        // model模块 下的不能使用验证
        Objects.requireNonNull(examinationInfo.getExamination(), "请填写试卷内容");

        if (CollectionUtils.isEmpty(examinationInfo.getQuestionAnswers())) {
            throw new BusinessLogicException("请添加试卷题目");
        }

        ExaminationDTO examinationDTO = objectMapper.convertValue(examinationInfo.getExamination(), ExaminationDTO.class);
        ValidationUtils.validate(examinationDTO, groups);

        boolean flag;
        for (ExaminationInfo.QuestionAnswer questionAnswer : examinationInfo.getQuestionAnswers()) {
            flag = false;
            Objects.requireNonNull(questionAnswer.getQuestion(), "题目不能为空");
            QuestionDTO questionDTO = objectMapper.convertValue(questionAnswer.getQuestion(), QuestionDTO.class);
            List<TAnswer> answers = questionAnswer.getAnswers();
            questionDTO.setAnswers(answers);
            ValidationUtils.validate(questionDTO, groups);

            for (TAnswer answer : answers) {
                if (answer.getChecked()) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                throw new BusinessLogicException("题目“" + questionDTO.getContent() + "”至少包含一个正确答案");
            }
        }
    }

    /**
     * 疾病分类
     * @return
     */
    @RequestMapping("/disease")
    public ReturnDto queryDisease(){
        return ReturnDto.buildListOnEmptyFail(examinationService.queryDisease());
    }

    /**
     * 药品分类
     * @return
     */
    @PostMapping("queryByPid")
    public ReturnDto queryCategoryByPid(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        List<YbCategory> categoryData = examinationService.queryCategory();
        return ReturnDto.buildListOnEmptyFail(categoryData);
    }

    /**
     * 试卷题
     * @param examId
     * @return
     */
    @PostMapping("/question")
    public ReturnDto getExaminationInfo(Integer examId){
        return ReturnDto.buildSuccessReturnDto(examinationService.getExaminationInfo(examId));
    }

    /**
     * 培训分类
     * @return
     */
    @RequestMapping("/queryTrains")
    @ResponseBody
    public ReturnDto queryTrains(){
        return  ReturnDto.buildListOnEmptyFail(examinationService.queryTrains());
    }

    /**
     * app试卷题
     * @param
     * @return
     */
    @PostMapping("/taskExamination")
    public ReturnDto taskExamination(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer taskId;
        Integer examId;
        ExaminationInfo examinationQuestion = new ExaminationInfo();
        Map<String, Object> result = new HashMap<>();
        if (param.containsKey("taskId")){
            taskId = Integer.parseInt(param.get("taskId").toString());
            Map<String,Object> task = taskService.taskDetail(taskId);
            BTaskBlob bTaskBlob = examinationService.selectTaskBlobByTaskId(taskId);
            examId = bTaskBlob.getExaminationId();
            examinationQuestion = examinationService.getExaminationInfo(examId);
            TExaminationExt tExaminationExt = examinationService.examContant(taskId);
            String rewardType = String.valueOf(task.get("rewardType"));
            RewardRule rewardRule = examinationService.rewardRule(task);
            Integer rewardTotal = examinationService.examReward(examinationQuestion.getExamination().getQuestNum(), rewardRule);
            Map<String, Object> reward = new HashedMap();
            reward.put("rewardType", rewardType);
            reward.put("rewardTotal", rewardTotal);
            reward.put("rewardRule",rewardRule);
            reward.put("punish",task.get("punish"));
            reward.put("lowTarget", task.get("lowTarget"));
            result.put("reward", reward);
        }else {
            examId = Integer.parseInt(param.get("examId").toString());
            examinationQuestion = examinationService.getExaminationInfo(examId);
        }



        result.put("question", examinationQuestion);
        //result.put("content", tExaminationExt);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 答题计分
     * @return
     */
    @PostMapping("/commitExam")
    public ResponseEntity<ReturnDto> commitExam(@RequestBody JKHashMap<String, Object> exam) throws BusinessLogicException {
        try {
            JKHashMap result = examinationService.commitExam(exam);

            return ResponseEntity.ok(ReturnDto.buildSuccessReturnDto(result));
        } catch (Exception e) {
            if (!(e instanceof BusinessLogicException)) {
                log.error("答题错误", e);
            }
            throw new BusinessLogicException(e.getMessage());
        }
    }

    /**
     * 试卷跟踪
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/followExam")
    public ReturnDto followExam(HttpServletRequest request, HttpServletResponse response)  {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return ReturnDto.buildListOnEmptyFail(examinationService.examFollow(param));
    }

    /**
     * 健康豆查询
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/beans")
    public ReturnDto queryBeans(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = examinationService.beans(param);
        if (result.containsKey("error")){
            return ReturnDto.buildFailedReturnDto(String.valueOf(result.get("error")));
        }else {
            return ReturnDto.buildSuccessReturnDto(result);
        }
    }

    /**
     * 兑换健康豆
     * @param ybJkexcrecord
     * @return
     */
    @PostMapping("takeOutBeans")
    @ResponseBody
    public ReturnDto takeOutBeans( YbJkexcrecord ybJkexcrecord){
        Map<String, Object> result = examinationService.takeOutBeans(ybJkexcrecord);
        if (result.containsKey("error")){
            return ReturnDto.buildFailedReturnDto(String.valueOf(result.get("error")));
        }else {
            return ReturnDto.buildSuccessReturnDto(result);
        }
    }

    /**
     * app提现记录
     * @param pageNum
     * @param pageSize
     * @param siteId
     * @param storeId
     * @param storeAdminId
     * @return
     */
    @PostMapping("beansList")
    public ReturnDto beansList(Integer pageNum, Integer pageSize, Integer siteId, Integer storeId, Integer storeAdminId)throws BusinessLogicException{
        try {
            List<YbJkexcrecord> ybJkexcrecords = examinationService.queryTakeOutList(pageNum, pageSize, siteId, storeId, storeAdminId);
            PageInfo pageInfo = new PageInfo(ybJkexcrecords);
            return ReturnDto.buildSuccessReturnDto(pageInfo);
        } catch (Exception e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }
}
