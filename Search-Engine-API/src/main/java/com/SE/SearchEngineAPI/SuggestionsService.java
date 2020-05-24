package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import Models.Suggestion;

 
@Service
public class SuggestionsService {
	
	private MongoOperations mongoOperations;
	
	@Autowired
	public SuggestionsService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

    public void saveSuggestion(String _query) {
    	  Suggestion suggestion = mongoOperations.findAndModify(query(where("searchString").is(_query.toLowerCase())),
    	                 new Update().inc("frequency",1), options().returnNew(true).upsert(true),Suggestion.class);
    }
}
    

