package org.studia.barterapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Category;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the class of the AdminAddCategory page
 * This class is used when the admin wants to add a category so that users can choose this category when
 * they are creating a new post
 */
public class AdminAddCategory extends AppCompatActivity {

    private EditText newCategoryTitleEditText;
    private Button addNewCategoryBtn;

    private FirebaseFirestore db;

    private String title;
    private ArrayList<String> allCategoryNames;

    /**
     * This is the first method called when an instance of this class is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_category);

        // Initialize the edit texts and buttons
        newCategoryTitleEditText = (EditText) findViewById(R.id.newCategoryEditText);
        addNewCategoryBtn = (Button) findViewById(R.id.addNewCategoryBtn);

        // Initialize the database objects
        db = FirebaseFirestore.getInstance();

        allCategoryNames = new ArrayList<>();
        db.collection("categoriesObj").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot document : task.getResult() ) {

                        // Pull the category and add to arraylists
                        allCategoryNames.add(document.toObject(Category.class).getCategoryName());
                    }
                }
            }
        });

        // add button adds the changes to the database
        addNewCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the title
                title = newCategoryTitleEditText.getText().toString();

                // Check if the title is not null and photo is picked
                if (allCategoryNames.contains(title)) {
                    Toast.makeText(AdminAddCategory.this, "Kategoria jest już w bazie", Toast.LENGTH_SHORT).show();
                }
                else if (!title.equals("")){
                    // Call uploadFile() method
                    uploadFile();
                    Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(AdminAddCategory.this, "Wprowadź nazwę kategorii", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Uploads the changes to the firestore and storage databases
     */
    private void uploadFile() {
            // Create a new category with the title
            DocumentReference newCategoryRef = db.collection("categoriesObj").document();
            Category newCategory = new Category( title,newCategoryRef.getId());

            // Save the category to the firestore database
                db.collection("categoriesObj").document(newCategory.getId()).set(newCategory).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminAddCategory.this,"Kategoria opublikowana!", Toast.LENGTH_LONG).show();
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