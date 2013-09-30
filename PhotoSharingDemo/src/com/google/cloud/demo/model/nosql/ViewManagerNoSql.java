package com.google.cloud.demo.model.nosql;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.demo.model.View;
import com.google.cloud.demo.model.ViewManager;

public class ViewManagerNoSql extends DemoEntityManagerNoSql<View> implements
		ViewManager {
	private final AlbumManagerNoSql albumManager;

	public ViewManagerNoSql(AlbumManagerNoSql albumManager) {
		super(View.class);
	    this.albumManager = albumManager;
	}

	@Override
	public View getView(String albumId, Long id) {
	    return getEntity(createViewKey(albumId, id));
	}
	
	public Key createViewKey(@Nullable String albumId, long id) {
	    if (albumId != null) {
	    	Key parentKey = albumManager.createAlbumKey(albumId);
	    	return KeyFactory.createKey(parentKey, getKind(), id);
	    } else {
	    	return KeyFactory.createKey(getKind(), id);
	    }
	}

	@Override
	protected View fromEntity(Entity entity) {
	    return new ViewNoSql(entity);
	}

	@Override
	public ViewNoSql fromParentKey(Key parentKey) {
	    return new ViewNoSql(parentKey, getKind());
	}

	@Override
	public void addAlbumView(String albumId) {
		if (albumId != null)
		{
			View view = newView(albumId);
			Long id = Long.parseLong(albumId);
			view.setAlbumId(id.longValue());
			view.setViewTime(System.currentTimeMillis());
	        upsertEntity(view);

		}
		return;
	}
	
	@Override
	public Iterable<View> getAlbumViews(String albumId) {
		long timeWindow = System.currentTimeMillis() - 60L * 60L * 1000L;
	    Query query = new Query(getKind());
	    Query.Filter viewIdFilter =  new Query.FilterPredicate(ViewNoSql.FIELD_NAME_ALBUM_ID, FilterOperator.EQUAL, Long.parseLong(albumId));
	    List<Filter> filters = Arrays.asList(viewIdFilter, 
	    		new Query.FilterPredicate(ViewNoSql.FIELD_NAME_UPLOAD_TIME, FilterOperator.GREATER_THAN_OR_EQUAL, timeWindow),
				new Query.FilterPredicate(ViewNoSql.FIELD_NAME_ACTIVE, FilterOperator.EQUAL, true));
	    Filter filter = new Query.CompositeFilter(CompositeFilterOperator.AND, filters);
	    query.setFilter(filter);
	    FetchOptions options = FetchOptions.Builder.withDefaults();
	    return queryEntities(query, options);
	}
	
	 @Override
	 public ViewNoSql newView(String albumId) {
	    return new ViewNoSql(albumManager.createAlbumKey(albumId), getKind());
	 }

}
