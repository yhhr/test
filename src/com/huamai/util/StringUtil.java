package com.huamai.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 字符串相关工具类
 * @author Administrator
 *
 */
public class StringUtil {
	/**
	 * 将字符串转换为Integer
	 * @param str 要转换的数字字符串
	 * @return 转换后的数字
	 */
 public static Integer transferStrToInteger(String str){
	 Integer num=null;
	 try {
		if(str!=null&&!"".equals(str.trim())&&!"null".equals(str.trim())){
			 num=Integer.parseInt(str);
		 }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	}
	 return num;
 }
   /**
	 * 将字符串转换为Long
	 * @param str 要转换的数字字符串
	 * @return 转换后的数字
	 */
public static Long transferStrToLong(String str){
	 Long num=null;
	 try {
		if(str!=null&&!"".equals(str.trim())&&!"null".equals(str.trim())){
			 num=Long.parseLong(str);
		 }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	}
	 return num;
}
/**
	 * 将字符串转换为Double
	 * @param str 要转换的数字字符串
	 * @return 转换后的数字
	 */
public static Double transferStrToDouble(String str){
	Double num=null;
	 try {
		if(str!=null&&!"".equals(str.trim())&&!"null".equals(str.trim())){
			 num=Double.parseDouble(str);
		 }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	}
	 return num;
}
/**
 * 将java日期字符串转换为Timestamp
 * @param dateStr java日期字符串
 * @param sdf 日期转换对象
 * @return sql日期
 */
public static java.sql.Timestamp transferDateStrToTimestamp(String dateStr,SimpleDateFormat sdf){
	Date date=null;
	java.sql.Timestamp sqlDate=null;
	try {
		if(dateStr!=null&&!"".equals(dateStr.trim())){
			date = sdf.parse(dateStr);
			sqlDate=new java.sql.Timestamp(date.getTime());
		}
	} catch (Exception e) {
	}
	return sqlDate;
}

	/**
	 * 判断子字符串是空
	 */
	public static boolean strIsNull(Object str){
		if(str==null||str.toString().trim().isEmpty()){
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串不是空
	 */
	public static boolean strIsNotNull(Object str){
		return !strIsNull(str);
	}

	/**
	 * 获取字符串
	 */
	public static String getStr(Object obj){
		if(obj==null){
			return "";
		}
		return obj.toString();
	}
	/**
	 * 如果是空就返回0
	 */
	public static String getStrZero(Object obj){
		if(obj==null){
			return "0";
		}
		return obj.toString();
	}
	/**
	 * 生成36位的UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}

}
