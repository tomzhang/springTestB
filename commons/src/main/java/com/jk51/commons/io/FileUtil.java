package com.jk51.commons.io;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.io.*;
import java.util.*;



/**
 * 文件处理辅助类
 * @author wangzf
 *
 */
public class FileUtil {
	/**
	 * 生成随机文件名
	 * @param
	 * @return
	 */
	public static String getRandomFileName(){
		//时间戳(年-毫秒)作为文件前缀
		String date = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS");
		Long random_num = 0L;
		String random_suffix = null;
		do{
			//生成长整型的随机数字
			random_num = new Long(RandomUtils.nextLong());
			if(random_num.toString().length() >= 8){
				//此算法在访问比较密集时比较容易引起文件名命名冲突
				//random_suffix = random_num.toString().substring(random_num.toString().length() -7 , random_num.toString().length() - 1);
				//只取前八位
				random_suffix = random_num.toString().substring(0,8);
			}
		}while(random_num.toString().length() < 8);//如果随机数的长度小于8位则重新生成随机数
		
		return date + random_suffix;
	}
	
	/**
	 * 获取文件后缀名
	 * @param file 文件
	 * @return 后缀名字串
	 */
	public static String getSuffix(File file){
		if(file == null) return null;
		return getSuffix(file.getName());
	}

	/**
	 * 获取文件后缀名
	 * @param fileName 文件名
	 * @return 后缀名字串
	 */
	public static String getSuffix(String fileName){
		if(StringUtils.isEmpty(fileName)) return null;
		int pos = fileName.lastIndexOf(CommonConstant.DOT);
		if(pos == -1) return null;
		return fileName.substring(pos + 1);
	}

	
	/**
	 * 格式化文件后缀名序列号
	 * @param index 当前文件索引号
	 * @param total 文件总个数
	 * @return 格式化后的文件后缀名
	 */
	public static String formatSuffixSeq(int index,int total){
		int length = (total + StringUtils.EMPTY).length() + 1;
		int indexLength = (index + StringUtils.EMPTY).length();
		String suffix = StringUtils.EMPTY;
		if (length < 3) {
			length = 3;
		}
		for (int i = 0; i < length - indexLength; i++) {
			suffix += "0";
		}
		suffix += index;
		return suffix;
	}
	
	public static List<File> getSubFiles(File file,FileFilter filter,Comparator<File> comparator){
		File[] subfiles = null;
		if(filter != null){
			subfiles = file.listFiles(filter);
		}else{
			subfiles = file.listFiles();
		}
		if(subfiles == null) return new ArrayList<File>();
		List<File> list = Arrays.asList(subfiles);
		if (comparator != null) {
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	public static String getContent(File file) throws IOException{
		if(file == null || !file.exists()){
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader inputReader = new InputStreamReader(fis);
		BufferedReader bufferReader = new BufferedReader(inputReader);
		try{
			String line = null;
			while((line = bufferReader.readLine()) != null){
				sb.append(line).append("\r\n");
			}
		}finally{
			bufferReader.close();
			inputReader.close();
			fis.close();
		}
		return sb.toString();
	}
}
