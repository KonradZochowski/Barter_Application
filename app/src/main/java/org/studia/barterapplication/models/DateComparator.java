package org.studia.barterapplication.models;

import java.util.Comparator;

public class DateComparator implements Comparator<Post> {


    //Methods
    /** This method compares date of two post from newest to oldest.
     * @param p1 first post to compare
     * @param p2 second post to compare
     * */
    @Override
    public int compare(Post p1, Post p2) {
        if (p1.getTimestamp() == null || p2.getTimestamp() == null)
            return 0;
        return p2.getTimestamp().compareTo(p1.getTimestamp());

    }
}
