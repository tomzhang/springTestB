package com.jk51.modules.exportreport.service;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName ExportClassifyFromdb
 * @Description 从数据库查询分类导出
 * @Date 2018-09-15 15:18
 */

public class ExportClassifyFromdb {
    //数据表头 list
    private List<String> header = new ArrayList<>();

    private List<String> cols = new ArrayList<>();

    //查询参数
    private Map<String, Object> params = new HashedMap();

    public ExportClassifyFromdb() {
    }

    public ExportClassifyFromdb(Map<String, Object> params) {
        this.params = params;
    }

    public ExportClassifyFromdb(String[] header, Map<String, Object> params) {
        if(header != null){
            this.header = Arrays.asList(header);
        }
        this.params = params;
    }

    public ExportClassifyFromdb(List<String> header, Map<String, Object> params) {
        this.header = header;
        this.params = params;
    }

    public void setHeaders(String ... _header){
        if(_header == null){
            //抛出异常
        }
        header.clear();
        for(String h : _header){
            header.add(h);
        }
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<String> getCols() {
        return cols;
    }

    public void setCols(List<String> cols) {
        this.cols = cols;
    }

    /*public void setCols(String ... _cols){
        if(_cols == null || _cols.length == 0){
            return;
        }
        cols.clear();
        for(String c : _cols){
            cols.add(c);
        }
    }*/
}
