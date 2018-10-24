package com.jk51.communal.exceptionUtil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/4/26
 * 修改记录:
 */
public class ExceptionUtil {


    public static String exceptionDetail(Throwable e){

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw,true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
}
