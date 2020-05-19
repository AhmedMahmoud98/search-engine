package Trends;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span; 

public class TrendsExtractor implements Runnable {
	private List<String> Queries;
	private NameFinderME nameFinder;
	
	public TrendsExtractor(List<String> _Queries) throws IOException {
		Queries = _Queries;
		
		// load the name extraction model from file
        InputStream is = new FileInputStream("en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
 
        // feed the model to name finder class
        nameFinder = new NameFinderME(model);
	}
	
	public void run()  {
		processQueries();
	}
	
	
    public void processQueries() {
    	Span nameSpans[];
    	String[] Query;
    	String extractedName = null;
    	
    	while (true)
    	{
    		if(! Queries.isEmpty())
    		{
    			Query = Queries.get(0).split(" ");
    			nameSpans = nameFinder.find(Query);
    			for(Span Name: nameSpans) {
    	            for(int index = Name.getStart(); index < Name.getEnd(); index++) {
    	            	extractedName += Query[index];
    	            }
    			}
    			/* TODO: Write Trends in DB */
    		}
    	}
    }
} 