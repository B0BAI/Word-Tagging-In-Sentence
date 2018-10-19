import java.util.stream.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ApplicationII {
    private static Map<Integer, String> sentenceMap = new HashMap<>();
    private static List<String> sentenceList;
    private static List<String> newSentenceList;

    private static String TARGET_WORD = "AND";

    private static Runnable convertSentenceToList(String sentence) {
       return  () -> sentenceList = new ArrayList<String>( Stream.of(sentence.split(" "))
                .map(String::new)
                .collect(Collectors.toList()));
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

        String TARGET_SENTENCE = "";

        convertSentenceToList(TARGET_SENTENCE).run();
        convertSentenceListToMap(sentenceList).run();

        System.out.println(sentenceMap);

    }
}
