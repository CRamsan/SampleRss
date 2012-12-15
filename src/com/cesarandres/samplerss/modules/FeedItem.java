package com.cesarandres.samplerss.modules;

/**
 * @author Cesar Ramirez
 * 
 *         This class will keep a reference to all the required elements(text and pictures) that
 *         are going to be displayed by the application.
 * 
 */
public class FeedItem {
	private String title;
	private String author;
	private String published;
	private String thumbnail;
	private String originalLink;
	private String id;

	public FeedItem(String id, String title, String author, String published,
			String thumbnail, String originalLink) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.published = published;
		this.thumbnail = thumbnail;
		this.originalLink = originalLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getOriginalLink() {
		return originalLink;
	}

	public void setOriginalLink(String originalLink) {
		this.originalLink = originalLink;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}