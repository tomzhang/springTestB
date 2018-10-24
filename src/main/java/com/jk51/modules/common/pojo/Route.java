package com.jk51.modules.common.pojo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

public class Route{

	@JsonProperty("paths")
	private List<PathsItem> paths;

	@JsonProperty("origin")
	private String origin;

	@JsonProperty("destination")
	private String destination;

	public void setPaths(List<PathsItem> paths){
		this.paths = paths;
	}

	public List<PathsItem> getPaths(){
		return paths;
	}

	public void setOrigin(String origin){
		this.origin = origin;
	}

	public String getOrigin(){
		return origin;
	}

	public void setDestination(String destination){
		this.destination = destination;
	}

	public String getDestination(){
		return destination;
	}
}
