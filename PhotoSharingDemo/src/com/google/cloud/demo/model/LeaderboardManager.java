package com.google.cloud.demo.model;

import com.google.cloud.demo.model.nosql.LeaderboardEntryNoSql;

public interface LeaderboardManager extends DemoEntityManager<LeaderboardEntry> {
	
	LeaderboardEntryNoSql newLeaderboardEntry(String albumId);

	LeaderboardEntry getLeaderboardEntry(String entryId);

}
