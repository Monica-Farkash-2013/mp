package com.google.cloud.demo.model;

import com.google.appengine.api.blobstore.BlobKey;

public interface Album extends DemoEntity {
  Long getId();

  BlobKey getBlobKey();

  void setBlobKey(BlobKey blobKey);

  boolean isShared();

  void setShared(boolean shared);

  String getTitle();

  void setTitle(String title);

  String getOwnerNickname();

  void setOwnerNickname(String nickname);

  String getOwnerId();

  long getUploadTime();

  void setUploadTime(long uploadTime);

  boolean isActive();

  void setActive(boolean active);

  void setSubscribers(String subscribers);

  String getSubscribers();
  
  void setTags(String tags);

  String getTags();

  void setViews(long views);

  Long getViews();
}
