 package org.studia.barterapplication.activities;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Category;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

 /**
 * This class is used when the user wants to add a post by clicking the "plus" button in my posts page
 */
public class AddPost extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Properties
    private EditText enterTitleEditText;
    private EditText setPriceEditText;
    private EditText addDescriptionEditText;

    private Spinner selectVoivodeshipSpinner;
    private Spinner selectCitySpinner;
    private Spinner categoryNameSpinner;

    private Button submitPostBtn;

    private ImageView addPostPhotoImageView;

    private String selectedVoivodeship;
    private String selectedCity;

    private StorageReference storageReference;

    private User currentUser;

    private FirebaseFirestore db;

    private boolean photoPicked;

    private Category selectedCategory;

    private ArrayList<Category> allCategories;
    private ArrayList<String> allCategoryNames;

    private Uri postImageUri;

    private Post newPost;

    private Date date;

    private String sellerName;



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the database objects
        storageReference = FirebaseStorage.getInstance().getReference("images");

        db = FirebaseFirestore.getInstance();

        // Set the photoPicked to false as default
        photoPicked = false;

        // Pull the user from the Barter global class
        currentUser = Barter.getCurrentUser();

        // Pull two arraylists, allCategories and allCategoryNames from the database
        allCategories = new ArrayList<>();
        allCategoryNames = new ArrayList<>();
        db.collection("categoriesObj").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots)
                {
                    // Pull the category and add to arraylists
                    Category aCategoryFromBase = documentSnapshots.toObject(Category.class);
                    Log.d("name", aCategoryFromBase.getCategoryName());
                    allCategoryNames.add(aCategoryFromBase.getCategoryName());
                    allCategories.add(aCategoryFromBase);
                }

            }
        })
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {

                    // When the process is completed, set the adapter for the categoryNameSpinner which displays all the category names
                    ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter(AddPost.this,android.R.layout.simple_spinner_item, allCategoryNames);

                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoryNameSpinner.setAdapter(categoryAdapter);
                    categoryNameSpinner.setOnItemSelectedListener(AddPost.this);
                }
            }
        });

        // Initialize all the EditText, Spinner, Button and ImageView objects
        enterTitleEditText = (EditText) findViewById(R.id.enterTitleEditText);
        setPriceEditText = (EditText) findViewById(R.id.enterPriceEditText);
        addDescriptionEditText = (EditText) findViewById(R.id.addDescriptionEditText);

        selectVoivodeshipSpinner = (Spinner) findViewById(R.id.selectVoivodeshipSpinner);
        selectCitySpinner = (Spinner) findViewById(R.id.selectCitySpinner);
        categoryNameSpinner = (Spinner) findViewById(R.id.categorySpinner);

        submitPostBtn = (Button) findViewById(R.id.submitPostBtn);

        addPostPhotoImageView = (ImageView) findViewById(R.id.addPostPhotoImageView);

        // Create the adapter for the voivodeship
        ArrayAdapter<CharSequence> voivodeshipAdapter = ArrayAdapter.createFromResource(this,R.array.voivodeships, android.R.layout.simple_spinner_item);

        // Set drop down view resource for the voivodeship adapter
        voivodeshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter for the spinners
        selectVoivodeshipSpinner.setAdapter(voivodeshipAdapter);

        // Set listeners (this class in this case since it implements the AdapterView.OnItemSelectedListener interface) for the spinners
        selectVoivodeshipSpinner.setOnItemSelectedListener( (AdapterView.OnItemSelectedListener) this);
        selectCitySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // When the ImageView is clicked, open the file chooser
        addPostPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // When submit button is clicked upload the changes to database
        submitPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !photoPicked ) {
                    Toast.makeText(AddPost.this, "Wybierz zdjęcie", Toast.LENGTH_SHORT).show();
                }
                else if ( enterTitleEditText.getText() == null || enterTitleEditText.getText().toString().equals("") ) {
                     Toast.makeText(AddPost.this, "Wprowadź tytuł", Toast.LENGTH_SHORT).show();
                }
                else if ( setPriceEditText.getText() == null || setPriceEditText.getText().toString().equals("") ) {
                     Toast.makeText(AddPost.this, "Wprowadź szacowaną wartość", Toast.LENGTH_SHORT).show();
                }
                else if ( addDescriptionEditText.getText() == null || addDescriptionEditText.getText().toString().equals("") ) {
                     Toast.makeText(AddPost.this, "Wprowadź opis", Toast.LENGTH_SHORT).show();
                }
                else if ( addDescriptionEditText.getText().length() > 500 ) {
                    Toast.makeText(AddPost.this, "Maksymalna długość opisu to 500 znaków", Toast.LENGTH_SHORT).show();
                }
                else if ( Integer.parseInt(String.valueOf(setPriceEditText.getText())) > 999999999 ) {
                    Toast.makeText(AddPost.this, "Maksymalna cena to 999 999 999 zł", Toast.LENGTH_SHORT).show();
                }
                else if ( enterTitleEditText.getText().length() > 25 ) {
                    Toast.makeText(AddPost.this, "Maksymalna długość tytułu to 25 znaków", Toast.LENGTH_SHORT).show();
                }
                else {

                     uploadFile();

                     notifyUser();

                     Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                     startActivity(intent);
                     finish();
                }
            }
        });
    }

    /**
     * Sends a notification to user when the user added a new post.
     */
    private void notifyUser() {

        Intent resultIntent = new Intent(this, MyPosts.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1,
                                                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Barter.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nowe ogłoszenie!")
                .setContentText("Twoje ogłoszenie zostało utworzone i widnieje już na liście.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * This method uploads the changes to both storage and firestore databases
     */
    private void uploadFile() {
        if ( postImageUri != null ) {
            DocumentReference newPostRef = db.collection("postsObj").document();
            StorageReference fileReference = storageReference.child("posts_pictures/" + newPostRef.getId());

            fileReference.putFile(postImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    date = currentTime();
                                    // Create a new post
                                    newPost = new Post(enterTitleEditText.getText().toString().trim(), addDescriptionEditText.getText().toString().trim(), selectedVoivodeship, selectedCity,
                                            Integer.parseInt(setPriceEditText.getText().toString().trim()), uri.toString(), selectedCategory, currentUser.getDocumentId(), currentUser.getUserName(), newPostRef.getId(), date);
                                    Toast.makeText(AddPost.this,"Ogłoszenie utworzone", Toast.LENGTH_SHORT).show();

                                    // Update the database through Barter global class
                                    Barter.updatePostInDatabase(newPostRef.getId(), newPost);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this,"Nie wybrano pliku", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method opens the file picker
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    /**
     * This method is called when another application is opened. In our case it's file picker.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            // Set the related image data
            postImageUri = data.getData();

            photoPicked = true;

            // Load the image
            Picasso.get().load(postImageUri).into(addPostPhotoImageView);
        }
    }

    /**
     * This method checks the origin of the action and holds the data depending on which spinner it is
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if ( parent.getId() == selectVoivodeshipSpinner.getId() ) {
            selectedVoivodeship = selectVoivodeshipSpinner.getItemAtPosition(position).toString();
            fillCitiesSpinner(Barter.chooseCitiesFromVoivodeship(selectedVoivodeship));
        }
        else if ( parent.getId() == selectCitySpinner.getId() ) {
            selectedCity = selectCitySpinner.getItemAtPosition(position).toString();
        }
        else if ( parent.getId() == categoryNameSpinner.getId() ) {
            selectedCategory = allCategories.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

     /**
      * This is a function to set city adapter
      */
    private void fillCitiesSpinner(@ArrayRes int array){
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCitySpinner.setAdapter(cityAdapter);
    }

     /**
      * This is a function to generate current time of added post
      */
    private Date currentTime(){
        return Calendar.getInstance().getTime();
    }
}