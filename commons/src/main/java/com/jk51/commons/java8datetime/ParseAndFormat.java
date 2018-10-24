package com.jk51.commons.java8datetime;

import java.time.format.DateTimeFormatter;

/**
 * 该类用于LocalDateTime, LocalDate, LocalTime的解析和格式化，配合{@link Transform}使用
 *
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
public class ParseAndFormat {
    public static final DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateTimeFormatter_1 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 kk:mm");
    public static final DateTimeFormatter dateTimeFormatter_2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter dateTimeFormatter_3 =DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
