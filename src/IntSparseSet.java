import java.util.AbstractSet;
import java.util.Iterator;

/**
 * Created by khan on 19.03.16. IntSparseSet
 */

class IntSparseSet extends AbstractSet<Integer> {
    private final int low, high;
    private final int[] sparse, dense;
    private int n;

    public IntSparseSet(int low, int high) {
        this.high = high;
        this.low = low;
        this.n = 0;
        sparse = new int[high - low + 1];
        dense = new int[high - low + 1];
    }

    private boolean setContains(Integer x) {
        int index = Math.abs(x - low);
        return 0 <= sparse[index] && sparse[index] < n && dense[sparse[index]] == x;
    }

    private boolean inRange(Integer x) {
        return x >= low && x < high;
    }

    @Override
    public boolean contains(Object o) {
        Integer x = (Integer) o;
        return inRange(x) && setContains(x);
    }

    @Override
    public boolean add(Integer x) {
        if (inRange(x) && !setContains(x)) {
            dense[n] = x;
            sparse[Math.abs(x - low)] = n++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        Integer x = (Integer) o;
        if (contains(x)) {
            int temp = Math.abs(x - low);
            dense[sparse[temp]] = dense[n - 1];
            sparse[dense[n - 1] - low] = sparse[temp];
            n--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        n = 0;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntSparseSetIterator();
    }

    private class IntSparseSetIterator implements Iterator<Integer> {
        private int i;

        @Override
        public boolean hasNext() {
            return i < n;
        }

        @Override
        public Integer next() {
            return dense[i++];
        }

        @Override
        public void remove() {
            IntSparseSet.this.remove(dense[i - 1]);
        }
    }
}