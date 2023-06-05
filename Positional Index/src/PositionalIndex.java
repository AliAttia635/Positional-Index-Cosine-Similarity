import java.util.*;

class PositionalIndex {
    private Map<String, DictEntry> index;

    private int collectionSize;

    public PositionalIndex() {
        index = new HashMap<>();

        collectionSize = 10;
    }

    public void addTerm(String term, int docId, int position) {

        index.putIfAbsent(term, new DictEntry());
        DictEntry entry = index.get(term);
        entry.term_freq++;
        entry.addPosting(docId, position);
        if (entry.postings.get(docId).size() == 1) {
            entry.doc_freq++;
        }
    }

    public int getDocumentWordsLength(int docId) {
        int length = 0;
        for (DictEntry entry : index.values()) {
            if (entry.postings.containsKey(docId)) {
                length += entry.postings.get(docId).size();
            }
        }

        return length;
    }

    public double getInverseDocumentFrequency(String term) {
        if (!index.containsKey(term)) {
            return 0;
        }
        int docFreq = index.get(term).doc_freq;
        return Math.log10(collectionSize / (double) docFreq);
    }

    public int getTermFrequencyInDocument(String term, int docId) {

        DictEntry entry = index.get(term);
        if (entry != null && entry.postings.containsKey(docId)) {
            return entry.postings.get(docId).size();
        }
        return 0;
    }

    public int getTermFrequencyInQuery(String term, String[] queryTerms) {
        int frequency = 0;
        for (String queryTerm : queryTerms) {
            if (queryTerm.equals(term)) {
                frequency++;
            }
        }
        return frequency;
    }

    public double getDocumentVectorLength(int docId) {
        double length = 0.0;
        for (DictEntry entry : index.values()) {
            if (entry.postings.containsKey(docId)) {
                int termFrequency = entry.postings.get(docId).size();
                length += Math.pow(termFrequency, 2);
            }
        }
        return Math.sqrt(length);
    }

    public double getQueryVectorLength(String[] queryTerms) {
        double length = 0.0;
        for (String term : queryTerms) {
            int termFrequency = getTermFrequencyInQuery(term, queryTerms);
            length += Math.pow(termFrequency, 2);
        }
        return Math.sqrt(length);
    }

    public Set<String> getTerms() {
        return index.keySet();
    }
}

