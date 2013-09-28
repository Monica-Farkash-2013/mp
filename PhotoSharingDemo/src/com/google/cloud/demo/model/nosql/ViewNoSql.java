package com.google.cloud.demo.model.nosql;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.cloud.demo.model.View;

public class ViewNoSql extends DemoEntityNoSql implements View {
	  static final String FIELD_NAME_ACTIVE = "active";
	  static final String FIELD_NAME_ALBUM_ID = "albumId";
	  static final String FIELD_NAME_UPLOAD_TIME = "viewTime";

	protected ViewNoSql(Entity entity) {
		super(entity);
	}

	public ViewNoSql(Key parentKey, String kind) {
	    super(parentKey, kind);
	    setActive(true);
	}

	public static final String getKind() {
	    return View.class.getSimpleName();
	}
	
	@Override
	public Long getId() {
		return entity.getKey().getId();
   	}
	
	@Override
	public String getViewOwnerId() {
	    return entity.getParent().getName();
	  }


	@Override
	public long getViewTime() {
		return (Long) entity.getProperty(FIELD_NAME_UPLOAD_TIME);
	}

	@Override
	public void setViewTime(long viewTime) {
	    entity.setProperty(FIELD_NAME_UPLOAD_TIME, viewTime);
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
	public boolean isActive() {
	    Boolean active = (Boolean) entity.getProperty(FIELD_NAME_ACTIVE);
	    // By default, if not set false, a photo is active.
	    return active != null && active;
	}

	@Override
	public void setActive(boolean active) {
	    entity.setProperty(FIELD_NAME_ACTIVE, active);
	}

}
