package com.google.cloud.demo.model;

public interface LeaderboardEntry extends DemoEntity {
	  Long getId();
	  
	  long getViewsNumber();

	  void setViewsNumber(long views);

	  Long getAlbumId();

	  void setAlbumId(Long albumId);

	String getUserId();

	void setUserId(String userId);

}
