package org.studia.barterapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.OtherUsersPostAdapter;

import org.studia.barterapplication.models.MessageRoom;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the class of the OtherUsersProfile page
 */
public class OtherUsersProfile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView otherUsersProfilePostTextView;
    private TextView otherUsersProfileUsernameTextView;

    private ImageButton otherUsersProfileMailBtn;
    private ImageButton otherUsersProfilePhoneBtn;

    private Button otherUserSendMessageBtn;
    private Button otherUserSendOfferBtn;

    private ImageView otherUsersProfilePhotoImageView;

    private User currentSeller;
    private User currentUser;

    private boolean messageRoomExists;

    private FirebaseFirestore db;

    private ArrayList<Post> posts;

    /**
     * This is the first method called when an instance of this class is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_profile);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the database instance
        db = FirebaseFirestore.getInstance();

        // Initialize the text views
        otherUsersProfilePostTextView = (TextView) findViewById(R.id.otherUsersProfilePostTextView);
        otherUsersProfileUsernameTextView = (TextView) findViewById(R.id.otherUsersProfileUsernameTextView);

        // Initialize the image buttons
        otherUsersProfileMailBtn = (ImageButton) findViewById(R.id.otherUsersProfileMailBtn);
        otherUsersProfilePhoneBtn = (ImageButton) findViewById(R.id.otherUsersProfilePhoneBtn);

        // Initialize the buttons
        otherUserSendMessageBtn = (Button) findViewById(R.id.otherUserSendMessageBtn);
        otherUserSendOfferBtn = (Button) findViewById(R.id.otherUserSendOfferBtn);

        // Initialize the imageview for the profile photo
        otherUsersProfilePhotoImageView = (ImageView) findViewById(R.id.otherUsersProfilePhotoImageView);

        // Initialize the recycler view for the posts
        recyclerView = (RecyclerView) findViewById(R.id.otherUserPostList);
        recyclerView.setHasFixedSize(true);

        // Set the layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Pull the current seller from the global class Barter
        currentSeller = Barter.getCurrentSeller();

        // Pull the current user
        currentUser = Barter.getCurrentUser();

        // Pull all the posts of this user from the database using whereEqualTo() method and a for-loop
        posts = new ArrayList<>();
        db.collection("postsObj").whereEqualTo("sellerId", currentSeller.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot document : task.getResult()) {

                        posts.add(document.toObject(Post.class));
                    }
                    mAdapter = new OtherUsersPostAdapter(posts, OtherUsersProfile.this);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });

        // If there's no avatar in the user's profile, then load a placeholder. Load user's photo, otherwise.
        if ( currentSeller.getAvatar() != "" ) {
            Picasso.get().load(currentSeller.getAvatar()).fit().into(otherUsersProfilePhotoImageView);
        }
        else {
            Picasso.get().load(R.drawable.ic_user).error(R.drawable.ic_user).fit().into(otherUsersProfilePhotoImageView);
        }

        // Set the textviews with related information about the user
        otherUsersProfileUsernameTextView.setText(currentSeller.getUserName().toString());
        otherUsersProfilePostTextView.setText("Ogłoszenia użytkownika " + currentSeller.getUserName().toString());

        // send the offer
        otherUserSendOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getDocumentId().equals(currentSeller.getDocumentId())){
                    // send warning if its your profile
                    Toast.makeText(OtherUsersProfile.this, "Nie możesz wysłać oferty do samego siebie", Toast.LENGTH_SHORT).show();
                } else {
                    // open add offer activity
                    Intent intent = new Intent(OtherUsersProfile.this, AddOffer.class);
                    startActivity(intent);
                }
            }
        });

        // Set listener for send message button
        otherUserSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser.getDocumentId().equals(currentSeller.getDocumentId())){
                    Toast.makeText(OtherUsersProfile.this, "Nie możesz wysłać wiadomości do samego siebie", Toast.LENGTH_SHORT).show();
                } else {
                    messageRoomExists = false;
                    db.collection("messageRooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if ( task.isSuccessful() ) {
                                for ( DocumentSnapshot documentSnapshot: task.getResult()) {
                                    if (documentSnapshot.getId().equals(Barter.findTheirMessageRoom(currentSeller.getDocumentId(),currentUser.getDocumentId()))) {
                                        messageRoomExists = true;
                                    }
                                }
                                createMessageRoom(messageRoomExists);
                            }
                        }
                    });
                }
            }
        });


        // Open the social media dialog box with the email info
        otherUsersProfileMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(currentSeller.getEmail());
            }
        });

        // Open the social media dialog box with the phone info
        otherUsersProfilePhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(currentSeller.getPhoneNumber().toString());
            }
        });
    }

    /**
     * This method creates and opens up SocialMediaDialog
     * @param socialMedia
     */
    public void openDialog(String socialMedia) {
        SocialMediaDialog dialog = new SocialMediaDialog(socialMedia);
        dialog.show(getSupportFragmentManager(),"social media dialog");
    }

    /**
     * This method is in all pages which creates the top menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    /**
     * This method is in all pages which creates the functionality of the top menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_icon:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity( settingsIntent);
                return true;
            case android.R.id.home:
                Intent barterIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(barterIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * After clicking on send message button, this method creates a message room in database, if there is no message room for these two user
     * and opens message room activity page
     * @param messageRoomExists
     */
    public void createMessageRoom(boolean messageRoomExists) {
        if ( !messageRoomExists ) {
            MessageRoom newMessageRoom = new MessageRoom(currentSeller.getDocumentId(), currentUser.getDocumentId());
            db.collection("messageRooms").document(Barter.findTheirMessageRoom(currentSeller.getDocumentId(), currentUser.getDocumentId())).set(newMessageRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( task.isSuccessful() ) {
                        Toast.makeText(OtherUsersProfile.this, "Utworzono czat", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OtherUsersProfile.this, MessageRoomActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else {
            Intent intent = new Intent(OtherUsersProfile.this, MessageRoomActivity.class);
            startActivity(intent);
        }
    }
}