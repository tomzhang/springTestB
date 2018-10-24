package com.jk51.model.health;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class YbMemberHealthLog implements Serializable {
    private Integer id;

    /**
     * 卡号 手机号或者身份证号
     */
    private String cardNo;

    /**
     * 接口码
     */
    private Integer functioncode;

    /**
     * 机器码
     */
    private Integer icpcode;

    /**
     * 检查时间
     */
    private Date checkdate;

    private String xml;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getFunctioncode() {
        return functioncode;
    }

    public void setFunctioncode(Integer functioncode) {
        this.functioncode = functioncode;
    }

    public Integer getIcpcode() {
        return icpcode;
    }

    public void setIcpcode(Integer icpcode) {
        this.icpcode = icpcode;
    }

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        YbMemberHealthLog other = (YbMemberHealthLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCardNo() == null ? other.getCardNo() == null : this.getCardNo().equals(other.getCardNo()))
            && (this.getFunctioncode() == null ? other.getFunctioncode() == null : this.getFunctioncode().equals(other.getFunctioncode()))
            && (this.getIcpcode() == null ? other.getIcpcode() == null : this.getIcpcode().equals(other.getIcpcode()))
            && (this.getCheckdate() == null ? other.getCheckdate() == null : this.getCheckdate().equals(other.getCheckdate()))
            && (this.getXml() == null ? other.getXml() == null : this.getXml().equals(other.getXml()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCardNo() == null) ? 0 : getCardNo().hashCode());
        result = prime * result + ((getFunctioncode() == null) ? 0 : getFunctioncode().hashCode());
        result = prime * result + ((getIcpcode() == null) ? 0 : getIcpcode().hashCode());
        result = prime * result + ((getCheckdate() == null) ? 0 : getCheckdate().hashCode());
        result = prime * result + ((getXml() == null) ? 0 : getXml().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cardNo=").append(cardNo);
        sb.append(", functioncode=").append(functioncode);
        sb.append(", icpcode=").append(icpcode);
        sb.append(", checkdate=").append(checkdate);
        sb.append(", xml=").append(xml);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}