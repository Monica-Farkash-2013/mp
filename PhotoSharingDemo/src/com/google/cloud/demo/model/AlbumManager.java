package com.google.cloud.demo.model;

public interface AlbumManager extends DemoEntityManager<Album> {
	  /**
	   * Lookups a specific Album.
	   *
	   * @param userId the owner's user id.
	   * @param id the Album id.
	   *
	   * @return the Album entity; return null if Album is not found.
	   */
	  Album getAlbum(String userId, long id);

	  /**
	   * Queries an {@code Iterable} collection of Albums owned by the user.
	   *
	   * @param userId the user id.
	   * @return an {@code Iterable} collection of Albums.
	   */
	  Iterable<Album> getOwnedAlbums(String userId);

	  /**
	   * Queries all Albums shared to a user with specific user id. The result set
	   * does not include Albums owned by the user.
	   *
	   * @param userId the user id.
	   *
	   * @return an {@code Iterable} collection of Albums shared to the user.
	   */
	  Iterable<Album> getSharedAlbums(String userId);

	  /**
	   * Gets all deactived Albums.
	   *
	   * @return an {@code Iterable} collection of deactived Albums.
	   */
	  Iterable<Album> getDeactivedAlbums();

	  /**
	   * Gets all active Albums.
	   *
	   * @return an {@code Iterable} collection of active Albums.
	   */
	  Iterable<Album> getActiveAlbums();

	  /**
	   * Creates a new Album object based on user id. The object is not yet
	   * serialized to datastore yet.
	   *
	   * @param userId the user id.
	   *
	   * @return a Album object.
	   */
	  Album newAlbum(String userId);

	  /**
	   * Marks a Album inactive so that the Album is ready for delete.
	   *
	   * @return the deactived Album object; null if operation failed.
	   */
	  Album deactiveAlbum(String userId, long id);

	Iterable<Album> getAlbums(String userId, String albumId);

	Album getAlbumS(String userId, String albumId);

}
