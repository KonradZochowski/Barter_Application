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
import android.widget.ImageButton;
import android.widget.Toast;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.MyPostAdapter;

import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the class of the MyPosts activity
 */
public class MyPosts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;

    private ArrayList<Post> posts;

    private ImageButton myPostsAddPostBtn;

    /**
     * This is the first method called when an object of this class is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the database object
        db = FirebaseFirestore.getInstance();

        // Initialize the post add button and set an onclick listener to it
        myPostsAddPostBtn = (ImageButton) findViewById(R.id.myPostsAddPostBtn);
        myPostsAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPost.class);
                startActivity(intent);
            }
        });

        // Initialize the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.postList);
        recyclerView.setHasFixedSize(true);

        // Set the layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Pull the current user from the global class Barter
        User currentUser = Barter.getCurrentUser();

        // Pull all the posts of this current user, use whereEqualTo() method to search the username
        posts = new ArrayList<>();
        db.collection("postsObj").whereEqualTo("sellerId" , currentUser.getDocumentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot document : task.getResult()) {

                        posts.add(document.toObject(Post.class));
                    }
                    Toast.makeText(MyPosts.this, "Ilość twoich ogłoszeń: " + String.valueOf(posts.size()), Toast.LENGTH_SHORT).show();

                    // Set the adapters
                    mAdapter = new MyPostAdapter(posts, MyPosts.this);
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