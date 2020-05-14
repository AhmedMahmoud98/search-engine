package Indexer;

public class termDocumentKey {
	public String term;
	public int docID;

	public termDocumentKey(String trm, int dcID) {
		this.term = trm;
		this.docID = dcID;
    }

	@Override   
	public boolean equals(Object obj) {
		if (!(obj instanceof termDocumentKey))
			return false;
		
		termDocumentKey ref = (termDocumentKey)obj;
		return this.term.equals(ref.term) && this.docID == ref.docID;
	}

    @Override
    public int hashCode() {
        return term.hashCode() ^ docID;
    }

}
