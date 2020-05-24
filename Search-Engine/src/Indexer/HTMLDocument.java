package Indexer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import TextProcessing.Stemmer;
import TextProcessing.StopWords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HTMLDocument {

	private List<String> terms;

	public HTMLDocument(String docIP) {
		terms = new ArrayList<String>();
		try {
			String sanitized = docIP.replaceAll("[\uFEFF-\uFFFF]", "");
			Document doc = Jsoup.connect(sanitized).get();
			String docText = doc.text();
			setTerms(docText);
		} catch (IOException e) {
			/* Website refuse to connect */
			e.printStackTrace();
		}
		//this.terms = docIP.toString();
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
}