package com.SE.SearchEngineAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Models.*;
import Queries.QueryProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


@Service
public class PhraseService {
    private MongoOperations mongoOperations;

    @Autowired
    public PhraseService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public PhraseService()	{ }

    public Map <String, Double> phraseQuery(String phrase) {
    	QueryProcessor.setQuery(phrase);
        List<String> phraseStringsList = QueryProcessor.process();
        Set<String> allPhraseStringsDocuments;
        List<Document_> termsDocumentsEntries;
     
        Map <String, Double> phraseTfIdf = new LinkedHashMap <String, Double>();
        allPhraseStringsDocuments = getAllPhraseStringsDocuments(phraseStringsList);
        termsDocumentsEntries = getTermsDocumentsEntries(phraseStringsList, allPhraseStringsDocuments);
        termsDocumentsEntries = handleDuplicates(termsDocumentsEntries, phraseStringsList);
        phraseTfIdf = getPhraseDocumentsAndCalculateTFIDF(phraseStringsList, termsDocumentsEntries, phraseStringsList.size(), allPhraseStringsDocuments.size());
        
        return phraseTfIdf;
    }
    
    public  List<Document_>  handleDuplicates(List<Document_> termsDocumentsEntries, List<String> phraseStringsList) {
    	Set<String> duplicates = new HashSet<>();
    	Set<String> uniques = new HashSet<>();

    	List<Document_> dupDocuments = new ArrayList<Document_>();
    	for(String term: phraseStringsList)
    		if (!uniques.add(term))
    			duplicates.add(term);
   
		for(Document_ document: termsDocumentsEntries)
			if(duplicates.contains(document.getTerm()))
				dupDocuments.add(document);
		
		termsDocumentsEntries.addAll(dupDocuments);
    	return termsDocumentsEntries;
    }
    
    
    public List<Integer> positionsIntersection(List<Integer> list1, List<Integer> list2, int Value) {
    	if(Value == 0)
    		return list2;
    	
        List<Integer> list = new ArrayList<Integer>();
        for (int t : list1) {
            if(list2.contains(t + Value)) {
                list.add(t);
            }
        }
        return list;
    }
    
    public List<Document_> getTermsDocuments(List<String> terms){
   	 	Query query = new Query();
         
        List<Criteria> OR = new ArrayList<>();
        for (String term : terms) 
       		 OR.add(new Criteria().and("term").is(term));
         
        if(OR != null)
       	 query.addCriteria(new Criteria().orOperator(OR.toArray(new Criteria[OR.size()])))
       	 .fields().include("document").include("term"); 

       return this.mongoOperations.find(query, Document_.class);
   }
    
    public List<Document_> getTermsDocumentsEntries(List<String> terms, Set<String> documents) {
	   	 Query query = new Query();
	     
	     List<Criteria> OR = new ArrayList<>();
	     for (String term : terms) 
	    	 for(String document: documents)
	    		 OR.add(new Criteria().and("term").is(term).and("document").is(document));
	      
	     if(OR.isEmpty())	return new ArrayList<>();

	     query.with(Sort.by(Sort.Direction.ASC, "document"))
	    	 .addCriteria(new Criteria().orOperator(OR.toArray(new Criteria[OR.size()])))
	    	 .fields().include("term").include("document").include("positions");
	
	    return this.mongoOperations.find(query, Document_.class);
    }

    public List<Link> getDocumentsSizes(String document){
        Query query = new Query();
        Criteria c = new Criteria().where("linkURL").is(document); 
        query.addCriteria(c).fields().include("linkURL").include("textSize");

        return this.mongoOperations.find(query, Link.class);
    }

    public int getNumberOfDocuments() {
        return this.mongoOperations.find(new Query(), Popularity.class).size();
    }
    
    public Set<String> getAllPhraseStringsDocuments(List<String> phraseStringsList) {

    	Map<String, Integer> allDocumentsRepetations = new HashMap<String, Integer>();
    	Set<String> allPhraseStringsDocuments = new HashSet<String>();
    	List<Document_> allDocuments = getTermsDocuments(phraseStringsList);
    	int QuerySize = new HashSet<>(phraseStringsList).size();
        /* Loop on all documents that have at least one term from the phrase 
         * and extract those which have all phrase terms (their Existence = Phrase Size) */
    	for(Document_ document : allDocuments) {
    		Object repVal = allDocumentsRepetations.get(document.getDocument());
    		int newVal = repVal == null ? 1 : (int) repVal + 1;
    		allDocumentsRepetations.put(document.getDocument(), newVal);
    		if(newVal == QuerySize)
    			allPhraseStringsDocuments.add(document.getDocument());
    	}
    	
    	return allPhraseStringsDocuments;
    }
    
    public Map <String, Double> getPhraseDocumentsAndCalculateTFIDF(List<String> phraseString,
    	List<Document_> termsDocumentsEntries, int QuerySize, int DocumentsSize) {
    	
    	List<Integer> termPositions;
    	List<Integer> phrasePositions = new ArrayList<Integer>();
    	List<String> phraseDocuments = new ArrayList<String>();
    	List<Double> phraseDocumentRepetations = new ArrayList<Double>();
    	Map <String, Double> phraseTfIdf = new LinkedHashMap <String, Double>();


        int TotalNumberOfDocuments = getNumberOfDocuments();
        	/* Loop on all documents that have all the terms in the Phrase then based on 
         * the terms positions in each document, we can indicate if this document has the phrase */
        int termsDocumentsIterator = 0;
        int tempIterator = 0;
        for (int documentsIterator = 0; documentsIterator < DocumentsSize; documentsIterator++) {	
        	for (int termsIterator = 0; termsIterator < QuerySize; termsIterator++) {
        		for (int termsIterator2 = 0; termsIterator2 < QuerySize; termsIterator2++)
        		{
        			if(termsDocumentsEntries.get(termsDocumentsIterator + termsIterator2).getTerm().equals(phraseString.get(termsIterator)))
        			{
        				tempIterator = termsDocumentsIterator + termsIterator2;
        				break;
        			}
        		}
        		termPositions = new ArrayList<Integer>(IntStream.of(termsDocumentsEntries.get(tempIterator).getPositions()).boxed().collect(Collectors.toList()));
        		phrasePositions = positionsIntersection(phrasePositions, termPositions, termsIterator);
        	}
    		termsDocumentsIterator += QuerySize;
        	if(!phrasePositions.isEmpty()) {
        		phraseDocuments.add(termsDocumentsEntries.get(termsDocumentsIterator - 1).getDocument());
        		phraseDocumentRepetations.add((double)phrasePositions.size());
        	}
        }
        
    	double tf = 0;
        double tfIdf = 0;
        int documentSize = 0;
        int documentsIterator = 0;
        /* Loop on all documents that have the Phrase to calculate tfIDF of each one */
    	for(String document: phraseDocuments) {
        	documentSize = getDocumentsSizes(document).get(0).getTextSize();
        	tf = phraseDocumentRepetations.get(documentsIterator)/((double)documentSize/(double)QuerySize);
        	tfIdf = tf * (Math.log((TotalNumberOfDocuments * 1.0) / phraseDocuments.size()) / Math.log(2));
        	phraseTfIdf.put(document, tfIdf);
    	}
        
        return phraseTfIdf;
    }
   
}
