package Models;

import org.springframework.data.mongodb.core.index.CompoundIndex;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndex(name = "keyindex", def = "{'queryTerm' : 1, 'visitedUrl' : 1}")
@Document(collection = "VisitedURLs")
public class VisitedUrl {
  private String queryTerm;
  private String visitedUrl;
  private int frequency;

  public VisitedUrl() {

  }

  public VisitedUrl(String _visitedUrl, String _query, int _frequency) {
    this.visitedUrl = _visitedUrl;
    this.queryTerm = _query;
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
	this.queryTerm = _query;
  }
  
  public String getQuery() {
	    return queryTerm;
  }

  public void Frequency(int _frequency) {
    this.frequency = _frequency;
  }

  @Override
  public String toString() {
    return "VisitedUrl [visitedUrl=" + this.visitedUrl +
    		", query=" + this.queryTerm + 
    		", frequency=" + this.frequency +"]";
  }
}