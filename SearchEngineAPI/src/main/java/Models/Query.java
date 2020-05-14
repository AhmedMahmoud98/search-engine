package Models;

public class Query {
  
  private String queryString;
  private String userLocation;
  
  public Query() {

  }

  public Query(String qryString,String usrLocation) {
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
}