package com.cesarandres.samplerss.modules;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLParser {
	private static final String FEED = "feed";
	private static final String ENTRY = "item";
	private static final String UPDATED = "updated";
	private static final String PUBLISHED = "pubDate";
	private static final String AUTHOR = "creator";
	private static final String TITLE = "title";
	private static final String CONTENT = "encoded";
	private static final String THUMBNAIL_NS = "img";
	private static final String THUMBNAIL = "thumbnail";
	private static final String ORIGINAL_LINK_NS = "feedburner";
	private static final String ORIGINAL_LINK = "origLink";
	private static final String ID = "id";

	private static final String TAG = "XMLParser";

	public String parseClean(String content) {
		FormatHandler handler = new FormatHandler();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser
					.parse(new InputSource(new StringReader(content)), handler);

		} catch (Exception e) {
			Log.e(TAG, "IOException while parsing file.");
		}
		return handler.getResult();
	}

	public String parseClean(File file) {

		FormatHandler handler = new FormatHandler();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);

		} catch (Exception e) {
		}
		return handler.getResult();
	}

	public ArrayList<FeedItem> parseGetObjects(File file) {

		DataHandler handler = new DataHandler();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);

		} catch (Exception e) {
		}
		return handler.getResult();
	}

	public String parseGetUpdateTime(File file) {

		UpdateTimeHandler handler = new UpdateTimeHandler();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);

		} catch (Exception e) {
		}
		return handler.getResult();
	}

	public String parseGetUpdateTime(String content) {

		UpdateTimeHandler handler = new UpdateTimeHandler();
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser
					.parse(new InputSource(new StringReader(content)), handler);

		} catch (Exception e) {
		}
		return handler.getResult();
	}

	public String parseGetContent(File file, String id) {

		ContentHandler handler = new ContentHandler(id);
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);

		} catch (Exception e) {
		}
		return handler.getResult();
	}

	private class DataHandler extends DefaultHandler {

		private boolean entry_flag = false;
		private boolean updated_flag = false;
		private boolean published_flag = false;
		private boolean title_flag = false;
		private boolean id_flag = false;
		private boolean author_flag = false;
		private boolean thumbnail_flag = false;
		private boolean originalLink_flag = false;
		private boolean content_flag = false;

		private ArrayList<FeedItem> data;
		private String title;
		private String author;
		private String published;
		private String thumbnail;
		private String originalLink;
		private String id;

		public DataHandler() {
			data = new ArrayList<FeedItem>();
		}

		public ArrayList<FeedItem> getResult() {
			return data;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = true;
			} else if (!entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = true;
				title = "";
				author = "";
				published = "";
				thumbnail = "";
				originalLink = "";
				id = "";
			} else if (entry_flag && qName.equalsIgnoreCase(ID)) {
				id_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(TITLE)) {
				title_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(PUBLISHED)) {
				published_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(CONTENT)) {
				content_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(THUMBNAIL)) {
				thumbnail = attributes.getValue("url");
				thumbnail_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(AUTHOR)) {
				author_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(ORIGINAL_LINK)) {
				originalLink_flag = true;
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = false;
				data.add(new FeedItem(id, title, author, published, thumbnail,
						originalLink));
			} else if (entry_flag && qName.equalsIgnoreCase(TITLE)) {
				title_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ID)) {
				id_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(PUBLISHED)) {
				published_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(CONTENT)) {
				content_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(AUTHOR)) {
				author_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(THUMBNAIL)) {
				thumbnail_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ORIGINAL_LINK)) {
				originalLink_flag = false;
			}

		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			if (entry_flag && published_flag) {
				published = new String(ch, start, length);
			} else if (entry_flag && title_flag) {
				title = (new String(ch, start, length));
			} else if (entry_flag && id_flag) {
				id = new String(ch, start, length);
			} else if (entry_flag && author_flag) {
				author = (new String(ch, start, length));
			} else if (entry_flag && thumbnail_flag) {
			} else if (entry_flag && originalLink_flag) {
				originalLink = (new String(ch, start, length));
			} else if (entry_flag && content_flag) {

			}
		}
	}

	private class ContentHandler extends DefaultHandler {

		private boolean updated_flag = false;
		private boolean entry_flag = false;
		private boolean id_flag = false;
		private boolean content_flag = false;
		private boolean itemFound = false;
		private String id = "";
		StringBuffer builder;

		public ContentHandler(String id) {
			this.id = id;
			builder = new StringBuffer();
		}

		public String getResult() {
			return builder.toString();
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = true;
			} else if (!entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(ID)) {
				id_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(CONTENT)) {
				content_flag = true;
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = false;
				if (itemFound) {
					itemFound = false;
				}
			} else if (entry_flag && qName.equalsIgnoreCase(ID)) {
				id_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(CONTENT)) {
				content_flag = false;
			}

		}

		public void characters(char ch[], int start, int length)
				throws SAXException {

			if (entry_flag && id_flag) {
				String idFound = new String(ch, start, length);
				if (idFound.equalsIgnoreCase(id)) {
					itemFound = true;
				}
			} else if (entry_flag && content_flag && itemFound) {
				builder.append(new String(ch, start, length));
			}
		}
	}

	private class FormatHandler extends DefaultHandler {

		private StringBuilder content;
		private boolean entry_flag = false;
		private boolean updated_flag = false;
		private boolean id_flag = false;
		private boolean published_flag = false;
		private boolean title_flag = false;
		private boolean author_flag = false;
		private boolean thumbnail_flag = false;
		private boolean originalLink_flag = false;
		private boolean content_flag = false;
		private int id = 0;
		private String line = "";

		public FormatHandler() {
			content = new StringBuilder();
			content.append("<" + FEED + ">");
		}

		public String getResult() {
			content.append("</" + FEED + ">");
			return content.toString();
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				content.append("<" + qName + ">");
				updated_flag = true;
			} else if (!entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				content.append("<" + qName + ">");
				content.append("<" + ID + ">");
				entry_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(TITLE)) {
				content.append("<" + qName + ">");
				title_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase(PUBLISHED)) {
				content.append("<" + qName + ">");
				published_flag = true;
			} else if (entry_flag
					&& qName.equalsIgnoreCase("content:" + CONTENT)) {
				content.append("<" + localName + ">");
				content_flag = true;
			} else if (entry_flag
					&& qName.equalsIgnoreCase(THUMBNAIL_NS + ":" + THUMBNAIL)) {
				content.append("<" + THUMBNAIL + " url=\""
						+ attributes.getValue("url") + "\" />");
				thumbnail_flag = true;
			} else if (entry_flag && qName.equalsIgnoreCase("dc:" + AUTHOR)) {
				content.append("<" + localName + ">");
				author_flag = true;
			} else if (entry_flag
					&& qName.equalsIgnoreCase(ORIGINAL_LINK_NS + ":"
							+ ORIGINAL_LINK)) {
				content.append("<" + ORIGINAL_LINK + ">");
				originalLink_flag = true;
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				content.append("</" + qName + ">");
				updated_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				content.append("</" + ID + ">");
				content.append("</" + qName + ">");
				entry_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(TITLE)) {
				content.append("</" + qName + ">");
				title_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(PUBLISHED)) {
				content.append("</" + qName + ">");
				published_flag = false;
			} else if (entry_flag
					&& qName.equalsIgnoreCase("content:" + CONTENT)) {
				content.append("</" + localName + ">");
				content_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase("dc:" + AUTHOR)) {
				content.append("</" + localName + ">");
				author_flag = false;
			} else if (entry_flag
					&& qName.equalsIgnoreCase(THUMBNAIL_NS + ":" + THUMBNAIL)) {
				thumbnail_flag = false;
			} else if (entry_flag
					&& qName.equalsIgnoreCase(ORIGINAL_LINK_NS + ":"
							+ ORIGINAL_LINK)) {
				content.append("</" + ORIGINAL_LINK + ">");
				originalLink_flag = false;
			}

		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			line = new String(ch, start, length);
			line = line.replaceAll("&", "&amp;");
			if (!entry_flag && updated_flag) {
				content.append(line);
			} else if (entry_flag && published_flag) {
				content.append(line + " ");
				content.append(Integer.toString(id));
				id++;
			} else if (entry_flag && id_flag) {
			} else if (entry_flag && title_flag) {
				content.append(line);
			} else if (entry_flag && author_flag) {
				content.append(line + " ");
			} else if (entry_flag && thumbnail_flag) {
			} else if (entry_flag && originalLink_flag) {
				content.append(line);
			} else if (entry_flag && content_flag) {
				content.append(line);
			}
		}
	}

	private class UpdateTimeHandler extends DefaultHandler {

		private boolean entry_flag = false;
		private boolean updated_flag = false;

		private String updateTime = "";

		public String getResult() {
			return updateTime;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = true;
			} else if (!entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = true;
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (!entry_flag && qName.equalsIgnoreCase(UPDATED)) {
				updated_flag = false;
			} else if (entry_flag && qName.equalsIgnoreCase(ENTRY)) {
				entry_flag = false;
			}
		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			if (!entry_flag && updated_flag) {
				updateTime = new String(ch, start, length);
			}
		}
	}

}
