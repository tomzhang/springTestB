package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.XStream;

/**
 * Created by hulan on 2017/1/19.
 *
 * 在接下来的例子我们将类型转换为字符串的列表
 */
public class ConverterExample2 {
    private static final String xml = "<Music><type>rock2</type><type>jazz2</type></Music>";

    public static void main(String[] args) {
        XStream xStream = new XStream();
        xStream.processAnnotations(Music2.class);
        Music2 music2 = (Music2) xStream.fromXML(xml);
        for (String str : music2.type) {
            System.out.println(str);
        }
        System.out.println(music2);
    }

}
