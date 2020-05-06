import com.mongodb.*;
import com.mongodb.client.model.Filters;

import java.util.List;
import java.util.Map;

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

    public void saveTermCollection( Map<String , List<Integer>> terms){
        DBCollection collection = database.getCollection("Term");

        for (Map.Entry<String,List<Integer>> entry : terms.entrySet()) {
            System.out.println(entry.getValue());
//            DBObject term = new BasicDBObject()
//                    .append("term", entry.getKey())
//                    .append("termFrequency", entry.getValue().size())
//                    .append("documents", entry.getValue());

            collection.update(new BasicDBObject("term", entry.getKey()),
                    new BasicDBObject("$inc", new BasicDBObject("termFrequency", entry.getValue().size()))
                                      .append("$push" , new BasicDBObject("documents", entry.getValue()))
                                       , true
                                       , false);
            //collection.insert(term);
        }
    }

    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms){
        DBCollection collection = database.getCollection("Document");

        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            DBObject entry = new BasicDBObject()
                    .append("term", termDocument.getKey().term)
                    .append("document", termDocument.getKey().docID)
                    .append("termDocumentFrequency", termDocument.getValue().size())
                    .append("positions" , termDocument.getValue())    ;

            System.out.println(entry);

            collection.update(new BasicDBObject("term", termDocument.getKey().term)
                                                .append("document",termDocument.getKey().docID)
                    , entry
                    , true
                    , false);


             //collection.insert(entery);

        }
    }
}
