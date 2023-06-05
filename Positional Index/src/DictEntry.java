import java.util.*;

class DictEntry {

    int doc_freq;
    int term_freq;
    Map<Integer, List<Integer>> postings;

    public DictEntry() {
        doc_freq = 0;
        term_freq = 0;
        postings = new HashMap<>();
    }

    public void addPosting(int docId, int position) {
        postings.putIfAbsent(docId, new ArrayList<>());
        postings.get(docId).add(position);
    }
}
