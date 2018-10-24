package com.jk51.modules.esn.entity;

import com.jk51.commons.string.StringUtil;

public class DefUrl {

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
	    if (StringUtil.isEmpty(imageId)) {
	        this.imageId = "";
        }else {
            this.imageId = imageId;
        }
		if (StringUtil.isEmpty(hostId)) {
            this.hostId = "";
        }
	}
	
	public String toString()
	{
		return this.hostId + "|" + this.imageId;
	}
	
}
