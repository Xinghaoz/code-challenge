package com.shutterfly.www.event;

import java.util.Date;

public class ImageUploadEvent extends Event {
	String imageId;
	String cameraMake;
	String cameraModel;
	
	public ImageUploadEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
		super(type, verb, customerId, eventTime);	
	}
	
	public void setCameraMake(String cameraMake) {
		this.cameraMake = cameraMake;
	}
	
	public void setCameraModel(String cameraModel) {
		this.cameraModel = cameraModel;
	}
}