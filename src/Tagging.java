import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Tagging {

    Map<Integer, String> sentenceMap = new ConcurrentHashMap<>();
    List<String> sentenceList = new ArrayList<>();
    List<String> wordsToBeTaggedList = new ArrayList<>();

    private static List<String> convertStringToList(String string) {
        return Stream.of(string.split(" "))
                .map(String::new)
                .collect(Collectors.toList());
    }

    private static void convertSentenceListToMap() {
        AtomicInteger index = new AtomicInteger(-1);
        for (String item : Tagging.sentenceList) sentenceMap.put(index.incrementAndGet(), item);
    }

    private static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    private static Map<Integer, String> filterSentenceMap() {
        String firstWordOnListOfWordsTobeTagged = Tagging.wordsToBeTaggedList.get(0);
        return Tagging.sentenceMap.entrySet()
                .parallelStream()
                .filter(map -> firstWordOnListOfWordsTobeTagged.equalsIgnoreCase(removeSpecialCharacters(map.getValue())))
                .filter(map -> !isNumeric(firstWordOnListOfWordsTobeTagged))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String removeSpecialCharacters(String value) {
        return value.replaceAll("[^a-zA-Z0-9]", "");
    }

    private static Boolean verifyWordRange(int wordKey) {
        int wordToBeTaggedListSize = wordsToBeTaggedList.size() - 1;
        return removeSpecialCharacters(sentenceMap.get(wordToBeTaggedListSize + wordKey))
                .equals(wordsToBeTaggedList
                        .get(wordToBeTaggedListSize));
    }



    private static void processWordTagging() {
        filterSentenceMap().entrySet()
                .parallelStream()
                .forEach(entry -> {
                    if (verifyWordRange(entry.getKey())) {
                        tagTargetedWords(entry.getKey(), entry.getValue());
                    }
                   // System.out.println(verifyWordRange(entry.getKey()));
                });
    }

    private static void tagNumber(int key, String value) {
        sentenceMap.put(key, String.format("<number>%s</number>", value));
    }

    private static Thread processNumberTag() {
        return new Thread(() ->
                sentenceMap.entrySet().parallelStream().forEach(entry -> {
                    if (isNumeric(entry.getValue())) {
                        tagNumber(entry.getKey(), entry.getValue());
                    }
                }));
    }

    private static void tagTargetedWords(int key, String value) {
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

    private static void initializeWordToBeTaggedList(String wordTobeTagged) {
        new Thread(() -> {
            wordsToBeTaggedList.addAll(convertStringToList(wordTobeTagged));
        }).start();
    }

    default String tag(String wordTobeTagged) {
        initializeWordToBeTaggedList(wordTobeTagged);
        convertSentenceListToMap();
        processNumberTag().start();

        processWordTagging();
        String taggedContent = convertSentenceMapToString();

        writeOutputToFile(taggedContent, wordTobeTagged);
        System.out.println(sentenceList.size());
        return taggedContent;
    }

    default void loadFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(string -> sentenceList.addAll(convertStringToList(string)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
