package org.studia.barterapplication.models;

import java.util.ArrayList;

/**
 * This class represents the user of the app
 */

public class User{

    //Properties
    String documentId;
    String userName;
    String email;
    String avatar;
    String phoneNumber;
    ArrayList<Post> favourites;
    boolean admin;

    //Constructors
    //this for firebase
    public User() {
    }

    /**Constructor to initialize username, email and id
     * @param userName
     * @param email
     * @param documentId
     * */
    public User(String userName, String email,String documentId) {
        this.email = email;
        this.userName = userName;
        this.documentId = documentId;
        this.avatar = "";
        this.phoneNumber = "";
        this.favourites = new ArrayList<>();
        admin = false;
    }

    /**Constructor to initialize username and email
     * @param userName
     * @param email
     * */
    public User(String email, String userName) {
        this.email = email;
        this.userName = userName;
        this.avatar = "";
        this.phoneNumber = "";
        this.favourites = new ArrayList<>();
        admin = false;
    }

    public User(String userName, String email, String avatar, String phoneNumber)
    {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.favourites = new ArrayList<>();
        admin = false;
    }

    /**Constructor to initialize almost all properties
     * @param userName
     * @param email
     * @param avatar
     * @param phoneNumber
     * @param favourites
     * */
    public User(String userName, String email, String avatar, String phoneNumber, ArrayList<Post> favourites) {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.favourites = favourites;
        admin = false;
    }

    //Methods
    // Accessor Methods
    public String getDocumentId() {
        return documentId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }


    // Mutator Methods

    public void setFavourites(ArrayList<Post> favourites) {
        this.favourites = favourites;
    }

    public void setAvatar(String anAvatar) {
        this.avatar = anAvatar;
    }

    public void setPhoneNumber( String aPhoneNumber) {
        this.phoneNumber = aPhoneNumber;
    }

    public void addOfferToFavourites(Post b) {
        this.favourites.add(b);
    }

    public void removeOfferFromFavourites(int index) {
         this.favourites.remove(index);
    }

    public ArrayList<Post> getFavourites() {
        return favourites;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * This method is used to check if the user is an admin
     * @param admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * toString method of the user
     * @return
     */
    @Override
    public String toString() {
        return this.userName;
    }

    /**
     * This method compares Ids of two users
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {

        if( o == null ){
            return false;
        }
        else if( o instanceof User){
            User otherUser = (User) o;
            return this.getDocumentId().equals( otherUser.getDocumentId());
        }
        else {
            return false;
        }

    }

}

