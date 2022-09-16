package org.studia.barterapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.MessageFriendsAdapter;

import org.studia.barterapplication.models.MessageRoom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This page shows users, their list of message friends.
 */
public class MyMessages extends AppCompatActivity {

    RecyclerView myMessageFriendsRecycler;
    MessageFriendsAdapter messageFriendsAdapter;

    ArrayList<String> myFriendsIds;

    FirebaseFirestore db;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        myFriendsIds = new ArrayList<String>();

        setMyFriendsIds();

    }


    /**
    * This method pull the list of Id which belong to message friends of current user, from the database.
    * */
    private void setMyFriendsIds() {


        db.collection("messageRooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        MessageRoom mR = documentSnapshot.toObject(MessageRoom.class);

                        assert mR != null;
                        if( mR.getReceiverId().equals(Barter.getCurrentUser().getDocumentId()))
                        {

                            myFriendsIds.add(mR.getSenderId());
                        }
                        else if ( mR.getSenderId().equals(Barter.getCurrentUser().getDocumentId()))
                        {

                            myFriendsIds.add(mR.getReceiverId());
                        }


                    }

                }

                setMessageFriendsView();
            }
        });

    }


    /**
    *   This method sets recyclerview and its adapter.
    *
    * */
    private void setMessageFriendsView() {

        myMessageFriendsRecycler = (RecyclerView) findViewById(R.id.myMessageFriends);
        myMessageFriendsRecycler.setLayoutManager(new LinearLayoutManager(this));

        Log.d("messageSon",myFriendsIds.toString());
        messageFriendsAdapter =  new MessageFriendsAdapter(myFriendsIds);

        myMessageFriendsRecycler.setAdapter(messageFriendsAdapter);

        messageFriendsAdapter.notifyDataSetChanged();

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