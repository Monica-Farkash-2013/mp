package com.google.cloud.demo.model;

public interface Subscription extends DemoEntity {
	  Long getId();
	  
	  Long getAlbumId();

	  void setAlbumId(Long albumId);

	  String getOwnerNickname();

	  void setOwnerNickname(String nickname);

	  String getOwnerId();

	  String getEmail();

	  void setEmail(String email);


}
