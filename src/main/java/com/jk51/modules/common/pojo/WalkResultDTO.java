package com.jk51.modules.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalkResultDTO{

	private Route route;

	private Integer count;

	private Integer infocode;

	private Integer status;

	private String info;

	public void setRoute(Route route){
		this.route = route;
	}

	public Route getRoute(){
		return route;
	}

	public void setCount(Integer count){
		this.count = count;
	}

	public Integer getCount(){
		return count;
	}

	public void setInfocode(Integer infocode){
		this.infocode = infocode;
	}

	public Integer getInfocode(){
		return infocode;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setInfo(String info){
		this.info = info;
	}

	public String getInfo(){
		return info;
	}

	@Override
 	public String toString(){
		return 
			"WalkResultDTO{" + 
			"route = '" + route + '\'' + 
			",count = '" + count + '\'' + 
			",infocode = '" + infocode + '\'' + 
			",status = '" + status + '\'' + 
			",info = '" + info + '\'' + 
			"}";
		}
}
