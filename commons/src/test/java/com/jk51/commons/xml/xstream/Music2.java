package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by hulan on 2017/1/19.
 */
@XStreamAlias("Music")
public class Music2 {
    @XStreamImplicit
    List<String> type;
    public String toString(){
        return "Music2 [type=" + type + "]";
    }
}
