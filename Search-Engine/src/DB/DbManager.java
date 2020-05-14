package DB;
import com.mongodb.*;
import com.mongodb.client.model.Filters;

import Crawler.CrawlerObject;
import Indexer.termDocumentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Singleton Pattern */
public class DbManager {
    private DB database;
    private static DbManager instance;
    
    public DbManager(){
    	
        /* Initialize default connection */
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDB("SearchEngine");
    }

    public static DbManager getInstance()
    {
        if (instance == null)
            instance = new DbManager();

        return instance;
    }

    public void saveTermCollection( Map<String , Set<Integer>> terms){
        DBCollection collection = database.getCollection("Term");

        for (Map.Entry<String,Set<Integer>> entry : terms.entrySet()) {

            collection.update(new BasicDBObject("term", entry.getKey()),
                    new BasicDBObject("$inc", new BasicDBObject("termFrequency", entry.getValue().size()))
                                      .append("$push" , new BasicDBObject("documents", new BasicDBObject("$each", entry.getValue())))
                                       , true
                                       , false);
        }
    }
    public void saveRobot( Map<String , ArrayList<String>> robots){
        DBCollection collection = database.getCollection("Robot");

        for (Map.Entry<String,ArrayList<String>> entry : robots.entrySet()) {
            //System.out.println(entry.getValue());
//            DBObject term = new BasicDBObject()
//                    .append("term", entry.getKey())
//                    .append("termFrequency", entry.getValue().size())
//                    .append("documents", entry.getValue());

            collection.update(new BasicDBObject("Link", entry.getKey()),
                    new BasicDBObject
                            ("$push", new BasicDBObject("Disallowed", new BasicDBObject("$each", entry.getValue())))
                    , true
                    , false);

            //collection.insert(term);
        }
    }
    public void saveCrawler(ArrayList<CrawlerObject> crawled ){
        DBCollection collection = database.getCollection("CrawlerTable");

        for (int i =0;i<crawled.size();i++) {
            //System.out.println(entry.getValue());
//            DBObject term = new BasicDBObject()
//                    .append("term", entry.getKey())
//                    .append("termFrequency", entry.getValue().size())
//                    .append("documents", entry.getValue());
        	CrawlerObject temp = crawled.get(i);

            collection.update(new BasicDBObject("CrawledIndex", i),
                    new BasicDBObject("Link",temp.getLinkURL()).append
                                      ("Source", temp.getPointingLinks())
                                      .append("Number Of Links", temp.getNumberOfURLs())
                                      .append("Visited", temp.isVisited())
                                      .append("CrawledIndex", i)
                                       , true
                                       , false);
           
            //collection.insert(term);
        }
    }

    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms){
        DBCollection collection = database.getCollection("Document");

        List<DBObject> entreis= new ArrayList<DBObject>();
        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            DBObject entry = new BasicDBObject()
                    .append("term", termDocument.getKey().term)
                    .append("document", termDocument.getKey().docID)
                    .append("termDocumentFrequency", termDocument.getValue().size())
                    .append("positions" , termDocument.getValue())    ;

            entreis.add(entry);
            /*
            collection.update(new BasicDBObject("term", termDocument.getKey().term)
                                                .append("document",termDocument.getKey().docID)
                    , entry
                    , true
                    , false);
            */
        }
        collection.insert(entreis);
    }
}
