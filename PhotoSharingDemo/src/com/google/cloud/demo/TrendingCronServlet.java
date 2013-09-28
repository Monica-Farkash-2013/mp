package com.google.cloud.demo;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.cloud.demo.model.Album;
import com.google.cloud.demo.model.AlbumManager;
import com.google.cloud.demo.model.LeaderboardEntry;
import com.google.cloud.demo.model.LeaderboardManager;
import com.google.cloud.demo.model.View;
import com.google.cloud.demo.model.ViewManager;


public class TrendingCronServlet extends HttpServlet {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp){
	AppContext appContext = AppContext.getAppContext();
	ConfigManager configManager = appContext.getConfigManager();
    AlbumManager albumManager = appContext.getAlbumManager();
    ViewManager viewManager = appContext.getViewManager();
    LeaderboardManager leaderboardManager = AppContext.getAppContext().getLeaderboardManager();

    LeaderboardEntry entryA = leaderboardManager.getLeaderboardEntry("EntryA");
    LeaderboardEntry entryB = leaderboardManager.getLeaderboardEntry("EntryB");
    LeaderboardEntry entryC = leaderboardManager.getLeaderboardEntry("EntryC");
    long entryA_albumId = entryA.getAlbumId();
    long entryB_albumId = entryB.getAlbumId();
    long entryC_albumId = entryC.getAlbumId();
    long entryA_views = entryA.getViewsNumber();
    long entryB_views = entryB.getViewsNumber();
    long entryC_views = entryC.getViewsNumber();
    String entryA_userId = entryA.getUserId();
    String entryB_userId = entryB.getUserId();
    String entryC_userId = entryC.getUserId();
    boolean any_changes = false;
    
    Iterable<Album> albumIter = albumManager.getActiveAlbums();
  	try {
    	for (Album album : albumIter) {
    		Iterable<View> albumViews = viewManager.getAlbumViews(album.getId().toString());
    		long viewCount = 0;
    		for (View view : albumViews) {
    			viewCount++;
    		}
    		if(Long.valueOf(viewCount).compareTo(Long.valueOf(entryC_views)) > 0 ) {
        		if(Long.valueOf(viewCount).compareTo(Long.valueOf(entryB_views)) > 0 ) {
            		if(Long.valueOf(viewCount).compareTo(Long.valueOf(entryA_views)) > 0 ) {           		    
            		    entryC_albumId = entryB_albumId;
            		    entryB_albumId = entryA_albumId;
            		    entryA_albumId = album.getId();            		    
            		    entryC_views = entryB_views;
            		    entryB_views = entryA_views;
            		    entryA_views = viewCount; 
            		    entryC_userId = entryB_userId;
            		    entryB_userId = entryA_userId;
            		    entryA_userId =	album.getOwnerId();
            		}
            		else {
            		    entryC_albumId = entryB_albumId;
               		    entryC_views = entryB_views;
               		    entryC_userId = entryB_userId;
               		    entryB_albumId = album.getId();
               		    entryB_views = viewCount;
            		    entryB_userId =	album.getOwnerId();
           		}
        		}
        		else {
           		    entryC_albumId = album.getId();
           		    entryC_views = viewCount;        			
        		    entryC_userId =	album.getOwnerId();
        		}
        		any_changes = true;
    		} 
        }
    	if(any_changes) {
			leaderboardManager.deleteEntity(entryA);
			entryA = leaderboardManager.newLeaderboardEntry("EntryA");
			entryA.setAlbumId(entryA_albumId);
			entryA.setViewsNumber(entryA_views);
			entryA.setUserId(entryA_userId);
			leaderboardManager.upsertEntity(entryA);
			
			leaderboardManager.deleteEntity(entryB);
			entryB = leaderboardManager.newLeaderboardEntry("EntryB");
			entryB.setAlbumId(entryB_albumId);
			entryB.setViewsNumber(entryB_views);
			entryB.setUserId(entryB_userId);
			leaderboardManager.upsertEntity(entryB);
			
			leaderboardManager.deleteEntity(entryC);
			entryC = leaderboardManager.newLeaderboardEntry("EntryC");
			entryC.setAlbumId(entryC_albumId);
			entryC.setViewsNumber(entryC_views);
			entryC.setUserId(entryC_userId);
			leaderboardManager.upsertEntity(entryC);           			
    	}
  	} catch (DatastoreNeedIndexException e) {
        try {
			resp.sendRedirect(configManager.getErrorPageUrl(
			  configManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  	}

  }
}
