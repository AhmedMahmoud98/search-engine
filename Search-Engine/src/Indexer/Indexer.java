package Indexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DB.DbManager;

public class Indexer implements Runnable {

    private final int NUMBER_OF_THREAD = 10;
    private List<String> documentsURLs;
	/* synchronization with Crawler, It indicates number of k documents that don't indexed yet */
	public AtomicInteger SYNCHRONIZATION;  
	public AtomicInteger STOP;  

    public Indexer(AtomicInteger synchronization,AtomicInteger stop) {
        documentsURLs = new ArrayList<String>();
        this.SYNCHRONIZATION = synchronization;
        this.STOP = stop;
    }

    public void getDocumentsURLs(int startingIndex) throws IOException {
    	DbManager DBManager = DbManager.getInstance();
    	documentsURLs =  DBManager.getCrawledUrls(startingIndex);
    	System.out.println(documentsURLs.size());
    }
    
	@Override
	public void run() {
		try {
			this.constructIndex();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

    public void constructIndex() throws InterruptedException, IOException {
    	int Count = 0;
    	while(true)
    	{
    		synchronized (this.SYNCHRONIZATION) {
    			if(this.SYNCHRONIZATION.get() == 0) {
    				if(STOP.get()==-1) {
    					return;
    				}
    				this.SYNCHRONIZATION.wait();
    			}
    			
    		}
	        getDocumentsURLs(Count);
	       
	        
	        final int DOCUMENTS_PER_THREAD = (int) Math.ceil(documentsURLs.size() / (float)NUMBER_OF_THREAD);
	        long timeBefore = 0, timeAfter= 0, IndexingTime = 0;
	        timeBefore = System.currentTimeMillis();
	        
	        List<Thread> threads = new ArrayList<Thread>();
	        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
	            final int startIndex = i * DOCUMENTS_PER_THREAD;
	            final int endIndex = Math.min(documentsURLs.size(), (i + 1) * DOCUMENTS_PER_THREAD);
	            Thread indx = new Thread(new IndexerThread(documentsURLs, startIndex, endIndex, Count));
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
	        this.SYNCHRONIZATION.decrementAndGet();
	        Count += 1;
    	}
    }


}
