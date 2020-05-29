package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
        Map<String, Double> finalRanked = new HashMap<>();

        QueryProcessor.setQuery(_query.getQueryString());
        ArrayList<String> processed = QueryProcessor.process();
        List<Term> termDocs;
        List<Document_> docsPerTerm;
        String[] docs;
        double tfidf;

        List<String> urls = new ArrayList<String>();

        ArrayList<Double> queryTFIDF = new ArrayList<>();
        Map<String, List<Double>> rankings = new HashMap<>();
        List<Double> temp;
        int docsCount = getNumberOfDocuments();
        String qWord;
        String doc;
        for (int i=0; i<processed.size(); i++) {
            qWord = processed.get(i);
            System.out.println(qWord);
            if (qWord.split("\\s+").length > 1){
                // Phrase
            }
            else{
                termDocs = getTerms(qWord);
                if (! termDocs.isEmpty()) {
                    docs = termDocs.get(0).getDocuments();
                    for (int j=0; j<docs.length; j++){
                        doc = docs[j];
                        urls.add(doc);
                        rankings.computeIfAbsent(doc, k -> new ArrayList<>(processed.size()));

                        docsPerTerm = getDocsPerTerm(termDocs.get(0).getTerm(), docs[j]);
                        tfidf = docsPerTerm.get(j).getTermFrequency() * (Math.log((docsCount*1.0) / termDocs.get(0).getTermDocumentsFreq()) / Math.log(2));
                        temp = rankings.get(doc);
                        temp.add(i, tfidf);
                        rankings.put(doc, temp);
                    }
                }
                else{
                    // TERM is not in DataBase.
                }

            }
        }
        List<Popularity> popularityScore = getPopularity(urls);

        /*
        System.out.println(popularityScore);
        for (int i=0; i<popularityScore.size(); i++){
            finalRanked.put(popularityScore.get(i).getLink());
        }
        */
        return new ArrayList<String>(finalRanked.keySet());
    }
    
    public int getNumberOfDocuments() {
        return this.mongoOperations.find(new Query(), Popularity.class).size();
    }

    public List<Popularity> getPopularity(List<String> urls) {
        Query query = new Query();
        Criteria orCrit = new Criteria();
        List<Criteria> orExpr = new ArrayList<Criteria>();
        for (int i=0; i<urls.size(); i++){
            System.out.println(urls.get(i));
            Criteria temp = new Criteria();
            temp.and("link").is(urls.get(i));
            orExpr.add(temp);
        }

        query.with(Sort.by(Sort.Direction.DESC, "popularity"))
                .addCriteria(orCrit.orOperator(orExpr.toArray(new Criteria[orExpr.size()])));
        //System.out.println(this.mongoOperations.find(query, "PopularityTable"));
        return this.mongoOperations.find(query, Popularity.class);
    }
    public List<Term> getTerms(String term){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term);
        query.addCriteria(c);

        return this.mongoOperations.find(query, Term.class);
    }
    public List<Document_> getDocsPerTerm(String term, String doc){
        Query query = new Query();
        Criteria c = new Criteria().where("term").is(term).and("document").is(doc);
        query.addCriteria(c);

        return this.mongoOperations.find(query, Document_.class);
    }
}