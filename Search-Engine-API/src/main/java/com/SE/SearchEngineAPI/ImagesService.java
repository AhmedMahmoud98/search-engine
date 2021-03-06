package com.SE.SearchEngineAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import Models.Image;
import Models.TermImages;
import Queries.QueryProcessor;

@Service
public class ImagesService {
	private MongoOperations mongoOperations;
	
	@Autowired
	public ImagesService(MongoOperations mongoOperations) throws IOException {
		this.mongoOperations = mongoOperations;
	}
	
	public List<Image> getImages(String _query) {
		 QueryProcessor.setQuery(_query);
	     List<String> queryStrings = QueryProcessor.process();
	     List<TermImages> termImages = getTermsImages(queryStrings);
	     Set<String> uniqueImages = new HashSet<String>();
	     Set<Image> images = new HashSet<Image>();
	     for(TermImages termImage : termImages) {
	    	 for(int i = 0;i < termImage.getImageUrl().length; i++)
	    		 if (uniqueImages.add(termImage.getImageUrl()[i]))
	    			 images.add(new Image(termImage.getImageUrl()[i],termImage.getWebsiteUrl()[i]));
	     }
	     return images.stream().collect(Collectors.toList());
	}
	
	private List<TermImages> getTermsImages(List<String> queryStrings) {
		 Query query = new Query();
         
	        List<Criteria> OR = new ArrayList<>();
	        for (String term : queryStrings) 
	       		 OR.add(new Criteria().and("term").is(term));
	        
	        if(OR.isEmpty())
	        	return new ArrayList<TermImages>();

	       query.addCriteria(new Criteria().orOperator(OR.toArray(new Criteria[OR.size()])))
	       	.fields().exclude("term"); 
	        
	       return this.mongoOperations.find(query, TermImages.class);	
	}

}
