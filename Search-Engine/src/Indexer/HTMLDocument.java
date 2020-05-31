package Indexer;
import DB.DbManager;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import TextProcessing.Stemmer;
import TextProcessing.StopWords;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLDocument {

	private List<String> terms;
	private Map<String,List<String>> imageTerms;
	private String url;

	public HTMLDocument(String docIP, int docID) {
		terms = new ArrayList<String>();
		imageTerms = new HashMap<String, List<String>>();
		try {
			String sanitized = docIP.replaceAll("[\uFEFF-\uFFFF]", "");
			url = sanitized;
			try {
				Document doc = Jsoup.connect(sanitized).get();
				String docText = doc.text();
				String title = doc.getElementsByTag("title").text();
				Elements p = doc.getElementsByTag("p");
				String docTextP = "";
				for (Element x: p) 
					docTextP += x.text().toLowerCase();
				parseImages(doc);
				int termsSize = setTerms(docText , title);
				
				DbManager dbManager = new DbManager();
				dbManager.UpdateCrawler(docID, docTextP, title, termsSize);
				
			} catch (UnsupportedMimeTypeException e) {
				System.out.println(url + " isn't a valid Url");				/* Not a valid Url */
			}

		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}

	private void parseImages(Document doc){
		// Get all Images
		List<Element> images= doc.getElementsByTag("img");

		// Index image by alt and src
		List<String> srcs =  images.stream()
				.map(s -> s.attr("src"))
				.collect(Collectors.toList());

		List<String> alt = images.stream()
				.map(s -> s.attr("alt"))
				.collect(Collectors.toList());

		// Loop on Each image Src
		for (int i = 0 ; i < srcs.size(); i++) {
			// Split Src and alt urls to get  image name
			String[] srcTerms = srcs.get(i).split("/");
			String imageSrc = srcTerms[srcTerms.length - 1];
			imageSrc = imageSrc.toLowerCase();

			String[] altTerms = alt.get(i).split("/");
			String imageAlt = altTerms[altTerms.length - 1];
			imageAlt = imageAlt.toLowerCase();

			// Remove URL encoding chars
			imageSrc = imageSrc.replaceAll("%..", "-");
			imageAlt = imageAlt.replaceAll("%..","-");

			// Split image Name into words and remove extansion
			Set<String> Imageterms = new HashSet<String>(Arrays.asList(imageSrc.split("-|\\.|_")));
			Imageterms.addAll(Arrays.asList(imageAlt.split("-|\\.|_")));
			Iterator<String> it = Imageterms.iterator();
			List<String> stopWords = StopWords.getStopWords();
			Stemmer stemmer = new Stemmer();

			while(it.hasNext()){
				String term = it.next();
				// Probably not a word
				if(term.length() < 3) continue;
				// Dimensions
				if(term.contains("px")) continue;
				// Query Params
				if(term.contains("?")) continue;
				// Remove any extension
				if(term.contains("svg") | term.contains("jpg") | term.contains("png")) continue;

				/* Remove stop words */
				if(!stopWords.contains(term)) {
						stemmer.add(stringToChar(term), term.length());
						stemmer.stem();
						term = stemmer.toString();
					}


				List<String> imageUrls = imageTerms.get(term);
				if(imageUrls == null){
					imageUrls = new ArrayList<>();
				}
				imageUrls.add(srcs.get(i));
				imageTerms.put(term , imageUrls);
			}

		}
		DbManager dbManager = new DbManager();
		dbManager.saveImageCollection(imageTerms , url);
	}

	private int setTerms(String text , String title){
		/* Remove any non alphanumeric charachter */
		text = text.replaceAll("[^a-zA-Z0-9 ]", ""); // double quotes
		text = text.toLowerCase();
		title = title.replaceAll("[^a-zA-Z0-9 ]", "");
		title = title.toLowerCase();
		//System.out.println(text);
		String[] temp = text.split("\\s+"); // any number of spaces
		String[] temp2 = title.split("\\s+");

		List<String> stopWords = StopWords.getStopWords();


		/* Stem words */
		Stemmer stemmer = new Stemmer();

		/* Add title words first */
		for (int j = 0 ; j <temp2.length; j++){
			if(!stopWords.contains(temp2[j])) {
				stemmer.add(stringToChar(temp[j]), temp[j].length());
				stemmer.stem();
				terms.add(stemmer.toString());
			}
		}
		/* Add Stop word as border */
		terms.add("me");

		/* Add document coontent*/
		for (int i = 0 ; i < temp.length ; i++) {
			/* Remove stop words */
			if(!stopWords.contains(temp[i])) {
				stemmer.add(stringToChar(temp[i]), temp[i].length());
				stemmer.stem();
				terms.add(stemmer.toString());
			}
		}
		return terms.size();
	}

	private char[] stringToChar(String str) {
		char[] ch = new char[str.length()];
		for (int i = 0; i < str.length(); i++) {
			ch[i] = str.charAt(i);
		}
		return ch;
	}
	
	public List<String> getTerms() {
		return terms;
	}

	public Map<String,List<String>> getImageTerms(){
		return imageTerms;
	}
}