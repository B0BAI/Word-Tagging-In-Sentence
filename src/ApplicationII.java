import java.util.stream.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ApplicationII {
    private static Map<Integer, String> sentenceMap = new HashMap<>();
    private static List<String> sentenceList;

    private static String TARGET_WORD = "BOBAI";

    private static Runnable convertSentenceToList(String sentence) {
        return () -> sentenceList = new ArrayList<String>(Stream.of(sentence.split(" "))
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
            Map<Integer, String> taggingresult = sentenceMap.entrySet()
                    .stream()
                    .filter(map -> TARGET_WORD.equalsIgnoreCase(map.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            taggingresult.entrySet()
                    .parallelStream()
                    .forEach(entry -> {
                        tagTargetWord(entry.getKey(), entry.getValue()).run();
                    });
        };
    }

    public static void main(String[] args) {

        String TARGET_SENTENCE = "Bobai small pieces of functionality relies Bobai repeatable results, a standard mechanism for input and output, and an exit code for a program to indicate success or lack thereof.  We know this works from Bobai evidence The Bobai applies SRP by Bobai data from A to B, and it’s up to members of the pipeline to Bobai if the input is Bobai.";

        convertSentenceToList(TARGET_SENTENCE).run();
        convertSentenceListToMap(sentenceList).run();
        processTagging(sentenceMap).run();


        System.out.println(sentenceMap);

    }
}
