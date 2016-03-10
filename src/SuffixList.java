import java.util.Iterator;

/**
 * Created by khan on 10.03.16. Home_taks
 */
class SuffixList implements Iterable<String> {
    private StringBuilder s;

    public SuffixList(StringBuilder s) {
        this.s = s;
    }

    public Iterator<String> iterator() {
        return new SuffixIterator();
    }

    class SuffixIterator implements Iterator<String> {
        private int pos;

        public SuffixIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < s.length();
        }

        public String next() {
            return s.substring(pos++, s.length());
        }
    }
}