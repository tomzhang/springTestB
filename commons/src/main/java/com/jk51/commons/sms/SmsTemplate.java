package com.jk51.commons.sms;

public class SmsTemplate {
    private int id;
    private String word;
    private String paramName;
    private int yp_tpl_id;
    private int dj_status;
    private int yp_status;

    public SmsTemplate(int id, String word,String paramName, int yp_tpl_id,int dj_status) {
        this.id = id;
        this.word = word;
        this.paramName = paramName;
        this.yp_tpl_id = yp_tpl_id;
        this.dj_status = dj_status;
        this.yp_status = yp_status;
    }

    public SmsTemplate() {
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public int getDj_status() {
        return dj_status;
    }

    public void setDj_status(int dj_status) {
        this.dj_status = dj_status;
    }

    public int getYp_status() {
        return yp_status;
    }

    public void setYp_status(int yp_status) {
        this.yp_status = yp_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getYp_tpl_id() {
        return yp_tpl_id;
    }

    public void setYp_tpl_id(int yp_tpl_id) {
        this.yp_tpl_id = yp_tpl_id;
    }
}
