package com.jk51.modules.es.utils;

public class JKStringUtils {

	public static boolean isBlank(String str){
		
		if(null == str || "".equals(str)){
			return true;
		}else{
			return false;
		}
	}
}
