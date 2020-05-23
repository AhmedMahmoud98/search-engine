package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span; 
import Models.Trend;
import Models.CustomQuery;

@Service
public class TrendsService {
	private CustomQuery query;
	private NameFinderME nameFinder;
	
	private MongoOperations mongoOperations;
	
	@Autowired
	public TrendsService(MongoOperations mongoOperations) throws IOException {
		this.mongoOperations = mongoOperations;
		
		// load the name extraction model from file
        InputStream is = new FileInputStream("en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
 
        // feed the model to name finder class
        nameFinder = new NameFinderME(model);
	}

    public void extractTrends(CustomQuery _query) {
    	query = _query;
    	Span nameSpans[];
    	String[] queryStrings;
    	String extractedName = "";
    	 
    	queryStrings = query.getQueryString().split(" ");
    	nameSpans = nameFinder.find(queryStrings);
    	for(Span Name: nameSpans) {
    	    for(int index = Name.getStart(); index < Name.getEnd(); index++)
    	    {
    	        extractedName += queryStrings[index];
    	        if(index != Name.getEnd() - 1)
    	        	extractedName += " ";
    	    }
    	    Trend trend = mongoOperations.findAndModify(query(where("trendName").is(extractedName).and("country").is(query.getUserLocation())),
    	                  new Update().inc("frequency",1), options().returnNew(true).upsert(true),Trend.class);
    	    extractedName = "";
    	}
    }
    
    public List<Trend> getTrends(String country) {
    	  Query query = new Query();
    	  Criteria c = new Criteria().where("country").is(country); 
    	  query.with(Sort.by(Sort.Direction.DESC, "frequency"))
    	  .addCriteria(c)
    	  .limit(7);

    	  return this.mongoOperations.find(query, Trend.class);
    }
    
} 