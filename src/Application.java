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

    private static Runnable convertSentenceToList(String sentence) {
        return () -> sentenceList.addAll(Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList()));
    }

    private static Runnable convertSentenceListToMap(List<String> list) {
        return () -> {
            AtomicInteger index = new AtomicInteger(-1);
            for (String item : list) sentenceMap.put(index.incrementAndGet(), item);
        };
    }

    private static Runnable tagTargetWord(int key, String value) {
        return () -> sentenceMap.put(key, "<b>" + value + "</b>");
    }

    private static Runnable processTagging(Map<Integer, String> sentenceMap) {
        return () -> {
            Map<Integer, String> taggingResult = sentenceMap.entrySet()
                    .stream()
                    .filter(map -> TARGET_WORD.equalsIgnoreCase(map.getValue().replaceAll("[^a-zA-Z0-9]", "")))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            taggingResult.entrySet()
                    .parallelStream()
                    .forEach(entry -> tagTargetWord(entry.getKey(), entry.getValue()).run());
        };
    }

    private static void getTextFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> convertSentenceToList(line).run());
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

    private static Runnable writeOutputToFile(String output){
        return ()->{
            try {
                Files.write(Paths.get("output.txt"), output.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("DONE!");
        };
    }


    public static void main(String[] args) {
        getTextFileContent("sentence.txt");

        convertSentenceListToMap(sentenceList).run();
        processTagging(sentenceMap).run();
        writeOutputToFile(convertSentenceMapToString(sentenceMap)).run();
    }
}
