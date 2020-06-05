package PerformanceAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import Crawler.CrawlerController;
import DB.DbManager;
import Indexer.Indexer;
import Ranker.PageRank;

public class PerformanceController {
		static AtomicInteger counter = new AtomicInteger();
		static PrintWriter  out;
		static int numberOfRequests = 150;
		
		static String Connection = "http://localhost:8080/api/Pages?query=salah&country=eg&pageNumber=1";
	 public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		 out = new PrintWriter("Performance.txt");
		 
		 TestCrawlerIndexerTableSize();
		 System.out.println();
		 out.println();
		 TestLatencyOfRequests();
		 System.out.println();
		 out.println();
		 TestNumberOfRequests();
		 out.println();
		 System.out.println();
		
		 
		
		 out.close();
	 }
	 public static void TestCrawlerIndexerTableSize() throws InterruptedException {
		 out.println("Testing Request Latency With Different Sizes For Crawler And Indexer Tables...");
		 System.out.println("Testing Request Latency With Different Sizes For Crawler And Indexer Tables...");
		 ArrayList<String> search= new ArrayList<String>();
		 //search.add("http://localhost:8080/api/Pages?query=salah&country=eg&pageNumber=1");
		 //search.add("http://localhost:8080/api/Pages?query=egypt&country=eg&pageNumber=1");
		 //search.add("http://localhost:8080/api/Pages?query=ahly&country=eg&pageNumber=1");
		 SendRequestsSync sr =new SendRequestsSync(Connection,counter,false);
		 sr.run();//Call using current Process not new thread to avoid the overhead of threading 
	       
			for(int i = 500;i<=2000;i+=500) {
				/* Remove Mongo Logging */
		        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
				 DbManager DBManager = DbManager.getInstance();
			     DBManager.dropTables();
			     DBManager.addTablesIndices();
				 AtomicInteger synchronization = new AtomicInteger();
				 AtomicInteger stop = new AtomicInteger();
			     CrawlerController _crawler = new CrawlerController(5, i, synchronization,stop);
			     Thread crawler = new Thread(_crawler);
			     crawler.start();
			     Indexer _indexer = new Indexer(synchronization,stop);
			     Thread indexer = new Thread(_indexer);
			     indexer.start();
			     crawler.join();
			     indexer.join();
			     System.out.println("Here");
			     double timeBefore = 0, timeAfter= 0,Time=0;
				  sr =new SendRequestsSync(Connection,counter,false);
				 timeBefore = System.currentTimeMillis();
				 sr.run();//Call using current Process not new thread to avoid the overhead of threading 
				 timeAfter = System.currentTimeMillis();
				 Time = timeAfter - timeBefore;
				 System.out.println("Without Ranking Process");
				 out.println("Without Ranking Process");
				 System.out.println("Size of The Crawler Table is : "+DBManager.getCrawlerTableSize()+"  ,And Size of The Indexer Table is : "+DBManager.getIndexerTableSize()+"  ,And The Time For Response is : "+Time+" ms");
				 out.println("Size of The Crawler Table is : "+DBManager.getCrawlerTableSize()+"  ,And Size of The Indexer Table is : "+DBManager.getIndexerTableSize()+"  ,And The Time For Response is : "+Time+" ms");
				 PageRank _Ranker = new PageRank(3, 0.7);
			     PageRank.main(new String[]{""});
			     timeBefore = 0;
			     timeAfter= 0;
			     Time=0;
				 sr =new SendRequestsSync(Connection,counter,false);
				 timeBefore = System.currentTimeMillis();
				 sr.run();//Call using current Process not new thread to avoid the overhead of threading 
				 timeAfter = System.currentTimeMillis();
				 Time = timeAfter - timeBefore;
				 System.out.println("With Ranking Process");
				 out.println("With Ranking Process");
				 System.out.println("Size of The Crawler Table is : "+DBManager.getCrawlerTableSize()+"  ,And Size of The Indexer Table is : "+DBManager.getIndexerTableSize()+"  ,And The Time For Response is : "+Time+" ms");
				 out.println("Size of The Crawler Table is : "+DBManager.getCrawlerTableSize()+"  ,And Size of The Indexer Table is : "+DBManager.getIndexerTableSize()+"  ,And The Time For Response is : "+Time+" ms");
				
				
			}
			
	       

	        
	        

	       
	        
		  
	 }
	 public static void TestLatencyOfRequests()  throws InterruptedException{
		 out.println("Testing Latency While Increasing Number Of Requests...");
		 System.out.println("Testing Latency While Increasing Number Of Requests...");
		 for(int j = 0;j<20;j++) {
			 double timeBefore = 0, timeAfter= 0,Time=0;
			
			 counter.set(0);
			 ArrayList<Thread> threads =new ArrayList<Thread>();
			for(int i=0;i<j*10;i++) {
				SendRequestsSync sr =new SendRequestsSync(Connection,counter,false);
				Thread t = new Thread(sr);
				t.start();
				threads.add(t);
			}
			Thread.sleep(500);
			timeBefore = System.currentTimeMillis();
			SendRequestsSync sr =new SendRequestsSync(Connection,counter,false);
			sr.run();//Call using current Process not new thread to avoid the overhead of threading 
			 timeAfter = System.currentTimeMillis();
			 Time = timeAfter - timeBefore;
			
		
			 System.out.println(j*10 +"  Request Sent Recived Response in Time =  : "+ Time+" ms");
			 out.println(j*10 +"  Request Sent Recived Response in Time =  : "+ Time+" ms");
			 System.out.println("Waiting For API To Recover");
			 Thread.sleep(500);
			
		 }
		 Thread.sleep(50000);
	 }
	 public static void TestNumberOfRequests() throws InterruptedException {
		 out.println("Testing Number Of Requests...");
		 System.out.println("Testing Number Of Requests...");
		 for(int j=0;j<=numberOfRequests;j+=10) {
			 
		 
		 counter.set(0);
		 
			 
			ArrayList<Thread> threads =new ArrayList<Thread>();
			for(int i=0;i<j;i++) {
				SendRequestsSync sr =new SendRequestsSync(Connection,counter,true);
				Thread t = new Thread(sr);
				t.start();
				threads.add(t);
			}
			
			for(int i =0;i<threads.size();i++) {
				threads.get(i).join();
			}
			 out.println("Number OF Sent Request : "+j+"  , Got Responses For : "+counter.get());
			 System.out.println("Number OF Sent Request : "+j+"  , Got Responses For : "+counter.get());
			 System.out.println("Waiting For API To Recover");
			 Thread.sleep(3000);
		 }
			
			
		 
	 }
	 
}
