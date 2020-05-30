package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import DB.*;

// JAR File is added to ClassPath in Libraries and marked true on order and export 

//mongod --dbpath /var/lib/mongo --logpath /var/log/mongodb/mongod.log --fork

public class CrawlerController implements Runnable {
	public static Map<String, ArrayList<String>> ROBOTS_DISALLOWED;
	public static int NUMBER_OF_WEBSITES;
	public static ArrayList<CrawlerObject> LINKS;
	public static ArrayList<SeedsObject> SEEDS;
	public static ArrayList<String> INITIAL_SEEDS;
	public static int NUMBER_OF_THREADS;
	/* synchronization with Indexer */
	public static AtomicInteger SYNCHRONIZATION;   
	public static int NUM_OF_K_DOCUMENTS = 0;
	
	public CrawlerController(int _numOfThreads, int numOfUrls, AtomicInteger synchronization){
		this.NUMBER_OF_WEBSITES = numOfUrls;
		this.NUMBER_OF_THREADS = _numOfThreads;
		this.SYNCHRONIZATION = synchronization;
	}

	public void Crawl() throws InterruptedException {
		// Creating The Links Container and The Websites That is Disallowed To join
		SEEDS = new ArrayList<SeedsObject>();
		ROBOTS_DISALLOWED = new HashMap<String, ArrayList<String>>();
		LINKS = new ArrayList<CrawlerObject>();
		INITIAL_SEEDS = new ArrayList<String>();
		//////////////////////////////////////Initial Seeds for Crawling///////////////////////////////////////////////////////////////////
		INITIAL_SEEDS.add("https://www.geeksforgeeks.org/greedy-algorithms");
		INITIAL_SEEDS.add("https://www.geeksforgeeks.org/computer-network-tutorials");

		// Loading Previous State Of The Crawler From DataBase
		GetCrawledLinks();
		GetRobots();
		GetSeeds();
		if(SEEDS.size()==0) {
			LoadFirstSeeds();
		}
		if (LINKS.size() == 0) {
			for (int i = 0; i < SEEDS.size(); i++) {
				if (Crawler.CheckRobots(SEEDS.get(i).getLink())) {
					CrawlerObject c = new CrawlerObject();
					c.setNumberOfURLs(0);
					c.setLinkURL(SEEDS.get(i).getLink());
					LINKS.add(c);
				}
			}
		}
		int currentIndex = 0;
		for (int i = 0; i < LINKS.size(); i++) {
			if (!LINKS.get(i).isVisited()) {
				currentIndex = i;
				break;
			}
		}
		if (currentIndex < 0) {
			currentIndex = 0;
		}
		// Crawling New Pages If new Pages are needed more than database or if the
		// database is empty

		// Starting The Threads To Start Crawling
		
		ArrayList<Thread> threads = new ArrayList<Thread>();

		while (LINKS.size() < NUMBER_OF_WEBSITES) {
			threads.clear();
			for (int j = 0; j < NUMBER_OF_THREADS; j++) {
				if (currentIndex < LINKS.size()) {
					CrawlerObject current = LINKS.get(currentIndex);
					Crawler c = new Crawler(current, LINKS, j);
					current.setVisited(true);
					Thread t = new Thread(c);
					threads.add(t);
					t.start();
					currentIndex++;
					
				}

			}

			for (int i = 0; i < threads.size(); i++) {
				threads.get(i).join();
			}
			
			if (threads.size() == 0) {
				break;
			}
		}
		
		synchronized (CrawlerController.SYNCHRONIZATION) {
			CrawlerController.SaveLinks();
			CrawlerController.SaveRobots();
			CrawlerController.NUM_OF_K_DOCUMENTS = 0;
			CrawlerController.SYNCHRONIZATION.incrementAndGet();
			CrawlerController.SYNCHRONIZATION.notifyAll();
		}
		

			// ReCrawlling and ReCrawlling Conditions and Time To Check ReCrawlling
			boolean recrawl = false;
			//ReCrawling Condition if any document of the seeds Changed
			for(int i =0;i<SEEDS.size();i++) {
				Document document = null;
				try {
					document = Jsoup.connect(SEEDS.get(i).getLink()).get();
					if(!document.body().text().equals(SEEDS.get(i).getBody())) {
						recrawl = true;
						
					}
					
				} catch (IOException e) {
					
				}
			}
			
			//ReCrawling
			if(recrawl) {
				System.out.println("Needs To Recrawl");
				LINKS.clear();//Clear Old Crawled and start Recrawling again
				currentIndex = 0;
				for (int i = 0; i < SEEDS.size(); i++) {
					if (Crawler.CheckRobots(SEEDS.get(i).getLink())) {
						CrawlerObject c = new CrawlerObject();
						c.setNumberOfURLs(0);
						c.setLinkURL(SEEDS.get(i).getLink());
						LINKS.add(c);
					}
				}
				
				System.out.println(LINKS.size());
				while (LINKS.size() < NUMBER_OF_WEBSITES) {
					threads.clear();
					for (int j = 0; j < NUMBER_OF_THREADS; j++) {
						if (currentIndex < LINKS.size()) {
							CrawlerObject current = LINKS.get(currentIndex);
							Crawler c = new Crawler(current, LINKS, j);
							current.setVisited(true);
							Thread t = new Thread(c);
							threads.add(t);
							t.start();
							currentIndex++;

						}

					}

					for (int i = 0; i < threads.size(); i++) {
						System.out.println(LINKS.size());
						threads.get(i).join();

					}
					if (threads.size() == 0) {
						break;
					}
				}
				SaveRobots();
				SaveLinks();
				LoadFirstSeeds();
			}
			else {
				System.out.println("Doesnt Need To Recrawl");
				
			}
			
		
	}
	/////////////////////////////////////////////////Create Initial Seeds To The DataBase///////////////////////////////////////////////////////
	public static void LoadFirstSeeds(){
		for(int i =0;i<INITIAL_SEEDS.size();i++) {
			Document document = null;
			try {
				document = Jsoup.connect(INITIAL_SEEDS.get(i)).get();
				SEEDS.add(new SeedsObject(INITIAL_SEEDS.get(i),document.body().text()));
			} catch (IOException e) {
				
			}
		}
		SaveSeeds();
		
	}
	/////////////////////////////////////////////////SAVE Items To The DataBase ////////////////////////////////////////////////////////////////

