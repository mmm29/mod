package mod;

import java.util.HashMap;
import java.util.Iterator;

class IteratorAdapter<A, B> implements Iterator<B> {
    private final Iterator<A> iter;

    public IteratorAdapter(Iterator<A> iter) {
        this.iter = iter;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public B next() {
        A nextElement = iter.next();
        return (B) nextElement;
    }
}

public class MockWarehouse implements IWarehouse {
    private HashMap<String, MockProduct> products;

    public MockWarehouse() {
        this.products = new HashMap<>();
    }

    @Override
    public boolean addProduct(String name, int price) {
        MockProduct product = new MockProduct(name, price);

        if (this.products.containsKey(name)) {
            return false;
        }

        this.products.put(name, product);
        return true;
    }

    @Override
    public IProduct findProduct(String name) {
        return this.products.get(name);
    }

    @Override
    public Iterator<IProduct> listProducts() {
        return new IteratorAdapter<MockProduct, IProduct>(this.products.values().iterator());
    }

    @Override
    public boolean removeProduct(String name) {
        return this.products.remove(name) != null;
    }
}