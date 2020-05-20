package Crawler;

public class SeedsObject {
	private String Link;
	private String Body;
	public String getLink() {
		return Link;
	}
	public void setLink(String link) {
		Link = link;
	}
	public String getBody() {
		return Body;
	}
	public void setBody(String body) {
		Body = body;
	}
	public SeedsObject(String link, String body) {
		Link = link;
		Body = body;
	}


}
