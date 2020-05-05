import java.util.List;

public class HTMLDocument {

	private int documnetID;
	private List<String> terms;
	
	public HTMLDocument(int docID, List<String> terms) {
		this.terms = terms;
		this.documnetID = docID;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public int getDocID() {
		return documnetID;
	}

	public void setDocID(int docID) {
		this.documnetID = docID;
	}
}