package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import Models.VisitedUrl;

@Service
public class VisitedUrlsService {

	private MongoOperations mongoOperations;
	
	@Autowired
	public VisitedUrlsService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

    public VisitedUrl saveVisitedUrl(String visitedUrl, String _query) {
    	    VisitedUrl _visitedUrl = mongoOperations.findAndModify(query(where("visitedUrl").is(visitedUrl).and("query").is(_query)),
    	                  new Update().inc("frequency",1), options().returnNew(true).upsert(true),VisitedUrl.class);
    	    return _visitedUrl;
    } 
} 