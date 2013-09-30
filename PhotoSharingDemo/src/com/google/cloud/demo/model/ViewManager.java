package com.google.cloud.demo.model;

import com.google.cloud.demo.model.nosql.ViewNoSql;

public interface ViewManager extends DemoEntityManager<View> {

	Iterable<View> getAlbumViews(String albumId);

	View newView(String albumId);

	View getView(String albumId, Long id);

	void addAlbumView(String albumId);

}
