import java.util.*;

class DictEntry {

    int doc_freq; // Frequency of the term in the documents
    int term_freq; // Frequency of the term in the current document
    Map<Integer, List<Integer>> postings; // Map to store the document IDs and their corresponding positions

    public DictEntry() {
        doc_freq = 0;
        term_freq = 0;

        // Initialize the map to store the document IDs and their positions
        postings = new HashMap<>();
    }

    // Add a posting to the entry for the given document ID and position
    public void addPosting(int docId, int position) {


        // Check if the document ID already exists in the postings map
        postings.putIfAbsent(docId, new ArrayList<>());
        // If the document ID doesn't exist, create a new empty list for it

        // Add the position to the list of positions for the document ID
        postings.get(docId).add(position);
    }
}