	public static void SaveRobots() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveRobot(ROBOTS_DISALLOWED);
	}

	public static void SaveLinks() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveCrawler(LINKS);
	}
	
	public static void SaveSeeds() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveSeeds(SEEDS);
	}
	/////////////////////////////////////////////Load Items From DataBase/////////////////////////////////////////////////////////////////////////

	public static void GetRobots() {
		DbManager DBManger = DbManager.getInstance();
		DBCollection RobotsDB = DBManger.getRobots().getCollection();
		Iterator<DBObject> objects = RobotsDB.find().iterator();

		while (objects.hasNext()) {
			Map robotFromDB = objects.next().toMap();
			String linkName = (String) robotFromDB.get("Link");
			ArrayList<String> disallowed = (ArrayList<String>) robotFromDB.get("Disallowed");

			ROBOTS_DISALLOWED.put(linkName, disallowed);
		}
	}

	public static void GetCrawledLinks() {
		DbManager DBManger = DbManager.getInstance();
		DBCollection crawled = DBManger.getCrawledLinks().getCollection();
		Iterator<DBObject> objects = crawled.find().iterator();

		while (objects.hasNext()) {
			Map crawledFromDB = objects.next().toMap();
			String linkName = (String) crawledFromDB.get("linkURL");
			ArrayList<String> sourceLinksArray = (ArrayList<String>) crawledFromDB.get("pointingLinks");
			HashSet<String> sourceLinks = new HashSet<String>(sourceLinksArray);

			int numberOfLinks = (int) crawledFromDB.get("numberOfURLs");
			
			boolean Visited = (boolean) crawledFromDB.get("visited");
			CrawlerObject c = new CrawlerObject(linkName, sourceLinks, numberOfLinks, Visited);
			LINKS.add(c);
		}
	}
	
	public static void GetSeeds() {
		DbManager DBManger = DbManager.getInstance();
		DBCollection SeedsDB = DBManger.getSeeds().getCollection();
		Iterator<DBObject> objects = SeedsDB.find().iterator();

		while (objects.hasNext()) {
			Map SeedFromDB = objects.next().toMap();
			String linkName = (String) SeedFromDB.get("Link");
			String content  = (String) SeedFromDB.get("Content");

			SEEDS.add(new SeedsObject(linkName,content));
		}
	}
	
	@Override
	public void run() {
		try {
			this.Crawl();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class Crawler implements Runnable {
	private ArrayList<CrawlerObject> links;

	private CrawlerObject mCrawlerObj;
	private int ThreadNumber;

	public Crawler(CrawlerObject mCrawlerObj, ArrayList<CrawlerObject> lINKS2, int ThreadNumber) {
		this.links = lINKS2;
		this.mCrawlerObj = mCrawlerObj;
		this.ThreadNumber = ThreadNumber;

	}
	////////////////////////////////////////////////////// Add Website That Cites
	////////////////////////////////////////////////////// another Website
	////////////////////////////////////////////////////// /////////////////////////////////////////////////////////

	public void AddRefer(String elementURL) {
		for (int i = 0; i < links.size(); i++) {
			CrawlerObject temp = links.get(i);
			if (temp.getLinkURL().equals(elementURL)) {
				temp.getPointingLinks().add(mCrawlerObj.getLinkURL());

			}

		}
	}
	//////////////////////////////////////////To Get Domain Name From Given URL ////////////////////////////////////////////////////////////
	public static String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    String domain = uri.getHost();
	   // return domain.startsWith("www.") ? domain.substring(4) : domain;
	    return domain;
	}

	/////////////////////////////////////////////////// Check if the url already
	/////////////////////////////////////////////////// exists so not to crawl it
	/////////////////////////////////////////////////// again///////////////////////////////////

	public boolean CheckExist(String URL) {
		for (int i = 0; i < links.size(); i++) {
			if(URL.contains("//")) {
				URL=URL.split("//")[1];
			}
			
			String comp = links.get(i).getLinkURL();
			if(comp.contains("//")) {
				comp=comp.split("//")[1];
				
			}
			if (comp.equals(URL)) {
				return true;
			}
		}
		return false;
	}
	///////////////////////////////////////////////////////// Crawl Current
	///////////////////////////////////////////////////////// URL///////////////////////////////////////////////////////////////////////

	private void getPageLinks(String URL) {

		Document document;
		try {
			document = Jsoup.connect(URL).get();
			
			
			Elements linksOnPage = document.select("a[href]");
			mCrawlerObj.setNumberOfURLs(linksOnPage.size());
			

			for (Element page : linksOnPage) {
				//To Check If 2 differinets   urls have the same website link 
				String toBeAddedUrl = page.attr("abs:href");
				if(toBeAddedUrl.contains("#")) {
					toBeAddedUrl = toBeAddedUrl.split("/#")[0];
		    		
		    	}
				if (toBeAddedUrl.endsWith("/")) {
					toBeAddedUrl = toBeAddedUrl.substring(0, toBeAddedUrl.length() - 1);

				}
				//To Check If The Content is Html page 
				
				if(toBeAddedUrl.contains("png")||toBeAddedUrl.contains("jpg")||toBeAddedUrl.contains("svg")||toBeAddedUrl.contains("jpeg")||toBeAddedUrl.contains("pdf")) {
					continue;
					
				}
				
				//Start Adding to Database and Links 

				synchronized (links) {
					AddRefer(page.attr("abs:href"));
					if (CheckRobots(page.attr("abs:href")) && !CheckExist(toBeAddedUrl)
							&& links.size() < CrawlerController.NUMBER_OF_WEBSITES ) {
						CrawlerObject toBeAdded = new CrawlerObject();
						toBeAdded.setLinkURL(toBeAddedUrl);
						links.add(toBeAdded);
						CrawlerController.NUM_OF_K_DOCUMENTS = CrawlerController.NUM_OF_K_DOCUMENTS + 1;

						synchronized (CrawlerController.SYNCHRONIZATION) {
							if(CrawlerController.NUM_OF_K_DOCUMENTS >= 1000) {
								CrawlerController.SaveLinks();
								CrawlerController.SaveRobots();
								CrawlerController.NUM_OF_K_DOCUMENTS = 0;
								CrawlerController.SYNCHRONIZATION.incrementAndGet();
								CrawlerController.SYNCHRONIZATION.notifyAll();
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

	}

	////////////////////////////////////////// Check If Its Allowed To Crawl This
	////////////////////////////////////////// Link or Not From The Robot File
	////////////////////////////////////////// //////////////////////////////////////

	public static boolean CheckRobots(String URL) {
		////////////////////////////////// Filter The URL to its basic website
		////////////////////////////////// ////////////////////////////////////
		/*
		 * String[] temp = URL.split("/"); String filteredURL = null; if
		 * ((temp[0].equals("https:") || temp[0].equals("http:"))) { String[] secondTemp
		 * = temp[2].split("\\.");
		 * 
		 * if (secondTemp[0].equals("www")) { filteredURL = temp[0] + "//" + temp[2] +
		 * "/robots.txt";
		 * 
		 * } else { filteredURL = "https://www." + secondTemp[1] + ".com/robots.txt";
		 * 
		 * }
		 * 
		 * } else { filteredURL = temp[0] + "/robots.txt";
		 * 
		 * }
		 */
		String filteredURL = null;
		try {
			filteredURL = "https://"+getDomainName(URL)+"/robots.txt";
		} catch (URISyntaxException e2) {
			
		}
		////////////////////////////////////// NEW Robot File Need To be Fetched
		////////////////////////////////////// //////////////////////////////////

		if (!CrawlerController.ROBOTS_DISALLOWED.containsKey(filteredURL)) {
			ArrayList<String> disallowed = new ArrayList<String>();
			

			try {
				
				URL url = new URL(filteredURL);

				
				URLConnection urlcon = url.openConnection();
				InputStream stream = urlcon.getInputStream();
			
					
				
				Scanner sc = new Scanner(stream);
				boolean foundUser = false;

				while (sc.hasNextLine()) {
					String line = sc.nextLine();

					if (line.equalsIgnoreCase("User-agent: *")) {
						foundUser = true;

					} else if (foundUser) {
						if (line.contains("User-agent")) {
							break;
						}
						String[] slashes = line.split(" ");

						if (slashes[0].equalsIgnoreCase("Disallow:")) {

							disallowed.add(filteredURL.replace("/robots.txt", "") + slashes[1]);
						}

					}
				}
				CrawlerController.ROBOTS_DISALLOWED.put(filteredURL, disallowed);

			} catch (Exception e) {
				CrawlerController.ROBOTS_DISALLOWED.put(filteredURL, disallowed);
			}
		}

		/////////////////////////////// Now we have the Robot File Start Searching if
		/////////////////////////////// its allowed or Not///////////////
		ArrayList<String> searchInside = CrawlerController.ROBOTS_DISALLOWED.get(filteredURL);
		for (int i = 0; i < searchInside.size(); i++) {
			if (URL.contains(searchInside.get(i)) && !searchInside.get(i).contains("*")) {// In Case Doesn't have to
																							// match Two Strings
				return false;

			} else if (searchInside.get(i).contains("*")) {
				String tempMatch = searchInside.get(i);
				tempMatch = tempMatch.replace("*", "(.*)");
				String tempURLMatch = URL;
				if (URL.endsWith("/")) {
					tempURLMatch = URL.substring(0, URL.length() - 1);

				}
				if (tempMatch.endsWith("/")) {
					tempMatch = tempMatch.substring(0, tempMatch.length() - 1);

				}
				if (tempURLMatch.matches(tempMatch)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void run() {
		getPageLinks(mCrawlerObj.getLinkURL());

	}

}