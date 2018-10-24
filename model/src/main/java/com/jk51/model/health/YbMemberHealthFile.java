package com.jk51.model.health;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class YbMemberHealthFile implements Serializable {
    private Integer id;

    /**
     * 会员ID 对应yb_member.id
     */
    private Long memberId;

    /**
     * 卡号 手机号或者身份证号
     */
    private String cardNo;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 姓名
     */
    private String fullname;

    /**
     * 性别
     */
    private String gender;

    /**
     * 民族
     */
    private String nation;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 地址
     */
    private String addr;

    /**
     * 到期
     */
    private Date expire;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
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
        YbMemberHealthFile other = (YbMemberHealthFile) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getMemberId() == null ? other.getMemberId() == null : this.getMemberId().equals(other.getMemberId()))
            && (this.getCardNo() == null ? other.getCardNo() == null : this.getCardNo().equals(other.getCardNo()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getFullname() == null ? other.getFullname() == null : this.getFullname().equals(other.getFullname()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getNation() == null ? other.getNation() == null : this.getNation().equals(other.getNation()))
            && (this.getBirthday() == null ? other.getBirthday() == null : this.getBirthday().equals(other.getBirthday()))
            && (this.getAddr() == null ? other.getAddr() == null : this.getAddr().equals(other.getAddr()))
            && (this.getExpire() == null ? other.getExpire() == null : this.getExpire().equals(other.getExpire()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMemberId() == null) ? 0 : getMemberId().hashCode());
        result = prime * result + ((getCardNo() == null) ? 0 : getCardNo().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getFullname() == null) ? 0 : getFullname().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getNation() == null) ? 0 : getNation().hashCode());
        result = prime * result + ((getBirthday() == null) ? 0 : getBirthday().hashCode());
        result = prime * result + ((getAddr() == null) ? 0 : getAddr().hashCode());
        result = prime * result + ((getExpire() == null) ? 0 : getExpire().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", cardNo=").append(cardNo);
        sb.append(", phone=").append(phone);
        sb.append(", fullname=").append(fullname);
        sb.append(", gender=").append(gender);
        sb.append(", nation=").append(nation);
        sb.append(", birthday=").append(birthday);
        sb.append(", addr=").append(addr);
        sb.append(", expire=").append(expire);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}