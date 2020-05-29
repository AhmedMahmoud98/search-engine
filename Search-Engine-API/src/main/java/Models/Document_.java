package Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Document")
public class Document_ {
	  private String term;
	  private String document;
	  private double termFrequency;
	  private int[] positions;
	  private boolean inTitle;
	 
	  public Document_() {

	  }

	public boolean isInTitle() {
		return inTitle;
	}

	public void setInTitle(boolean inTitle) {
		this.inTitle = inTitle;
	}

	public Document_(String _term, String _document, double _termFrequency, int[] _positions, boolean _inTitle) {
	    this.term = _term;
	    this.document = _document;
	    this.termFrequency = _termFrequency;
	    this.positions = _positions;
	    this.inTitle = _inTitle;
	  }

	  public String getTerm() {
	    return this.term;
	  }
	  
	  public String getDocument() {
		  return this.document;
	  }

	  public double getTermFrequency() {
		  return this.termFrequency;
	  }
	  
	  public int[] getPositions() {
		  return this.positions;
	  }
	  
	  public void setTerm(String _term) {
		  this.term = _term;
	  }
	  
	  public void setDocument(String _document) {
		  this.document = _document;
	  }
	  
	  public void setTermFrequency(double freq) {
		  this.termFrequency =  freq;
	  }
	  
	  public void setPositions(int[] _positions) {
		  this.positions =  _positions;
	  }
	  

	  @Override
	  public String toString() {
	    return "Document [term=" + this.term + ", "
	    		+ "document=" + this.document + ","
	    		+ " termFrequency=" + this.termFrequency +"]";
	  }
	}

