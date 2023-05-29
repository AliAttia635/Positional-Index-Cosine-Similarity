import java.util.*;

public class Posting {
    int dtf; // Document Term Frequency (frequency of the term in the document)
    Map<Integer, Integer> positions; // Map to store document IDs and their corresponding positions
    Posting next; // Reference to the next posting in the linked list

    public Posting(int docId, int position) {

        dtf = 1; // Initialize the document term frequency to 1 (since this is the first occurrence of the term in the document)
        positions = new HashMap<>(); // Initialize the map to store document IDs and positions
        positions.put(docId, position); // Add the document ID and its corresponding position to the map
        next = null; // Set the reference to the next posting to null (no next posting initially)
    }
}
