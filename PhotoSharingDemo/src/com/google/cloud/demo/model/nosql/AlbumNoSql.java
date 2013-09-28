package com.google.cloud.demo.model.nosql;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.cloud.demo.model.Album;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;


public class AlbumNoSql extends DemoEntityNoSql implements Album {
	  static final String FIELD_NAME_OWNER_ID = "ownerId";
	  static final String FIELD_NAME_OWNER_NICKNAME = "owner";
	  static final String FIELD_NAME_TITLE = "title";
	  static final String FIELD_NAME_SHARED = "shared";
	  static final String FIELD_NAME_BLOB_KEY = "blobKey";
	  static final String FIELD_NAME_UPLOAD_TIME = "createTime";
	  static final String FIELD_NAME_ACTIVE = "active";
	  static final String EMPTY_TITLE = "no title";
	  static final String FIELD_NAME_SUBSCRIBERS = "subscribers";
	  static final String FIELD_NAME_TAGS = "tags";
	  static final String FIELD_NAME_VIEWS = "views";
	  
	  public AlbumNoSql(Entity entity) {
	    super(entity);
	  }

	  public AlbumNoSql(Key parentKey, String kind) {
	    super(parentKey, kind);
	    setActive(true);
	    entity.setProperty(FIELD_NAME_OWNER_ID, parentKey.getName());
	  }

	  @Override
	  public BlobKey getBlobKey() {
	    return (BlobKey) entity.getProperty(FIELD_NAME_BLOB_KEY);
	  }

	  @Override
	  public void setBlobKey(BlobKey blobKey) {
	    entity.setProperty(FIELD_NAME_BLOB_KEY, blobKey);
	  }

	  @Override
	  public boolean isShared() {
	    Boolean shared = (Boolean) entity.getProperty(FIELD_NAME_SHARED);
	    return shared != null && shared;
	  }

	  @Override
	  public void setShared(boolean shared) {
	    entity.setProperty(FIELD_NAME_SHARED, shared);
	  }

	  @Override
	  public String getTitle() {
		  String ret = (String) entity.getProperty(FIELD_NAME_TITLE);
		  if (ret.isEmpty())
			  ret = EMPTY_TITLE;
	    return ret; 
	  }

	  @Override
	  public void setTitle(String title) {
	    entity.setProperty(FIELD_NAME_TITLE, title);
	  }

	  @Override
	  public String getOwnerNickname() {
	    return (String) entity.getProperty(FIELD_NAME_OWNER_NICKNAME);
	  }

	  @Override
	  public void setOwnerNickname(String nickename) {
	    entity.setProperty(FIELD_NAME_OWNER_NICKNAME, nickename);
	  }

	  @Override
	  public String getOwnerId() {
	    return (String) entity.getProperty(FIELD_NAME_OWNER_ID);
	  }

	  @Override
	  public long getUploadTime() {
	    return (Long) entity.getProperty(FIELD_NAME_UPLOAD_TIME);
	  }

	  @Override
	  public void setUploadTime(long uploadTime) {
	    entity.setProperty(FIELD_NAME_UPLOAD_TIME, uploadTime);
	  }

	  @Override
	  public Long getId() {
	    return entity.getKey().getId();
	  }

	  @Override
	  public boolean isActive() {
	    Boolean active = (Boolean) entity.getProperty(FIELD_NAME_ACTIVE);
	    // By default, if not set false, a photo is active.
	    return active != null && active;
	  }

	  @Override
	  public void setActive(boolean active) {
	    entity.setProperty(FIELD_NAME_ACTIVE, active);
	  }

	@Override
	public void setSubscribers(String subscribers) {
	    entity.setProperty(FIELD_NAME_SUBSCRIBERS, subscribers);
	}

	@Override
	public String getSubscribers() {
	    return (String) entity.getProperty(FIELD_NAME_SUBSCRIBERS);
	}

	@Override
	public void setTags(String tags) {
	    entity.setProperty(FIELD_NAME_TAGS, tags);
	}

	@Override
	public String getTags() {
	    return (String) entity.getProperty(FIELD_NAME_TAGS);
	}

 	@Override
 	public void setViews(long views) {
 		entity.setProperty(FIELD_NAME_VIEWS, views);
 	}

 	@Override
 	public Long getViews() {
 	    return (Long) entity.getProperty(FIELD_NAME_VIEWS);
 	}
}
