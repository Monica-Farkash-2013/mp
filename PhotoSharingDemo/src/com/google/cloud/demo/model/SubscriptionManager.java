package com.google.cloud.demo.model;

import com.google.cloud.demo.model.nosql.SubscriptionNoSql;

public interface SubscriptionManager extends DemoEntityManager<Subscription> {
	
	Iterable<Subscription> getSubscriberAlbums(String subscriberId);

	SubscriptionNoSql newSubscription(String subscriberId);

	void addSubscription(String albumId, String userId);

	void unSubscribe(String albumId, String subscriberId);

	boolean isSubscribed(String albumId, String subscriberId);

}
