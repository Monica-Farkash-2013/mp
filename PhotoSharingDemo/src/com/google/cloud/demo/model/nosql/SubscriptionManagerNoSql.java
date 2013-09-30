package com.google.cloud.demo.model.nosql;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.cloud.demo.model.Subscription;
import com.google.cloud.demo.model.SubscriptionManager;

public class SubscriptionManagerNoSql extends DemoEntityManagerNoSql<Subscription>
		implements SubscriptionManager {
	private DemoUserManagerNoSql userManager;

	protected SubscriptionManagerNoSql(DemoUserManagerNoSql userManager) {
		super(Subscription.class);
	    this.userManager = userManager;
	}

	@Override
	public Iterable<Subscription> getSubscriberAlbums(String subscriberId) {
	    Query query = new Query(getKind());
	    query.setAncestor(userManager.createDemoUserKey(subscriberId));
	    //Query.Filter filter = new Query.FilterPredicate(AlbumNoSql.FIELD_NAME_ACTIVE,
	    //    FilterOperator.EQUAL, true);
	    //query.setFilter(filter);
	    FetchOptions options = FetchOptions.Builder.withDefaults();
	    return queryEntities(query, options);
	}

	@Override
	public SubscriptionNoSql newSubscription(String subscriberId) {
	    return new SubscriptionNoSql(userManager.createDemoUserKey(subscriberId), getKind());
	}

	@Override
	protected Subscription fromParentKey(Key parentKey) {
	    return new SubscriptionNoSql(parentKey, getKind());
	}

	@Override
	protected Subscription fromEntity(Entity entity) {
	    return new SubscriptionNoSql(entity);
	}
	
	@Override
	public void addSubscription(String albumId, String subscriberId) {
		if (albumId != null && subscriberId != null)
		{
			Subscription subscription = newSubscription(subscriberId);
			Long id = Long.parseLong(albumId);
			subscription.setAlbumId(id.longValue());
			subscription.setOwnerNickname(userManager.getUser(subscriberId).getNickname());
			subscription.setEmail(userManager.getUser(subscriberId).getEmail());
	        upsertEntity(subscription);
		}
		return;
	}

	@Override
	public void unSubscribe(String albumId, String subscriberId) {
		if (albumId != null && subscriberId != null)
		{
			Long id = Long.parseLong(albumId);
			Iterable<Subscription> subscriptionIter = getSubscriberAlbums(subscriberId);
			for (Subscription subscription : subscriptionIter) {
				if(subscription.getAlbumId().compareTo(id) == 0)
					deleteEntity(subscription);
			}
		}
		return;
	}

	@Override
	public boolean isSubscribed(String albumId, String subscriberId) {
		if (albumId != null && subscriberId != null)
		{
			Long id = Long.parseLong(albumId);
			Iterable<Subscription> subscriptionIter = getSubscriberAlbums(subscriberId);
			for (Subscription subscription : subscriptionIter) {
				if(subscription.getAlbumId().compareTo(id) == 0)
					return true;
			}
		}
		return false;
	}


}
