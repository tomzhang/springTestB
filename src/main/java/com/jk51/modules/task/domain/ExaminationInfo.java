package com.jk51.modules.task.domain;

import com.jk51.model.task.TAnswer;
import com.jk51.model.task.TExamination;
import com.jk51.model.task.TQuestion;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * 试卷
 */
public class ExaminationInfo {
    /**
     * 试卷基本信息
     */
    private TExamination examination;

    /**
     * 题目和选项
     */
    List<QuestionAnswer> questionAnswers;

    private String content;

    public TExamination getExamination() {
        return examination;
    }

    public void setExamination(TExamination examination) {
        this.examination = examination;
    }

    public List<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 更新题目数量 题目类型
     */
    public void updateQuestion() {
        if (Objects.nonNull(questionAnswers)) {
            examination.setQuestNum((byte)questionAnswers.size());
            Set<String> set = new HashSet<>();
            for (QuestionAnswer questionAnswer : questionAnswers) {
                if (Objects.isNull(questionAnswer.getQuestion().getExpound())) {
                    questionAnswer.getQuestion().setExpound("");
                }
                long count = questionAnswer.getAnswers().stream().filter(TAnswer::getChecked).count();
                if (count > 1) {
                    set.add("20");
                } else {
                    set.add("10");
                }
            }
            examination.setQuestTypes(set.stream().collect(joining(",")));
        }
    }

    /**
     * 问题编号为key的map
     * @return
     */
    public Map<Integer, TQuestion> getQuestionMap() {
        return questionAnswers.stream().collect(toMap(t1 -> t1.getQuestion().getId(), QuestionAnswer::getQuestion));
    }

    /**
     * 问题正确答案
     * @return
     */
    public Map<Integer, List<Character>> getQuestionTrueAnswerMap() {
        return questionAnswers.stream().collect(toMap(t1 -> t1.getQuestion().getId(), this::getTrueAnswer));
    }

    public List<Character> getTrueAnswer(ExaminationInfo.QuestionAnswer questionAnswer) {
        List<TAnswer> answers = questionAnswer.getAnswers();
        final int size = answers.size();
        List<Character> characters = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (answers.get(i).getChecked()) {
                characters.add((char) ('A' + i));
            }
        }

        return characters;
    }

    @Override
    public String toString() {
        return "ExaminationInfo{" +
            "examination=" + examination +
            ", questionAnswers=" + questionAnswers +
            '}';
    }

    public static class QuestionAnswer {
        private TQuestion question;
        private List<TAnswer> answers;

        public QuestionAnswer(TQuestion t, List<TAnswer> tAnswers) {
            question = t;
            answers = tAnswers;
        }

        public QuestionAnswer() {
        }

        public List<TAnswer> getAnswers() {
            return answers;
        }

        public void setAnswers(List<TAnswer> answers) {
            this.answers = answers;
        }

        public TQuestion getQuestion() {
            return question;
        }

        public void setQuestion(TQuestion question) {
            this.question = question;
        }
    }


}
