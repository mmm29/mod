package mod;

import java.util.Iterator;

public interface IWarehouse {
     Iterator<IProduct> listProducts();

     IProduct findProduct(String name);

     boolean removeProduct(String name);

     boolean addProduct(String name, int price);
}
