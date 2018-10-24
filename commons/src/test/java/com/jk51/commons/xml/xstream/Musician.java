package com.jk51.commons.xml.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hulan on 2017/1/19.
 */
@XStreamAlias("Musician")
public class Musician {

    private Date birthdate;

    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return "Musician [birthdate=" + format.format(birthdate) + "]";
    }
}
