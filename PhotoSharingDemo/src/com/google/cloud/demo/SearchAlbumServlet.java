package com.google.cloud.demo;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchAlbumServlet extends HttpServlet {


/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    AppContext appContext = AppContext.getAppContext();
    String search_txt = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_STREAM);
    
      res.sendRedirect(appContext.getPhotoServiceManager().getRedirectUrl1(
              req.getParameter(ServletUtils.REQUEST_PARAM_NAME_TARGET_URL), appContext.getCurrentUser().getUserId(), null, null, "searchstream", null,search_txt));
  }

}

