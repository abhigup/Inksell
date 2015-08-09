package models;

import enums.CategoryType;

/**
 * Created by Abhinav on 09/08/15.
 */
public class CategoryEntity {

    public CategoryEntity(CategoryType type, String category)
    {
        this.type = type;
        this.category = category;
    }

    public CategoryType type;

    public String category;

    @Override
    public String toString() {
        return this.category;            // What to display in the Spinner list.
    }
}
