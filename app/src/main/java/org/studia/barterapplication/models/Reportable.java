package org.studia.barterapplication.models;

/**
 * This interface allows the object to be reportable by user.
 */
public interface Reportable {

    /**This method reports a object.
     * @param description
     * @param category
     * @param ownerId
     * */
    void report( String description, int category, String ownerId);
}
