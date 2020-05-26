import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import DB.DbManager;
import Indexer.Indexer;
import Ranker.PageRank;

import java.util.logging.Level;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException{

        /* Remove Mongo Logging */
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        
        //Map<Integer, String> URLs = new LinkedHashMap<Integer, String>();
        
        DbManager db = new DbManager();
        Indexer ind = new Indexer();
        ind.constructIndex();
        //PageRank Ind = new PageRank(3, 0.7);
        //PageRank.main(new String[]{""});

    }
}
