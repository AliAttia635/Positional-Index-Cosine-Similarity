import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CosineSimilarity {
    private static Map<String, Map<String, List<Integer>>> positionalIndex;

    public static void main(String[] args) {
        String[] fileNames = {
                "file1.txt",
                "file2.txt",
                "file3.txt",
        };

        // Step 1: Read 10 text files
        List<String> fileContents = new ArrayList<>();
        for (String fileName : fileNames) {
            try {
                String fileContent = readFile(fileName);
                fileContents.add(fileContent);
            } catch (IOException e) {
                System.err.println("Error reading the file: " + e.getMessage());
            }
        }

        // Step 2: Build the positional index for those 10 files
        positionalIndex = buildPositionalIndex(fileContents);

        // Step 3: Get a query (set of words)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the query words (separated by spaces): ");
        String query = scanner.nextLine();
        scanner.close();

        // Step 4: Compute the cosine similarity between each file and the query
        List<Double> similarities = computeCosineSimilarities(fileContents, query);

        // Step 5: Rank the files according to the value of the cosine similarity
        List<String> rankedFiles = rankFiles(fileNames, similarities);

        // Output the ranked files with cosine similarity
        System.out.println("Ranking of files based on cosine similarity:");
        for (int i = 0; i < rankedFiles.size(); i++) {
            String fileName = rankedFiles.get(i);
            double similarity = similarities.get(i);
            System.out.println(fileName + " - Cosine Similarity: " + similarity);
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }
        }
        return content.toString();
    }

    private static Map<String, Map<String, List<Integer>>> buildPositionalIndex(List<String> fileContents) {
        Map<String, Map<String, List<Integer>>> positionalIndex = new HashMap<>();
        int fileId = 0;

        for (String fileContent : fileContents) {
            String[] words = fileContent.toLowerCase().split("\\s+");
            Map<String, List<Integer>> termPositions = new HashMap<>();

            for (int position = 0; position < words.length; position++) {
                String word = words[position];
                List<Integer> positions = termPositions.getOrDefault(word, new ArrayList<>());
                positions.add(position);
                termPositions.put(word, positions);
            }

            for (String word : termPositions.keySet()) {
                Map<String, List<Integer>> filePositions = positionalIndex.getOrDefault(word, new HashMap<>());
                filePositions.put("file" + (fileId + 1), termPositions.get(word));
                positionalIndex.put(word, filePositions);
            }

            fileId++;
        }

        return positionalIndex;
    }

    private static List<Double> computeCosineSimilarities(List<String> fileContents, String query) {
        Map<String, Integer> queryWordFrequency = getWordFrequencies(query);
        List<Double> similarities = new ArrayList<>();

        for (String fileContent : fileContents) {
            Map<String, Integer> fileWordFrequency = getWordFrequencies(fileContent);
            double similarity = computeCosineSimilarity(fileWordFrequency, queryWordFrequency);
            similarities.add(similarity);
        }

        return similarities;
    }

    private static Map<String, Integer> getWordFrequencies(String text) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");

        for (String word : words) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }

        return wordFrequency;
    }

    private static double computeCosineSimilarity(Map<String, Integer> wordFrequencyText, Map<String, Integer> wordFrequencyQuery) {
        Set<String> uniqueWords = new HashSet<>(wordFrequencyText.keySet());
        uniqueWords.addAll(wordFrequencyQuery.keySet());

        double dotProduct = 0.0;
        double magnitudeText = 0.0;
        double magnitudeQuery = 0.0;

        for (String word : uniqueWords) {
            int frequencyText = wordFrequencyText.getOrDefault(word, 0);
            int frequencyQuery = wordFrequencyQuery.getOrDefault(word, 0);

            dotProduct += frequencyText * frequencyQuery;
            magnitudeText += Math.pow(frequencyText, 2);
            magnitudeQuery += Math.pow(frequencyQuery, 2);
        }

        magnitudeText = Math.sqrt(magnitudeText);
        magnitudeQuery = Math.sqrt(magnitudeQuery);

        if (magnitudeText == 0 || magnitudeQuery == 0) {
            return 0.0;
        } else {
            return dotProduct / (magnitudeText * magnitudeQuery);
        }
    }

    private static List<String> rankFiles(String[] fileNames, List<Double> similarities) {
        List<String> rankedFiles = new ArrayList<>();

        // Create a map of file name to similarity score
        Map<String, Double> fileSimilarities = new HashMap<>();
        for (int i = 0; i < fileNames.length; i++) {
            fileSimilarities.put(fileNames[i], similarities.get(i));
        }

        // Sort the files based on similarity score in descending order
        fileSimilarities.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(entry -> rankedFiles.add(entry.getKey()));

        return rankedFiles;
    }
}
