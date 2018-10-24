package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by hulan on 2017/1/19.
 */
@XStreamAlias("Music")
public class Music {
    public enum genre{
        ROCK,JAZZ
    };
    genre type;
    public String toString(){
        return "Music [type=" + type + "]";
    }
}
