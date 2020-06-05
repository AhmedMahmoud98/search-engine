package Main;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import Crawler.CrawlerController;
import DB.DbManager;
import Indexer.Indexer;
import Ranker.PageRank;

import java.util.logging.Level;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException{
    	String toBeAddedUrl = "www.facebook.com/Aly/Ramzy/content#reg";

    	if(toBeAddedUrl.contains("#")) {
			int index = toBeAddedUrl.lastIndexOf("/");
			toBeAddedUrl = toBeAddedUrl.substring(0,index);
			System.out.println(toBeAddedUrl);
    	}
    		
        /* Remove Mongo Logging 
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        
        DbManager DBManager = DbManager.getInstance();
        DBManager.dropTables();
		DBManager.addTablesIndices();
		
        AtomicInteger synchronization = new AtomicInteger();

        CrawlerController _crawler = new CrawlerController(5, 5000, synchronization);

        Thread crawler = new Thread(_crawler);
        crawler.start();
        Indexer _indexer = new Indexer(synchronization);
        Thread indexer = new Thread(_indexer);
        indexer.start();

        crawler.join();
        PageRank _Ranker = new PageRank(3, 0.7);
        PageRank.main(new String[]{""});*/
    }
}
