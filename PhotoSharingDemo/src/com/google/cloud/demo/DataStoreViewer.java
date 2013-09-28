package com.google.cloud.demo;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

public class DataStoreViewer extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String entityParam = req.getParameter("e");

	    resp.setContentType("text/plain");
	    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	    // Original query
	    final Query queryOrig = new Query(entityParam);
	    queryOrig.addSort(Entity.KEY_RESERVED_PROPERTY, Query.SortDirection.ASCENDING);

	    for (final Entity entityOrig : datastore.prepare(queryOrig).asIterable()) {

	        // Query for this entity and all its descendant entities and collections
	        final Query query = new Query();
	        query.setAncestor(entityOrig.getKey());
	        query.addSort(Entity.KEY_RESERVED_PROPERTY, Query.SortDirection.ASCENDING);

	        for (final Entity entity : datastore.prepare(query).asIterable()) {
	            resp.getWriter().println(entity.getKey().toString());

	            // Print properties
	            final Map<String, Object> properties = entity.getProperties();
	            final String[] propertyNames = properties.keySet().toArray(new String[properties.size()]);
	            for(final String propertyName : propertyNames) {
	                resp.getWriter().println("-> " + propertyName + ": " + entity.getProperty(propertyName));
	            }
	        }
	    }
	}
}
