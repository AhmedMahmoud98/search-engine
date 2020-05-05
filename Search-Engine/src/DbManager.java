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
                    .append("documents", entry.getValue());

            collection.insert(term);
        }

    }

    public void saveDocumentCollection( Map<String , Map<Integer, List<Integer>>> terms){
        DBCollection collection = database.getCollection("Document");

        for (Map.Entry<String ,Map<Integer, List<Integer>>>  term : terms.entrySet()) {
            for (Map.Entry<Integer ,List<Integer>>  document : term.getValue().entrySet()) {
                DBObject entery = new BasicDBObject()
                    .append("term", term.getKey())
                    .append("document", document.getValue())
                    .append("positions" , document.getValue())    ;

                 collection.insert(entery);
            }
        }

    }
}
