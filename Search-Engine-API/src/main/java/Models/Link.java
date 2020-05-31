package Models;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CrawlerTable")
public class Link {
	 private String linkURL;
	 private String[] pointingLinks;
	 private int numberOfURLs;
	 private boolean visited;
	 private int crawledIndex;
	 private String title;
	 private String text;
	 private int textSize;
	 
	public Link() {}
	 
	public Link(String linkURL, String[] pointingLinks, int numberOfURLs, boolean visited, int crawledIndex,
			String title, String text, int textSize) {
		super();
		this.linkURL = linkURL;
		this.pointingLinks = pointingLinks;
		this.numberOfURLs = numberOfURLs;
		this.visited = visited;
		this.crawledIndex = crawledIndex;
		this.title = title;
		this.text = text;
		this.textSize = textSize;
	}
	
	public String getLinkURL() {
		return linkURL;
	}
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	public String[] getPointingLinks() {
		return pointingLinks;
	}
	public void setPointingLinks(String[] pointingLinks) {
		this.pointingLinks = pointingLinks;
	}
	public int getNumberOfURLs() {
		return numberOfURLs;
	}
	public void setNumberOfURLs(int numberOfURLs) {
		this.numberOfURLs = numberOfURLs;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public int getCrawledIndex() {
		return crawledIndex;
	}
	public void setCrawledIndex(int crawledIndex) {
		this.crawledIndex = crawledIndex;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
	@Override
	public String toString() {
		return "Link [linkURL=" + linkURL + ", pointingLinks=" + Arrays.toString(pointingLinks) + ", numberOfURLs="
				+ numberOfURLs + ", visited=" + visited + ", crawledIndex=" + crawledIndex + ", title=" + title
				+ ", text=" + text + ", textSize=" + textSize + "]";
	}

	 
	

}
