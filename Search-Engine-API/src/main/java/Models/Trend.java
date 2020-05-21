package Models;

import java.util.Objects;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Trends")
public class Trend {
	@Id
	private String trendName;
	private String country;
	private int frequency;
	 
	public Trend() {
	
	}
	
	public Trend(String trndNm, int trndFrq, String cntry) {
	   this.country = cntry;
	   this.trendName = trndNm;
	   this.frequency = trndFrq;
	 }
	   
	 public String getTrendName() {
		return this.trendName;
	 }
	
	 public String getCountry() {
		return this.country;
	 }
		
	 public void setTrendName(String trndNm) {
		this.trendName = trndNm; 
	 }
			  
	  public void setCountry(String cntry) {
		this.country = cntry;
	  }
		
	  public int getFrequency() {
	    return frequency;
	  }
	  
	  public void setFrequency(int trndFrq) {
	    this.frequency = trndFrq;
	  }
	  
	  @Override
	  public String toString() {
	    return "Trend [trendName=" + trendName + 
	    		", frequency=" + frequency + 
	    		", country=" + country +"]";
	  }
	  
	  
}