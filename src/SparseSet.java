import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

interface Hintable {
    void setHint(int hint);

    int hint();
}

class SparseSet<T extends Hintable> extends AbstractSet<T> {
    private final ArrayList<T> dense;

    public SparseSet() {
        this.dense = new ArrayList<>();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        return dense.size() != 0 && dense.get(((T) o).hint()) == o;
    }

    @Override
    public boolean add(T t) {
        if (!contains(t)) {
            t.setHint(dense.size());
            dense.add(t);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked") T t = (T) o;
        if (contains(t)) {
            int last = dense.size() - 1;
            dense.set(t.hint(), dense.get(last));
            dense.get(last).setHint(t.hint());
            dense.remove(last);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        dense.clear();
    }

    @Override
    public int size() {
        return dense.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SparseSetIterator();
    }

    private class SparseSetIterator implements Iterator<T> {
        private int i;

        @Override
        public boolean hasNext() {
            return i < dense.size();
        }

        @Override
        public T next() {
            return dense.get(i++);
        }

        @Override
        public void remove() {
            SparseSet.this.remove(dense.get(i - 1));
        }
    }
}