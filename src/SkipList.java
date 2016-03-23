import java.util.*;

class SkipList<K extends Comparable<K>, V> extends AbstractMap<K, V> {
    private final Elem l;
    private final int levels;
    private final Random rand;
    private int size;

    SkipList(int levels) {
        this.levels = levels;
        l = new Elem();
        rand = new Random(System.currentTimeMillis());
        size = 0;
    }

    private void skip(K key, ArrayList<Elem> p) {
        Elem x = l;
        for (int i = 0; i < levels; i++) {
            p.add(null);
        }
        for (int i = levels - 1; i >= 0; i--) {
            while (x.next.get(i) != null && x.next.get(i).key.compareTo(key) < 0) {
                x = x.next.get(i);
            }
            p.set(i, x);
        }
    }

    private Elem succeed(Elem x) {
        return x.next.get(0);
    }

    @Override
    public boolean isEmpty() {
        return succeed(l) == null;
    }

    @Override
    public boolean containsKey(Object key) {
        ArrayList<Elem> p = new ArrayList<>(levels);
        //noinspection unchecked
        skip((K) key, p);
        Elem x = succeed(p.get(0));
        return x != null && x.key.equals(key);
    }

    @Override
    public V get(Object key) {
        ArrayList<Elem> p = new ArrayList<>(levels);
        //noinspection unchecked
        skip((K) key, p);
        Elem x = succeed(p.get(0));
        if (x == null || !x.key.equals(key)) {
            return null;
        }
        return x.value;
    }

    @Override
    public V put(K key, V value) {
        ArrayList<Elem> p = new ArrayList<>(levels);
        skip(key, p);
        if (succeed(p.get(0)) != null && succeed(p.get(0)).key.equals(key)) {
            V temp = succeed(p.get(0)).value;
            succeed(p.get(0)).value = value;
            return temp;
        }
        Elem x = new Elem(key, value);
        int r = rand.nextInt() * 2, i;
        for (i = 0; i < levels && r % 2 == 0; i++) {
            x.next.add(p.get(i).next.get(i));
            p.get(i).next.set(i, x);
            r /= 2;
        }
        for (; i < levels; i++) {
            x.next.add(null);
        }
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        ArrayList<Elem> p = new ArrayList<>(levels);
        //noinspection unchecked
        skip((K) key, p);
        Elem x = succeed(p.get(0));
        if (x == null || !x.key.equals(key)) {
            return null;
        }
        for (int i = 0; i < levels && p.get(i).next.get(i) == x; i++) {
            p.get(i).next.set(i, x.next.get(i));
        }
        size--;
        return x.value;
    }

    @Override
    public void clear() {
        size = 0;
        Elem x = l;
        while (succeed(x) != null) {
            Elem temp = succeed(x);
            for (int i = 0; i < x.next.size(); i++) {
                x.next.set(i, null);
            }
            x = temp;
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private Elem currentElem = l;

                    @Override
                    public boolean hasNext() {
                        return succeed(currentElem) != null;
                    }

                    @Override
                    public Entry<K, V> next() {
                        currentElem = succeed(currentElem);
                        class skipListEntry extends SimpleEntry<K, V> {

                            private skipListEntry(K key, V value) {
                                super(key, value);
                            }

                            @Override
                            public V setValue(V value) {
                                put(getKey(), value);
                                return value;
                            }
                        }
                        return new skipListEntry(currentElem.key, currentElem.value);
                    }

                    @Override
                    public void remove() {
                        SkipList.this.remove(currentElem.key);
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    private class Elem {
        private final ArrayList<Elem> next;
        private K key;
        private V value;

        Elem() {
            next = new ArrayList<Elem>(levels) {{
                for (int i = 0; i < levels; i++) add(null);
            }};
        }

        Elem(K key, V value) {
            next = new ArrayList<>(levels);
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}