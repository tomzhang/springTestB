package com.jk51.modules.im.service.indexCount.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-07-18
 * 修改记录:
 */
public class Proportion {

    //指标名称
    private String name;
    //指标较前一日比例
    private Float proportion;
    //指标值
    private float value;

    private String unit;




    public static Proportion createProportion(float proportionValue,String name,float value,String unit){
        Proportion proportion = new Proportion();
        proportion.setProportion(proportionValue);
        proportion.setValue(value);
        proportion.setName(name);
        proportion.setUnit(unit);
        return proportion;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getProportion() {
        return proportion;
    }

    public void setProportion(Float proportion) {
        this.proportion = proportion;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
