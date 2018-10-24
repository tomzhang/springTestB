package com.jk51.modules.esn.entity;

public class GoodsUrl {

	private String hostId;
	
	private String imageId;

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	public String toString()
	{
		return this.hostId + "|" + this.imageId;
	}
}
