package com.jk51.modules.integral.domain;

/**
 * 积分规则
 */

/**
 * 必须将内部类修饰为static的或手动提供有参构造,
 * 内部类需要有参构造来设置外部类的引用
 *
 */
public class IntegralRule<T> {

    private T rule;

    /***
     * 注册送积分规则
     */
    public static class RegisterRule {
        // 首次注册赠送
        private int firstRegister;

        public int getFirstRegister() {
            return firstRegister;
        }

        public void setFirstRegister(int firstRegister) {
            this.firstRegister = firstRegister;
        }
    }

    /**
     * 签到送积分规则
     */
    public static class SignRule {
        // 每次签到
        private int every;
        // 联系签到天数
        private int signday;
        // 额外赠送
        private int extGive;

        public int getEvery() {
            return every;
        }

        public void setEvery(int every) {
            this.every = every;
        }

        public int getSignday() {
            return signday;
        }

        public void setSignday(int signday) {
            this.signday = signday;
        }

        public int getExtGive() {
            return extGive;
        }

        public void setExtGive(int extGive) {
            this.extGive = extGive;
        }
    }

    /**
     * 购物送积分规则
     *
     */
    public static class ShoppingRule {
        private int type;//1:满额送固定积分   2:累计
        private int payLevel1;
        private int payLevel2;
        private int payLevel3;
        private int payLevel4;

        private int integralLevel1;
        private int integralLevel2;
        private int integralLevel3;
        private int integralLevel4;

        public void setType(int type) {
            this.type = type;
        }

        public int getPayLevel1() {
            return payLevel1;
        }

        public void setPayLevel1(int payLevel1) {
            this.payLevel1 = payLevel1;
        }

        public int getPayLevel2() {
            return payLevel2;
        }

        public void setPayLevel2(int payLevel2) {
            this.payLevel2 = payLevel2;
        }

        public int getPayLevel3() {
            return payLevel3;
        }

        public void setPayLevel3(int payLevel3) {
            this.payLevel3 = payLevel3;
        }

        public int getPayLevel4() {
            return payLevel4;
        }

        public void setPayLevel4(int payLevel4) {
            this.payLevel4 = payLevel4;
        }

        public int getIntegralLevel1() {
            return integralLevel1;
        }

        public void setIntegralLevel1(int integralLevel1) {
            this.integralLevel1 = integralLevel1;
        }

        public int getIntegralLevel2() {
            return integralLevel2;
        }

        public void setIntegralLevel2(int integralLevel2) {
            this.integralLevel2 = integralLevel2;
        }

        public int getIntegralLevel3() {
            return integralLevel3;
        }

        public void setIntegralLevel3(int integralLevel3) {
            this.integralLevel3 = integralLevel3;
        }

        public int getIntegralLevel4() {
            return integralLevel4;
        }

        public void setIntegralLevel4(int integralLevel4) {
            this.integralLevel4 = integralLevel4;
        }

        @Override
        public String toString() {
            return "ShoppingRule{" +
                    "type=" + type +
                    ", payLevel1=" + payLevel1 +
                    ", payLevel2=" + payLevel2 +
                    ", payLevel3=" + payLevel3 +
                    ", payLevel4=" + payLevel4 +
                    ", integralLevel1=" + integralLevel1 +
                    ", integralLevel2=" + integralLevel2 +
                    ", integralLevel3=" + integralLevel3 +
                    ", integralLevel4=" + integralLevel4 +
                    '}';
        }
    }

}
