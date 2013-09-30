package com.google.cloud.demo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.cloud.demo.model.Album;
import com.google.cloud.demo.model.AlbumManager;
import com.google.cloud.demo.model.DemoUser;
import com.google.cloud.demo.model.Subscription;
import com.google.cloud.demo.model.SubscriptionManager;

public class ManageAlbumsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger =
		      Logger.getLogger(UploadHandlerServlet.class.getCanonicalName());

	@Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    AppContext appContext = AppContext.getAppContext();
	    DemoUser currentUser = appContext.getCurrentUser();
        AlbumManager albumManager = appContext.getAlbumManager();
        SubscriptionManager subscriptionManager = appContext.getSubscriptionManager();

 	    boolean succeeded = false;
	    StringBuilder builder = new StringBuilder();
	    
	    Enumeration paramNames = req.getParameterNames();
	    while(paramNames.hasMoreElements())
	    {
            String paramName =(String)paramNames.nextElement();
            logger.info(paramName);
        }
	    
	    if (currentUser != null) {
	          String[] values = req.getParameterValues(ServletUtils.REQUEST_PARAM_NAME_CHECKBOX_GROUP);
	          if(values != null) {
			      Iterable<Album> albumIter = albumManager.getOwnedAlbums(currentUser.getUserId());
			      try {
			        for (Album album : albumIter) {
			            for(int i=0; i<values.length; i++) {
			            	String val = values[i];
			            	String albumId = album.getId().toString();
			                if(val.equals(albumId))
			                	albumManager.deactiveAlbum(currentUser.getUserId(), album.getId());
			            }
			        }
			      } catch (DatastoreNeedIndexException e) {
			    	  builder.append(ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY);
			      }
	          }
	          values = req.getParameterValues(ServletUtils.REQUEST_PARAM_NAME_CHECKBOX_GROUP1);
	          if(values != null) {
	        	  Iterable<Subscription> subscriptions_Iter = subscriptionManager.getSubscriberAlbums(currentUser.getUserId().toString()); 
			      try {
			        for (Subscription subscription : subscriptions_Iter) {
			            for(int i=0; i<values.length; i++) {
			            	String val = values[i];
			            	String albumId = subscription.getAlbumId().toString();
			                if(val.equals(albumId))
			                	subscriptionManager.deleteEntity(subscription);
			            }
			        }
			      } catch (DatastoreNeedIndexException e) {
			    	  builder.append(ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY);
			      }
	          }
	          
	          succeeded = true;
	    } else {;
	      builder.append("Bad parameters");
	    }
	    if (succeeded) {
	      res.sendRedirect(appContext.getPhotoServiceManager().getRedirectUrl(
	              req.getParameter(ServletUtils.REQUEST_PARAM_NAME_TARGET_URL), appContext.getCurrentUser().getUserId(), null, null, "managestream", null));
	    } else {
	      res.sendError(400, builder.toString());
	    }
	  }

}
