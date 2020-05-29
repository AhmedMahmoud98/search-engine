package com.SE.SearchEngineAPI;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

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

        QueryProcessor.setQuery(_query.getQueryString());
        ArrayList<String> processed = QueryProcessor.process();
        List<Term> termDocs;
        List<Document_> docsPerTerm;
        String[] docs;
        double tfidf;

        Set<String> urls = new HashSet<>();

        ArrayList<Double> queryTFIDF = new ArrayList<>();
        tfidf = 1.0 / processed.size();
        for (int k=0; k < processed.size(); k++){
            queryTFIDF.add(tfidf);
        }
        Map<String, List<Double>> rankings = new HashMap<>();
        List<Double> temp;
        int docsCount = getNumberOfDocuments();
        String qWord;
        String doc;
        for (int i=0; i<processed.size(); i++) {
            qWord = processed.get(i);
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
                        if (rankings.get(doc) == null){
                            temp = new ArrayList<>(processed.size());
                            for (int k=0; k<processed.size(); k++){
                                temp.add(0.0);
                            }
                            rankings.put(doc, temp);
                        }
                        docsPerTerm = getDocsPerTerm(termDocs.get(0).getTerm(), docs[j]);
                        tfidf = docsPerTerm.get(0).getTermFrequency() * (Math.log((docsCount*1.0) / termDocs.get(0).getTermDocumentsFreq()) / Math.log(2));
                        temp = rankings.get(doc);
                        temp.set(i, tfidf);

                        rankings.replace(doc, temp);
                    }
                }
            }
        }
        // TF-IDF Score
        double value;
        double avgSum = 0;
        Map<String, Double> finalRanked = new HashMap<>();
        for (String s: urls){
            value = IntStream.range(0, processed.size()).mapToDouble(i -> queryTFIDF.get(i) * rankings.get(s).get(i)).sum();
            avgSum += value;
            finalRanked.put(s, value);
        }
        avgSum /= urls.size();
        System.out.println(avgSum);

        // Popularity score
        List<Popularity> popularityScore = getPopularity(new ArrayList<>(urls));

        String link;
        avgSum = 0;
        double popScoreCoeff = 10;
        for (Popularity popularity : popularityScore) {
            link = popularity.getLink();
            value = popularity.getPopularity();
            avgSum += value;
            finalRanked.replace(link, (popScoreCoeff * value) + finalRanked.get(link));
        }
        avgSum /= urls.size();
        System.out.println(avgSum);

        finalRanked = sortByValue((HashMap<String, Double>) finalRanked);
        return new ArrayList<String>(finalRanked.keySet());
    }
    
    public int getNumberOfDocuments() {
        return this.mongoOperations.find(new Query(), Popularity.class).size();
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());


        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public List<Popularity> getPopularity(List<String> urls) {
        Query query = new Query();
        Criteria orCrit = new Criteria();
        List<Criteria> orExpr = new ArrayList<Criteria>();
        for (int i=0; i<urls.size(); i++){
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