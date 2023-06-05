import java.util.*;

public class Posting {
    int dtf;
    Map<Integer, Integer> positions;
    Posting next;

    public Posting(int docId, int position) {
        dtf = 1;
        positions = new HashMap<>();
        positions.put(docId, position);
        next = null;
    }
}
