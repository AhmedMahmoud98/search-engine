package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Models.*;
import Queries.QueryProcessor;
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

@Service
public class RankingService {
    private CustomQuery query;
    private MongoOperations mongoOperations;

    @Autowired
    public RankingService(MongoOperations mongoOperations) throws IOException {
        this.mongoOperations = mongoOperations;

    }

    public ArrayList<String> rank(CustomQuery _query) {
        ArrayList<String> ranked = new ArrayList<String>();

        QueryProcessor.setQuery(_query.getQueryString());
        ArrayList<String> processed = QueryProcessor.process();
        List<Term> termDocs;
        List<Document_> docsPerTerm;
        String[] docs;
        System.out.println(processed);
        double tfidf;

        List<String> urls = new ArrayList<String>();
        urls.add("https://www.geeksforgeeks.org/php/");
        urls.add("https://www.geeksforgeeks.org/category/algorithm/page/4/");
        List<Popularity> popularityScore = getPopularity(urls);
        int docsCount = popularityScore.size();
        for (int i=0; i<processed.size(); i++){
            if (processed.get(i).split("\\s+").length > 1){
                // Phrase
            }
            termDocs = getTerms(processed.get(i));
            if (!termDocs.isEmpty()){
                docs = termDocs.get(0).getDocuments();
                for (int j=0; j<docs.length; j++){
                    docsPerTerm = getDocsPerTerm(termDocs.get(0).getTerm(), docs[j]);
                    tfidf = docsPerTerm.get(j).getTermFrequency() * Math.log((docsCount*1.0) / termDocs.get(0).getTermDocumentsFreq());
                    System.out.println(docsCount);
                }
            }
            else{
                // TERM is not in DataBase.
            }

        }


        for (int i=0; i<popularityScore.size(); i++){
            ranked.add(popularityScore.get(i).getLink());
        }
        return ranked;
    }

    public List<Popularity> getPopularity(List<String> urls) {
        Query query = new Query();
        Criteria c = new Criteria().where("link").is(urls.get(0));
        Criteria temp = new Criteria();
        for (int i=1; i<urls.size(); i++){
            System.out.println(urls.get(i));
            temp.where("link").is(urls.get(i));
            c.orOperator(temp);
        }
        query.with(Sort.by(Sort.Direction.DESC, "popularity"))
                //.addCriteria(c)
                .limit(75);
        //System.out.println(this.mongoOperations.find(query, "PopularityTable"));
        return this.mongoOperations.find(query, Popularity.class);
    }
    public List<Term> getTerms(String term){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term); ;
        query.addCriteria(c);

        return this.mongoOperations.find(query, Term.class);
    }
    public List<Document_> getDocsPerTerm(String term, String doc){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term).and("document").is(doc); ;
        query.addCriteria(c);

        return this.mongoOperations.find(query, Document_.class);
    }
}