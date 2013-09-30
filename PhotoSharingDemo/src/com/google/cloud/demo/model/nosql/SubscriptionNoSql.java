package com.google.cloud.demo.model.nosql;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.cloud.demo.model.Subscription;
import com.google.cloud.demo.model.View;

public class SubscriptionNoSql extends DemoEntityNoSql implements Subscription {
	static final String FIELD_NAME_ALBUM_ID = "albumId";
	  private static final String FIELD_NAME_NICKNAME = "nickname";
	  private static final String FIELD_NAME_EMAIL = "email";

	protected SubscriptionNoSql(Entity entity) {
		super(entity);
	}

	public SubscriptionNoSql(Key parentKey, String kind) {
	    super(parentKey, kind);
	}

	public static final String getKind() {
	    return View.class.getSimpleName();
	}
	
	@Override
	public Long getId() {
		return entity.getKey().getId();
 	}
	
	@Override
	public String getOwnerId() {
	    return entity.getParent().getName();
	  }

	@Override
	public Long getAlbumId() {
		Long albumId = (Long) entity.getProperty(FIELD_NAME_ALBUM_ID);
    	return albumId == null ? 0 : albumId;
	}

	@Override
	public void setAlbumId(Long albumId) {
	    entity.setProperty(FIELD_NAME_ALBUM_ID, albumId);
	}

	@Override
	public String getEmail() {
		return (String) entity.getProperty(FIELD_NAME_EMAIL);
	}
	
	@Override
	public void setEmail(String email) {
		entity.setProperty(FIELD_NAME_EMAIL, email);
	}
	
	@Override
	public String getOwnerNickname() {
		return (String) entity.getProperty(FIELD_NAME_NICKNAME);
	}
	
	@Override
	public void setOwnerNickname(String nickname) {
		entity.setProperty(FIELD_NAME_NICKNAME, nickname);
	}

}
