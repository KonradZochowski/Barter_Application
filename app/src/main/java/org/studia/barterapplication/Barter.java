package org.studia.barterapplication;

import android.app.Application;

import androidx.annotation.NonNull;

import org.studia.barterapplication.models.Category;
import org.studia.barterapplication.models.Message;
import org.studia.barterapplication.models.MessageRoom;
import org.studia.barterapplication.models.Offer;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**This class stores necessary and temporary information while app is running.
 * Global class.
 */
public class Barter extends Application {

    private static User currentUser;
    private static Post currentPost;
    static Category currentCategory;
    private static User currentSeller;
    public static final String CHANNEL_ID = "post_notifications";

    public Barter() {
    }

    //accessor methods

    /**Get method for currentCategory in the global class.
     * */
    public static Category getCurrentCategory() {
        return currentCategory;
    }

    /**Get method for currentSeller in the global class.
     * */
    public static User getCurrentSeller() {
        return currentSeller;
    }

    /**Get method for currentUser in the global class.
     * */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**Get method for currentPost in the global class.
     * */
    public static Post getCurrentPost() {
        return currentPost;
    }


    //mutator methods

    /**Set method for currentPost in the global class.
     * @param currentPost
     * */
    public static void setCurrentPost(Post currentPost) {
        Barter.currentPost = currentPost;
    }

    /**Set method for currentSeller in the global class.
     * @param currentSeller
     * */
    public static void setCurrentSeller(User currentSeller) {
        Barter.currentSeller = currentSeller;
    }

    /**Set method for currentUser in the global class.
     * @param currentUser
     * */
    public static void setCurrentUser(User currentUser) {
        Barter.currentUser = currentUser;
    }

    //database methods

    /**This method updates post's information in database
     * @param documentId
     * @param newPost
     *
     * */
    public static boolean updatePostInDatabase(String documentId, Post newPost)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("postsObj").document(documentId).set(newPost).isComplete();
    }

    /**This method updates offer's information in database
     * @param documentId
     * @param newOffer
     *
     * */
    public static boolean updateOfferInDatabase(String documentId, Offer newOffer)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("offersObj").document(documentId).set(newOffer).isComplete();
    }

    /**This method updates offers status in database
     * @param documentId
     * @param newStatus
     *
     * */
    public static void updateOfferStatusInDatabase(String documentId, String newStatus)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("offersObj").document(documentId).update("offerStatus",newStatus);
    }

    /**This method delete an offer from everywhere, when user or admin wanted to so.
     * @param documentId offer with this Id will be deleted
     * */
    public static void deleteOffer(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("offersObj").document(documentId).delete();
    }

    /**This method updates user's information in database
     * @param documentId
     * @param newUser
     *
     * */
    public static boolean updateUserInDatabase(String documentId, User newUser)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("usersObj").document(documentId).set(newUser).isComplete();
    }

    /**This method delete a post from everywhere,when user or admin wanted to so.
     * @param p post will be deleted
     * */
    public static void deletePost(Post p) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("postsObj").document(p.getId()).delete();
    }



    /**
     * This method creates a unique messageRoom id for two user, when they start to send message to each other
     * */
    public static String findTheirMessageRoom(String id1, String id2)
    {
        if( id1.compareTo(id2) > 0)
        {
            return id1 + id2;
        }
        else
        {
            return id2 + id1;
        }

    }


    /**
     * This method call when users send messages to each other,
     * it saves messages to database also
     * @param m message
     * */
    public static void sendMessage(Message m)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("messageRooms").document(findTheirMessageRoom(m.getReceiverId(),m.getSenderId())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        //get messages
                        ArrayList<Message> messages = task.getResult().toObject(MessageRoom.class).getMessages();
                        //add new message
                        messages.add(m);
                        //update database
                        updateMessages(messages, findTheirMessageRoom(m.getReceiverId(),m.getSenderId()) );
                    }
                });

    }

    /**
     * Updates messages in database
     * */
    private static void updateMessages(ArrayList<Message> messages, String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("messageRooms").document(id).update("messages",messages);


    }

    /**
     * Formatting date
     * */
    public static String getDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));
        String formattedTimestamp = sdf.format(date);
        return formattedTimestamp;
    }

    /**
     * This is a function to choose Cities list
     */
    public static int chooseCitiesFromVoivodeship(String voivodeship){
        if ( voivodeship.equals("Cała Polska")){
            return R.array.cala_polska;
        }
        else if (voivodeship.equals("Dolnośląskie")){
            return R.array.dolnoslaskie;
        }
        else if (voivodeship.equals("Kujawsko-pomorskie")){
            return R.array.kujawsko_pomorskie;
        }
        else if (voivodeship.equals("Lubelskie")){
            return R.array.lubelskie;
        }
        else if (voivodeship.equals("Lubuskie")){
            return R.array.lubuskie;
        }
        else if (voivodeship.equals("Łódzkie")){
            return R.array.lodzkie;
        }
        else if (voivodeship.equals("Małopolskie")){
            return R.array.malopolskie;
        }
        else if (voivodeship.equals("Mazowieckie")){
            return R.array.mazowieckie;
        }
        else if (voivodeship.equals("Opolskie")){
            return R.array.opolskie;
        }
        else if (voivodeship.equals("Podkarpackie")){
            return R.array.podkarpackie;
        }
        else if (voivodeship.equals("Podlaskie")){
            return R.array.podlaskie;
        }
        else if (voivodeship.equals("Pomorskie")){
            return R.array.pomorskie;
        }
        else if (voivodeship.equals("Śląskie")){
            return R.array.slaskie;
        }
        else if (voivodeship.equals("Świętokrzyskie")){
            return R.array.swietokrzyskie;
        }
        else if (voivodeship.equals("Warmińsko-mazurskie")){
            return R.array.warminsko_mazurskie;
        }
        else if (voivodeship.equals("Wielkopolskie")){
            return R.array.wielkopolskie;
        }
        else if (voivodeship.equals("Zachodniopomorskie")){
            return R.array.zachodniopomorskie;
        }
        else return 0;
    }

}
