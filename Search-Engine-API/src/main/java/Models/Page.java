package Models;

public class Page {
  private String title;
  private String url;
  private String summary;
  
  public Page() {

  }
  
  public Page(String _url, String _title, String _summary) {
	  this.url = _url;
	  this.title = _title;
	  this.summary = _summary;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }
  
  public String getSummary() {
	return summary;
  }

  public void setTitle(String tit) {
    this.title = tit;
  }

  public void setUrl(String ur) {
    this.url = ur;
  }
  
  public void setSummary(String summry) {
	    this.summary = summry;
  }

  @Override
  public String toString() {
    return "Page [title=" + title + ", url=" + url + ", summary=" + summary +"]";
  }
}