package org.studia.barterapplication.models;

/**
 * This is the model class of Category
 */

public class Category {

    //Properties
    String categoryName;
    String id;

    //Constructors
    public Category() {// dont delete this
    }
    


    /**This constructor initializes categoryName and picture
     * @param name
     * */
    public Category(String name)
    {
        categoryName = name;
    }

    /**This constructor initializes categoryName, picture and id
     * @param categoryName
     * @param id
     * */
    public Category(String categoryName, String id) {
        this.categoryName = categoryName;
        this.id = id;
    }

    //Methods

    /**
     * Set method for category name
     * @param categoryName is the desired category name
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     *Get method for category's name
     * @return category's name as a string
     */
    public String getCategoryName() {
        return categoryName;
    }


    /**
     * Equals method for category object in order to check if two objects are the same category
     * @param o is an object
     * @return true if object o and this are the same category
     */
    @Override
    public boolean equals(Object o) {

        if( o == null ){
            return false;
        }
        else if( o instanceof Category){
            Category otherCategory = (Category) o;
            return this.getId().equals( otherCategory.getId());
        }
        else {
            return false;
        }

    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * ToString method for category object
     * @return category's name as a string
     */
    @Override
    public String toString() {

        return  getCategoryName();
    }

    /**
     * getId method
     * @return category's id
     */
    public String getId() {
        return id;
    }

}

