package Indexer;

public class termDocumentKey {
	public String term;
	public String docUrl;

	public termDocumentKey(String trm, String dcID) {
		this.term = trm;
		this.docUrl = dcID;
    }

	@Override   
	public boolean equals(Object obj) {
		if (!(obj instanceof termDocumentKey))
			return false;
		
		termDocumentKey ref = (termDocumentKey)obj;
		return this.term.equals(ref.term) && this.docUrl.equals(ref.docUrl);
	}

    @Override
    public int hashCode() {
        return term.hashCode() ^ docUrl.hashCode();
    }

}
