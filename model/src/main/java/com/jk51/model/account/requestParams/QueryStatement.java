package com.jk51.model.account.requestParams;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jk51.commons.pricetransform.DoubleTransformUtil;
import com.jk51.model.order.Page;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017/3/11
 * 修改记录:
 */
public class QueryStatement extends Page{

    private String merchantNum;
    private String name;
    private Integer bilStatus;
    private Integer examineStatus;
    private Integer settlementStatus;
    private Double sPayment;
    private Double ePayment;
    private String bilNum;
    private Date sOutDate;
    private Date eOutDate;
    private Date sActualDate;
    private Date eActualDate;
    private Date financeDate;
    private Date financeDateEnd;
    private Integer settlementType;
    private String findType;//查询方：merchant（商户后台），51jk（51后台）

    public String getFindType() {
        return findType;
    }

    public void setFindType(String findType) {
        this.findType = findType;
    }

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBilStatus() {
        return bilStatus;
    }

    public void setBilStatus(Integer bilStatus) {
        this.bilStatus = bilStatus;
    }

    public Integer getExamineStatus() {
        return examineStatus;
    }

    public void setExamineStatus(Integer examineStatus) {
        this.examineStatus = examineStatus;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Double getsPayment() {
        if (sPayment == null){
            return sPayment;
        }else {
//            return sPayment*100;
            return DoubleTransformUtil.multiplyDou(sPayment,100).doubleValue();
        }
    }

    public void setsPayment(Double sPayment) {
        this.sPayment = sPayment;
    }

    public Double getePayment() {
        if (ePayment == null){
            return ePayment;
        }else {
//            return ePayment*100;
            return DoubleTransformUtil.multiplyDou(ePayment,100).doubleValue();
        }
    }

    public void setePayment(Double ePayment) {
        this.ePayment = ePayment;
    }

    public String getBilNum() {
        return bilNum;
    }

    public void setBilNum(String bilNum) {
        this.bilNum = bilNum;
    }

    public Date getsOutDate() {
        return sOutDate;
    }

    public void setsOutDate(Date sOutDate) {
        this.sOutDate = sOutDate;
    }

    public Date geteOutDate() {
        return eOutDate;
    }

    public void seteOutDate(Date eOutDate) {
        this.eOutDate = eOutDate;
    }

    public Date getsActualDate() {
        return sActualDate;
    }

    public void setsActualDate(Date sActualDate) {
        this.sActualDate = sActualDate;
    }

    public Date geteActualDate() {
        return eActualDate;
    }

    public void seteActualDate(Date eActualDate) {
        this.eActualDate = eActualDate;
    }

    public Date getFinanceDate() {
        if (financeDate == null){
            return financeDate;
        }else {
          return new Date(financeDate.getTime() - (long)3 * 24 * 60 * 60 * 1000);
        }
    }

    public void setFinanceDate(Date financeDate) {
        this.financeDate = financeDate;
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettelmentType(Integer settelmentType) {
        this.settlementType = settlementType;
    }

    public Date getFinanceDateEnd() {
        if (financeDateEnd == null){
            return financeDateEnd;
        }else {
            return new Date(financeDateEnd.getTime() - (long)3 * 24 * 60 * 60 * 1000);
        }
    }

    public void setFinanceDateEnd(Date financeDateEnd) {
        this.financeDateEnd = financeDateEnd;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }
}
