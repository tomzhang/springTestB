package com.jk51.commons;

/**
 * Created by Administrator on 2017/1/19.
 */
public class SCommonConstant {
    public static final String CHARSET = "UTF-8";
    public static final String FORMAT = "JPG";
    public static final String DES = "DES";
    public static final String AES = "AES";
    public static final String SHA_1 = "SHA1";
    public static final String MD5 = "MD5";
    public static final String BARCODE_PNG = ".png";
    public static final String DOT = ".";
    public static final String FILE_SUFFIX_EXCEL_2003 = "xls";
    public static final String FILE_SUFFIX_EXCEL_2007 = "xlsx";
    public static final String REGEXP_DOT = "[.]";

    //回复类型 110=被添加自动回复 120=消息自动回复 130=关键词自动回复
    public static final int AUTO_REPLY_FOCUS  = 110;   //被添加自动回复 （拒绝退款）
    public static final int AUTO_REPLY_DEFAULT = 120;   //消息自动回复
    public static final int AUTO_REPLY_KEYWORD = 130; //关键词自动回复

    // 回复规则
    public static final String AUTO_REPLY_RULE_FOCUS = "关注自动回复";
    public static final String AUTO_REPLY_RULE_DEFAULT = "消息自动回复";

    //默认站点ID
    public static final int DEFAULT_SITE_ID = 100030;

}
