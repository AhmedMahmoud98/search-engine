package Ranker;

import DB.DbManager;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.lang.reflect.Array;
import java.util.*;
import java.io.IOException;

import static Crawler.CrawlerController.GetCrawledLinks;

public class PageRank {
    private static int iterations;
    private static Map<String, PopularityObject> LINKS;
    private static Map<String, Double> pageRank;
    private static double dampingFactor;

    public PageRank(int maxIter, double dampFact) {
        iterations = maxIter;
        dampingFactor = dampFact;
        LINKS = new HashMap<String, PopularityObject>();
        pageRank = new HashMap <String, Double>();
    }

    public static void main(String[] args) {

        SetLINKS();
        // System.out.println(LINKS.get(0));
        Map<String, Double> tempPageRank = new HashMap<String, Double>();
        double init = 1.0 / LINKS.size();
        // System.out.println(LINKS.size());
        LINKS.forEach((k, v) -> pageRank.put(k, init));
        LINKS.forEach((k, v) -> tempPageRank.put(k, init));

        // System.out.println(pageRank.get("https://www.geeksforgeeks.org/greedy-algorithms"));
        // System.out.println(iterations + dampingFactor + tolerence);

        double dampingOffset = (1 - dampingFactor) / LINKS.size();
        String url;
        String pointingUrl;
        HashSet<String> pointingLinks;
        double temp;
        for (int i=0; i<iterations; i++){
            Iterator linksIter = LINKS.entrySet().iterator();
            while (linksIter.hasNext()){
                temp = 0;
                Map.Entry linkMap = (Map.Entry) linksIter.next();
                url = (String) linkMap.getKey();
                pointingLinks = ((PopularityObject) linkMap.getValue()).getPointingLinks();
                Iterator<String> itr = pointingLinks.iterator();

                while(itr.hasNext()){
                    pointingUrl = itr.next();
                    if(url.equals(pointingUrl)){
                        continue;
                    }
                    temp += pageRank.get(pointingUrl) / LINKS.get(pointingUrl).getNumberOfURLs();
                }
                temp = dampingOffset + dampingFactor * temp;
                tempPageRank.put(url, temp);
            }
            Swap(tempPageRank);
        }
        SavePageRank();
    }

    private static void Swap(Map<String, Double> tempPageRank) {
        pageRank.forEach((key, v) -> pageRank.put(key, tempPageRank.get(key)));
    }

    private static void SavePageRank(){
        DbManager DBManager = DbManager.getInstance();
        DBManager.savePageRank(pageRank);
    }
    private static void SetLINKS() {
        DbManager DBManger = DbManager.getInstance();
        DBCollection crawled = DBManger.getCrawledLinks().getCollection();
        Iterator<DBObject> objects = crawled.find().iterator();

        while (objects.hasNext()) {
            Map crawledFromDB = objects.next().toMap();
            String linkName = (String) crawledFromDB.get("Link");
            ArrayList<String> sourceLinksArray = (ArrayList<String>) crawledFromDB.get("Source");
            HashSet<String> sourceLinks = new HashSet<String>(sourceLinksArray);

            int numberOfLinks = (int) crawledFromDB.get("Number Of Links");
            PopularityObject c = new PopularityObject(sourceLinks, numberOfLinks);
            LINKS.put(linkName, c);

        }
    }
}
