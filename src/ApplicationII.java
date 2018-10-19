import java.util.stream.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ApplicationII {
    private static Map<Integer, String> sentenceMap = new HashMap<>();
    private static List<Word> sentenceList;
    private static List<String> newSentenceList;

    private static String TARGET_WORD = "AND";

    private static List<String> convertSentenceToList(String sentence) {
        return Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList());
    }

    private static Runnable convertSentenceListToMap(List<String> list) {
        return () -> {
            AtomicInteger index = new AtomicInteger();
            for (String item : list) sentenceMap.put(index.incrementAndGet(), item);
        };
    }

    private static Runnable tagTargetWord(int index) {
        return () -> newSentenceList.set(index, "<b>" + newSentenceList.get(index) + "</b>");
    }


    public static void main(String[] args) {

        String TARGET_SENTENCE = "Bobai is the man that Bobai told you about bobai again";
        convertSentenceListToMap(convertSentenceToList(TARGET_SENTENCE)).run();
        System.out.println(sentenceMap);

    }
}
