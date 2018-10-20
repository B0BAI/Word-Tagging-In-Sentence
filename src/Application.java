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

        Application.setContentFilePath("sentence.txt").tag("expansion");

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getNano());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
