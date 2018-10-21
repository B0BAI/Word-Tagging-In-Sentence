import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Tagging {

    Map<Integer, String> sentenceMap = new HashMap<>();
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

    private static void processTagging(String wordTobeTagged) {
        Map<Integer, String> taggingResult = Tagging.sentenceMap.entrySet()
                .stream()
                .filter(map -> wordTobeTagged.equalsIgnoreCase(map.getValue().replaceAll("[^a-zA-Z0-9]", "")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        taggingResult.entrySet()
                .parallelStream()
                .forEach(entry -> tagTargetWord(entry.getKey(), entry.getValue()));
    }

    private static void tagTargetWord(int key, String value) {
        sentenceMap.put(key, "<b>" + value + "</b>");
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

    default void tag(String wordTobeTagged) {
        convertSentenceListToMap();
        processTagging(wordTobeTagged);
        writeOutputToFile(convertSentenceMapToString(), wordTobeTagged);
    }

    default void loadFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(Tagging::convertSentenceToList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
