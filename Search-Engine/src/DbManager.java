import com.mongodb.*;

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
            DBObject term = new BasicDBObject()
                    .append("term", entry.getKey())
                    .append("termFrequency", entry.getValue().size())
                    .append("documents", entry.getValue());

            collection.insert(term);
        }
    }

    public void saveDocumentCollection( Map<termDocumentKey, List<Integer>> terms){
        DBCollection collection = database.getCollection("Document");

        for (Map.Entry<termDocumentKey, List<Integer>>  termDocument : terms.entrySet()) {
            DBObject entery = new BasicDBObject()
                .append("term", termDocument.getKey().term)
                .append("document", termDocument.getKey().docID)
                .append("termDocumentFrequency", termDocument.getValue().size())
                .append("positions" , termDocument.getValue())    ;

             collection.insert(entery);
            
        }
    }
}
