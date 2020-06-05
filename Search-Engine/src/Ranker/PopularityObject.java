package Ranker;

import java.util.HashSet;

public class PopularityObject {
    private HashSet<String> pointingLinks;
    private int numberOfURLs;

    public PopularityObject(HashSet<String> pointingLinks, int numberOfURLs) {
        super();
        this.pointingLinks = pointingLinks;
        this.numberOfURLs = numberOfURLs;
    }

    public PopularityObject() {
        pointingLinks = new HashSet<String>();
        numberOfURLs = -1;
    }

    public HashSet<String> getPointingLinks() {
        return pointingLinks;
    }

    public void setPointingLinks(HashSet<String> pointingLinks) {
        this.pointingLinks = pointingLinks;
    }

    public int getNumberOfURLs() {
        return numberOfURLs;
    }

    public void setNumberOfURLs(int numberOfURLs) {
        this.numberOfURLs = numberOfURLs;
    }
}
