import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Array of filenames to process
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
                "file10.txt"};

        Scanner scanner = new Scanner(System.in);

// Create an instance of the PositionalIndex class to store the term positions in documents
        PositionalIndex positionalIndex = new PositionalIndex();

        try {
            for (int i = 0; i < filenames.length; i++) {
                String filename = filenames[i];

                // Create a FileReader to read the contents of the file
                FileReader fileReader = new FileReader(filename);

                // Create a BufferedReader for efficient reading
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                // Assign a unique document ID
                int docId = i + 1;

                // String to store each line of the file
                String line;

                // Initialize the position of each term in the document
                int position = 1;

                // loop to Read each line in the file
                while ((line = bufferedReader.readLine()) != null) {

                    // Replace non-alphanumeric characters with a space
                    line = line.replaceAll("[^a-zA-Z0-9]", " ");

                    // Normalize whitespace by replacing multiple spaces with a single space
                    line = line.replaceAll("\\s+", " ");

                    // Remove leading and trailing spaces
                    line = line.trim();

                    // Convert the line to lowercase
                    line = line.toLowerCase();

                    // Split the line into individual words
                    String[] words = line.split(" ");

                    // Process each word in the line
                    for (String word : words) {

                        // Add the term to the positional index with document ID and position
                        positionalIndex.addTerm(word, docId, position);

                        // Increment the position for the next term
                        position++;

                    }
                }

                // Close the BufferedReader to free system resources
                bufferedReader.close();

            }
        } catch (IOException e) {

            // Print the stack trace if an exception occurs during file processing
            e.printStackTrace();
        }

        // Prompt the user to enter a query
        System.out.print("Enter a query: ");

        // Read the query input from the user
        String query = scanner.nextLine();

        // Convert the query to lowercase for case-insensitive matching
        query = query.toLowerCase();


        // Map to store document scores based on the query
        Map<Integer, Double> documentScores = new HashMap<>();

        // Split the query into individual terms
        // Split the query based on non-word characters to extract individual terms
        String[] queryTerms = query.split("\\W+");


        // Calculate scores for each document based on the query
        for (int docId = 1; docId <= filenames.length; docId++) {
            double score = 0;

            // Calculate score for each term in the query
            for (String term : queryTerms) {

                // Get term frequency in the query
                int termFrequencyInQuery = positionalIndex.getTermFrequencyInQuery(term, queryTerms);

                // Get term frequency in the current document
                int termFrequencyInDocument = positionalIndex.getTermFrequencyInDocument(term, docId);

                // Calculate the score contribution for the term in the document
                score += termFrequencyInQuery * termFrequencyInDocument;
            }

            // Calculate the denominator for normalization
            double documentVectorLength = positionalIndex.getDocumentVectorLength(docId);
            double queryVectorLength = positionalIndex.getQueryVectorLength(queryTerms);
            double denominator = documentVectorLength * queryVectorLength;

            // Normalize the score and store it for the document
            score /= denominator;
            documentScores.put(docId, score);
        }


        // Sort the documents based on scores
        List<Map.Entry<Integer, Double>> sortedDocuments = new ArrayList<>(documentScores.entrySet());
        sortedDocuments.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));


        // Print the sorted documents and their scores
        for (Map.Entry<Integer, Double> entry : sortedDocuments) {
            int docId = entry.getKey();
            String filename = filenames[docId - 1];
            double score = entry.getValue();
            System.out.println(filename + ": " + score);
        }

        // Prompt the user to enter another word
        System.out.print("Enter another word: ");

        // Read the word input from the user
        String word = scanner.nextLine();

        // Calculate and print TF-IDF scores for the given word
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


        // Close the scanner to free system resources
        scanner.close();
    }



    public static Map<String, Map<Integer, String>> getTF_IDF(PositionalIndex positionalIndex, int numOfDocuments) {
        // Map to store document lengths (total words in each document)
        Map<Integer, Integer> documentLengths = new HashMap<>();

        for (int docId = 1; docId <= numOfDocuments; docId++) {
            // Calculate the length (total words) of the document
            int length = positionalIndex.getDocumentWordsLength(docId);

            // Store the document length in the map
            documentLengths.put(docId, length);
        }

        // Map to store term frequencies for each term in each document
        Map<String, Map<Integer, Integer>> termFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {

            // Map to store term frequencies for each document
            Map<Integer, Integer> frequencies = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                // Calculate the term frequency of the term in the document
                int frequency = positionalIndex.getTermFrequencyInDocument(term, docId);

                // Store the term frequency in the map
                frequencies.put(docId, frequency);
            }

            // Store the term frequencies for the term in the outer map
            termFrequencies.put(term, frequencies);
        }

        // Map to store normalized term frequencies for each term in each document
        Map<String, Map<Integer, Double>> normalizedTermFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {
            // Map to store normalized term frequencies for each document
            Map<Integer, Double> frequencies = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                // Retrieve the term frequency of the term in the document
                int frequency = termFrequencies.get(term).get(docId);

                // Retrieve the document length of the document
                int length = documentLengths.get(docId);

                // Calculate the normalized term frequency by dividing the term frequency by the document length
                double normalizedFrequency = frequency / (double) length;

                // Store the normalized term frequency in the map
                frequencies.put(docId, normalizedFrequency);
            }

            // Store the normalized term frequencies for the term in the outer map
            normalizedTermFrequencies.put(term, frequencies);
        }

        // Map to store inverse document frequencies for each term
        Map<String, Double> inverseDocumentFrequencies = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {
            // Retrieve the inverse document frequency of the term
            double IDF = positionalIndex.getInverseDocumentFrequency(term);

            // Store the inverse document frequency in the map
            inverseDocumentFrequencies.put(term, IDF);
        }

        // Map to store TF-IDF scores for each term and document
        Map<String, Map<Integer, String>> TF_IDF = new HashMap<>();

        for (String term : positionalIndex.getTerms()) {
            // Map to store TF-IDF scores for each document
            Map<Integer, String> TF_IDFScores = new HashMap<>();

            for (int docId = 1; docId <= numOfDocuments; docId++) {
                // Retrieve the normalized term frequency of the term in the document
                double TF = normalizedTermFrequencies.get(term).get(docId);

                // Retrieve the inverse document frequency of the term
                double IDF = inverseDocumentFrequencies.get(term);

                // Calculate the TF-IDF score by multiplying the normalized term frequency and the inverse document frequency
                DecimalFormat df = new DecimalFormat("0.000");
                String TF_IDFScore = df.format(TF * IDF);

                // Store the TF-IDF score for the document in the map
                TF_IDFScores.put(docId, TF_IDFScore);
            }

            // Store the TF-IDF scores for the term in the outer map
            TF_IDF.put(term, TF_IDFScores);
        }

        // Return the map containing the TF-IDF scores for each term and document
        return TF_IDF;
    }

}