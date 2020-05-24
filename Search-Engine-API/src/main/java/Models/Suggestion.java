package Models;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Suggestions")
public class Suggestion {
	  private String searchString;
	  private int frequency;
	  

	  public Suggestion() {

	  }

	  public Suggestion(String s , int freq) {
	    this.searchString = s;
	    this.frequency = freq ;
	  }

	  public String getSearchString() {
	    return this.searchString;
	  }

	  public int getFrequency() {
	    return frequency;
	  }
	  
	  public void setFrequency(int freq) {
		    this.frequency =  freq;
	  }
	  
	  public void setSearchString(String s) {
	    this.searchString = s;
	  }
	  
	  public static List<String> searchStrings(List<Suggestion> arr){
		  List<String> res = arr.stream().map(Suggestion::getSearchString).collect(Collectors.toList());
		  return res;
	  }

	  @Override
	  public String toString() {
	    return "Suggestion [searchString=" + this.searchString + ", frequency=" + this.frequency +"]";
	  }
	}

