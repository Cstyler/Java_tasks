import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;

interface Hintable {
    void setHint(int hint);

    int hint();
}

class SparseSet<T extends Hintable> extends AbstractSet<T> {
    private final ArrayList<T> dense;
    private int n;

    public SparseSet() {
        this.dense = new ArrayList<>();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        return n != 0 && dense.get(((T) o).hint()) == o;
    }

    @Override
    public boolean add(T t) {
        if (!contains(t)) {
            t.setHint(n++);
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
            int last = n-- - 1;
            dense.set(t.hint(), dense.get(last));
            dense.get(last).setHint(t.hint());
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
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i;

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public T next() {
                return dense.get(i++);
            }

            @Override
            public void remove() {
                SparseSet.this.remove(dense.get(i - 1));
            }
        };
    }
}