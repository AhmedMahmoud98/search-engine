package Models;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Images")
public class TermImages {
	private String term;
	private String[] imageUrl;
	private String[] websiteUrl;
	
	public TermImages() {
		
	}
	public TermImages(String term, String[] imageUrl, String[] websiteUrl) {
		super();
		this.term = term;
		this.imageUrl = imageUrl;
		this.websiteUrl = websiteUrl;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String[] getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String[] imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String[] getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String[] websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	@Override
	public String toString() {
		return "TermImages [term=" + term + ", imageUrl=" + Arrays.toString(imageUrl) + ", websiteUrl="
				+ Arrays.toString(websiteUrl) + "]";
	}



}
