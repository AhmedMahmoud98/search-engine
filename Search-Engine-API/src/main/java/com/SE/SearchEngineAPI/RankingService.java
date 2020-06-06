package com.SE.SearchEngineAPI;

import java.util.*;
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
public class RankingService {
    private MongoOperations mongoOperations;

    @Autowired
    public RankingService(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;

    }

    public ArrayList<String> rank(CustomQuery _query) {

        QueryProcessor.setQuery(_query.getQueryString());
        ArrayList<String> processed = QueryProcessor.process();
        List<Document_> docsPerTerm;
        double tfidf;
                
        Set<String> urls = new HashSet<>();
        ArrayList<Double> queryTFIDF = new ArrayList<>();
        tfidf = 1.0 / processed.size();
        for (int k=0; k < processed.size(); k++){
            queryTFIDF.add(tfidf);
        }
        
        Map<String, List<Double>> rankings = new HashMap<>();
        List<Double> temp;
        List<Double> zeros = new ArrayList<>();
        int docsCount = getNumberOfDocuments();
        String qWord;
        String doc;
        Map<String, Double> phraseTemp;
        PhraseService phServ = new PhraseService(this.mongoOperations);
        VisitedUrlsService visServ = new VisitedUrlsService(this.mongoOperations);
        boolean phraseExists = false;
        for (int i = 0; i < processed.size(); i++){
            zeros.add(0.0);
        }
        
        for (int i = 0; i < processed.size(); i++) {
            qWord = processed.get(i);
            if (qWord.split("\\s+").length > 1){
                // Phrase Query

                phraseTemp = phServ.phraseQuery(qWord);
                phraseExists = phraseExists || (phraseTemp.keySet().size() > 0);
                for (String url : phraseTemp.keySet()){
                    urls.add(url);
                    if (rankings.get(url) == null) {
                        temp = new ArrayList<>(zeros);
                        rankings.put(url, temp);
                    }
                    temp = rankings.get(url);
                    temp.set(i, 10 + phraseTemp.get(url));
                }
            }
            else {
                docsPerTerm = getTermDocuments(qWord);

                for (int j = 0; j < docsPerTerm.size(); j++) {
                    doc = docsPerTerm.get(j).getDocument();
                    urls.add(doc);
                    if (rankings.get(doc) == null) {
                        temp = new ArrayList<>(zeros);
                        rankings.put(doc, temp);
                    }

                    tfidf = docsPerTerm.get(j).getTermFrequency() * (Math.log((docsCount * 1.0) / docsPerTerm.size()) / Math.log(2));
                    tfidf += tfidf * (docsPerTerm.get(j).isInTitle() ? 1 : 0);

                    temp = rankings.get(doc);
                    temp.set(i, tfidf);
                }
            }
        }

        // TF-IDF Score
        double value;
        double avgSum = 0;
        HashMap<String, Double> finalRanked = new HashMap<>();
        for (String s: urls){
            value = IntStream.range(0, processed.size()).mapToDouble(i -> queryTFIDF.get(i) * rankings.get(s).get(i)).sum();
            avgSum += value;
            finalRanked.put(s, value);
        }

        // Used to check for a good coefficient for the popularity part in the Scoring function.
        // avgSum /= urls.size();
        // System.out.println(avgSum);

        // Popularity score
        List<Popularity> popularityScore = getPopularity(new ArrayList<>(urls));
        String link;
        avgSum = 0;
        int popScoreCoeff = 4;
        for (Popularity popularity : popularityScore) {
            link = popularity.getLink();
            value = popularity.getPopularity();
            avgSum += value;
            finalRanked.replace(link, (popScoreCoeff * value) + finalRanked.get(link));
        }

        // avgSum /= urls.size();
        // System.out.println(avgSum);

        // Personalized score
        List<VisitedUrl> visitedUrls = visServ.getVisitedUrls(processed);
        double persCoeff = 0.001;
        for(VisitedUrl vis:visitedUrls) {
            value = finalRanked.get(vis.getVisitedUrl()) + persCoeff * vis.getFrequency();
            finalRanked.replace(vis.getVisitedUrl(), value);
        }
        
        finalRanked = sortByValue(finalRanked);
        return new ArrayList<>(finalRanked.keySet());
    }
    
    public int getNumberOfDocuments() {
        return this.mongoOperations.find(new Query(), Popularity.class).size();
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        List<Map.Entry<String, Double> > list =
                new LinkedList<>(hm.entrySet());


        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        HashMap<String, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public List<Popularity> getPopularity(List<String> urls) {
        Query query = new Query();
        Criteria orCrit = new Criteria();
        List<Criteria> orExpr = new ArrayList<>();
        for (String url : urls) {
            Criteria temp = new Criteria();
            temp.and("link").is(url);
            orExpr.add(temp);
        }

        if(orExpr.isEmpty())    return new ArrayList<>();

        query.with(Sort.by(Sort.Direction.DESC, "popularity"))
                .addCriteria(orCrit.orOperator(orExpr.toArray(new Criteria[orExpr.size()])));

        return this.mongoOperations.find(query, Popularity.class);
    }

    public List<Document_> getTermDocuments(String term) {
    	 Query query = new Query();
         Criteria c = new Criteria().where("term").is(term); 
         query.addCriteria(c).fields().exclude("positions");
        
         return this.mongoOperations.find(query, Document_.class);
    }
    
}