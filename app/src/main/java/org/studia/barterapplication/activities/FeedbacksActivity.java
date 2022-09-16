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
import android.widget.Toast;

import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.MyFeedbackAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This activity page is used to display the feedback given by users to admins
 */
public class FeedbacksActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private ArrayList<String> names;
    private ArrayList<String> feedbacks;
    private ArrayList<String> userIDs;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * First method that is called when this activity is open
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        // Create the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the firestore database instance
        db = FirebaseFirestore.getInstance();

        // Create two arraylist for the usernames and feedbacks
        names = new ArrayList<>();
        feedbacks = new ArrayList<>();
        userIDs = new ArrayList<>();

        // Find the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.feedbackList);
        recyclerView.setHasFixedSize(true);

        // Set the layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Pull the data about usernames and feedbacks using a for loop
        db.collection("feedbacks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot documentSnapshot : task.getResult() ) {
                        feedbacks.add(documentSnapshot.getString("feedback"));
                        names.add(documentSnapshot.getString("username"));
                        userIDs.add(documentSnapshot.getString("userID"));
                    }
                    Toast.makeText(FeedbacksActivity.this,"Liczba wiadomości: " + String.valueOf(names.size()), Toast.LENGTH_SHORT).show();
                    mAdapter = new MyFeedbackAdapter(names, feedbacks, userIDs,FeedbacksActivity.this);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });
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
}