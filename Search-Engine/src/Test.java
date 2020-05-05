import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Test {

    public static void main(String[] args){

        /* Remove Mongo Logging */
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.


        DbManager db = new DbManager();
        Map<String, List<Integer>> termDictionary = new HashMap<String  , List<Integer>>();
        List<Integer> lis= new ArrayList<Integer>();
        lis.add(3);
        lis.add(300);
        termDictionary.put("philo" , lis );
        db.saveTermCollection(termDictionary);
    }
}
