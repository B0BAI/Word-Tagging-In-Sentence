import java.util.stream.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Application {
    private static String TARGET_WORD = "BOBAI";

    private static List<String> convertSentenceToList(String sentence) {
        return Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList());
    }

    private static List<String> sentenceList;
    private static List<String> newSentenceList;

    private static Runnable tagTargetWord(int index) {
        return () -> newSentenceList.set(index, "<b>" + newSentenceList.get(index) + "</b>");
    }

    public static void main(String[] args) {


        String TARGET_SENTENCE = "Bobai is the man that Bobai told you about bobai again";

        sentenceList = convertSentenceToList(TARGET_SENTENCE);
        newSentenceList = new ArrayList<>(sentenceList);

        AtomicInteger count = new AtomicInteger();

        sentenceList.forEach(item -> {
            int index = count.getAndIncrement();
            if (item.equalsIgnoreCase(TARGET_WORD)) {
                tagTargetWord(index).run();
            }
        });

        newSentenceList.forEach(System.out::println);
    }
}
