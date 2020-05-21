package Models;

public class CustomQuery {
  
  private String queryString;
  private String userLocation;
  
  public CustomQuery() {

  }

  public CustomQuery(String qryString,String usrLocation) {
    this.queryString = qryString;
    this.userLocation = usrLocation;
  }

  public String getQueryString() {
    return queryString;
  }

  public String getUserLocation() {
    return userLocation;
  }

  public void setQueryString(String qryString) {
    this.queryString = qryString;
  }

  public void setUserLocation(String usrLocation) {
    this.userLocation = usrLocation;
  }
  
  @Override
  public String toString() {
    return "Query [queryString=" + this.queryString + ", userLocation=" + this.userLocation +"]";
  }
}