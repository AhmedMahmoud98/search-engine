package Indexer;
import DB.DbManager;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import TextProcessing.Stemmer;
import TextProcessing.StopWords;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLDocument {

	private List<String> terms;
	private Map<String,List<String>> imageTerms;
	private String url;

	public HTMLDocument(String docIP) {
		terms = new ArrayList<String>();
		imageTerms = new HashMap<String, List<String>>();
		try {
			String sanitized = docIP.replaceAll("[\uFEFF-\uFFFF]", "");
			url = sanitized;
			try {
				Document doc = Jsoup.connect(sanitized).get();
				String docText = doc.text();
				parseImages(doc);
				setTerms(docText);
				
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

	private void setTerms(String text){
		/* Remove any non alphanumeric charachter */
		text = text.replaceAll("[^a-zA-Z0-9 ]", ""); // double quotes
		text = text.toLowerCase();
		//System.out.println(text);
		String[] temp = text.split("\\s+"); // any number of spaces
		List<String> stopWords = StopWords.getStopWords();

		/* Stem words */
		Stemmer stemmer = new Stemmer();
		for (int i = 0 ; i < temp.length ; i++) {
			/* Remove stop words */
			if(!stopWords.contains(temp[i])) {
				stemmer.add(stringToChar(temp[i]), temp[i].length());
				stemmer.stem();
				terms.add(stemmer.toString());
				// stemmer.reset();
			}
		}
		//System.out.println(terms);
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