package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Trends")
public class Trend {
  @Id
  private String trendName;
  private int trendFreq;

  public Trend() {

  }

  public Trend(String trndNm,int trndFrq) {
    this.trendName = trndNm;
    this.trendFreq = trndFrq;
  }

  public String getTrendName() {
    return trendName;
  }

  public int getTrendFreq() {
    return trendFreq;
  }

  public void setTrendName(String trndNm) {
    this.trendName = trndNm;
  }

  public void setTrendFreq(int trndFrq) {
    this.trendFreq = trndFrq;
  }

  @Override
  public String toString() {
    return "Trend [trendName=" + trendName + ", trendFreq=" + trendFreq +"]";
  }
}