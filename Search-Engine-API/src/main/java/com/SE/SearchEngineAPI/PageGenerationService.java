package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.stereotype.Service;

import Models.Link;
import Models.Page;
import TextProcessing.StopWords;


@Service
public class PageGenerationService {

		private MongoOperations mongoOperations;
		
		@Autowired
		public PageGenerationService(MongoOperations mongoOperations) throws IOException {
			this.mongoOperations = mongoOperations;

		}

	    public List<Page> generatPages(List<String> Links, String Query) {
	    	String[] query = Query.split("\\s+");
	    	String queryTerm1 = query.length > 0 ? query[0].replaceAll("^\"|\"$", "").toLowerCase() : "";
			String queryTerm2 = query.length > 1 ? query[1].replaceAll("^\"|\"$", "").toLowerCase() : "";
			Link link;
	    	List<Page> pages =  new ArrayList<Page>();

	    	for(String linkURL: Links) {
	    		link = getLinkData(linkURL);
	    		pages.add(new Page(link.getLinkURL(),link.getTitle(),this.getSummary(link.getText(), queryTerm1, queryTerm2)));
	    	}

	    	return pages;
	    	
	    }
	    
	    public Link getLinkData(String link) {
	    	Query query = new Query();
	        Criteria c = new Criteria().where("linkURL").is(link); 
	        query.addCriteria(c).fields().include("title").include("text").include("linkURL"); ;

	        return this.mongoOperations.find(query, Link.class).get(0);
	    }
	    
	    public String getSummary(String linkText, String queryTerm1, String queryTerm2) {

			String summary = "";
			
			if(queryTerm1.equals("") && queryTerm2.equals(""))
				return linkText.substring(0, Math.min(150, linkText.length())) + "...";
			
			int firstParagraphFirstIndex, firstParagraphLastIndex, secondParagraphFirstIndex, secondParagraphLastIndex;
			
	    	firstParagraphFirstIndex = linkText.indexOf(queryTerm1);
			firstParagraphLastIndex = linkText.indexOf(" ", firstParagraphFirstIndex + 80);
			firstParagraphLastIndex = firstParagraphLastIndex == -1 ? linkText.length() : firstParagraphLastIndex;
			
			if(firstParagraphFirstIndex != -1)
				summary = linkText.substring(firstParagraphFirstIndex, Math.min(firstParagraphLastIndex, linkText.length())) + "... ";

			if(! queryTerm2.equals("")) {
				secondParagraphFirstIndex = linkText.indexOf(queryTerm2);
				secondParagraphLastIndex = linkText.indexOf(" ", secondParagraphFirstIndex + 80);
				secondParagraphLastIndex = secondParagraphLastIndex == -1 ? linkText.length() : secondParagraphLastIndex;
			}
			else {
				secondParagraphFirstIndex = linkText.indexOf(queryTerm1, firstParagraphLastIndex + 1);
				secondParagraphLastIndex = linkText.indexOf(" ", secondParagraphFirstIndex + 80);
				secondParagraphLastIndex = secondParagraphLastIndex == -1 ? linkText.length() : secondParagraphLastIndex;
			}
			
			if(secondParagraphFirstIndex != -1)
				summary += linkText.substring(secondParagraphFirstIndex, Math.min(secondParagraphLastIndex, linkText.length())) + "...";

			if(!queryTerm1.equals("") && summary != null ) 
				summary = summary.replace(queryTerm1, "<strong>"+ queryTerm1 +"</strong>");
			
			if(!queryTerm2.equals("") && summary != null )
				summary = summary.replace(queryTerm2, "<strong>"+ queryTerm2 +"</strong>");
		
			if(summary.equals(""))
				summary = linkText.substring(0, Math.min(200, linkText.length())) + "...";
	    	
	    	return summary;
	    }
}
