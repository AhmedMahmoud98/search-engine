package Models;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CrawlerTable")
public class Link {
	 private String link;
	 private String[] source;
	 private int numberOfLink;
	 private boolean visited;
	 private int crawledIndex;
	 
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String[] getSource() {
		return source;
	}
	public void setSource(String[] source) {
		this.source = source;
	}
	public int getNumberOfLink() {
		return numberOfLink;
	}
	public void setNumberOfLink(int numberOfLink) {
		this.numberOfLink = numberOfLink;
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
	@Override
	public String toString() {
		return "Link [link=" + link + ", source=" + Arrays.toString(source) + ", numberOfLink=" + numberOfLink
				+ ", visited=" + visited + ", crawledIndex=" + crawledIndex + "]";
	}

}
