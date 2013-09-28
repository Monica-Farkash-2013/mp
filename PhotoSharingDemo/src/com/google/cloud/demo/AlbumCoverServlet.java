package com.google.cloud.demo;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.cloud.demo.model.Photo;
import com.google.cloud.demo.model.PhotoManager;

public class AlbumCoverServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    AppContext appContext = AppContext.getAppContext();
	    PhotoManager photoManager = appContext.getPhotoManager();
	    String user = req.getParameter("user");
	    String id = req.getParameter("id");
	    Long photoId = ServletUtils.validatePhotoId(id);
 	    boolean succeeded = false;
	    StringBuilder builder = new StringBuilder();
	    String albumId = null;
	    
	    if (photoId != null && user != null) {
	    	Photo photo = photoManager.getPhoto(user, photoId);
	    	albumId = photo.getAlbumId();
	      	Iterable<Photo> photoIter = photoManager.getOwnedAlbumPhotos(user, albumId);
	    	try {
	        	for (Photo other_photo : photoIter) {
	        		String otherId = other_photo.getId().toString();
	          		if(other_photo.isAlbumCover() && (otherId.compareTo(id) != 0))
	          			photoManager.toggleCoverPhoto(user, other_photo.getId());
	          		
	        	}
	    		photoManager.toggleCoverPhoto(user, photoId);
	      	} catch (DatastoreNeedIndexException e) {
	    	  builder.append(ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY);
	      	}
	    	succeeded = true;
	    } else {;
	      builder.append("Bad parameters");
	    }
	    if (succeeded) {
	    	String target = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_TARGET_URL);
	    	PhotoServiceManager serviceManager = appContext.getPhotoServiceManager();
	    	res.sendRedirect(serviceManager.getRedirectUrl(target, user, id, albumId, "viewstream", null));
	    } else {
	      res.sendError(400, builder.toString());
	    }
	  }

}
