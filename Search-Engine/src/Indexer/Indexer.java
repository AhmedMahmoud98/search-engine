package Indexer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Indexer {

    private final int DOCUMENTS_PER_THREAD = 500;
    private Map<Integer, String> documentsURLs;


    public Indexer() {
        documentsURLs = new LinkedHashMap<Integer, String>();
    }

    public void getDocumentsURLs() throws IOException {
        /* TODO : GET Documents URLS From DB */
    	
    	File file = new File("../urls.txt");   
    	Scanner sc = new Scanner(file , "UTF-8");     //file to be scanned
    	int temp = 0;  
    	
    	while(sc.hasNextLine())  
    	{  
    		documentsURLs.put(temp, sc.nextLine());
    		temp++;
    	}
    }

    public void constructIndex() throws InterruptedException, IOException {
        getDocumentsURLs();
        
        final int numOfThreads = (int) Math.ceil(documentsURLs.size() / (float)DOCUMENTS_PER_THREAD);
        final Map.Entry<Integer, String>[] documentsEntries =
                (Map.Entry<Integer, String>[]) documentsURLs.entrySet().toArray(new Map.Entry[documentsURLs.size()]);
        
        long timeBefore = 0, timeAfter= 0, IndexingTime = 0;
        timeBefore = System.currentTimeMillis();
        
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < numOfThreads; i++) {
            final int startIndex = i * DOCUMENTS_PER_THREAD;
            final int endIndex = Math.min(documentsURLs.size(), (i + 1) * DOCUMENTS_PER_THREAD);
            Thread indx = new Thread(new IndexerThread(documentsEntries, startIndex, endIndex));
            indx.start();
            threads.add(indx);
        }
        
        for (Thread thread : threads)
        	thread.join();
        
        timeAfter = System.currentTimeMillis();
        IndexingTime = timeAfter - timeBefore;
        System.out.print(String.format("Indexing done at: %d s \n", IndexingTime/1000));
        
        threads.clear();
        documentsURLs.clear();
    }
}
