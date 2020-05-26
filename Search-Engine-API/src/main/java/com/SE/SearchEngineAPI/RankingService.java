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

import Models.Page;
import Models.Popularity;
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
public class RankingService {
    private CustomQuery query;
    private MongoOperations mongoOperations;

    @Autowired
    public RankingService(MongoOperations mongoOperations) throws IOException {
        this.mongoOperations = mongoOperations;

    }

    public ArrayList<String> rank(CustomQuery _query) {
        ArrayList<String> ranked = new ArrayList<String>();
        List<Popularity> popularityScore = getPopularity();

        for (int i=0; i<popularityScore.size(); i++){
            ranked.add(popularityScore.get(i).getLink());
        }
        return ranked;
    }

    public List<Popularity> getPopularity() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "popularity"))
                .limit(70);

        return this.mongoOperations.find(query, Popularity.class);
    }

}