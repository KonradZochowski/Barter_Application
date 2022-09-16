package org.studia.barterapplication.models;

import java.util.ArrayList;

import java.util.Collections;

/**
 * Advertisements lists posts. Users can exchange items in here
 */
public class Advertisements implements Filterable, Sortable{

    //Properties
    ArrayList<Post> posts;

    //Constructors
    /**
     * Constructor that initializes posts as empty list
     * */
    public Advertisements()
    {
        posts = new ArrayList<Post>();
    }

    /**
     * Constructor that initializes posts with given Arraylist
     * @param posts List of posts
     * */
    public Advertisements(ArrayList<Post> posts)
    {
        this.posts = posts;
    }

    //Methods
    /**
     * Accessor method of posts.
     * */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Setter method of posts.
     * */
    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    /**This filter posts according to their city property.
     * @param city
     * @return
     */
    @Override
    public Advertisements filterByCity(String city, String aVoivodeship) {

        ArrayList<Post> postsFiltered = new ArrayList<Post>();
        if(city.matches("Cała Polska")){
            postsFiltered.addAll(this.getPosts());
        }
        else if(city.matches("Całe Dolnośląskie|Całe Kujawsko-pomorskie|Całe Lubelskie|" +
                "Całe Lubuskie|Całe Łódzkie|Całe Małopolskie|Całe Mazowieckie|Całe Opolskie|" +
                "Całe Podkarpackie|Całe Podlaskie|Całe Pomorskie|Całe Śląskie|Całe Świętokrzyskie|" +
                "Całe Warmińsko-mazurskie|Całe Wielkopolskie|Całe Zachodniopomorskie")){
            for(Post aPost : this.getPosts())
            {
                if(aPost.getVoivodeship().equals(aVoivodeship)){
                    postsFiltered.add(aPost);
                }
            }
        }
        else {
                for(Post aPost : this.getPosts())
                {
                    if(aPost.getCity().equals(city)){
                        postsFiltered.add(aPost);
                    }
                }
        }
        return new Advertisements(postsFiltered);
    }

    /**This filter posts according to its price and boundaries given.
     * @param minPrice
     * @param maxPrice
     * @return
     */
    @Override
    public Advertisements filterByPrice(int minPrice, int maxPrice) {
        ArrayList<Post> postsFiltered = new ArrayList<Post>();

        for(Post aPost : this.getPosts())
        {
            if(aPost.getPrice() <= maxPrice && aPost.getPrice() >= minPrice)
            {
                postsFiltered.add(aPost);
            }
        }
        return new Advertisements(postsFiltered);
    }


    /**This filter posts according to given category
     * @param category
     * @return
     */
    @Override
    public Advertisements filterByCategory(String category) {
        ArrayList<Post> postsFiltered = new ArrayList<Post>();

        if (category.matches("Wszystkie kategorie")){
            postsFiltered.addAll(this.getPosts());
        }
        else {
            for (Post aPost : this.getPosts()) {
                if (aPost.getCategory().getCategoryName().equals(category)) {
                    postsFiltered.add(aPost);
                }
            }
        }
            return new Advertisements(postsFiltered);
    }

    /**
     * This method sorts advertisements's post list according to prices.
     * @param isLowToHigh if it is true advertisements will be sorted from the lowest price to the highest
     *               if it is false advertisements will be sorted from the highest  price to the lowest
     * */
    @Override
    public void sortByPrice(boolean isLowToHigh) {

        Collections.sort(posts);

        if(!isLowToHigh)
        {
            Collections.reverse(posts);
        }
    }


    /**
     * This method sorts a list by date.
     * @param isNewToOld if it is true list will be sorted by date from newest to oldest
     *               if it is false list will be sorted by date from oldest to newest
     * */
    @Override
    public void sortByDate(boolean isNewToOld) {
        Collections.sort(posts, new DateComparator());

        if (!isNewToOld){
            Collections.reverse(posts);
        }
    }

    @Override
    public String toString() {
        return posts.toString();
    }
}

