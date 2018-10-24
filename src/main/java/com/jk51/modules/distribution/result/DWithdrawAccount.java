package com.jk51.modules.distribution.result;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-17 16:27
 * 修改记录:
 */
public class DWithdrawAccount implements Serializable {

        private Integer id  ;//   int(11) NOT NULL AUTO_INCREMENT COMMENT '提现账号表',
        private Integer distributorId  ;//  int(11) NOT NULL COMMENT '分销商id',
        private Integer owner  ;//  int(11) NOT NULL COMMENT '药店总部',
        private String name  ;//  varchar(50) NOT NULL COMMENT '开户人姓名',
        private String account  ;//  varchar(100) NOT NULL COMMENT '提现帐号',
        private String type  ;//  varchar(30) NOT NULL COMMENT '账号类型: 100:ali (支付宝) ，200:wx (微信)，300:银联',
        private String bankName  ;//  varchar(50) DEFAULT '0' COMMENT '开户行名称',

        public DWithdrawAccount() {
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Integer getDistributorId() {
                return distributorId;
        }

        public void setDistributorId(Integer distributorId) {
                this.distributorId = distributorId;
        }

        public Integer getOwner() {
                return owner;
        }

        public void setOwner(Integer owner) {
                this.owner = owner;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getAccount() {
                return account;
        }

        public void setAccount(String account) {
                this.account = account;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getBankName() {
                return bankName;
        }

        public void setBankName(String bankName) {
                this.bankName = bankName;
        }
}
