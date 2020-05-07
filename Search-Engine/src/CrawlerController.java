import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class CrawlerController {
	public static 	Map<String, ArrayList<String>> robotsDisallowed;
	public static 	Map<String, HashSet<String>> Refer;
	public static int NumberOfWebsites;

	public static void main(String[] args) throws InterruptedException {
		// Creating The Links Container and The Websites That is Disallowed To join
		ArrayList<String> links = new ArrayList<String>();
		ArrayList<String> seeds = new ArrayList<String>();
		robotsDisallowed = new HashMap<String, ArrayList<String>>();
		Refer = new HashMap<String, HashSet<String>>();
	    seeds.add("https://www.geeksforgeeks.org/greedy-algorithms");
	    seeds.add("https://www.geeksforgeeks.org/computer-network-tutorials");
		for(int i =0;i<seeds.size();i++) {
			if(Crawler.CheckRobots(seeds.get(i))) {
				links.add(seeds.get(i));
			}
		}
		
	
		// Starting The Threads To Start Crawling
		int numberOfThreads = 5;
		NumberOfWebsites = 1000;
		int currentIndex = 0;
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		while (links.size() < NumberOfWebsites) {
			threads.clear();
			for (int j = 0; j < numberOfThreads; j++) {
				if(currentIndex<links.size()) {
					Crawler c = new Crawler(links.get(currentIndex),links,j);
					Thread t = new Thread(c);
					threads.add(t);
					t.start();
					currentIndex++;
					
				}
				

			}
			for(int i =0;i<threads.size();i++) {
				threads.get(i).join();
			}
			if(threads.size()==0) {
				break;
			}
			
		}
		/*
		 * System.out.print(links.size());
		 * 
		 * for (Entry<String, HashSet<String>> entry : Refer.entrySet()) {
		 * if(!entry.getKey().equals("https://www.geeksforgeeks.org/")) { continue; }
		 * System.out.println(entry.getKey() + " = " ); HashSet printTemp =
		 * entry.getValue(); Iterator value = printTemp.iterator(); while
		 * (value.hasNext()) { System.out.println(value.next()); }
		 * System.out.println("FINISHED ITeration");
		 * 
		 * }
		 */
		 
		
		
	

	}

}

class Crawler implements Runnable {
	private ArrayList<String> links;
	
	private String MyURL;
	private int ThreadNumber;
	

	public Crawler(String URL, ArrayList<String> links,int ThreadNumber) {
		this.links = links;
		MyURL = URL;
		this.ThreadNumber = ThreadNumber;
		
	}

	private void getPageLinks(String URL) {
		// 4. Check if you have already crawled the URLs
		// (we are intentionally not checking for duplicate content in this example)
		//boolean process = false;
		
		/*
		 * synchronized (links) { if (!links.contains(URL)) {
		 * 
		 * // 4. (i) If not add it to the index if (links.add(URL)) { //
		 * System.out.println(URL + "   " + ThreadNumber); process = true; }
		 * 
		 * // 2. Fetch the HTML code
		 * 
		 * }
		 * 
		 * }
		 */
		//if (process) {
			Document document;
			try {
				document = Jsoup.connect(URL).get();
				// 3. Parse the HTML to extract links to other URLs
				Elements linksOnPage = document.select("a[href]");

				// 5. For each extracted URL... go back to Step 4.
				
					for (Element page : linksOnPage) {
						
						synchronized (links) {
							if(!CrawlerController.Refer.containsKey(page.attr("abs:href"))) {//New Element doesn't have Refer Before 
								HashSet<String> tempRefer = new HashSet<String>();
								tempRefer.add(URL);
								CrawlerController.Refer.put(page.attr("abs:href"), tempRefer);
								
								
							}
							else {
								CrawlerController.Refer.get(page.attr("abs:href")).add(URL);
								
							}
						if (CheckRobots(page.attr("abs:href"))&&!links.contains(page.attr("abs:href"))&&links.size()<CrawlerController.NumberOfWebsites) {
							links.add(page.attr("abs:href"));
							
							//System.out.println(page.attr("abs:href")+" "+ThreadNumber);

						}
						}

					}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}

	}

	public static boolean CheckRobots(String URL) {
		////////////////////////////////// Filter The URL to its basic website
		////////////////////////////////// ////////////////////////////////////
		String[] temp = URL.split("/");
		String filteredURL = null;
		if ((temp[0].equals("https:") || temp[0].equals("http:"))) {
			String[] secondTemp = temp[2].split("\\."); 
			
			if(secondTemp[0].equals("www")) {
				filteredURL = temp[0] + "//" + temp[2] + "/robots.txt";
			}
			else {
				filteredURL = "https://www."+secondTemp[1]+".com/robots.txt";
			}
			
			

		} else {
			filteredURL = temp[0] + "/robots.txt";
		}
		////////////////////////////////////// NEW Robot File Need To be Fetched
		////////////////////////////////////// //////////////////////////////////
		

		if (!CrawlerController.robotsDisallowed.containsKey(filteredURL)) {
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
							
							
							disallowed.add(filteredURL.replace("/robots.txt", "")+ slashes[1]);
						}

					}
				}
				CrawlerController.robotsDisallowed.put(filteredURL, disallowed);

			} catch (Exception e) {
				CrawlerController.robotsDisallowed.put(filteredURL, disallowed);
				
			}
		}
		
		/////////////////////////////// Now we have the Robot File Start Searching if
		/////////////////////////////// its allowed or Not///////////////
		ArrayList<String> searchInside = CrawlerController.robotsDisallowed.get(filteredURL);
		for (int i = 0; i < searchInside.size(); i++) {
			if (URL.contains(searchInside.get(i))&&!searchInside.get(i).contains("*")) {//In Case Doesn't have to match Two Strings 
				return false;

			}
			else if (searchInside.get(i).contains("*")) {
				String tempMatch = searchInside.get(i);
				tempMatch = tempMatch.replace("*", "(.*)");
				String tempURLMatch = URL;
				if(URL.endsWith("/")) {
					tempURLMatch = URL.substring(0, URL.length() - 1);
					
				}
				if(tempMatch.endsWith("/")) {
					tempMatch = tempMatch.substring(0, tempMatch.length() - 1);
					
				}
				if(tempURLMatch.matches(tempMatch)) {
					return false;
				}
				
			}
		}

		return true;

	}

	@Override
	public void run() {

		getPageLinks(MyURL);

	}

}