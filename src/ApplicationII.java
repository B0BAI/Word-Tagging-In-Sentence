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

    private static String[] convertSentenceToArray(String sentence) {
        return Arrays.stream(sentence.split(" "))
                .map(String::trim)
                .toArray(String[]::new);
    }

    private static List<Word> sentenceList;
    private static List<String> newSentenceList;


    private static Map<Integer, String> convertSentenceListToMap(String sentenceList[]) {

    }

    private static Runnable tagTargetWord(int index) {
        return () -> newSentenceList.set(index, "<b>" + newSentenceList.get(index) + "</b>");
    }


    public static void main(String[] args) {


    }
}
