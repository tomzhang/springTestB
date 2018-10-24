package com.jk51.modules.exportreport.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-16
 * 修改记录:
 */
public class ExportParams {
    //执行对象的mapper名称，如:tradesMapper
    private String beanName;
    //执行对象的mapper的方法，如：getStoreTradesReport
    private String methodName;
    //数据表头 list
    private List<String> header = new ArrayList<>();

    private List<String> cols = new ArrayList<>();

    //查询参数
    private Map<String, Object> params;

    public ExportParams() {
    }

    public ExportParams(String beanName, String methodName, Map<String, Object> params) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
    }

    public ExportParams(String beanName, String methodName, String[] header, Map<String, Object> params) {
        this.beanName = beanName;
        this.methodName = methodName;
        if(header != null){
            this.header = Arrays.asList(header);
        }
        this.params = params;
    }

    public ExportParams(String beanName, String methodName, List<String> header, Map<String, Object> params) {
        this.beanName = beanName;
        this.methodName = methodName;
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

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
}
