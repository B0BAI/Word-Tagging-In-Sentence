import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        Tagging.sentenceList.parallelStream().forEachOrdered(item -> sentenceMap.put(index.incrementAndGet(), item));
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

    private static Boolean verifyWordRange(int wordMapKey) {
        int wordToBeTaggedListSize = wordsToBeTaggedList.size() - 0x1;
        return removeSpecialCharacters(sentenceMap.get(wordToBeTaggedListSize + wordMapKey))
                .equalsIgnoreCase(wordsToBeTaggedList
                        .get(wordToBeTaggedListSize));
    }

    private static void assembleWordsToBeTagged(int wordMapKey) {
        StringBuilder string = new StringBuilder();
        IntStream.range(0, wordsToBeTaggedList.size())
                .map(i -> wordMapKey + i).parallel()
                .forEachOrdered(mapKey -> {
                    string.append(String.format("%s ", sentenceMap.get(mapKey)));
                    if (mapKey > wordMapKey) {
                        sentenceMap.remove(mapKey);
                    }
                });
        tagTargetedWords(wordMapKey, string.toString().trim());
    }


    private static void processWordTagging() {
        filterSentenceMap().entrySet()
                .parallelStream()
                .forEach(entry -> {
                    if (verifyWordRange(entry.getKey())) {
                        assembleWordsToBeTagged(entry.getKey());
                    }
                });
    }

    private static void tagNumber(int key, String value) {
        sentenceMap.put(key, String.format("<b>%s</b>", value));
    }

    private static void processNumberTag() {
        new Thread(() ->
                sentenceMap.entrySet().parallelStream().parallel().forEach(entry -> {
                    if (isNumeric(entry.getValue())) {
                        tagNumber(entry.getKey(), entry.getValue());
                    }
                })).start();
    }

    private static void tagTargetedWords(int key, String value) {
        sentenceMap.put(key, String.format("<mark> %s </mark>", value));
    }

    private static Map<Integer, String> sortSentenceMap() {
        return Tagging.sentenceMap.entrySet().parallelStream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private static void writeOutputToFile(String taggedContent, String outputFile) {
        try {
            Files.write(Paths.get(String.format("%s.txt", outputFile)), taggedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertSentenceMapToString() {
        return Tagging.sortSentenceMap().entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(" "));
    }

    private static void initializeWordToBeTaggedList(String wordTobeTagged) {
        new Thread(() -> wordsToBeTaggedList.addAll(convertStringToList(wordTobeTagged))).start();
    }

    default void tag(String wordTobeTagged) {
        initializeWordToBeTaggedList(wordTobeTagged);
        convertSentenceListToMap();
        processNumberTag();
        processWordTagging();

        String taggedContent = convertSentenceMapToString();
        writeOutputToFile(taggedContent, wordTobeTagged);
        System.out.println(sentenceMap);
        System.out.printf("Word Count: %d%n", sentenceList.size());
    }

    default void loadFileContent(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.parallel().forEachOrdered(string -> sentenceList.addAll(convertStringToList(string)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
