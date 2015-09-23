package models;

/**
 * Created by Abhinav on 23/09/15.
 */
public class TagsEntity {

    public int tagId;

    public String tagName;

    @Override
    public String toString() {
        return this.tagName;            // What to display in the AutoComplete list.
    }
}
