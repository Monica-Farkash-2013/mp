<%@ page contentType="text/html;charset=UTF-8" language="java"
  isELIgnored="false"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.cloud.demo.*"%>
<%@ page import="com.google.cloud.demo.model.*"%>
<%@ page import="com.google.appengine.api.users.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreNeedIndexException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  UserService userService = UserServiceFactory.getUserService();
  AppContext appContext = AppContext.getAppContext();
  ConfigManager configManager = appContext.getConfigManager();
  DemoUser currentUser = appContext.getCurrentUser();
  PhotoServiceManager serviceManager = appContext.getPhotoServiceManager();
  PhotoManager photoManager = appContext.getPhotoManager();
  CommentManager commentManager = appContext.getCommentManager();
  AlbumManager albumManager = appContext.getAlbumManager();
  ViewManager viewManager = appContext.getViewManager();
  LeaderboardManager leaderboardManager = appContext.getLeaderboardManager();
  SubscriptionManager subscriptionManager = appContext.getSubscriptionManager();
%>
<!DOCTYPE html>

<head>
<script type="text/javascript">
var tabLinks = new Array();
var contentDivs = new Array();

function init() {

    // Grab the tab links and content divs from the page
    var tabListItems = document.getElementById('tabs').childNodes;
    for ( var i = 0; i < tabListItems.length; i++ ) {
      if ( tabListItems[i].nodeName == "LI" ) {
        var tabLink = getFirstChildWithTagName( tabListItems[i], 'A' );
        var id = getHash( tabLink.getAttribute('href') );
        tabLinks[id] = tabLink;
        contentDivs[id] = document.getElementById( id );
      }
    }

    // Assign onclick events to the tab links, and
    // highlight the first tab
	
    for ( var id in tabLinks ) {
      tabLinks[id].onclick = showTab;
      tabLinks[id].onfocus = function() { this.blur() };
    }
    
    var tabId = window.location.href;
    var hashPos = tabId.lastIndexOf ( '#' );
	if (hashPos != -1) {
		tabId = getHash(tabId);
	}
	else {
		tabId = getUrlVars()["tabId"];
		if (!tabId || tabId.length === 0)
			tabId = "viewstream";
	}
    	
    for ( var id in tabLinks ) {
        if ( id == tabId ) tabLinks[id].className = 'selected';
    }

    // Hide all content divs except the first
     for ( var id in contentDivs ) {
      if ( id != tabId ) contentDivs[id].className = 'tabContent hide';
    }
    
 	if(tabId == "trendingstream") {
		tabId = getUrlVars()["search_txt"];
		if(tabId == null)
			tabId = "no_reports";
		setButton(tabId);
	}
  }

  function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
  }

  function changeURI(key, value) {
  	key = escape(key); 
  	value = escape(value);
  	var kvp = document.location.search.substr(1).split('&');
  	var i=kvp.length; var x; while(i--) 
  	{
      x = kvp[i].split('=');
      if (x[0]==key)
      {
              x[1] = value;
              kvp[i] = x.join('=');
              break;
      }
  	}
  	if(i<0) {
      	kvp[kvp.length] = [key,value].join('=');
    } else{
  		//this will reload the page, it's likely better to store this until finished
  		var loc = kvp.join('&');
		document.location.search = loc; 
    }

  }
  function isAlbumCover(sel, isCover) {
		var element;
		if( sel == 1)
			element = document.getElementById("selectCover1");
		else if( sel == 2)
			element = document.getElementById("selectCover2");
		else if( sel == 3)
			element = document.getElementById("selectCover3");
		var show = element.style.visibility;
		if (isCover)
			element.style.visibility = "visible";			
		else
			element.style.visibility = "hidden";
}

  function selectCover(sel, url) {
		//alert("We got a pageShow call!..."); 
		var element;
		if( sel == 1)
			element = document.getElementById("selectCover1");
		else if( sel == 2)
			element = document.getElementById("selectCover2");
		else if( sel == 3)
			element = document.getElementById("selectCover3");
		var show = element.style.visibility;
		if (show == "visible")
			element.style.visibility = "hidden";			
		else
			element.style.visibility = "visible";
		var allUrl = "http://"; 
		allUrl = allUrl.concat(document.location.host);
		document.location.replace(allUrl.concat(url));
}

  function showTab() {
    var selectedId = getHash( this.getAttribute('href') );

    // Highlight the selected tab, and dim all others.
    // Also show the selected content div, and hide all others.
    for ( var id in contentDivs ) {
      if ( id == selectedId ) {
        tabLinks[id].className = 'selected';
        contentDivs[id].className = 'tabContent';
      } else {
        tabLinks[id].className = '';
        contentDivs[id].className = 'tabContent hide';
      }
    }
    
    var url1 = window.location.href;  
    var tabId;
    
	if(selectedId == "trendingstream") {
		tabId = getUrlVars()["search_txt"];
		if(tabId == null)
			tabId = "no_reports";
		setButton(tabId);
	}

    var url2 = url1.split("#", 2);
    var url3 = url2[0].split("?", 2);
    var newUrl = url3[0].concat(this.getAttribute('href')); // + window.location.hash;
	window.location.replace(newUrl);
    // Stop the browser following the link
    return false;
  }

  function getFirstChildWithTagName( element, tagName ) {
    for ( var i = 0; i < element.childNodes.length; i++ ) {
      if ( element.childNodes[i].nodeName == tagName ) return element.childNodes[i];
    }
  }

  function getHash( url ) {
    var hashPos = url.lastIndexOf ( '#' );
    return url.substring( hashPos + 1 );
  }

  function setButton(choice) {
	  //alert("put a breakpoint here");
	var btn = document.getElementsByName("course");
	for(var i = 0; btn.length; i++) {
		if (btn[i].value == choice)
			btn[i].checked = true;
		else
			btn[i].checked = false;
	}
  }
  
