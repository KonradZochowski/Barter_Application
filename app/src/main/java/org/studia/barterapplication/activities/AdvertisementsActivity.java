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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.MyAdvertisementsPostAdapter;

import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.Advertisements;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * AdvertisementsActivity page Class
 */
public class AdvertisementsActivity extends AppCompatActivity implements AdvertisementsMenuDialog.AdvertisementsMenuDialogListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private SearchView searchView;

    private ImageButton menuImageBtn;
    private ImageButton advertisementsAddPostBtn;

    private Button priceLowToHighBtn;
    private Button priceHighToLowBtn;
    private Button aToZBtn;
    private Button zToABtn;

    private FirebaseFirestore db;

    private ArrayList<Post> posts;

    private Advertisements advertisements;
    private String savedVoivodeship;
    private String savedCity;
    private String savedFirstPrice;
    private String savedSecondPrice;
    private String savedCategory;

    /**
     * This method is called first when this activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisements);

        // Initialize the database instance
        db = FirebaseFirestore.getInstance();

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.advertisementsPostList);
        recyclerView.setHasFixedSize(true);

        // Set the layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Pull the posts from the database
        posts = new ArrayList<>();
        db.collection("postsObj").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for (DocumentSnapshot document : task.getResult() ) {

                        // Add the post to the posts array
                        posts.add(document.toObject(Post.class));
                    }

                    // Create the adapters
                    advertisements = new Advertisements(posts);
                    mAdapter = new MyAdvertisementsPostAdapter(advertisements, AdvertisementsActivity.this);
                    recyclerView.setAdapter(mAdapter);

                    // This sets the search views to make the search
                    searchView = (SearchView) findViewById(R.id.advertisementsSearchView);
                    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    searchView.setIconifiedByDefault(false);

                    // Set filter listener
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            ((MyAdvertisementsPostAdapter) mAdapter).getFilter().filter(newText);
                            return false;
                        }
                    });
                }
            }
        });

        // Find and initialize the buttons
        menuImageBtn = (ImageButton) findViewById(R.id.menuImageBtn);
        advertisementsAddPostBtn = (ImageButton) findViewById(R.id.advertisementsAddPostBtn);

        priceLowToHighBtn = (Button) findViewById(R.id.priceLowToHighBtn);
        priceHighToLowBtn = (Button) findViewById(R.id.priceHighToLowBtn);
        aToZBtn = (Button) findViewById(R.id.dateNewToOldBtn);
        zToABtn = (Button) findViewById(R.id.dateOldToNewBtn);

        // Menu button opens up the menu dialog
        menuImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuDialog();
            }
        });

        // add post button opens up the add post page
        advertisementsAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPost.class);
                startActivity(intent);
            }
        });

        // atoz button applies sorting
        aToZBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyAdvertisementsPostAdapter) mAdapter).sort(v);
            }
        });

        // ztoa button applies sorting
        zToABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyAdvertisementsPostAdapter) mAdapter).sort(v);
            }
        });

        // priceLowToHighBtn applies sorting
        priceLowToHighBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyAdvertisementsPostAdapter) mAdapter).sort(v);
            }
        });

        // priceHighToLowBtn applies sorting
        priceHighToLowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyAdvertisementsPostAdapter) mAdapter).sort(v);
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

    /**
     * This method opens up the menu dialog
     */
    public void openMenuDialog() {
        AdvertisementsMenuDialog menuDialog = new AdvertisementsMenuDialog();
        menuDialog.show(getSupportFragmentManager(),"menu dialog");
    }

    /**
     *  This method applies the filterings through calling the filter method of the adapter
     */
    @Override
    public void applyTexts(String filteredVoivodeship, String filteredCity, String firstPrice, String secondPrice, String filteredCategory) {
        int intFirstPrice = Integer.parseInt(firstPrice);
        int intSecondPrice = Integer.parseInt(secondPrice);

        ((MyAdvertisementsPostAdapter) mAdapter).filter(filteredVoivodeship, filteredCity, intFirstPrice, intSecondPrice, filteredCategory);
    }

    @Override
    public String getVoivodeship() {
        return savedVoivodeship;
    }

    @Override
    public void setVoivodeship(String savedVoivodeship) {
        this.savedVoivodeship = savedVoivodeship;
    }

    @Override
    public String getCity() {
        return savedCity;
    }

    @Override
    public void setCity(String savedCity) {
        this.savedCity = savedCity;
    }

    @Override
    public String getFirstPrice() {
        return savedFirstPrice;
    }

    @Override
    public void setFirstPrice(String savedFirstPrice) {
        this.savedFirstPrice = savedFirstPrice;
    }

    @Override
    public String getSecondPrice() {
        return savedSecondPrice;
    }

    @Override
    public void setSecondPrice(String savedSecondPrice) {
        this.savedSecondPrice = savedSecondPrice;
    }

    @Override
    public String getCategory() {
        return savedCategory;
    }

    @Override
    public void setCategory(String savedCategory) {
        this.savedCategory = savedCategory;
    }

    // resets the filters
    public void resetFilters() {
        ((MyAdvertisementsPostAdapter) mAdapter).resetFilters();
    }
}