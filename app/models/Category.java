package models;

import play.data.validation.Constraints;
import java.io.Serializable;

public class Category implements Serializable {
    public Long id;

    @Constraints.Required(message = "Category name is required")
    public String name;

    public Category() {}

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}