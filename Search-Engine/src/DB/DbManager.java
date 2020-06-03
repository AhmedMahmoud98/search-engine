package DB;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.MongoClient;
import Crawler.CrawlerObject;
import Crawler.SeedsObject;
import Indexer.termDocumentKey;



import java.util.*;


/* Singleton Pattern */
public class DbManager {
	private MongoClient mongoClient;
    private DB database;
    private static DbManager instance;
    
    private DbManager() { 
  	
        /* Initialize default connection */
        mongoClient = new MongoClient();
        database = mongoClient.getDB("SearchEngine");
    }

    public static DbManager getInstance()
    {
        if (instance == null)
            instance = new DbManager();

        return instance;
    }
    
    public void addTablesIndices() {
    	MongoDatabase SearchEngine = mongoClient.getDatabase("SearchEngine");
    	MongoCollection<Document> crawlerCollection = SearchEngine.getCollection("CrawlerTable");
        MongoCollection<Document> documentCollection = SearchEngine.getCollection("Document");
        MongoCollection<Document> ImagesCollection = SearchEngine.getCollection("Images");
        MongoCollection<Document> popularityCollection = SearchEngine.getCollection("PopularityTable");
        
        crawlerCollection.createIndex(Indexes.ascending("Link"));
        crawlerCollection.createIndex(Indexes.ascending("CrawledIndex"));
        documentCollection.createIndex(Indexes.ascending("term"));
        documentCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("term"),Indexes.ascending("document")));
        ImagesCollection.createIndex(Indexes.ascending("term"));
        popularityCollection.createIndex(Indexes.ascending("link"));
        popularityCollection.createIndex(Indexes.descending("popularity"));
    }
    
    public void dropTables() {
    	MongoDatabase SearchEngine = mongoClient.getDatabase("SearchEngine");
    	MongoCollection<Document> crawlerCollection = SearchEngine.getCollection("CrawlerTable");
    	MongoCollection<Document> seedsCollection = SearchEngine.getCollection("SeedsTable");
        MongoCollection<Document> documentCollection = SearchEngine.getCollection("Document");
        MongoCollection<Document> ImagesCollection = SearchEngine.getCollection("Images");
        MongoCollection<Document> popularityCollection = SearchEngine.getCollection("PopularityTable");
        
        documentCollection.drop();
        ImagesCollection.drop();
        popularityCollection.drop();
        crawlerCollection.drop();
        seedsCollection.drop();
   
    }

///////////////////////////////////    Crawler	 //////////////////////////////////
    public void saveRobot( Map<String , ArrayList<String>> robots){
        DBCollection collection = database.getCollection("Robot");
        for (Map.Entry<String,ArrayList<String>> entry : robots.entrySet()) {
            collection.update(new BasicDBObject("Link", entry.getKey()),
                    new BasicDBObject ("Disallowed",  entry.getValue())
                                      .append("Link", entry.getKey())
                                       , true
                                       , false);

        }
    }
    
    public void saveCrawler(ArrayList<CrawlerObject> crawled ) {
        DBCollection collection = database.getCollection("CrawlerTable");
        int collectionSize = (int) collection.count();

        for (int i = collectionSize; i < crawled.size();i++) {
        	CrawlerObject temp = crawled.get(i);
            collection.update(new BasicDBObject("linkURL", temp.getLinkURL()),
                   new BasicDBObject("linkURL",temp.getLinkURL())
                    				   .append("pointingLinks", temp.getPointingLinks())
                                       .append("numberOfURLs", temp.getNumberOfURLs())
                                       .append("visited", temp.isVisited())
                                       .append("crawledIndex", i)
                                       .append("title", "")
                                       .append("text", "")
                                       .append("textSize","")
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
    
    public List<String> getCrawledUrls(int StartingIndex) {
    	MongoDatabase SearchEngine = mongoClient.getDatabase("SearchEngine");
        MongoCollection<Document> collection = SearchEngine.getCollection("CrawlerTable");
    	Iterator<Document>  objects = collection.find(and(gte("crawledIndex", StartingIndex*1000), lt("crawledIndex", (StartingIndex+1)*1000)))
    											.projection(Projections.include("linkURL")).iterator(); 
    	List<String> Urls = new ArrayList<String>();
    	while (objects.hasNext()) {
    		String Url = (String) new ArrayList<>(objects.next().values()).get(1);
    		Urls.add(Url);
    	}
    	return Urls;
    }
    
    public DBCursor getRobots(){
    	DBCollection collection = database.getCollection("Robot");
        return collection.find();
    }
    
    public void UpdateCrawler(int index, String text, String title, int termsSize) {
    	MongoDatabase SearchEngine = mongoClient.getDatabase("SearchEngine");
    	MongoCollection<Document> crawlerCollection = SearchEngine.getCollection("CrawlerTable");
    	BasicDBObject updateQuery = new BasicDBObject();
    	Bson filter = eq("crawledIndex", index);
    	updateQuery.put("$set", new BasicDBObject("text",text).append("title", title).append("textSize", termsSize));
    	crawlerCollection.updateOne(filter, updateQuery);
    }
    
////////////////////////////////////////////////	Indexer	    ///////////////////////////////////////
    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms, Map<String, Integer> documentsSizes){
        DBCollection collection = database.getCollection("Document");
        List<DBObject> entries= new ArrayList<DBObject>();
        boolean inTtitle ;
        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            if(termDocument.getValue().get(0) == -1) 
            	inTtitle = true;
            else                                     
            	inTtitle = false;

            DBObject entry = new BasicDBObject()
                    .append("term", termDocument.getKey().term)
                    .append("document", termDocument.getKey().docUrl)
                    .append("termFrequency", termDocument.getValue().size()/(float)documentsSizes.get(termDocument.getKey().docUrl))
                    .append("positions" , termDocument.getValue())
                    .append("inTitle" , inTtitle);
            entries.add(entry);
        }
        	 try {
        	       if(!entries.isEmpty())
        	        	collection.insert(entries);
    	        } catch(Exception e) {}

    }
    
    public void saveImageCollection(Map<String,List<String>> terms, String url){
        DBCollection collection = database.getCollection("Images");
        for (Map.Entry<String,List<String>> entry : terms.entrySet()) {
            List<String> urls = new ArrayList<String>();
            for (int i = 0 ; i < entry.getValue().size() ; i++){
                urls.add(url);
            }
            collection.update(new BasicDBObject("term", entry.getKey()),
                    new BasicDBObject("$push", new BasicDBObject("imageUrl", new BasicDBObject("$each", entry.getValue()))
                            .append("websiteUrl", new BasicDBObject("$each" , urls)))
                    , true
                    , false);
        }
    }


////////////////////////////////////////////////   Ranker	//////////////////////////////////////

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