package org.studia.barterapplication.models;

/**
 * This interface allows user to sort lists, it is used in Advertisements page
 */
public interface Sortable {


    /**
     * This method sorts a list according to prices.
     * @param isLowToHigh if it is true list will be sorted from the lowest price to the highest
     *               if it is false list will be sorted from the highest  price to the lowest
     * */
    void sortByPrice( boolean isLowToHigh);


    /**
     * This method sorts a list by date.
     * @param isNewToOld if it is true list will be sorted by date from newest to oldest
     *               if it is false list will be sorted by date from oldest to newest
     * */
    void sortByDate( boolean isNewToOld);

}