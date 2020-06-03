package Models;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Document")
public class Document_ implements Comparable<Document_> {
	  private String term;
	  private String document;
	  private double termFrequency;
	  private int[] positions;
	  private boolean inTitle;
	 
	  public Document_() {

	  }

    public Document_(String _term, String _document, double _termFrequency, int[] _positions, boolean _inTitle) {
	    this.term = _term;
	    this.document = _document;
	    this.termFrequency = _termFrequency;
	    this.positions = _positions;
	    this.inTitle = _inTitle;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public double getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(double termFrequency) {
		this.termFrequency = termFrequency;
	}

	public int[] getPositions() {
		return positions;
	}

	public void setPositions(int[] positions) {
		this.positions = positions;
	}
	
	public boolean isInTitle() {
		return inTitle;
	}

	public void setInTitle(boolean inTitle) {
		this.inTitle = inTitle;
	}

	@Override
	public String toString() {
		return "Document_ [term=" + term + ", document=" + document + ", termFrequency=" + termFrequency
				+ ", positions=" + Arrays.toString(positions) + ", inTitle=" + inTitle + "]";
	}
	
	@Override
    public int compareTo(Document_ o) {
         return this.getDocument().compareTo(((Document_) o).getDocument());
    }
}

