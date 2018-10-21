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

        System.out.println(Application.setContentFilePath("/sentence.txt").tag("434"));

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getSeconds());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
