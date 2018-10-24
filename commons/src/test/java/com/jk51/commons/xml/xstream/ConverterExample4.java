package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

import java.util.TimeZone;

/**
 * Created by hulan on 2017/1/19.
 *
 * 对象进行反序列化。我们使用DateConverter可以指定格式。
 */
public class ConverterExample4 {
    private static String xml = "<Musician><birthdate>01-19-2017 16:31:10</birthdate></Musician>";
    public static void main(String[] args){
        XStream xStream=new XStream();
        xStream.processAnnotations(Musician.class);
        xStream.registerConverter(new DateConverter("MM-dd-yyyy HH:mm:ss",
                new String[] {}, TimeZone.getTimeZone("UTC")));
        Musician musician = (Musician) xStream.fromXML(xml);
        System.out.println(musician);
    }
}
