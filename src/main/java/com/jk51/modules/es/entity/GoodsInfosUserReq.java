package com.jk51.modules.es.entity;

public class GoodsInfosUserReq {

	private String dbname;
	
	private String keyWords;
	
	private String brandid;
	
	private String minPrice;
	
	private String maxPrice;
	
	private int pageNum;
	
	private int currentPage;
	
	private int priceSort;
	
	public int getPriceSort() {
		return priceSort;
	}

	public void setPriceSort(int priceSort) {
		this.priceSort = priceSort;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getBrandid() {
		return brandid;
	}

	public void setBrandid(String brandid) {
		this.brandid = brandid;
	}

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public String toString(){
		return this.dbname + "|" + this.keyWords + "|" + this.brandid + "|" + this.minPrice + "|" + this.maxPrice + "|" + this.pageNum + "|" + this.currentPage + "|" + this.priceSort;
	}
	
}
