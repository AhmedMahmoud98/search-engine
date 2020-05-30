package Models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Term")
public class Term {
	  private String term;
	  private String[] documents;
	  private int termDocumentsFreq;
	 
	  public Term() {

	  }

	  public Term(String _term, String[] _documents, int _termDocumentFreq) {
	    this.term = _term;
	    this.documents = _documents;
	    this.termDocumentsFreq = _termDocumentFreq ;
	  }

	  public String getTerm() {
	    return this.term;
	  }
	  
	  public String[] getDocuments() {
		  return this.documents;
	  }

	  public int getTermDocumentsFreq() {
	    return termDocumentsFreq;
	  }
	  
	  public void setTermDocumentsFreq(int freq) {
		    this.termDocumentsFreq =  freq;
	  }
	  
	  public void setTerm(String _term) {
	    this.term = _term;
	  }
	  
	  public void setDocuments(String[] _documents) {
		  this.documents = _documents;
	  }

	  @Override
	  public String toString() {
	    return "Term [term=" + this.term + ", termDocumentFreq=" + this.termDocumentsFreq +"]";
	  }
	}

