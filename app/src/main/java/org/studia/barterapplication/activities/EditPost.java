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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the class of the EditPost page
 */
public class EditPost extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Properties
    private EditText changeTitleEditText;
    private EditText changePriceEditText;
    private EditText changeDescriptionEditText;

    private Spinner changeVoivodeshipSpinner;
    private Spinner changeCitySpinner;
    private Spinner changeCategorySpinner;

    private Button applyChangesBtn;

    private String selectedVoivodeship;
    private String selectedCity;

    private StorageReference storageReference;
    private FirebaseFirestore db;

    private ArrayList<Category> allCategories;
    private ArrayList<String> allCategoryNames;

    private Category selectedCategory;

    private Post currentPost;

    private Uri postImageUri;

    private User currentUser;

    private ImageView editPostPhotoImageView;

    private String currentVoivodeship;

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
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Take the currentPost and currentUser from the global class Barter
        currentPost = Barter.getCurrentPost();
        currentUser = Barter.getCurrentUser();

        // Initialize the buttons
        applyChangesBtn = (Button) findViewById(R.id.applyChangePostBtn);
        editPostPhotoImageView = (ImageView) findViewById(R.id.editPostPhotoImageView);

        setEditTextFields();
        setSpinners();
        Picasso.get().load(currentPost.getPicture()).fit().into(editPostPhotoImageView);

        // Fill the related textviews, spinners, etc.
        fill();

        // Initialize the database objects
        storageReference = FirebaseStorage.getInstance().getReference("images");

        db = FirebaseFirestore.getInstance();

        // Pull two arraylists, allCategories and allCategoryNames from the database
        allCategories = new ArrayList<>();
        allCategoryNames = new ArrayList<>();
        db.collection("categoriesObj").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot document : task.getResult() ) {

                        // Pull the category and add to arraylists
                        allCategories.add(document.toObject(Category.class));
                        allCategoryNames.add(document.toObject(Category.class).getCategoryName());
                    }

                    // Set the adapter and listener
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(EditPost.this,android.R.layout.simple_spinner_item, allCategoryNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    changeCategorySpinner.setAdapter(categoryAdapter);
                    changeCategorySpinner.setOnItemSelectedListener(EditPost.this);
                    changeCategorySpinner.setSelection(((ArrayAdapter) changeCategorySpinner.getAdapter()).getPosition(currentPost.getCategory().getCategoryName()));
                }
            }
        });

        // When the ImageView is clicked, open the file chooser
        editPostPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // When the apply button is clicked, update the information in the database
        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( changeTitleEditText.getText() == null || changeTitleEditText.getText().toString().equals("") ) {
                    Toast.makeText(EditPost.this, "Wprowadź tytuł", Toast.LENGTH_SHORT).show();
                }
                else if ( changePriceEditText.getText() == null || changePriceEditText.getText().toString().equals("") ) {
                    Toast.makeText(EditPost.this, "Wprowadź szacowaną wartość", Toast.LENGTH_SHORT).show();
                }
                else if ( changeDescriptionEditText.getText() == null || changeDescriptionEditText.getText().toString().equals("") ) {
                    Toast.makeText(EditPost.this, "Wprowadź opis", Toast.LENGTH_SHORT).show();
                }
                else if ( changeDescriptionEditText.getText().length() > 500 ) {
                    Toast.makeText(EditPost.this, "Maksymalna długość opisu to 500 znaków", Toast.LENGTH_SHORT).show();
                }
                else if ( Integer.parseInt(String.valueOf(changePriceEditText.getText())) > 999999999 ) {
                    Toast.makeText(EditPost.this, "Maksymalna cena to 999 999 999 zł", Toast.LENGTH_SHORT).show();
                }
                else if ( changeTitleEditText.getText().length() > 25 ) {
                    Toast.makeText(EditPost.this, "Maksymalna długość tytułu to 25 znaków", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Update
                    uploadFile();

                    notifyUser();

                    Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * Sends a notification to user when the user edited a post.
     */
    private void notifyUser() {
        Intent resultIntent = new Intent(this, MyPosts.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 3,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Barter.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Ogłoszenie edytowane!")
                .setContentText("Twoje ogłoszenie zostało edytowane i jego zmiany są już widoczne.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(3, mBuilder.build());
    }

    /**
     * Fills the EditTexts
     */
    private void setEditTextFields()
    {
        changeTitleEditText = (EditText) findViewById(R.id.changeTitleEditText);
        changePriceEditText = (EditText) findViewById(R.id.changePriceEditText);
        changeDescriptionEditText = (EditText) findViewById(R.id.changeDescriptionEditText);

    }

    /**
     * Initializes the spinners
     */
    private void setSpinners()
    {
        changeVoivodeshipSpinner = (Spinner) findViewById(R.id.changeVoivodeshipSpinner);
        changeCitySpinner = (Spinner) findViewById(R.id.changeCitySpinner);
        changeCategorySpinner = (Spinner) findViewById(R.id.changeCategorySpinner);

        ArrayAdapter<CharSequence> voivodeshipAdapter = ArrayAdapter.createFromResource(this,R.array.voivodeships, android.R.layout.simple_spinner_item);

        voivodeshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        changeVoivodeshipSpinner.setAdapter(voivodeshipAdapter);

        currentVoivodeship = currentPost.getVoivodeship();
        //chooseCitiesFromVoivodeship(currentVoivodeship);
        fillCitiesSpinner(Barter.chooseCitiesFromVoivodeship(currentVoivodeship));
    }

    /**
     * Fills the spinners, ImageView, and EditTexts with the related information
     */
    private void fill() {
        changeTitleEditText.setText(currentPost.getTitle());
        changeDescriptionEditText.setText(currentPost.getDescription());
        changePriceEditText.setText(String.valueOf(currentPost.getPrice()));

        changeVoivodeshipSpinner.setSelection( ((ArrayAdapter) changeVoivodeshipSpinner.getAdapter()).getPosition(currentPost.getVoivodeship()));
        changeCitySpinner.setSelection(((ArrayAdapter) changeCitySpinner.getAdapter()).getPosition(currentPost.getCity()));
        changeVoivodeshipSpinner.setOnItemSelectedListener( (AdapterView.OnItemSelectedListener) this);
        changeCitySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        Picasso.get().load(currentPost.getPicture()).fit().into(editPostPhotoImageView);
    }


    /**
     * Uploads information to database
     */
    private void uploadFile() {
        if ( postImageUri != null ) {
            StorageReference fileReference = storageReference.child("posts_pictures/" + currentPost.getId());

            fileReference.putFile(postImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Create a new post
                                    currentPost = new Post(changeTitleEditText.getText().toString().trim(), changeDescriptionEditText.getText().toString().trim(), selectedVoivodeship, selectedCity,
                                            Integer.parseInt(changePriceEditText.getText().toString().trim()), uri.toString(), selectedCategory, currentUser.getDocumentId(), currentPost.getSellerName(), currentPost.getId(), currentPost.getReports(), currentPost.getTimestamp());
                                    // Update the posts
                                    db.collection("postsObj").document(currentPost.getId()).set(currentPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditPost.this,"Zmiany zostały zapisane w bazie danych!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Make the same if the user didn't pick an image
            currentPost = new Post(changeTitleEditText.getText().toString().trim(), changeDescriptionEditText.getText().toString().trim(), selectedVoivodeship, selectedCity,
                    Integer.parseInt(changePriceEditText.getText().toString().trim()), currentPost.getPicture(), selectedCategory, currentUser.getDocumentId(), currentPost.getSellerName(), currentPost.getId(), currentPost.getReports(), currentPost.getTimestamp());

            db.collection("postsObj").document(currentPost.getId()).set(currentPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditPost.this,"Zmiany zostały zapisane w bazie danych!", Toast.LENGTH_LONG).show();
                }
            });
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

        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            // Set the related image data
            postImageUri = data.getData();

            // Load the image
            Picasso.get().load(postImageUri).into(editPostPhotoImageView);
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
        if ( parent.getId() == changeVoivodeshipSpinner.getId() ) {
            selectedVoivodeship = changeVoivodeshipSpinner.getItemAtPosition(position).toString();
            fillCitiesSpinner(Barter.chooseCitiesFromVoivodeship(selectedVoivodeship));
        }
        else if ( parent.getId() == changeCitySpinner.getId() ) {
            selectedCity = changeCitySpinner.getItemAtPosition(position).toString();
        }
        else if ( parent.getId() == changeCategorySpinner.getId() ) {
            selectedCategory = allCategories.get(position);
        }
    }

    /**
     * This is a function to set city adapter
     */
    private void fillCitiesSpinner(@ArrayRes int array){
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeCitySpinner.setAdapter(cityAdapter);
        if (((ArrayAdapter) changeCitySpinner.getAdapter()).getPosition(currentPost.getCity()) > 0){
            changeCitySpinner.setSelection(((ArrayAdapter) changeCitySpinner.getAdapter()).getPosition(currentPost.getCity()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}