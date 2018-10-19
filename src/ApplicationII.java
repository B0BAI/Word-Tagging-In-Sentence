import java.util.stream.*;
import java.util.*;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;


public class ApplicationII {
    private static String TARGET_WORD = "AND";

    private static List<String> convertSentenceToList(String sentence) {
        return Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList());
    }


    private static List<String> sentenceList;
    private static List<String> newSentenceList;

    private static Map<Integer, String> convertSentenceListToMap(List<String> sentenceList) {
        AtomicInteger index = new AtomicInteger();

        return sentenceList.stream()
                .collect(Collectors.toMap(
                        String::length, Function.identity(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private static Runnable tagTargetWord(int index) {
        return () -> newSentenceList.set(index, "<b>" + newSentenceList.get(index) + "</b>");
    }


    public static void main(String[] args) {


        String TARGET_SENTENCE = "Hey Bobai";

        sentenceList = convertSentenceToList(TARGET_SENTENCE);
        newSentenceList = new ArrayList<>(sentenceList);

        var list = new Object() {
            int index = 0;
        };
        sentenceList.forEach(item -> {
            if (item.equalsIgnoreCase(TARGET_WORD)) {
                tagTargetWord(list.index).run();
            }
            ++list.index;
        });

        System.out.println(convertSentenceListToMap(sentenceList));

       // newSentenceList.forEach(System.out::println);
    }
}
