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
        
        Application.setContentFilePath("./sentence.txt")
                .tag("McAfee Enterprise");

        Instant end = Instant.now();
        System.out.println("Time taking in sec: "+Duration.between(start, end).getSeconds());
        System.out.printf("Memory: %d%n", Runtime.getRuntime().freeMemory());
    }
}
