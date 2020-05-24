package DB;
import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import Crawler.CrawlerObject;
import Crawler.SeedsObject;
import Indexer.termDocumentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;

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
    ////////////////////////////////////////////////////////CRAWLER DATABASE FUNCTINONS/////////////////////////////////////////////////////////////////////
    public void saveRobot( Map<String , ArrayList<String>> robots){
        DBCollection collection = database.getCollection("Robot");

        for (Map.Entry<String,ArrayList<String>> entry : robots.entrySet()) {
        	
            collection.update(new BasicDBObject("Link", entry.getKey()),
                    new BasicDBObject
                                      ( "Disallowed",  entry.getValue())
                                      .append("Link", entry.getKey())
                                      
                                       , true
                                       , false);
           
        
        }
    }
    public void saveCrawler(ArrayList<CrawlerObject> crawled ){
        DBCollection collection = database.getCollection("CrawlerTable");

        for (int i =0;i<crawled.size();i++) {
      
        	CrawlerObject temp = crawled.get(i);

            collection.update(new BasicDBObject("Link", temp.getLinkURL()),
                    new BasicDBObject("Link",temp.getLinkURL()).append
                                      ("Source", temp.getPointingLinks())
                                      .append("Number Of Links", temp.getNumberOfURLs())
                                      .append("Visited", temp.isVisited())
                                      .append("CrawledIndex", i)
                                       , true
                                       , false);
           
        }
    }
    public void saveSeeds(ArrayList<SeedsObject> seeds) {
    	DBCollection collection = database.getCollection("SeedsTable");
    	for (int i =0;i<seeds.size();i++) {
    	      
        	SeedsObject temp = seeds.get(i);

            collection.update(new BasicDBObject("Link", temp.getLink()),
                    new BasicDBObject("Link",temp.getLink())
                                      .append("Content", temp.getBody())
                                       , true
                                       , false);
           
        }
    	
    }
    
    public DBCursor getSeeds(){
    	DBCollection collection = database.getCollection("SeedsTable");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    	
    }
    public DBCursor getCrawledLinks(){
    	DBCollection collection = database.getCollection("CrawlerTable");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    	
    }
    public DBCursor getRobots(){
    	DBCollection collection = database.getCollection("Robot");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    	
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms){
        DBCollection collection = database.getCollection("Document");

        List<DBObject> entries= new ArrayList<DBObject>();
        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            DBObject entry = new BasicDBObject()
                    .append("term", termDocument.getKey().term)
                    .append("document", termDocument.getKey().docID)
                    .append("termDocumentFrequency", termDocument.getValue().size())
                    .append("positions" , termDocument.getValue())    ;

            entries.add(entry);
            /*
            collection.update(new BasicDBObject("term", termDocument.getKey().term)
                                                .append("document",termDocument.getKey().docID)
                    , entry
                    , true
                    , false);
            */
        }
        collection.insert(entries);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void savePageRank(Map<String, Double> pageRank) {
        DBCollection collection = database.getCollection("PopularityTable");

        List<DBObject> entries= new ArrayList<DBObject>();
        for(Map.Entry<String, Double> link : pageRank.entrySet()){
            DBObject entry = new BasicDBObject()
                    .append("link", link.getKey())
                    .append("popularity", link.getValue());
            entries.add(entry);
        }
        collection.insert(entries);
    }
}