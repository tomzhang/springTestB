package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.XStream;

/**
 * Created by hulan on 2017/1/19.
 *
 *在这第一个例子,我们有一个XML,一个名为“音乐”的父元素。它的子元素类型。我们填充一个音乐对象有一个“类型”字段是一个枚举。
 */
public class ConverterExample {
    static String xml = "<Music><type>jazz</type></Music>";
    public static void main(String[] args){
        XStream xStream=new XStream();
        xStream.processAnnotations(Music.class);
        Music music=(Music) xStream.fromXML(xml);
        System.out.println(music);
    }
}
