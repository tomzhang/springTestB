package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.XStream;

import java.util.Map;

/**
 * Created by hulan on 2017/1/19.
 *
 * 在这个例子中音乐元素有一个艺术家序列元素每个专辑名称和作者姓名。我们把音乐对象包含专辑的map。
 */
public class ConverterExample3 {
    private static String xml = "<Music><album><name>name1</name><author>author1</author></album></Music>";
    public static void main(String[] args){
        XStream xStream=new XStream();
        xStream.processAnnotations(Music3.class);
        xStream.alias("name",String.class);
        xStream.alias("author",String.class);
        xStream.alias("album",Map.Entry.class);
        Music3 music3= (Music3) xStream.fromXML(xml);
        System.out.println(music3.albums.size());
        System.out.println(music3);
    }

}
