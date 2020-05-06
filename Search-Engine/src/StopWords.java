import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StopWords {
    public static List<String> stopWords;

    public static List<String> getStopWords(){
        if(stopWords == null){
            try {
                stopWords = Files.readAllLines(Paths.get("../assets/stopwords.txt"));
            } catch(IOException ex){
                System.out.println("Stop words not loaded");
            }
        }
        return stopWords;
    }

}
