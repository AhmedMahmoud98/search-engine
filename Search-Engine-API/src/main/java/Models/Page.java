package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;

import org.jsoup.Jsoup;

@Document(collection = "Pages")
public class Page {
  @Id
  private String title;
  private String url;
  private String summary;
  
  public Page() {

  }
  
  public Page(String _url, String queryTerm1, String queryTerm2) {
	  this.url = _url;
	try {
		org.jsoup.nodes.Document doc = Jsoup.connect(this.url).get();
		this.title = doc.getElementsByTag("title").text();
		int firstParagraphFirstIndex, firstParagraphLastIndex, secondParagraphFirstIndex, secondParagraphLastIndex;
		String docText = doc.text();
		firstParagraphFirstIndex = docText.indexOf(queryTerm1);
		firstParagraphLastIndex = docText.indexOf(" ", firstParagraphFirstIndex + 100);
		this.summary = docText.substring(firstParagraphFirstIndex, firstParagraphLastIndex) + "... ";
		
		if(queryTerm2 != "") {
			secondParagraphFirstIndex = docText.indexOf(queryTerm2);
			secondParagraphLastIndex = docText.indexOf(" ", secondParagraphFirstIndex + 100);
		}
		else {
			secondParagraphFirstIndex = docText.indexOf(queryTerm1, firstParagraphLastIndex + 1);
			secondParagraphLastIndex = docText.indexOf(" ", secondParagraphFirstIndex + 100);
		}
		if(secondParagraphFirstIndex != -1)
			this.summary += docText.substring(secondParagraphFirstIndex, secondParagraphLastIndex) + ".";
		
		/* Make The Query String bold */
		this.summary = this.summary.replace(queryTerm1, "<b>"+ queryTerm1+"</b>");
		
		if(secondParagraphFirstIndex != -1)
			this.summary = this.summary.replace(queryTerm2, "<b>"+ queryTerm2+"</b>");
	} catch (IOException e) {
		e.printStackTrace();
	}
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