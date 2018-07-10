package comboboxnewvaluetest;

import com.sun.javafx.collections.ObservableListWrapper;
import comboboxnewvaluetest.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 *
 * @author george
 */
public class CBoxNewValueTest extends Application {
    
    private StringConverter<Product> getStrConverter(List<Product> products) {
        return new StringConverter<Product>() {
            @Override
            public String toString(Product object) {
                return object == null ? "" : object.getName();
            }

            @Override
            public Product fromString(String string) {
                Optional<Product> found = products.stream().filter((pd) -> pd.getName().compareToIgnoreCase(string) == 0).findFirst();
                if(found.isPresent())
                    return found.get();
                return (Product)null;
            }
        };
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<Product> products = new ArrayList<>(10);
        products.add(new Product("Beer"));
        products.add(new Product("Vodka"));
        products.add(new Product("Wine"));
        products.add(new Product("Marmalade"));
        
        ObservableListWrapper<Product> obsProds = new ObservableListWrapper<Product>(products);
        
        ComboBox<Product> cmb = new ComboBox<>(obsProds);
        cmb.setConverter(getStrConverter(products));
        cmb.setEditable(true);
        ComboBoxNewValue<Product> cbxnv = new ComboBoxNewValue<>(
                cmb, 
                (s) -> {
                    TextInputDialog tidlg = new TextInputDialog(s);
                    tidlg.setTitle("Attention, please");
                    tidlg.setHeaderText("Are you sure you want to enter a new product?");
                    Optional<String> newName = tidlg.showAndWait();
                    Product result = null;
                    if(newName.isPresent()) {
                        result = new Product(newName.get());
                        obsProds.add(result);
                    }
                    return result;
                }, true, true);
        
        Button btn = new Button("This does nothing");
        
        FlowPane root = new FlowPane(cmb, btn);
        
        Scene scene = new Scene(root, 500, 250);
        
        primaryStage.setTitle("Combo Box new values are (hopefully) handled!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
