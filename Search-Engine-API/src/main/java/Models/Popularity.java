package Models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PopularityTable")
public class Popularity {
    private String link;
    private double popularity;

    public Popularity() { }

    public Popularity(String url, double pop) {
        this.link = url;
        this.popularity = pop;
    }

    public String getLink() {
        return this.link;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setLink(String url) {
        this.link = url;
    }

    public void setCountry(double pop) {
        this.popularity = pop;
    }

    @Override
    public String toString() {
        return "Popularity [link=" + link +
                ", popularity=" + popularity +"]";
    }

}