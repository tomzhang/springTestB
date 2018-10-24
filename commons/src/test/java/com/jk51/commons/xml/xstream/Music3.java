package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hulan on 2017/1/19.
 */
@XStreamAlias("Music")
public class Music3 {
    @XStreamImplicit
    Map<String, String> albums = new HashMap<String, String>();

    public String toString() {
        return "Music3 [testMap=" + albums + "]";
    }
}
