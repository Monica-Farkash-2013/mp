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
import com.google.cloud.demo.model.DemoModelException;
import com.google.cloud.demo.model.LeaderboardEntry;
import com.google.cloud.demo.model.LeaderboardManager;
import com.google.cloud.demo.model.View;

public class LeaderboardManagerNoSql extends DemoEntityManagerNoSql<LeaderboardEntry>
		implements LeaderboardManager {

	public LeaderboardManagerNoSql() {
		super(LeaderboardEntry.class);
		if(getLeaderboardEntry("EntryA") == null) {
			LeaderboardEntry l = newLeaderboardEntry("EntryA");
			upsertEntity(l);
		}
		if(getLeaderboardEntry("EntryB") == null) {
			LeaderboardEntry l = newLeaderboardEntry("EntryB");
			upsertEntity(l);
		}
		if(getLeaderboardEntry("EntryC") == null) {
			LeaderboardEntry l = newLeaderboardEntry("EntryC");
			upsertEntity(l);
		}
	}

	@Override
	public LeaderboardEntry getLeaderboardEntry(String entryId) {
	    return getEntity(createLeaderboardEntryKey(entryId));
	}
	
	public Key createLeaderboardEntryKey(String entryId) {
	    return KeyFactory.createKey(getKind(), entryId);
	}

	@Override
	protected LeaderboardEntry fromEntity(Entity entity) {
	    return new LeaderboardEntryNoSql(entity);
	}

	@Override
	public LeaderboardEntryNoSql fromParentKey(Key parentKey) {
	    throw new DemoModelException("Leaderboard is entity group root, so it cannot have parent key");
	}

	@Override
	public LeaderboardEntryNoSql newLeaderboardEntry(String entryId) {
	    return new LeaderboardEntryNoSql(getKind(), entryId);
	}

}
