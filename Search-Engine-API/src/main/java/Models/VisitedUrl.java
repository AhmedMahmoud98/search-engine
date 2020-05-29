package Models;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VisitedURLs")
public class VisitedUrl {
  @Indexed
  private String query;
  private String visitedUrl;
  private int frequency;

  public VisitedUrl() {

  }

  public VisitedUrl(String _visitedUrl, String _query, int _frequency) {
    this.visitedUrl = _visitedUrl;
    this.query = _query;
    this.frequency = _frequency;
  }

  public String getVisitedUrl() {
    return visitedUrl;
  }

  public int getFrequency() {
    return this.frequency;
  }

  public void setVisitedUrl(String visitdUrl) {
    this.visitedUrl = visitdUrl;
  }
  
  public void setQuery(String _query) {
	this.query = _query;
  }
  
  public String getQuery() {
	    return query;
  }

  public void Frequency(int _frequency) {
    this.frequency = _frequency;
  }

  @Override
  public String toString() {
    return "VisitedUrl [visitedUrl=" + this.visitedUrl +
    		", query=" + this.query + 
    		", frequency=" + this.frequency +"]";
  }
}