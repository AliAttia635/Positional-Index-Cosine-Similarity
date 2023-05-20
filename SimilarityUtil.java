import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimilarityUtil {

    public static double cosineTextSimilarity(String left, String right) {
        String[] leftWords = left.split(" ");
        String[] rightWords = right.split(" ");

        Map<String, Integer> leftWordCountMap = new HashMap<>();
        Map<String, Integer> rightWordCountMap = new HashMap<>();
        Set<String> uniqueSet = new HashSet<>();

        for (String leftWord : leftWords) {
            int count = leftWordCountMap.getOrDefault(leftWord, 0);
            leftWordCountMap.put(leftWord, count + 1);
            uniqueSet.add(leftWord);
        }

        for (String rightWord : rightWords) {
            int count = rightWordCountMap.getOrDefault(rightWord, 0);
            rightWordCountMap.put(rightWord, count + 1);
            uniqueSet.add(rightWord);
        }

        int[] leftVector = new int[uniqueSet.size()];
        int[] rightVector = new int[uniqueSet.size()];
        int index = 0;

        for (String uniqueWord : uniqueSet) {
            leftVector[index] = leftWordCountMap.getOrDefault(uniqueWord, 0);
            rightVector[index] = rightWordCountMap.getOrDefault(uniqueWord, 0);
            index++;
        }

        return cosineVectorSimilarity(leftVector, rightVector);
    }

    private static double cosineVectorSimilarity(int[] leftVector, int[] rightVector) {
        if (leftVector.length != rightVector.length)
            return 1;

        double dotProduct = 0;
        double leftNorm = 0;
        double rightNorm = 0;

        for (int i = 0; i < leftVector.length; i++) {
            dotProduct += leftVector[i] * rightVector[i];
            leftNorm += leftVector[i] * leftVector[i];
            rightNorm += rightVector[i] * rightVector[i];
        }

        double result = dotProduct / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
        return result;
    }

    public static void main(String[] args) {
        String left = "Here is another example text. It has different sentences than the first file.";
        String right = "example text sentences";

        System.out.println(cosineTextSimilarity(left, right));
    }
}
