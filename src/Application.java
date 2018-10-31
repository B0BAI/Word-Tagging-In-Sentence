import java.time.Duration;
import java.time.Instant;


public class Application implements Tagging {

    private static Application setContentFilePath(String filePath) {
        Application application = new Application();
        application.loadFileContent(filePath);
        return application;
    }

    public static void main(String[] args) {
        Instant start = Instant.now();

        Application.setContentFilePath("/Users/B0BAI/Desktop/sentence.txt")
                .tag("5G");

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getSeconds());
        //System.out.println(Runtime.getRuntime().freeMemory());
    }
}
