package com.jk51.modules.task.service;

import com.jk51.Bootstrap;
import com.jk51.model.task.TAnswer;
import com.jk51.model.task.TExamination;
import com.jk51.model.task.TQuestion;
import com.jk51.modules.task.domain.ExaminationInfo;
import com.jk51.modules.task.domain.dto.TExaminationQueryDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class ExaminationServiceTest {

    @Autowired
    ExaminationService examinationService;

    @Test
    public void selectAll() throws Exception {
        TExaminationQueryDTO queryDTO = new TExaminationQueryDTO();
        queryDTO.setId(1);
        queryDTO.setDiseaseCategory(10);
//        System.out.println(examinationService.select(queryDTO));
    }

    @Test
    public void getExaminationInfo() throws Exception {
        ExaminationInfo examinationInfo = examinationService.getExaminationInfo(1);
        System.out.println(examinationInfo);
    }

    public List<ExaminationInfo.QuestionAnswer> randomQuestionAnswer() {
        long t1 = System.nanoTime();
        List<ExaminationInfo.QuestionAnswer> questionAnswers = new ArrayList<>();
        for (byte i = 1; i < 10; i++) {
            ExaminationInfo.QuestionAnswer questionAnswer = new ExaminationInfo.QuestionAnswer();
            TQuestion tQuestion = new TQuestion();
            tQuestion.setContent(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
            tQuestion.setExpound(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
            tQuestion.setNum(i);
            questionAnswer.setQuestion(tQuestion);
            List<TAnswer> answers = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                TAnswer tAnswer = new TAnswer();
                tAnswer.setContent(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
                tAnswer.setNum(String.valueOf((char)('A' + j)));
                tAnswer.setChecked(new Random().nextBoolean());
                answers.add(tAnswer);
            }
            questionAnswer.setAnswers(answers);
            questionAnswers.add(questionAnswer);
        }
        long t2 = System.nanoTime();
        System.out.println(t2 - t1);
        return questionAnswers;
    }

    @Test
    public void addExamination() throws Exception {
        ExaminationInfo examinationInfo = new ExaminationInfo();
        TExamination examination = new TExamination();
        examination.setBrand(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setCategoryName(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setDiseaseCategory("10");
        examination.setDrugCategory(1);
        examination.setEnterprise(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setSecondTotal(100);
        examination.setTitle(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setTrainedCategory(20);
        examinationInfo.setExamination(examination);
        examinationInfo.setContent(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(500, 50000)));


        examinationInfo.setQuestionAnswers(randomQuestionAnswer());
        long t1 = System.nanoTime();
        examinationService.addExamination(examinationInfo);
        long t2 = System.nanoTime();
        System.out.println(t2 - t1);
    }

    @Test
    public void updateExamination() throws Exception {
        ExaminationInfo examinationInfo = examinationService.getExaminationInfo(1);
        TExamination examination = examinationInfo.getExamination();
        examination.setBrand(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setCategoryName(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setDiseaseCategory("10");
        examination.setDrugCategory(1000);
        examination.setEnterprise(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setSecondTotal(100);
        examination.setTitle(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(5, 10)));
        examination.setTrainedCategory(20);
        examinationInfo.setExamination(examination);
        examinationInfo.setContent(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(500, 50000)));

        examinationInfo.setQuestionAnswers(randomQuestionAnswer());

        long t1 = System.nanoTime();
        examinationService.updateExamination(examinationInfo);
        long t2 = System.nanoTime();
        System.out.println(t2 - t1);
    }
}
