package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
		long timeBefore = 0, timeAfter= 0, Time = 0;
		timeBefore = System.currentTimeMillis();
		org.jsoup.nodes.Document doc = Jsoup.connect(this.url).get();
	    timeAfter = System.currentTimeMillis();
	    Time = timeAfter - timeBefore;
	    System.out.println("Link Connection time: " + Time + " ms");
		timeBefore = System.currentTimeMillis();
		this.title = doc.title();
	    timeAfter = System.currentTimeMillis();
	    Time = timeAfter - timeBefore;
	    System.out.println("Extract Title time: " + Time + " ms");
		
		timeBefore = System.currentTimeMillis();
		queryTerm1 = queryTerm1.toLowerCase();
		queryTerm2 = queryTerm2.toLowerCase();
		int firstParagraphFirstIndex, firstParagraphLastIndex, secondParagraphFirstIndex, secondParagraphLastIndex;
		Elements p = doc.getElementsByTag("p");
		String docText = "";
		for (Element x: p) {
			docText += x.text().toLowerCase();
			}
		firstParagraphFirstIndex = docText.indexOf(queryTerm1);
		firstParagraphLastIndex = docText.indexOf(" ", firstParagraphFirstIndex + 100);
		
		if(firstParagraphFirstIndex != -1)
			this.summary = docText.substring(firstParagraphFirstIndex, Math.min(firstParagraphLastIndex, docText.length())) + "... ";

		if(queryTerm2 != "") {
			secondParagraphFirstIndex = docText.indexOf(queryTerm2);
			secondParagraphLastIndex = docText.indexOf(" ", secondParagraphFirstIndex + 100);
		}
		else {
			secondParagraphFirstIndex = docText.indexOf(queryTerm1, firstParagraphLastIndex + 1);
			secondParagraphLastIndex = docText.indexOf(" ", secondParagraphFirstIndex + 100);
		}
		
		if(secondParagraphFirstIndex != -1)
			this.summary += docText.substring(secondParagraphFirstIndex, Math.min(secondParagraphLastIndex, docText.length())) + "...";

		if(!queryTerm1.equals("") && this.summary != null ) {
			this.summary = this.summary.replace(queryTerm1, "<strong>"+ queryTerm1+"</strong>");
		}

		if(!queryTerm2.equals("") && this.summary != null )
			this.summary = this.summary.replace(queryTerm2, "<strong>"+ queryTerm2+"</strong>");
		
		if(this.summary == null)
			this.summary = docText.substring(0, Math.min(200, docText.length())) + "...";
		timeAfter = System.currentTimeMillis();
	    Time = timeAfter - timeBefore;
	    System.out.println("Summary time: " + Time + " ms");

	} catch (IOException e) {
		e.printStackTrace();
		e.getMessage();
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