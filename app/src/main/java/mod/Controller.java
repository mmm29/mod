package mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

class ProductItem {
    private IProduct product;

    public ProductItem(IProduct product) {
        this.product = product;
    }

    public IProduct getProduct() {
        return this.product;
    }

    @Override
    public String toString() {
        return this.product.getName();
    }
}

public class Controller {
    private IWarehouse warehouse;

    @FXML
    private TextField productNameField;
    @FXML
    private TextField addProductNameField;
    @FXML
    private TextField addProductPriceField;
    @FXML
    private TextField removeProductField;
    @FXML
    private Text addProductMessage;
    @FXML
    private Text searchProductMessage;
    @FXML
    private Text removeProductMessage;
    @FXML
    private ListView<ProductItem> productsList;
    @FXML
    private Text productInfoNameText;
    @FXML
    private Text productInfoPriceText;
    @FXML
    private Pane productInfoPane;

    public Controller(IWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    @FXML
    public void initialize() {
        productsList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                this.updateProductSelection(newValue.getProduct());
            }
        );
        productInfoPane.setVisible(false);
    }

    public void onSearchAction(ActionEvent e) {
        String productName = this.productNameField.getText();
        IProduct product = this.warehouse.findProduct(productName);

        if (product != null) {
            updateProductSelection(product);
            selectProductItemByName(product.getName());
        }
        else {
            showSearchProductMessage("Product not found");
        }
    }

    private boolean selectProductItemByName(String productName) {
        int itemIndex = findProductItemIndexByName(productName);
        if (itemIndex == -1) {
            return false;
        }

        productsList.getSelectionModel().select(itemIndex);
        return true;
    }

    private int findProductItemIndexByName(String productName) {
        ObservableList<ProductItem> items = productsList.getItems();

        int i = 0;
        for (ProductItem productItem : items) {
            if (productItem.getProduct().getName() == productName) {
                return i;
            }

            i++;
        }

        return -1;
    }

    private void updateProductSelection(IProduct product) {
        productInfoNameText.setText(product.getName());
        productInfoPriceText.setText(String.valueOf(product.getPrice()));
        productInfoPane.setVisible(true);
    }

    private void showMessageFor(Text messageNode, String message, int millis) {
        messageNode.setText(message);
        messageNode.setVisible(true);

        // Hide message after `millis` milliseconds.
        PauseTransition visiblePause = new PauseTransition(Duration.millis(millis));
        visiblePause.setOnFinished(event1 -> messageNode.setVisible(false));
        visiblePause.play();
    }

    private void showMessage(Text messageNode, String message) {
        showMessageFor(messageNode, message, 2000);
    }

    private void showSearchProductMessage(String message) {
        showMessage(this.searchProductMessage, message);
    }

    private void showAddProductMessage(String message) {
        showMessage(this.addProductMessage, message);
    }

    private void showRemoveProductMessage(String message) {
        showMessage(this.removeProductMessage, message);
    }

    private void updateProductList() {
        // Retrieve the product list.
        Iterator<IProduct> iterator = this.warehouse.listProducts();
        List<IProduct> products = new ArrayList<>();

        while (iterator.hasNext()) {
            products.add(iterator.next());
        }

        // Sort products by name.
        products.sort(new Comparator<IProduct>() {
            @Override
            public int compare(IProduct p1, IProduct p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });

        // Update the list view items.
        ObservableList<ProductItem> items = this.productsList.getItems();
        items.clear();
        
        for (IProduct product : products) {
            items.add(new ProductItem(product));
        }
    }

    public void onAddProductAction(ActionEvent e) {
        String productName = this.addProductNameField.getText();
        String productPriceText = this.addProductPriceField.getText();

        try {
            int productPrice = Integer.parseInt(productPriceText);
            if (this.warehouse.addProduct(productName, productPrice)) {
                // Successfully added a product.
                showAddProductMessage("Success");
            }
            else {
                // Failed to add the product.
                // Product already exists.
                showAddProductMessage("Product already exists");
            }
        }
        catch (NumberFormatException ex) {
            // The product price text field does not contain a number.
            showAddProductMessage("Price is not a number");
        }

        updateProductList();
    }

    public void onRemoveAction(ActionEvent e) {
        String productName = this.removeProductField.getText();
        if (this.warehouse.removeProduct(productName)) {
            // Successfully deleted.
            showRemoveProductMessage("Product removed");
        }
        else {
            // Product not found
            showRemoveProductMessage("Product not found");
        }

        updateProductList();
        this.productInfoPane.setVisible(false);
    }
}
