package com.google.cloud.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.cloud.demo.model.Album;
import com.google.cloud.demo.model.AlbumManager;
import com.google.cloud.demo.model.DemoUser;
import com.google.cloud.demo.model.Photo;
import com.google.cloud.demo.model.PhotoManager;

public class CreateAlbumServlet extends HttpServlet {
	
 /**
	 * 
	 */
	private static final Logger logger =
		      Logger.getLogger(PhotoServiceManager.class.getCanonicalName());
	
	private static final long serialVersionUID = 518284167764359702L;

@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    AppContext appContext = AppContext.getAppContext();
    DemoUser currentUser = appContext.getCurrentUser();
    String content = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_STREAM);
    String subscribers = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_SUBSCRIBERS);
    String tags = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_TAGS);
	String message = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_MESSAGE);
	String coverUrl = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_COVER_IMAGE_URL);
	
    //String coverImageUrl = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_COVER_IMAGE_URL);

    //BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    //Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
    //List<BlobKey> keys = blobs.get("streamCoverUrl");

    boolean succeeded = false;
    StringBuilder builder = new StringBuilder();
    if (currentUser != null && content != null) {
      content = content.trim();
      if (!content.isEmpty()) {
          AlbumManager albumManager = appContext.getAlbumManager();
          
          /*
           * If there is another album with the same name we are supposed to throw an error 
           */
         Iterable<Album> albumIter = albumManager.getActiveAlbums();
         ArrayList<Album> albums = new ArrayList<Album>();
      	 try {
        	for (Album albm : albumIter) {
	        	albums.add(albm);
	        }
      	 } catch (DatastoreNeedIndexException e) {
      	 }

      	for (Album albm : albums) {  
          String title =albm.getTitle();
          if ((albm.getTitle()).equals(content))
          {   
            res.sendRedirect(appContext.getPhotoServiceManager().getRedirectUrl(
                            req.getParameter(ServletUtils.REQUEST_PARAM_NAME_TARGET_URL), 
                                             appContext.getCurrentUser().getUserId(), 
                                             null, null, "createstream", "1"));
            return;
          }
      	  }
          /*
           * end code that checks if album same name
           */
          
          
          Album album = albumManager.newAlbum(currentUser.getUserId());
          album.setTitle(content);
          album.setOwnerNickname(ServletUtils.getProtectedUserNickname(currentUser.getNickname()));         
          album.setTags(tags);
          long views = 1;
          album.setViews(views);          
          album.setUploadTime(System.currentTimeMillis());
        
          albumManager.upsertEntity(album);
          succeeded = true;
          
          // MM : Sends email to subscribers to invite them
          album.setSubscribers(subscribers);
  		
          Properties props = new Properties();
          Session session = Session.getDefaultInstance(props, null);

          String msgBody = (album.getOwnerNickname()).concat(" invites you to see a new stream in connexus: ").concat(album.getTitle().concat(message));

          try {
              Message msg = new MimeMessage(session);
              msg.setFrom(new InternetAddress(currentUser.getEmail(),"Connexus"));
              msg.addRecipient(Message.RecipientType.TO,
                               new InternetAddress(album.getSubscribers(),"Mr. User"));
              msg.setSubject("Connexus invitation");
              msg.setText(msgBody);
              Transport.send(msg);
              logger.info(currentUser.getEmail() + " MCM sent email to " + album.getSubscribers() + msgBody);
          } catch (AddressException e) {
              // ...
          } catch (MessagingException e) {
              // ...
          }
           // MM: finished sending email
/*          if (keys != null && keys.size() > 0) {
              PhotoManager photoManager = appContext.getPhotoManager();
              Photo photo = photoManager.newPhoto(currentUser.getUserId());
              String title = req.getParameter("title");
              if (title != null) {
                photo.setTitle(title);
              }

              String isPrivate = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_PRIVATE);
              photo.setShared(isPrivate == null);
              
              //MM: added album support
              String albumId = album.getId().toString();
              photo.setAlbumId(albumId);

              photo.setOwnerNickname(ServletUtils.getProtectedUserNickname(currentUser.getNickname()));

              BlobKey blobKey = keys.get(0);
              photo.setBlobKey(blobKey);

              photo.setUploadTime(System.currentTimeMillis());

              photo = photoManager.upsertEntity(photo);
              //succeeded = true;
            }
*/          

      } else {
        builder.append("Comment could not be empty");
      }
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
