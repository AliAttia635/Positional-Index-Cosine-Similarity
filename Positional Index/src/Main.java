import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String[] filenames = {
                "file1.txt",
                "file2.txt",
                "file3.txt",
                "file4.txt",
                "file5.txt",
                "file6.txt",
                "file7.txt",
                "file8.txt",
                "file9.txt",
                "file10.txt"
        };

        Scanner scanner = new Scanner(System.in);

        PositionalIndex positionalIndex = new PositionalIndex();

        try {
            for (int i = 0; i < filenames.length; i++) {
                String filename = filenames[i];
                FileReader fileReader = new FileReader(filename);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                int docId = i + 1;
                String line;
                int position = 1;

                while ((line = bufferedReader.readLine()) != null) {
                    line = line.replaceAll("[^a-zA-Z0-9]", " ");
                    line = line.replaceAll("\\s+", " ");
                    line = line.trim();
                    line = line.toLowerCase();
                    String[] words = line.split(" ");

                    for (String word : words) {
                        positionalIndex.addTerm(word, docId, position);
                        position++;
                    }
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("Enter a query: ");
        String query = scanner.nextLine();
        query = query.toLowerCase();

        Map<Integer, Double> documentScores = new HashMap<>();
        String[] queryTerms = query.split("\\W+");

        for (int docId = 1; docId <= filenames.length; docId++) {
            double score = 0;
            for (String term : queryTerms) {
                int termFrequencyInQuery = positionalIndex.getTermFrequencyInQuery(term, queryTerms);
                int termFrequencyInDocument = positionalIndex.getTermFrequencyInDocument(term, docId);
                score += termFrequencyInQuery * termFrequencyInDocument;
            }

            double documentVectorLength = positionalIndex.getDocumentVectorLength(docId);
            double queryVectorLength = positionalIndex.getQueryVectorLength(queryTerms);
            double denominator = documentVectorLength * queryVectorLength;

            score /= denominator;
            documentScores.put(docId, score);
        }


        List<Map.Entry<Integer, Double>> sortedDocuments = new ArrayList<>(documentScores.entrySet());
        sortedDocuments.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));


        for (Map.Entry<Integer, Double> entry : sortedDocuments) {
            int docId = entry.getKey();
            String filename = filenames[docId - 1];
            double score = entry.getValue();
            System.out.println(filename + ": " + score);
        }

        System.out.print("Enter another word: ");

        String word = scanner.nextLine();

        Map<String, Map<Integer, String>> tfIdfScores = getTF_IDF(positionalIndex, filenames.length);
        Map<Integer, String> tfIdfScoresForWord = tfIdfScores.get(word);

        if (tfIdfScoresForWord != null) {
            for (Map.Entry<Integer, String> entry : tfIdfScoresForWord.entrySet()) {
                int docId = entry.getKey();
                String filename = filenames[docId - 1];
                String tfIdfScore = entry.getValue();
                System.out.println("TF-IDF score for " + word + " in " + filename + ": " + tfIdfScore);
            }
        } else {
            System.out.println("No TF-IDF scores found for the word: " + word);
        }

        scanner.close();
    }

    public static Map<String, Map<Integer, String>> getTF_IDF(PositionalIndex positionalIndex, int numOfDocuments) {
        Map<Integer, Integer> documentLengths = new HashMap<>();

        for (int docId = 1; docId <= numOfDocuments; docId++) {
            int length = positionalIndex.getDocumentWordsLength(docId);
            documentLengths.put(docId, length);
        }

        Map<String, Map<Integer, Integer>> termFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {

            Map<Integer, Integer> frequencies = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                int frequency = positionalIndex.getTermFrequencyInDocument(term, docId);

                frequencies.put(docId, frequency);
            }
            termFrequencies.put(term, frequencies);
        }

        Map<String, Map<Integer, Double>> normalizedTermFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {
            Map<Integer, Double> frequencies = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                int frequency = termFrequencies.get(term).get(docId);
                int length = documentLengths.get(docId);
                double normalizedFrequency = frequency / (double) length;
                frequencies.put(docId, normalizedFrequency);
            }

            normalizedTermFrequencies.put(term, frequencies);
        }

        Map<String, Double> inverseDocumentFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {
            double IDF = positionalIndex.getInverseDocumentFrequency(term);
            inverseDocumentFrequencies.put(term, IDF);
        }

        Map<String, Map<Integer, String>> TF_IDF = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {

            Map<Integer, String> TF_IDFScores = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                double TF = normalizedTermFrequencies.get(term).get(docId);
                double IDF = inverseDocumentFrequencies.get(term);

                DecimalFormat df = new DecimalFormat("0.000");
                String TF_IDFScore = df.format(TF * IDF);

                TF_IDFScores.put(docId, TF_IDFScore);
            }
            TF_IDF.put(term, TF_IDFScores);
        }
        return TF_IDF;
    }

}