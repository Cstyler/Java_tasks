/**
 * Created by khan on 10.03.16.
 */

class Element<T> {

    private final T x;
    private Element<T> parent;
    private int depth;

    public Element(T x) {
        this.x = x;
        parent = this;
        depth = 0;
    }

    public T x() {
        return x;
    }

    public boolean equivalent(Element<T> elem) {
        return find(this) == find(elem);
    }

    private Element<T> find(Element<T> x) {
        if (x.parent == x)
            return x;
        else
            return x.parent = find(x.parent);
    }

    public void union(Element<T> elem) {
        Element<T> rootX = find(this);
        Element<T> rootY = find(elem);
        if (rootX.depth < rootY.depth) {
            rootX.parent = rootY;
        } else {
            rootY.parent = rootX;
            if (rootX.depth == rootY.depth && rootX != rootY) {
                rootX.depth++;
            }
        }
    }
}