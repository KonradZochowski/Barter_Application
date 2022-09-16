package org.studia.barterapplication.models;

/**
 * This is the interface Filterable. It allows the user to make filtered searches on Advertisements
 */
public interface Filterable {

    /**This filter posts according to their city property.
     * @param city
     * @return
     */
    Advertisements filterByCity(String city, String aVoivodeship);

    /**This filter posts according to its price and boundaries given.
     * @param minPrice
     * @param maxPrice
     * @return
     */
    Advertisements filterByPrice(int minPrice, int maxPrice);


    /**This filter posts according to given word
     * @param category
     * @return
     */
    Advertisements filterByCategory(String category);


}
