import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HTMLDocument {

	private int documnetID;
	private List<String> terms;
	
	public HTMLDocument(int docID , String docIP) {
		try {
			Document doc = Jsoup.connect(docIP).get();
			String docText = doc.text();
			terms = parse(docText);
		} catch (IOException e) {
			/* Website refuse to connect */
			e.printStackTrace();
		}
		//this.terms = docIP.toString();
		this.documnetID = docID;
	}

	private List<String> parse(String text){
		text = text.replaceAll("[^a-zA-Z0-9 | \" *\"]", "");
		return Arrays.asList(text.split(" "));
	}
	public List<String> getTerms() {
		return terms;
	}

	public int getDocID() {
		return documnetID;
	}

}