import java.util.*;

class PositionalIndex {

    // Map to store the index, where each term is associated with a DictEntry object
    private Map<String, DictEntry> index;

    // Total number of documents in the collection
    private int collectionSize;

    public PositionalIndex() {
        // Initialize the index as a HashMap
        index = new HashMap<>();

        // Set the default collection size to 10 (can be changed as per requirements)
        collectionSize = 10;
    }

    public void addTerm(String term, int docId, int position) {

        // Add the term to the index if it doesn't exist already
        index.putIfAbsent(term, new DictEntry());

        // Get the DictEntry object for the term from the index
        DictEntry entry = index.get(term);

        // Increment the term frequency (number of times the term occurs in the collection)
        entry.term_freq++;

        // Add a posting (document ID and position) for the term
        entry.addPosting(docId, position);

        // Increment the document frequency if this is the first occurrence of the term in the document
        if (entry.postings.get(docId).size() == 1) {
            entry.doc_freq++;
        }
    }

    public int getDocumentWordsLength(int docId) {
        int length = 0;
        for (DictEntry entry : index.values()) {
            if (entry.postings.containsKey(docId)) {
                // Get the number of positions (words) for the term in the document
                length += entry.postings.get(docId).size();
            }
        }

        // Return the total number of words in the document
        return length;
    }

    public double getInverseDocumentFrequency(String term) {
        if (!index.containsKey(term)) {
            // If the term doesn't exist in the index, return 0 as its inverse document frequency
            return 0;
        }

        // Get the document frequency (number of documents containing the term)
        int docFreq = index.get(term).doc_freq;

        // Calculate the inverse document frequency using logarithm and return it
        return Math.log10(collectionSize / (double) docFreq);
    }

    public int getTermFrequencyInDocument(String term, int docId) {

        // Get the DictEntry object for the term
        DictEntry entry = index.get(term);
        if (entry != null && entry.postings.containsKey(docId)) {

            // Return the term frequency (number of positions) for the term in the document
            return entry.postings.get(docId).size();
        }

        // If the term or document doesn't exist, return 0 as the term frequency
        return 0;
    }

    public int getTermFrequencyInQuery(String term, String[] queryTerms) {
        int frequency = 0;
        for (String queryTerm : queryTerms) {
            if (queryTerm.equals(term)) {

                // Count the number of times the term occurs in the query
                frequency++;
            }
        }

        // Return the term frequency in the query
        return frequency;
    }

    public double getDocumentVectorLength(int docId) {
        double length = 0.0;
        for (DictEntry entry : index.values()) {
            if (entry.postings.containsKey(docId)) {

                // Get the term frequency (number of positions) for the term in the document
                int termFrequency = entry.postings.get(docId).size();

                // Add the square of the term frequency to the length
                length += Math.pow(termFrequency, 2);
            }
        }

        // Return the square root of the length as the document vector length
        return Math.sqrt(length);
    }

    public double getQueryVectorLength(String[] queryTerms) {
        double length = 0.0;
        for (String term : queryTerms) {

            // Get the term frequency (number of times it occurs) in the query
            int termFrequency = getTermFrequencyInQuery(term, queryTerms);

            // Add the square of the term frequency to the length
            length += Math.pow(termFrequency, 2);
        }

        // Return the square root of the length as the query vector length
        return Math.sqrt(length);
    }

    public Set<String> getTerms() {

        // Return a set of all the terms present in the index
        return index.keySet();
    }
}

