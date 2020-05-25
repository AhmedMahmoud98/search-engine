package DB;
import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import Crawler.CrawlerObject;
import Crawler.SeedsObject;
import Indexer.termDocumentKey;

import java.util.*;

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
                    new BasicDBObject("Link",temp.getLinkURL())
                    				   .append("Source", temp.getPointingLinks())
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

        return collection.find();


    }
    public DBCursor getCrawledLinks(){
    	DBCollection collection = database.getCollection("CrawlerTable");

        return collection.find();

    }
    public DBCursor getRobots(){
    	DBCollection collection = database.getCollection("Robot");

        return collection.find();

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void saveTermCollection( Map<String , Set<String>> terms){
        DBCollection collection = database.getCollection("Term");

        for (Map.Entry<String,Set<String>> entry : terms.entrySet()) {

            collection.update(new BasicDBObject("term", entry.getKey()),
                    new BasicDBObject("$inc", new BasicDBObject("termDocumentsFreq", entry.getValue().size()))
                                      .append("$push" , new BasicDBObject("documents", new BasicDBObject("$each", entry.getValue())))
                                       , true
                                       , false);
        }
    }
    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms, Map<String, Integer> documentsSizes){
        DBCollection collection = database.getCollection("Document");
        int indexIterator = 0;
        List<DBObject> entries= new ArrayList<DBObject>();
        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            DBObject entry = new BasicDBObject()
                    .append("term", termDocument.getKey().term)
                    .append("document", termDocument.getKey().docUrl)
                    .append("termFrequency", termDocument.getValue().size()/(float)documentsSizes.get(termDocument.getKey().docUrl))
                    .append("positions" , termDocument.getValue());

            entries.add(entry);
        }
        collection.insert(entries);
    }

    public void saveImageCollection(Map<String,List<String>> terms, String url){
        DBCollection collection = database.getCollection("Images");
        for (Map.Entry<String,List<String>> entry : terms.entrySet()) {

            if (entry.getValue().get(0) instanceof String) {
                System.out.println(entry.getValue());
            }
            collection.update(new BasicDBObject("term", entry.getKey()),
                    new BasicDBObject("$push", new BasicDBObject("imageUrl", new BasicDBObject("$each", entry.getValue())))
                            .append("$push" , new BasicDBObject("websiteUrl", url))
                            .append("$push", new BasicDBObject("imageUrl", new BasicDBObject("$each", entry.getValue())))
                    , true
                    , false);
        }
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
    public DBCursor getPageRank(){
        DBCollection collection = database.getCollection("PopularityTable");

        return collection.find();

    }
}