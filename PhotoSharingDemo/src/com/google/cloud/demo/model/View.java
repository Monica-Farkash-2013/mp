package com.google.cloud.demo.model;

public interface View extends DemoEntity {
	  Long getId();
	  
	  long getViewTime();

	  void setViewTime(long viewTime);

	  Long getAlbumId();

	  void setAlbumId(Long albumId);

	  boolean isActive();

	  void setActive(boolean active);

	  String getViewOwnerId();
	  
}