function onFileSelected() {
  filename = document.getElementById("input-file").value;
  if (filename == null || filename == "") {
    document.getElementById("btn-post").setAttribute("class", "inactive btn");
    document.getElementById("btn-post").disabled = true;
  } else {
    document.getElementById("btn-post").setAttribute("class", "active btn");
    document.getElementById("btn-post").disabled = false;
  }
}

function togglePhotoPost(expanded) {
  onFileSelected();
  if (expanded) {
    document.getElementById("btn-choose-image").style.display="none";
    document.getElementById("upload-form").style.display="block";
  } else {
    document.getElementById("btn-choose-image").style.display="inline-block";
    document.getElementById("upload-form").style.display="none";
  }
}

function onCommentChanged(id) {
  comment = document.getElementById("comment-input-" + id).value;
  if (comment == null || comment.trim() == "") {
    document.getElementById("comment-submit-" + id).setAttribute("class", "inactive btn");
    document.getElementById("comment-submit-" + id).disabled = true;
  } else {
    document.getElementById("comment-submit-" + id).setAttribute("class", "active btn");
    document.getElementById("comment-submit-" + id).disabled = false;
  }
}

function toggleCommentPost(id, expanded) {
  onCommentChanged(id);
  if (expanded) {
    document.getElementById("comment-input-" + id).setAttribute("class", "post-comment expanded");
    document.getElementById("comment-submit-" + id).style.display="inline-block";
    document.getElementById("comment-cancel-" + id).style.display="inline-block";
  } else {
    document.getElementById("comment-input-" + id).value = ""
    document.getElementById("comment-input-" + id).setAttribute("class", "post-comment collapsed");
    document.getElementById("comment-submit-" + id).style.display="none";
    document.getElementById("comment-cancel-" + id).style.display="none";
  }
}
</script>

 <%-- MCM: this is a "huge" page divided into divs. 
    The server sends all the "pages" the client might want to look at as a single page 
    with several divs. The client runs an application that allows him to pick a view
    which will end up being a div he wants to look at from the whole page. 
    This enables the fast switch between choices. --%>

<title>Photofeed</title>
<link rel="stylesheet" type="text/css" href="photofeed.css" />
</head>

 <%-- ************************************************************* --%>
 <%-- MCM: All the tabs of the "views" the client might choose from --%>
 <%-- ************************************************************* --%>


<body onload="init()">

