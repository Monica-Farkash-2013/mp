package com.google.cloud.demo.model.nosql;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.cloud.demo.model.LeaderboardEntry;

public class LeaderboardEntryNoSql extends DemoEntityNoSql implements LeaderboardEntry {
	  static final String FIELD_NAME_ALBUM_ID = "albumId";
	  static final String FIELD_NAME_VIEWS_NUMBER = "viewsNumber";
	  static final String FIELD_NAME_USER_ID = "userId";

	protected LeaderboardEntryNoSql(Entity entity) {
		super(entity);
	}

	public LeaderboardEntryNoSql(String kind, String entryName) {
	    super(kind, entryName);
	}

	public static final String getKind() {
	    return LeaderboardEntry.class.getSimpleName();
	}
	
	@Override
	public Long getId() {
		return entity.getKey().getId();
 	}
	
	@Override
	public long getViewsNumber() {
		Long no = (Long) entity.getProperty(FIELD_NAME_VIEWS_NUMBER);
		return no == null ? 0 : no;
	}

	@Override
	public void setViewsNumber(long views) {
	    entity.setProperty(FIELD_NAME_VIEWS_NUMBER, views);
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
	public String getUserId() {
		return (String) entity.getProperty(FIELD_NAME_USER_ID);
	}

	@Override
	public void setUserId(String userId) {
	    entity.setProperty(FIELD_NAME_USER_ID, userId);
	}

}
