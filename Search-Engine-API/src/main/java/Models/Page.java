package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Pages")
public class Page {
  @Id
  private String title;
  private String url;
  private String summary;
  
  public Page() {

  }

  public Page(String tit, String ur, String summry) {
    this.title = tit;
    this.url = ur;
    this.summary = summry;
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