<div id="fb-root"></div>
<script>
window.fbAsyncInit = function() {
	  FB.init({
	    appId      : '218987624932137', // App ID
	    channelUrl : '//zinabuna2013.appspot.com/channel.html', // Channel File http://balaurbun2013.appspot.com 
	    status     : true, // check login status
	    cookie     : true, // enable cookies to allow the server to access the session
	    xfbml      : true  // parse XFBML
	  });

	  // Here we subscribe to the auth.authResponseChange JavaScript event. This event is fired
	  // for any authentication related change, such as login, logout or session refresh. This means that
	  // whenever someone who was previously logged out tries to log in again, the correct case below 
	  // will be handled. 
	  FB.Event.subscribe('auth.authResponseChange', function(response) {
	    // Here we specify what we do with the response anytime this event occurs. 
	    if (response.status === 'connected') {
	      // The response object is returned with a status field that lets the app know the current
	      // login status of the person. In this case, we're handling the situation where they 
	      // have logged in to the app.
	      testAPI();
	    } else if (response.status === 'not_authorized') {
	      // In this case, the person is logged into Facebook, but not into the app, so we call
	      // FB.login() to prompt them to do so. 
	      // In real-life usage, you wouldn't want to immediately prompt someone to login 
	      // like this, for two reasons:
	      // (1) JavaScript created popup windows are blocked by most browsers unless they 
	      // result from direct interaction from people using the app (such as a mouse click)
	      // (2) it is a bad experience to be continually prompted to login upon page load.
	      FB.login();
	    } else {
	      // In this case, the person is not logged into Facebook, so we call the login() 
	      // function to prompt them to do so. Note that at this stage there is no indication
	      // of whether they are logged into the app. If they aren't then they'll see the Login
	      // dialog right after they log in to Facebook. 
	      // The same caveats as above apply to the FB.login() call here.
	      FB.login();
	    }
	  });
	  };

	  // Load the SDK asynchronously
	  (function(d){
	   var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
	   if (d.getElementById(id)) {return;}
	   js = d.createElement('script'); js.id = id; js.async = true;
	   js.src = "//connect.facebook.net/en_US/all.js";
	   ref.parentNode.insertBefore(js, ref);
	  }(document));

	  // Here we run a very simple test of the Graph API after login is successful. 
	  // This testAPI() function is only called in those cases. 
	  function testAPI() {
	    console.log('Welcome!  Fetching your information.... ');
	    FB.api('/me', function(response) {
	      console.log('Good to see you, ' + response.name + '.');
	    });
	  }
