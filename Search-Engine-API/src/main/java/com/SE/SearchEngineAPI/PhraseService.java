package com.SE.SearchEngineAPI;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


@Service
public class PhraseService {
    private MongoOperations mongoOperations;

    @Autowired
    public PhraseService(MongoOperations mongoOperations) throws IOException {
        this.mongoOperations = mongoOperations;

    }

    public Map <String, Double> phraseQuery(String phrase) throws IOException {
    	ArrayList<String> phraseStringsList = new ArrayList<String>(Arrays.asList(phrase.split("\\s+")));
        List<Term> termDocs;
        List<Document_> termDocumentPositions;
        
        List<String> termDocuments;
        List<Integer> termPositions;
        
        List<String> phraseDocuments = new ArrayList<String>();
        List<Integer> phrasePositions = new ArrayList<Integer>();
        Map <String, Double> phraseTfIdf = new LinkedHashMap <String, Double>();

        int TotalNumberOfDocuments = getNumberOfDocuments();

        boolean firstTerm = true;
        /* Loop on all terms in the Phrase To get all documents that term appear at 
         		* then perform intersection operation over those documents */
        for (String phraseTerm: phraseStringsList) {
            termDocs = getTerms(phraseTerm);
            if (!termDocs.isEmpty()){
            	termDocuments = new ArrayList<String>(Arrays.asList(termDocs.get(0).getDocuments()));
                if(firstTerm)
                {
                	phraseDocuments = termDocuments;
                	firstTerm = false;
                }
                else
                	phraseDocuments = intersection(phraseDocuments, termDocuments);
            }
        }
               
        double tf = 0;
        double tfIdf = 0;
        int termsIterator = 0;
        int documentSize = 0;

        List<Double> phraseDocumentRepetations = new ArrayList<Double>();
        List<String> phraseDocumentsFound = new ArrayList<String>();
        	/* Loop on all documents that have all the terms in the Phrase then based on 
         * the terms positions in each document, we can indicate if this document has the phrase */
        for (String phraseDocument: phraseDocuments) {	
        	for(String phraseTerm: phraseStringsList) {
        		termDocumentPositions = getTermPositions(phraseTerm, phraseDocument);
        		termPositions = IntStream.of(termDocumentPositions.get(0).getPositions()).boxed().collect(Collectors.toList());
        		if(termsIterator == 0)
        			phrasePositions = termPositions;
        		else
        			phrasePositions = intersection(phrasePositions, decreaseListByValue(termPositions, termsIterator));
        		termsIterator += 1;
        	}
        	
        	if(! phrasePositions.isEmpty()) {
        		phraseDocumentsFound.add(phraseDocument);
        		phraseDocumentRepetations.add((double)phrasePositions.size());
        	}
	        termsIterator = 0;
        }
        
        int documentsIterator = 0;
        /* Loop on all documents that have the Phrase to calculate tfIDF of each one */
    	for(String document: phraseDocumentsFound) {
        	documentSize = Jsoup.connect(document).get().text().split("\\s+").length;
        	tf = phraseDocumentRepetations.get(documentsIterator)/((double)documentSize/(double)phraseStringsList.size());
        	tfIdf = tf * (Math.log((TotalNumberOfDocuments * 1.0) / phraseDocumentsFound.size()) / Math.log(2));
        	phraseTfIdf.put(document, tfIdf);
    	}
        
        return phraseTfIdf;
    }
    
    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }
    
    public List<Integer> decreaseListByValue(List<Integer> list1, int Value) {
        List<Integer> list = new ArrayList<Integer>();

        for (Integer t : list1) {
        	list.add(t - Value);
        }

        return list;
    }

    public int getNumberOfDocuments() {
        return this.mongoOperations.find(new Query(), Popularity.class).size();
    }
    
    public List<Term> getTerms(String term){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term); ;
        query.addCriteria(c);

        return this.mongoOperations.find(query, Term.class);
    }
    
    public List<Document_> getTermPositions(String term, String doc){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term).and("document").is(doc); ;
        query.addCriteria(c);

        return this.mongoOperations.find(query, Document_.class);
    }
}
