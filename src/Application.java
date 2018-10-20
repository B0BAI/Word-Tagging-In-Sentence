import java.time.Duration;
import java.time.Instant;
import java.util.stream.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class Application {

    private static Map<Integer, String> sentenceMap = new HashMap<>();
    private static List<String> sentenceList = new ArrayList<>();

    private static String TARGET_WORD = "expansion";

    private static void convertSentenceToList(String sentence) {
        sentenceList.addAll(Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList()));
    }

    private static void convertSentenceListToMap(List<String> list) {
        AtomicInteger index = new AtomicInteger(-1);
        for (String item : list) sentenceMap.put(index.incrementAndGet(), item);
    }

    private static Runnable tagTargetWord(int key, String value) {
        return () -> sentenceMap.put(key, "<b>" + value + "</b>");
    }

    private static void processTagging(Map<Integer, String> sentenceMap) {
        Map<Integer, String> taggingResult = sentenceMap.entrySet()
                .stream()
                .filter(map -> TARGET_WORD.equalsIgnoreCase(map.getValue().replaceAll("[^a-zA-Z0-9]", "")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        taggingResult.entrySet()
                .parallelStream()
                .forEach(entry -> tagTargetWord(entry.getKey(), entry.getValue()).run());
    }

    private static void getTextFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(Application::convertSentenceToList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertSentenceMapToString(Map<Integer, String> sentenceMap) {
        return sentenceMap.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(" "));
    }

    private static void writeOutputToFile(String output) {
        try {
            Files.write(Paths.get("output.txt"), output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Instant start = Instant.now();

        getTextFileContent("/Users/B0BAI/W O R K S P A C E/Word-Tagging-In-Sentence/src/sentence.txt");

        convertSentenceListToMap(sentenceList);
        processTagging(sentenceMap);
        writeOutputToFile(convertSentenceMapToString(sentenceMap));

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).getNano());
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