</script>

  <div class="wrap">

    <div class="header group">
      <h1>
        <img src="img/photofeed.png" alt="Photofeed" /></h1>
    </div>
    <div class="logoutgroup">
    <%if(currentUser != null) { %>  
         <p>Hello <%= ServletUtils.getProtectedUserNickname(currentUser.getNickname()) %> , 
                  <%= currentUser.getEmail() %> , 
                  <a href=<%= userService.createLogoutURL(configManager.getLoginPageUrl())%>>Sign out
                  </a>
         </p>
    <% } else {%>
                  <a href=<%= userService.createLoginURL(configManager.getMainPageUrl())%>>Sign in
                  </a>
   
    <% } %>  
    </div>
      
	<ul class="header group" id="tabs">
     		<li><a href="#managestream">Manage</a></li>
     		<li><a href="#createstream">Create</a></li>
     		<li><a href="#viewstream">View</a></li>
     		<li><a href="#searchstream">Search</a></li>
     		<li><a href="#trendingstream">Trending</a></li>
     		<li><a href="#socialstream">Social</a></li>
	</ul>    
     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the MANAGE view --%>
     <%-- ******************************************************************* --%>
         
   
    <div class="glow"></div>
  	<div class="tabContent" id="managestream">
      <div class="view-title">
	        <p>MANAGE STREAMS</p>
	  <%
    	if	(currentUser != null) { 
	  %>
        <form action="<%= configManager.getManageAlbumsUrl() %>"
              method="post">     
	        <p>Streams I own:</p>
	        <table border="1">
			 <tr>
			 	<th>Name</th>
			 	<th>Last New Picture</th>
			 	<th>Number of Pictures</th>
			 	<th>Delete</th>
			 </tr>
		    <%
		      Iterable<Album> albumIter = albumManager.getOwnedAlbums(currentUser.getUserId());
		      ArrayList<Album> albums = new ArrayList<Album>();
		      try {
		        for (Album album : albumIter) {
		        	albums.add(album);
		        }
		      } catch (DatastoreNeedIndexException e) {
		        pageContext.forward(configManager.getErrorPageUrl(
		          ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
		      }
			  int table_count = 0;
		      for (Album album : albums) {
		    	  table_count++;
		    %>
				 <tr>
				 	<td><a href=<%= serviceManager.getRedirectUrl(null, currentUser.getUserId(), null, 
				 			album.getId().toString(), 
				 			 ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, null) %>> <%= album.getTitle() %></a></td>
				 	<td><%= ServletUtils.formatTimestamp(photoManager.getNewestPhotoTimestamp(currentUser.getUserId(),album.getId().toString()))%></td>
				 	<td><%= photoManager.getAlbumSize(currentUser.getUserId(),album.getId().toString())%></td>
					<td><input type="checkbox" name="delete-box" value=<%= album.getId().toString() %>></td>
				 </tr>
			 <%	} %>
			 </table> 
			 <%
		      	String btn_type = "submit";
			 	if (table_count == 0) {
			 		btn_type = "hidden";
			 %>
	     	<p>Please create new streams...</p>			 
  			<%	} %>
	        <input id="delete-streams" class="active btn" type=<%= btn_type %> value="Delete Checked">
 	    </form>    
	  <% } else {%> 	
	     <p>Please login in order to manage your streams...</p>
	  <% } %>
	        
      </div>
      <div class="manage-subscribed">
	  <%	      
        if	(currentUser != null) { 
      %>
        <form action="<%= configManager.getManageAlbumsUrl() %>"
              method="post">
                <p>Streams I subscribe to:</p>
                <table border="1">
                         <tr>
                                <th>Name</th>
                                <th>Last New Picture</th>
                                <th>Number of Pictures</th>
                                <th>Views</th>
                                <th>Unsubscribe</th>
                         </tr>

                    <% 
                      Iterable<Subscription> subscriptions_Iter = subscriptionManager.getSubscriberAlbums(currentUser.getUserId().toString()); 
                      ArrayList<Album> sub_albums = new ArrayList<Album>();
                      try {
                        for (Subscription sub_scription : subscriptions_Iter) {
                        	Album sub_alb = albumManager.getAlbumS(currentUser.getUserId(), sub_scription.getAlbumId().toString());
                        	if(sub_alb != null)
                                sub_albums.add(sub_alb);
                        }
                      } catch (DatastoreNeedIndexException e) {
                        pageContext.forward(configManager.getErrorPageUrl(
                          ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
                      }
                      
        			  int table_count = 0;
                      for (Album sub_album : sub_albums) {
                    	table_count++;
                  		Iterable<View> albumViews = viewManager.getAlbumViews(sub_album.getId().toString());
                		long viewCount = 0;
                		for (View view : albumViews) {
                			viewCount++;
                		}
					%>
                                  <tr>
                                        <td><a href=<%= serviceManager.getSubscribeUrl(null, sub_album.getOwnerId(), null,
                                                        sub_album.getId().toString(),
                                                         ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, "Subscribe") %>> <%= sub_album.getTitle() %></a></td>
                                        <td><%= ServletUtils.formatTimestamp(photoManager.getNewestPhotoTimestamp( sub_album.getOwnerId(), sub_album.getId().toString())) %></td>
                                        <td><%= photoManager.getAlbumSize(sub_album.getOwnerId(),sub_album.getId().toString())%></td>
                                        <td><%= viewCount%></td>
                                        <td><input type="checkbox" name="unsubscribe-box" value=<%= sub_album.getId().toString() %>></td>
                                 </tr>
                     <%     
                     }
                     %>
                         </table>
	 			 <%
			      	String btn_type = "submit";
				 	if (table_count == 0) {
				 		btn_type = "hidden";
				 %>
		     	<p>Please subscribe to new streams...</p>			 
	  			<%	} %>
                <input id="unsubscribe-streams" class="active btn" type=<%= btn_type%> value="Unsubscribe Checked">
            </form>     
      <% } %>
      </div>



    </div>
    
     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the CREATE view --%>
     <%-- ******************************************************************* --%>


	<div class="tabContent" id="createstream">
	<% int error_in_page=0;
	String same_name = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_PHOTO_LOC);
	if (same_name != null) error_in_page = Integer.valueOf(same_name);
	
	if (error_in_page==0) { %>
      <div class="view-title">
        <p>CREATE STREAMS</p>
        <% if (currentUser != null) { 
		 %>
        <form action="<%= configManager.getCreateAlbumUrl() %>"
              method="post">     
	        <input id="stream-name" class="input text" name="stream" type="text" value="Stream name here...">
	        <p>Name your stream</p>
	        <textarea name="subscribers" placeholder="Add subscribers using comma as a separator..."></textarea>
	        <textarea name="message" placeholder="Optional message for invite..."></textarea>
	        <p>Add subscribers</p>
	        <input id="btn-post" class="active btn" type="submit" value="Create Stream">
		     <div class="tags">
		     	<textarea name="tags" placeholder="Add tags using comma as a separator..."></textarea>
		     	<p>Tag your stream</p>
		     	<input id="cover-url" class="input text" name="streamCoverUrl" type="text" value="URL here...">
		     	<p>URL to cover image</p>
		     	<p>(Can be empty)</p>        
		     </div>
	    </form>
	    <% } else { %>
	     <p>Please login in order to create new streams...</p>	    
	    <% } %>
     </div>
     <% } else { %>
     <div class="view-title">
      
     <img class="errormsg" 
          src="img/CreateError.png"
          alt="Error Image" />
     
     </div>
     <%} %>
     </div>

     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the VIEW view --%>
     <%-- ******************************************************************* --%>

	<div class="tabContent" id="viewstream">
    
    <%-- MM: Here it prints the name and the owner of the album which we are showing --%>
    
	<%
	String streamId = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_ALBUM_ID); 
	String which_photos = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_PHOTO_LOC);
	int start_with_photo =1;
	if (which_photos != null) start_with_photo = Integer.valueOf(which_photos); 
	if(streamId != null && !streamId.isEmpty())
	{
		String streamUserId = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_PHOTO_OWNER_ID);
		viewManager.addAlbumView(streamId);
	%>
		<div class="view-title">
		<%
		Album albm = albumManager.getAlbum(streamUserId, Long.parseLong(streamId));
		if (albm != null) {	
		%>
			<p>View pictures from stream: "<%= albm.getTitle()%>"
	  		posted by user: "<%= albm.getOwnerNickname() %>" </p>
	  		<div class="fb-like" data-href="http://developers.facebook.com/docs/reference/plugins/like" data-width="450" data-show-faces="true" data-send="true">
		    </div>  
	  		
		<%
		}
		%>
		<% 
	    if	(currentUser != null) { 
		  if(currentUser.getUserId().toString().compareTo(streamUserId) != 0)
		  {
		%>
						<%-- MM: Subscribe  --%>
	        <div class="subscribe">
	        <% 
	        String subscribe = "Subscribe";
	        subscribe = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_SUBSCRIBE);
	        if(subscribe == null)	
	        	subscribe = "Subscribe";
	        else {
	        	if (subscribe.compareTo("Subscribe") == 0) {
	        		subscribe = "Unsubscribe";
	        		if(!subscriptionManager.isSubscribed(albm.getId().toString(), currentUser.getUserId().toString()))
	        			subscriptionManager.addSubscription(albm.getId().toString(), currentUser.getUserId().toString());
	        	}
	        	else {
	        		if (subscribe.compareTo("Unsubscribe") == 0) {
	        			subscribe = "Subscribe";
	        			if(subscriptionManager.isSubscribed(albm.getId().toString(), currentUser.getUserId().toString()))
	       					subscriptionManager.unSubscribe(albm.getId().toString(), currentUser.getUserId().toString());        	
	        		} 
	        		else 
	        			subscribe = "Subscribe";
	        	}
	        }
	        %>
	        	<form action="<%= serviceManager.getSubscribeUrl(null, streamUserId, null, 
	                     				albm.getId().toString(), 
	                     				ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, subscribe) %>"
	                 method="post">    
	                   <input id="btn-post" class="active btn" type="submit" value=<%= subscribe %>>
	            </form>
	        </div>
		<% 
		} } else {
		%>	
	     <p>Please login in order to subscribe to this stream...</p>	    			
		<% } %>
		</div>
		
    <%-- MM: adds the new picture to the album  --%>
    	<!-- KK -->
    	<%
      	Iterable<Photo> photoIter = photoManager.getSubsetOwnedAlbumPhotos(streamUserId, streamId, 3, start_with_photo-1);
      	ArrayList<Photo> photos = new ArrayList<Photo>();
      	int count=0;
      	try {
        	for (Photo photo : photoIter) {
          			photos.add(photo); 
          			
          			count++; 
          			if(count >= 3)
          				break;
        	}
      	} catch (DatastoreNeedIndexException e) {
        	pageContext.forward(configManager.getErrorPageUrl(
          		ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
      	}

      
      	%>
      	<div class="bubble">
      	 <img onmouseover="this.src='img/choose.png'" onmouseout="this.src=''" src="img/choose.png"/>
      	 </div>
      	<%
      	
        /* goes over the pictures, one by one, to be shown to the public */
      	  count = 0;
      	  String reloadUrl;
      	  for (Photo photo : photos) {
      		reloadUrl = serviceManager.getAlbumCoverImageUrl(photo);      		
        	String firstClass = "";
        	String lastClass = "";
        	if (count == 0) {
          		firstClass = "first";
        	}
        	if (count == photos.size() - 1) {
          		lastClass = "last";
        	}
        	if (photos.size()!=0)
        	{; // show a picture only if there is at least one in the list
    	%>
    		<div class="feed <%= firstClass %> <%= lastClass %>"  
    			onclick="selectCover( <%= Integer.toString(count + 1) %>, '<%= reloadUrl%>')">
      			<div class="post group">
        			<div class="image-wrap">
          				<img class="photo-image"
            				src="<%= serviceManager.getImageDownloadUrl(photo)%>"
            				alt="Photo Image" />
        			</div>
        			<div class="owner group">
          			<!-- MM: took out here code for cats images -->
          				<div class="desc">
           				<h3>
            					<%= ServletUtils.getProtectedUserNickname(photo.getOwnerNickname()) %></h3>          
            				<p class="timestamp"><%= ServletUtils.formatTimestamp(photo.getUploadTime()) %></p>
            				<p>
            				<p><c:out value="<%= photo.getTitle() %>" escapeXml="true"/>
           					<img class="check" src="img/check.png" 
    							onload="isAlbumCover( <%= Integer.toString(count + 1) %>, <%= photo.isAlbumCover() %>)"
           						id=<%= ServletUtils.REQUEST_PARAM_NAME_COVER_ID.concat(Integer.toString(count + 1)) %> />         				
          			</div>
       				<!-- /.desc -->
        		</div>
        		<!-- /.usr -->
      		</div>
      		<!-- /.post -->
    	</div>
    	<!-- /.feed -->
   	<%
	}
        	count++;
      	}
      	
       if (count ==3) { %>   	
	
 
      	<%-- MM:the More pictures button after the 3 shown pictures --%>
        <div class="next-3-pict">
        	<form action="<%= serviceManager.getRedirectUrl(null, streamUserId, null, 
		 			                                       albm.getId().toString(), 
		 			                                       ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, String.valueOf(start_with_photo+3))
		 			 %>"
                 method="post">    
                   <input id="btn-post" class="active btn" type="submit" value="More pictures">
            </form>
        </div>
    		<%-- MM: we enable the user to pick up a picture to add to the stream --%>
		<% } %>
		
    	<div id="upload-wrap">
      		<div id="upload">
        		<a id="btn-choose-image" class="active btn" onclick="togglePhotoPost(true)">Add an image</a>
        		<div id="upload-form" style="display:none">
          			<form action="<%= serviceManager.getUploadUrl()%>" method="post" 
            			enctype="multipart/form-data">
            			<input id="input-file" class="inactive file btn" type="file" name="photo"
              				onchange="onFileSelected()">
              			<input type="hidden" name="stream-id" value="<%= streamId%>">
            			<textarea name="title" placeholder="Write a description"></textarea>
            			<input id="btn-post" class="active btn" type="submit" value="Upload file">
            			<a class="cancel" onclick="togglePhotoPost(false)">Cancel</a>
          			</form>
        		</div>
      		</div>
      		<!-- /#upload -->
    	</div>
    	<!-- /#upload-wrap -->

      
      <% 
    
	}
	else {
		
   %>		
	     <div class="view-title">
          <p>VIEW ALBUMS</p>
        </div>
   <% 
		
   		Iterable<Album> albumIter = albumManager.getActiveAlbums();
   		ArrayList<Album> albums = new ArrayList<Album>();
      	try {
        	for (Album album : albumIter) {
	        	albums.add(album);
	        }
      	} catch (DatastoreNeedIndexException e) {
	        pageContext.forward(configManager.getErrorPageUrl(
	          ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
      	}

      	int count = 0;
      	for (Album album : albums) {
      		Photo coverPhoto = null;
      		String coverPhotoUrl = null;
          	Iterable<Photo> photoIter = photoManager.getOwnedAlbumPhotos(album.getOwnerId().toString(), album.getId().toString());
          	try {
            	for (Photo photo : photoIter) {
	          		if(photo.isAlbumCover())
	          			coverPhoto = photo;
            	}
          	} catch (DatastoreNeedIndexException e) {
            	pageContext.forward(configManager.getErrorPageUrl(
              		ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
          	}
			if(coverPhoto == null)
				coverPhotoUrl = ServletUtils.getUserIconImageUrl(album.getOwnerId());
			else
				coverPhotoUrl = serviceManager.getImageDownloadUrl(coverPhoto);	    	  
	%>
     		<div class="feed">
	      		<div class="post group">
		        	<div class="image-wrap">
		        		<a href="<%= serviceManager.getRedirectUrl(null, album.getOwnerId(), null, 
				 				album.getId().toString(), 
				 			 	ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, null) %>"> 
		          		<img class="photo-image"
		            		src="<%= coverPhotoUrl%>"
			 			 	alt="Photo Image" /></a>
	        		</div>
		        	<div class="owner group">
		          		<div class="desc">
		            		<h3><%= ServletUtils.getProtectedUserNickname(album.getOwnerNickname()) %></h3>	            
		            		<p class="timestamp"><%= ServletUtils.formatTimestamp(album.getUploadTime()) %></p>
		            		<p>
		            		<p><c:out value="<%= album.getTitle() %>" escapeXml="true"/>
		          		</div>
		          		<!-- /.desc -->
        			</div>
		        	<!-- /.usr -->
	      		</div>
	      		<!-- /.post -->
		      
     <%
	        	//Iterable<Comment> comments = commentManager.getComments(photo);
	        	//for (Comment comment : comments) {}
     %>
   			</div>
    		<!-- /.feed -->
	<%	
			count++;
      	}
	}
    %>
 	</div>
	<!-- /.view -->  



     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the SEARCH view --%>
     <%-- ******************************************************************* --%>

    <div class="tabContent" id="searchstream">
    <%--MM: should ask for what to search and allow the push of the "Search" button --%>
      <div class="view-title">
        <p>SEARCH STREAMS</p>
      
          <div>
            <form action="<%= configManager.getSearchAlbumUrl()%>"
             method="post">
               <input id="search_txt" class="input text" name="stream" type="text" value="search name here...">
               <input id="btn-post" class="active btn" type="submit" value="Search">
          </form>
          </div>
          <% 
          String search_text = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_SEARCH_TXT);
          if (search_text != null) {   		  
        	  	Iterable<Album> albumIter = albumManager.getActiveAlbums();
  				ArrayList<Album> albums = new ArrayList<Album>();
  				try {
    				for (Album album : albumIter) {
        				if ((album.getTitle()).indexOf(search_text) != -1) {albums.add(album); }
        				else 
        					if ((album.getTags()!=null)&& (album.getTags()).indexOf(search_text) != -1) {albums.add(album);}
        			}
  				} catch (DatastoreNeedIndexException e) {
        			pageContext.forward(configManager.getErrorPageUrl(
         			 ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
  				}   	
  			//}	
          //if (search_text != null) {   		  
          	int count = 0;
	      	for (Album album : albums) {
	      		Photo coverPhoto = null;
	      		String coverPhotoUrl = null;
	          	Iterable<Photo> photoIter = photoManager.getOwnedAlbumPhotos(album.getOwnerId().toString(), album.getId().toString());
	          	try {
	            	for (Photo photo : photoIter) {
		          		if(photo.isAlbumCover())
		          			coverPhoto = photo;
	            	}
	          	} catch (DatastoreNeedIndexException e) {
	            	pageContext.forward(configManager.getErrorPageUrl(
	              		ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
	          	}
				if(coverPhoto == null)
					coverPhotoUrl = ServletUtils.getUserIconImageUrl(album.getOwnerId());
				else
					coverPhotoUrl = serviceManager.getImageDownloadUrl(coverPhoto);	    	  
	%>
      		<div class="feed">
	      		<div class="post group">
		        	<div class="image-wrap">
		        		<a href="<%= serviceManager.getRedirectUrl(null, album.getOwnerId(), null, 
				 				album.getId().toString(), 
				 			 	ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, null) %>"> 
		          		<img class="photo-image"
		            		src="<%= coverPhotoUrl%>"
			 			 	alt="Photo Image" /></a>
	        		</div>
		        	<div class="owner group">
		          		<div class="desc">
		            		<h3><%= ServletUtils.getProtectedUserNickname(album.getOwnerNickname()) %></h3>	            
		            		<p class="timestamp"><%= ServletUtils.formatTimestamp(album.getUploadTime()) %></p>
		            		<p>
		            		<p><c:out value="<%= album.getTitle() %>" escapeXml="true"/>
		          		</div>
        			</div>
	      		</div>
   			</div>
	<%	
				count++;
				if (count ==2) break;
      		}
          }
    %>
     </div>
  </div>

     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the TRENDING view --%>
     <%-- ******************************************************************* --%>


 	<div class="tabContent" id="trendingstream">
      <div class="view-title">
        <p>TOP 3 TRENDING STREAMS</p>
      </div>
         	<%
          	String radio_choice = request.getParameter(ServletUtils.REQUEST_PARAM_NAME_SEARCH_TXT);
            %>     
      <%
      //boolean ff = false;
      //if(ff)
      {
   	  ArrayList<Album> albums = new ArrayList<Album>();
      Album albm = null;
      Long albId = leaderboardManager.getLeaderboardEntry("EntryA").getAlbumId();
      String usrId = leaderboardManager.getLeaderboardEntry("EntryA").getUserId();
      long a = leaderboardManager.getLeaderboardEntry("EntryA").getViewsNumber();
      if(albId != 0 && usrId != null) {
      	albm = albumManager.getAlbum(usrId, albId.longValue());
      	if(albm != null)
      		albums.add(albm);
      }
      albId = leaderboardManager.getLeaderboardEntry("EntryB").getAlbumId();
      usrId = leaderboardManager.getLeaderboardEntry("EntryB").getUserId();
      long b= leaderboardManager.getLeaderboardEntry("EntryB").getViewsNumber();
      if(albId != 0 && usrId != null) {
        	albm = albumManager.getAlbum(usrId, albId.longValue());
        	if(albm != null)
        		albums.add(albm);
        }
      albId = leaderboardManager.getLeaderboardEntry("EntryC").getAlbumId();
      usrId = leaderboardManager.getLeaderboardEntry("EntryC").getUserId();
      long c= leaderboardManager.getLeaderboardEntry("EntryC").getViewsNumber();
      if(albId != 0 && usrId != null) {
        	albm = albumManager.getAlbum(usrId, albId.longValue());
        	if(albm != null)
        		albums.add(albm);
        }
	  long nv;
	  int count = 0;
   	  for (Album album : albums) {
		Photo coverPhoto = null;
	   	String coverPhotoUrl = null;
	    Iterable<Photo> photoIter = photoManager.getOwnedAlbumPhotos(album.getOwnerId().toString(), album.getId().toString());
	    try {
	       	for (Photo photo : photoIter) {
	       		if(photo.isAlbumCover())
	        			coverPhoto = photo;
	       	}
	    } catch (DatastoreNeedIndexException e) {
	         	pageContext.forward(configManager.getErrorPageUrl(
	           		ConfigManager.ERROR_CODE_DATASTORE_INDEX_NOT_READY));
	    }
		if(coverPhoto == null)
			coverPhotoUrl = ServletUtils.getUserIconImageUrl(album.getOwnerId());
		else
			coverPhotoUrl = serviceManager.getImageDownloadUrl(coverPhoto);
		if (count ==0) nv=a; else if (count ==1) nv=b; else nv=c;
		%>
     		<div class="feed">
	      		<div class="post group">
		        	<div class="image-wrap">
		        		<a href="<%= serviceManager.getRedirectUrl(null, album.getOwnerId(), null, 
				 				album.getId().toString(), 
				 			 	ServletUtils.REQUEST_PARAM_NAME_VIEW_STREAM, null) %>"> 
		          		<img class="photo-image"
		            		src="<%= coverPhotoUrl%>"
			 			 	alt="Photo Image" /></a>
	        		</div>
		        	<div class="owner group">
		          		<div class="desc">
		            		<h3><%= ServletUtils.getProtectedUserNickname(album.getOwnerNickname()) %></h3>	            
		            		<p><%= String.valueOf(nv)%> views past hour.</p>
		            		<p>
		            		<p><c:out value="<%= album.getTitle() %>" escapeXml="true"/>
		          		</div>
		          		<!-- /.desc -->
        			</div>
		        	<!-- /.usr -->
	      		</div>
	      		<!-- /.post -->
   			</div>
    		<!-- /.feed -->
	<%	
			count++;
	if (count ==3) break;
      	}
      }
    %>
       	<div class = box >	
  		</div>
         <div class="next-3-pict" >
                 <%-- MCM: replace action form with update rate --%>
        	<form   
               action="<%= configManager.getSetCronTimeUrl() %>"method="post"> 
      		<tr>   		    
                 <td><input type="radio" name="course" value="no_reports">No reports</td><p></p>
                 <td><input type="radio" name="course" value="five_min">Every 5 minutes</td><p></p>
                 <td><input type="radio" name="course" value="one_hour">Every 1 hour </td><p></p>
                 <td><input type="radio" name="course" value="every_day">Every day </td><p></p>
     		</tr>
            <tr> Email trending report </tr>
              <input id="btn-post" class="active btn" type="submit" value="Update Rate">
            </form>
       </div>
     
    		<!-- /.feed -->
 
    </div>

     <%-- ******************************************************************* --%>
     <%-- MCM here starts the div shown if the client chooses the SOCIAL view --%>
     <%-- ******************************************************************* --%>

    
 	<div class="tabContent" id="socialstream">
      <div class="view-title">
        <p>SOCIAL STREAMS</p>
      </div>
		 <!--
		  Below we include the Login Button social plugin. This button uses the JavaScript SDK to
		  present a graphical Login button that triggers the FB.login() function when clicked.
		
		  Learn more about options for the login button plugin:
		  /docs/reference/plugins/login/ -->
		
		<fb:login-button show-faces="true" width="400" max-rows="2"></fb:login-button>
    </div>
  </div>
  <!-- /.wrap -->
</body>
</html>
