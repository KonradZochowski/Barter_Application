package org.studia.barterapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;

/**
 * This is the class of the MainActivity page
 */
public class MainActivity extends AppCompatActivity {

    // Properties
    ImageButton advertisementsImageBtn;
    ImageButton myPostsImageBtn;
    ImageButton profileImageBtn;
    ImageButton myMessagesImageBtn;
    ImageButton myOffersImageBtn;
    private Button adminPanel;

    FirebaseAuth mAuth;

    /**
     * This method is in all pages which creates the top menu
     * @param menu
     * @return true
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
     * @return true
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

    /**
     * This is the first method called when an instance of this class is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel for Post notifications
        createNotificationChannel();

        // Initialize the admin panel button and set a listener to it
        adminPanel = (Button) findViewById(R.id.adminPanelBtn);

        adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminPanel.class);
                startActivity(intent);
            }
        });
        if( !(Barter.getCurrentUser().isAdmin()))
        {
            adminPanel.setVisibility(View.INVISIBLE);
        }

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the image buttons
        advertisementsImageBtn = (ImageButton) findViewById(R.id.advertisementsImageBtn);
        myPostsImageBtn = (ImageButton) findViewById(R.id.myPostsImageBtn);
        profileImageBtn = (ImageButton) findViewById(R.id.profileImageBtn);
        myMessagesImageBtn = (ImageButton) findViewById(R.id.myMessagesImageBtn);
        myOffersImageBtn = (ImageButton) findViewById(R.id.myOffersImageBtn);

        // Initialize the firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        // If the email of the user is not verified, then sign the user out
        if (mAuth.getCurrentUser() != null) {
            if (!(mAuth.getCurrentUser().isEmailVerified() )) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Zaloguj się ponownie!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }

        // When the advertisements button is clicked, it leads to Advertisements Page
        advertisementsImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), AdvertisementsActivity.class);
                startActivity(intent);
            }
        });

        // When the my posts button is clicked, it leads to MyPosts page
        myPostsImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), MyPosts.class);
                startActivity(intent);
            }
        });

        // When the profile button is clicked, it leads to MyProfile page
        profileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), MyProfile.class);
                startActivity(intent);
            }
        });

        // When the messages button is clicked, it leads to MyMessages page
        myMessagesImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), MyMessages.class);
                startActivity(intent);
            }
        });

        // When the offers button is clicked, it leads to MyOffers page
        myOffersImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), MyOffers.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Creates notification channel for Post notifications.
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelOne = new NotificationChannel(
                    Barter.CHANNEL_ID, "Powiadomienia", NotificationManager.IMPORTANCE_DEFAULT);
            channelOne.setDescription( "Powiadomienia ogłoszeń");

            NotificationManager manager = getSystemService( NotificationManager.class);
            manager.createNotificationChannel( channelOne);
        }
    }
}