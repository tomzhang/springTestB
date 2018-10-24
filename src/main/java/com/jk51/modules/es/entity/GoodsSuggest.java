package com.jk51.modules.es.entity;

/**
 * Created by think on 2016/12/21.
 */
public class GoodsSuggest {

    private String keyword;
    private Integer num;

    public GoodsSuggest(String keyword) {
        this.keyword=keyword;
        num=10;
    }

    public void addNum(){
        num++;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
