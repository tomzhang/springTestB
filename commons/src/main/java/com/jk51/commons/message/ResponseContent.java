package com.jk51.commons.message;

abstract public class ResponseContent {
    // 暂时没用
    protected int code;

    protected String msg;

    protected Object result;

    /**
     * 成功只需要返回具体的数据
     * @param obj
     * @return
     */
    public static String render(Object obj) {
        throw new RuntimeException("方法未实现");
    }

    public static void bbc() {

    }

    /**
     * 错误需要一个错误码和错误原因的说明
     * @param code
     * @param msg
     * @return
     */
    public static String render(int code, String msg) {
        throw new RuntimeException("方法未实现");
    }
}
