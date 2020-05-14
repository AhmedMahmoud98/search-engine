package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Images")
public class Image {
  @Id
  private String imageUrl;
  private String siteUrl;
  
  public Image() {

  }

  public Image(String imgURL,String siteURL) {
    this.imageUrl = imgURL;
    this.siteUrl = siteURL;
  }

  public String getimageUrl() {
    return imageUrl;
  }

  public String getsiteUrl() {
    return siteUrl;
  }

  public void setimageUrl(String imgURL) {
    this.imageUrl = imgURL;
  }

  public void setsiteUrl(String siteURL) {
    this.siteUrl = siteURL;
  }

  @Override
  public String toString() {
    return "Image [imageUrl=" + imageUrl + ", siteUrl=" + siteUrl +"]";
  }
}