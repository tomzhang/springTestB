package com.jk51.commons.message;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/17-02-17
 * 修改记录 :
 */

    public class ReturnDto {

        private String code;
        private String message;
        private String value;

        public ReturnDto(){

        }

        public ReturnDto(String code, String message,String value) {
            this.code = code;
            this.message = message;
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static ReturnDto buildSuccessReturnDto(String value){

            return new ReturnDto("000","Success",value);
        }

        public static ReturnDto buildFailedReturnDto(String failMessage){
            return new ReturnDto("101",failMessage,null);
        }

        public static ReturnDto buildSystemErrorReturnDto(){
            return new ReturnDto("599","System Error",null);
        }

        @Override
        public String toString() {
            return "ReturnDto{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }

    }
