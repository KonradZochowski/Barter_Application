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

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.MyFavouritesAdapter;

import org.studia.barterapplication.models.User;

/**
 * This class represents the favourites activity page
 */
public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Take the current user from the global class barter
        currentUser = Barter.getCurrentUser();

        // Initialize the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.favourites);
        recyclerView.setHasFixedSize(true);

        // Set the layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapters
        mAdapter = new MyFavouritesAdapter( currentUser, currentUser.getFavourites(), this);
        recyclerView.setAdapter(mAdapter);
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