package Indexer;
import java.net.URL;
import java.util.*;

import DB.DbManager;


public class IndexerThread implements Runnable {
	
	// Each Thread must write the inverted File to the DB when Reaching certain Memory Limit
	private final int MEMORY_LIMIT = 640000000;
	
	/* Inverted File Dictionaries */
	private Map <termDocumentKey, List<Integer>> termDocumentDictionary;
	
	/* The Documents that this Thread should Process */
	private List<String> documentsURLs;
	private int docStartIndex;
	private int docEndIndex;
	private int iterationNum;

	/* Constructor */
	public IndexerThread(List<String> docsURLs, int docStIdx, int docEndIdx, int _iterationNum){

		this.docStartIndex = docStIdx;
		this.docEndIndex = docEndIdx;
		this.iterationNum = _iterationNum;
		this.documentsURLs = docsURLs;

		this.termDocumentDictionary = new LinkedHashMap <termDocumentKey, List<Integer>>();

	}
	
	public void run() {
		constructIndex();
	}

	/* The Main Function To Loop Through documents and Construct the Index */
	public void constructIndex() {
		
		/* The Processed Document must be set in this Variable */
		HTMLDocument document; 
		
		/* Calculate The Free Memory before start Processing */ 
		int FreeMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
		int consumedMemory =  0;
		String tempURL = null;
		
		/* Store Number of words at each Document */
		Map<String, Integer> documentsSizes = new LinkedHashMap<String, Integer>();

		/* Iterate Through Portion of The Documents that Assigned to that Thread */
		for(int i = docStartIndex ;i < docEndIndex; i++) 
		{
			/* Calculate The Free Memory after Processing Each Document To Check that It isn't exceeded the Limit */ 
			int currentMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
			consumedMemory = FreeMemory - currentMemory;
			
			/* Check if The Inverted File Exceeded The Memory Limit */
			if (consumedMemory > this.MEMORY_LIMIT)
			{
				/* Write The Inverted File to the DB, Remove It from Memory then Continue To Process Documents */ 
				StoreDictonaries(documentsSizes);

				termDocumentDictionary.clear();
			}
			
			/* The Processed Document ID with URL */
			String documentURL = documentsURLs.get(i);
			tempURL = documentURL.replaceAll("[\uFEFF-\uFFFF]", "");
			if(this.isValid(tempURL)) {

				/* Invoke HTMLDocument constrictor to tokenize html  */
				document = new HTMLDocument(tempURL, i + this.iterationNum * 1000);
				
				/* The Processed Document ID with Its Terms */
				List<String> terms = document.getTerms();
				
				/* Save The document Number of Words to Calculate the term frequency */
				documentsSizes.put(tempURL, document.getTerms().size());
	
				/* Variable Used To Track Each Term Position in the Document */
				int termPosition = 0;

				boolean flag = true;
				/* Loop Through All Terms in The File */
				for (String term : terms) 
				{
					if(term == "me"){
						flag = false;
						continue;
					}
					
					/* Add This Document To the Term List */ 
					List<Integer> termDocumentPositions= null;
					termDocumentKey Key = new termDocumentKey(term, tempURL);
					/* Check If This Terms is already appeared in This Document */
					if (termDocumentDictionary.get(Key) == null)
					{
						/* Make a New List For This Term in That Document with Its Position */
						termDocumentPositions = new ArrayList<Integer>();
						termDocumentDictionary.put(Key, termDocumentPositions);					
					}
					else {
						/* Get This Term Positions List */
						termDocumentPositions = termDocumentDictionary.get(Key);
					}
					if(flag){
						termDocumentPositions.add(0 , -1);
					}
					/* Add This Position To the Document Term List */ 
					termDocumentPositions.add(termPosition);
					
					/* Go To The Next Position */
					termPosition++;
				}
			}
		}
		/* Write The Inverted File to the DB, Remove It from Memory then Continue To Process Documents */
		StoreDictonaries(documentsSizes);
		termDocumentDictionary.clear();
		documentsSizes.clear();
	}
	
	private void StoreDictonaries(Map<String, Integer> documentsSizes) {
		DbManager DBManager = DbManager.getInstance();
		DBManager.saveDocumentCollection(termDocumentDictionary, documentsSizes);
	}
	
    public static boolean isValid(String url) 
    { 
        try { 
            new URL(url).toURI(); 
            return true; 
        } 
          
        catch (Exception e) { 
            return false; 
        } 
    } 
}


	
	
	