package Models;

public class CustomQuery {
  
  private String queryString;
  private String userLocation;
  private int pageNumber;
  
  public CustomQuery() {

  }

  public CustomQuery(String qryString, String usrLocation, int _pageNumber) {
    this.queryString = qryString;
    this.userLocation = usrLocation;
    this.pageNumber = _pageNumber;
  }

  public String getQueryString() {
    return queryString;
  }

  public String getUserLocation() {
    return userLocation;
  }
  
  public int getPageNumber() {
	return pageNumber;
  }

  public void setpageNumber(int _pageNumber) {
	this.pageNumber = _pageNumber;
  }

  public void setQueryString(String qryString) {
    this.queryString = qryString;
  }

  public void setUserLocation(String usrLocation) {
    this.userLocation = usrLocation;
  }
  
  @Override
  public String toString() {
    return "Query [queryString=" + this.queryString + 
    		", userLocation=" + this.userLocation + 
    		", pageNumber=" + this.pageNumber + "]";
  }
}