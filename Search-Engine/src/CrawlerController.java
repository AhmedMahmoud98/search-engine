import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

// JAR File is added to ClassPath in Libraries and marked true on order and export 

//mongod --dbpath /var/lib/mongo --logpath /var/log/mongodb/mongod.log --fork

public class CrawlerController {
	public static Map<String, ArrayList<String>> ROBOTS_DISALLOWED;
	public static int NUMBER_OF_WEBSITES;
	public static ArrayList<CrawlerObject> LINKS;

	public static void main(String[] args) throws InterruptedException {
		// Creating The Links Container and The Websites That is Disallowed To join
		ArrayList<String> seeds = new ArrayList<String>();
		ROBOTS_DISALLOWED = new HashMap<String, ArrayList<String>>();
		LINKS = new ArrayList<CrawlerObject>();
		seeds.add("https://www.geeksforgeeks.org/greedy-algorithms");
		seeds.add("https://www.geeksforgeeks.org/computer-network-tutorials");

		//Loading Previous State Of The Crawler From DataBase 
		GetCrawledLinks();
		GetRobots();
		if (LINKS.size() == 0) {
			for (int i = 0; i < seeds.size(); i++) {
				if (Crawler.CheckRobots(seeds.get(i))) {
					CrawlerObject c = new CrawlerObject();
					c.setNumberOfURLs(0);
					c.setLinkURL(seeds.get(i));
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
		//Crawling New Pages If new Pages are needed more than database or if the database is empty 

		// Starting The Threads To Start Crawling
		int numberOfThreads = 5;
		NUMBER_OF_WEBSITES = 1200;

		ArrayList<Thread> threads = new ArrayList<Thread>();

		while (LINKS.size() < NUMBER_OF_WEBSITES) {
			threads.clear();
			for (int j = 0; j < numberOfThreads; j++) {
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
		//Save your Output To DataBase
		SaveRobots();
		SaveLinks();
		//ReCrawlling  and  ReCrawlling Conditions and Time To Check ReCrawlling
		
		

		

	}

	public static void SaveRobots() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveRobot(ROBOTS_DISALLOWED);
	}

	public static void SaveLinks() {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveCrawler(LINKS);
	}

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
			String linkName = (String) crawledFromDB.get("Link");
			ArrayList<String> sourceLinksArray = (ArrayList<String>) crawledFromDB.get("Source");
			HashSet<String> sourceLinks = new HashSet<String>(sourceLinksArray);

			int numberOfLinks = (int) crawledFromDB.get("Number Of Links");
			int index = (int) crawledFromDB.get("CrawledIndex");
			boolean Visited = (boolean) crawledFromDB.get("Visited");
			CrawlerObject c = new CrawlerObject(linkName, sourceLinks, numberOfLinks, Visited);
			LINKS.add(c);

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
	//////////////////////////////////////////////////////Add Website That Cites another Website /////////////////////////////////////////////////////////

	public void AddRefer(String elementURL) {
		for (int i = 0; i < links.size(); i++) {
			CrawlerObject temp = links.get(i);
			if (temp.getLinkURL().equals(elementURL)) {
				temp.getPointingLinks().add(mCrawlerObj.getLinkURL());

			}

		}
	}

	/////////////////////////////////////////////////// Check if the url already exists so not to crawl it again///////////////////////////////////
	
	public boolean CheckExist(String URL) {
		for (int i = 0; i < links.size(); i++) {
			if (links.get(i).getLinkURL().equals(URL)) {
				return true;
			}
		}
		return false;
	}
	/////////////////////////////////////////////////////////Crawl Current URL///////////////////////////////////////////////////////////////////////

	private void getPageLinks(String URL) {
		
		Document document;
		try {
			document = Jsoup.connect(URL).get();
			
			Elements linksOnPage = document.select("a[href]");
			mCrawlerObj.setNumberOfURLs(linksOnPage.size());

			

			for (Element page : linksOnPage) {

				synchronized (links) {
					AddRefer(page.attr("abs:href"));
					if (CheckRobots(page.attr("abs:href")) && !CheckExist(page.attr("abs:href"))
							&& links.size() < CrawlerController.NUMBER_OF_WEBSITES) {
						CrawlerObject toBeAdded = new CrawlerObject();
						toBeAdded.setLinkURL(page.attr("abs:href"));
						links.add(toBeAdded);

						

					}
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

	}
	
	
	//////////////////////////////////////////Check If Its Allowed To Crawl This Link or Not From The Robot File //////////////////////////////////////

	public static boolean CheckRobots(String URL) {
		////////////////////////////////// Filter The URL to its basic website
		////////////////////////////////// ////////////////////////////////////
		String[] temp = URL.split("/");
		String filteredURL = null;
		if ((temp[0].equals("https:") || temp[0].equals("http:"))) {
			String[] secondTemp = temp[2].split("\\.");

			if (secondTemp[0].equals("www")) {
				filteredURL = temp[0] + "//" + temp[2] + "/robots.txt";

			} else {
				filteredURL = "https://www." + secondTemp[1] + ".com/robots.txt";

			}

		} else {
			filteredURL = temp[0] + "/robots.txt";

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