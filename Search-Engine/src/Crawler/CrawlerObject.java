package Crawler;
import java.util.HashSet;

public class CrawlerObject {
	private String linkURL;
	private HashSet<String> pointingLinks;
	private int numberOfURLs; 
	private boolean visited;
	
	public CrawlerObject(String linkURL, HashSet<String> pointingLinks, int numberOfURLs, boolean visited) {
		super();
		this.linkURL = linkURL;
		this.pointingLinks = pointingLinks;
		this.numberOfURLs = numberOfURLs;
		this.visited = visited;
	}
	
	public CrawlerObject() {
		linkURL = null;
		pointingLinks = new HashSet<String>();
		numberOfURLs = -1;
		visited =false;
	}
	
	public String getLinkURL() {
		return linkURL;
	}
	
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	
	public HashSet<String> getPointingLinks() {
		return pointingLinks;
	}
	
	public void setPointingLinks(HashSet<String> pointingLinks) {
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
}
