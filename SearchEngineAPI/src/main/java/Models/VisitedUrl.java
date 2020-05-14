package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VisitedURLs")
public class VisitedUrl {
  @Id
  private int id;
  private String visitedUrl;

  
  public VisitedUrl() {

  }

  public VisitedUrl(int tID, String visitdUrl) {
    this.visitedUrl = visitdUrl;
    this.id = tID;
  }

  public String getVisitedUrl() {
    return visitedUrl;
  }

  public int getId() {
    return id;
  }

  public void setVisitedUrl(String visitdUrl) {
    this.visitedUrl = visitdUrl;
  }

  public void setId(int tID) {
    this.id = tID;
  }

  @Override
  public String toString() {
    return "VisitedUrl [id=" + this.id + ", visitedUrl=" + this.visitedUrl +"]";
  }
}