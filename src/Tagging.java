import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Tagging {

    Map<Integer, String> sentenceMap = new ConcurrentHashMap<>();
    List<String> sentenceList = new ArrayList<>();

    private static void convertSentenceToList(String sentence) {
        sentenceList.addAll(Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList()));
    }

    private static void convertSentenceListToMap() {
        AtomicInteger index = new AtomicInteger(-1);
        for (String item : Tagging.sentenceList) sentenceMap.put(index.incrementAndGet(), item);
    }

    private static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    private static void processWordTagging(String wordTobeTagged) {
        Map<Integer, String> taggingResult = Tagging.sentenceMap.entrySet()
                .stream()
                .filter(map -> wordTobeTagged.equalsIgnoreCase(map.getValue().replaceAll("[^a-zA-Z0-9]", "")))
                .filter(map -> !isNumeric(wordTobeTagged))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        taggingResult.entrySet()
                .parallelStream()
                .forEach(entry -> {
                    tagTargetedWord(entry.getKey(), entry.getValue());
                });
    }

    private static void tagNumber(int key, String value) {
        sentenceMap.put(key, String.format("<number>%s</number>", value));
    }

    private static Runnable processNumberTag() {
        return () -> {
            sentenceMap.entrySet().parallelStream().forEach(entry -> {
                if (isNumeric(entry.getValue())) {
                    tagNumber(entry.getKey(), entry.getValue());
                }
            });
        };
    }

    private static void tagTargetedWord(int key, String value) {
        sentenceMap.put(key, String.format("<b>%s</b>", value));
    }

    private static void writeOutputToFile(String taggedContent, String outputFile) {
        try {
            Files.write(Paths.get(String.format("%s.txt", outputFile)), taggedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertSentenceMapToString() {
        return Tagging.sentenceMap.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(" "));
    }

    default String tag(String wordTobeTagged) {
        ExecutorService es = Executors.newCachedThreadPool();
        convertSentenceListToMap();
        processWordTagging(wordTobeTagged);
        es.execute(processNumberTag());
        String taggedContent = convertSentenceMapToString();
        writeOutputToFile(taggedContent, wordTobeTagged);
        return taggedContent;
    }

    default void loadFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(Tagging::convertSentenceToList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
