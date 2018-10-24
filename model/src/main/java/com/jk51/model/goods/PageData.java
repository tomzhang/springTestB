package com.jk51.model.goods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class PageData extends HashMap<Object, Object> implements Serializable {

    private static final long serialVersionUID = -70024608917721088L;
    
    /**
     * 返回string key 不在放回 0  
     * value 如果是boolean 值 true ：1    & false ：0 
     */
    public String getString(Object key) {
    	if(get(key) instanceof Boolean){
    		if((Boolean)get(key)){
    			return "1";
    		}else{
    			return "0";
    		}
    	}
    	
		return get(key) == null ? "" : get(key)+"";
	}

    
    /**
     * 返回BigDecimal key 不存在返回 0
     */
    public BigDecimal getBigDecimal(Object key) {
    	BigDecimal bd = null;
    	try{
    		 bd = new BigDecimal(getString(key)).setScale(2, BigDecimal.ROUND_HALF_UP);
    	}catch (Exception e) {
    		 bd = new BigDecimal(0);
		}
		return bd;
	}
    

    /**
     * 返回int key 不存在返回 0
     */
    public Integer getInteger(Object key) {
    	Integer it = null;
    	try{
    		it = getBigDecimal(key).intValue();
    	}catch (Exception e) {
    		it = 0;
		}
		return it;
	}
    
    /**
     * 返回int key 不存在返回 0
     */
    public int getInt(Object key) {
        return getInteger(key);
    }

    /**
     * 返回int key 不存在返回 0
     */
    public Long getLong(Object key) {
    	Long it = null;
    	try{
    		
    		it = getBigDecimal(key).longValue();
    	}catch (Exception e) {
    		it = 0l;
		}
		return it;
	}
    
	public PageData() {
	}
	
	public PageData(Map map) {
		this.putAll(map);
	}
	
	public PageData(String result) {
		this.put("result", result);
	}

	public PageData(String result,String message ) {
		this.put("result", result);
		this.put("message", message);
	}

	/**
	 * @param argsNew
	 * @param argsOld
	 * @param pageData
	 *            查询结果封装，新老属性名不一样
	 */
	public PageData(String[] argsNew, String[] argsOld, PageData pageData) {
		for (int i = 0; i < argsNew.length; i++) {
			this.put(argsNew[i], pageData.get(argsOld[i]));
		}
	}

	/**
	 * @param argsNew
	 * @param pageData
	 *            查询结果封装，属性名与数据库字段名一样
	 */
	public PageData(String[] argsNew, PageData pageData) {
		for (int i = 0; i < argsNew.length; i++) {
			this.put(argsNew[i], pageData.get(argsNew[i]));
		}
	}
}
