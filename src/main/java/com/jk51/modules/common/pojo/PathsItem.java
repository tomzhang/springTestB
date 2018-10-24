package com.jk51.modules.common.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PathsItem{

	@JsonProperty("duration")
	private Integer duration;

	@JsonProperty("distance")
	private Integer distance;

	public void setDuration(Integer duration){
		this.duration = duration;
	}

	public Integer getDuration(){
		return duration;
	}

	public void setDistance(Integer distance){
		this.distance = distance;
	}

	public Integer getDistance(){
		return distance;
	}
}
