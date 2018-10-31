import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application implements Tagging {

    private static Application setContentFilePath(String filePath) {
        Application application = new Application();
        application.loadFileContent(filePath);
        return application;
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        
        Application.setContentFilePath("/sentence.txt")
                .tag("intern");

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getSeconds());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
