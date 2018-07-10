package comboboxnewvaluetest.model;

/**
 *
 * @author george
 */
public class Product {
    private static Integer nextId = 0;
    
    private Integer id;
    private String name;

    public Product(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
