package org.studia.barterapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.OfferAdapter;
import org.studia.barterapplication.models.Offer;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.StringWithKey;
import org.studia.barterapplication.models.User;

import java.util.ArrayList;

/**
 * This is the class of the AddOffer activity
 */
public class AddOffer extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView myItemsRecyclerView;
    private RecyclerView otherUserItemsRecyclerView;

    private FirebaseFirestore db;

    private String senderId;
    private String receiverId;
    private String offerStatus;
    private User currentUser;
    private User currentReceiver;

    private Offer newOffer;

    private ArrayList<String> myItems;
    private ArrayList<String> otherUserItems;

    private Button addMyItemButton;
    private Button addOtherUserItemButton;
    private Button sendOfferButton;

    private Spinner myItemsSpinner;
    private Spinner otherUserItemsSpinner;

    private EditText enterMyCashEditText;
    private EditText enterOtherUserCashEditText;

    private ArrayList<StringWithKey> myItemsList;
    private ArrayList<StringWithKey> myChosenItemsList;
    private ArrayList<StringWithKey> otherUserItemsList;
    private ArrayList<StringWithKey> otherUserChosenItemsList;

    private StringWithKey myStringWithKey;
    private StringWithKey otherUserStringWithKey;

    private Intent intent;
    private String documentId;
    private String itemTitle;
    private String itemId;

    /**
     * This is the first method called when an object of this class is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        // Set the top icons
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the database object
        db = FirebaseFirestore.getInstance();

        // Initialize intent and passed extra information
        intent = getIntent();
        itemTitle = intent.getStringExtra("itemTitle");
        itemId = intent.getStringExtra("itemId");
        documentId = intent.getStringExtra("documentId");

        // Pull the user from the Barter global class
        currentUser = Barter.getCurrentUser();
        currentReceiver = Barter.getCurrentSeller();

        // set sender and receiver Id
        senderId = currentUser.getDocumentId();
        receiverId = currentReceiver.getDocumentId();

        // set default offer status to counter offer (changed to sent if offer is new)
        offerStatus = "Kontroferta";

        // Initialize items lists
        myItems = new ArrayList<>();
        otherUserItems = new ArrayList<>();

        myItemsList = new ArrayList<>();
        myChosenItemsList = new ArrayList<>();
        otherUserItemsList = new ArrayList<>();
        otherUserChosenItemsList = new ArrayList<>();

        // Initialize buttons
        addMyItemButton = (Button) findViewById(R.id.addMyItemButton);
        addOtherUserItemButton = (Button) findViewById(R.id.addOtherUserItemButton);
        sendOfferButton = (Button) findViewById(R.id.acceptOfferButton);

        // Initialize spinners
        myItemsSpinner = findViewById(R.id.myItemsSpinner);
        otherUserItemsSpinner = findViewById(R.id.otherUserItemsSpinner);

        // Initialize edit texts
        enterMyCashEditText = findViewById(R.id.enterMyCashEditText);
        enterOtherUserCashEditText = findViewById(R.id.enterOtherUserCashEditText);

        // Initialize recycler views and set the adapters
        myItemsRecyclerView = findViewById(R.id.propositionMyItemsOfferRecyclerView);
        myItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OfferAdapter myItemsAdapter = new OfferAdapter(myItems, myChosenItemsList);
        myItemsRecyclerView.setAdapter(myItemsAdapter);

        otherUserItemsRecyclerView = findViewById(R.id.propositionOtherUserItemsOfferRecyclerView);
        otherUserItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OfferAdapter otherUserItemsAdapter = new OfferAdapter(otherUserItems, otherUserChosenItemsList);
        otherUserItemsRecyclerView.setAdapter(otherUserItemsAdapter);

        // pull all posts titles and Ids where sellerId equals senderId and set it into spinner
        db.collection("postsObj").whereEqualTo("sellerId", senderId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots) {
                        // Pull the post and add to arraylist
                        Post aPostFromBase = documentSnapshots.toObject(Post.class);
                        myItemsList.add(new StringWithKey(aPostFromBase.getTitle(), aPostFromBase.getId()));
                    }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // When the process is completed, set the adapter for the myItemsSpinner which displays senders post titles
                    ArrayAdapter<CharSequence> senderOfferAdapter = new ArrayAdapter(AddOffer.this, android.R.layout.simple_spinner_item,myItemsList);
                    senderOfferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    myItemsSpinner.setAdapter(senderOfferAdapter);
                    myItemsSpinner.setOnItemSelectedListener(AddOffer.this);
                }
            }
        });

        // pull all posts titles and Ids where sellerId equals receiverId and set it into spinner
        db.collection("postsObj").whereEqualTo("sellerId", receiverId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots) {
                    // Pull the post and add to arraylist
                    Post aPostFromBase = documentSnapshots.toObject(Post.class);
                    otherUserItemsList.add(new StringWithKey(aPostFromBase.getTitle(), aPostFromBase.getId()));
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // When the process is completed, set the adapter for the otherUserItemsSpinner which displays receivers post titles
                    ArrayAdapter<CharSequence> receiverOfferAdapter = new ArrayAdapter(AddOffer.this, android.R.layout.simple_spinner_item,otherUserItemsList);
                    receiverOfferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    otherUserItemsSpinner.setAdapter(receiverOfferAdapter);
                    otherUserItemsSpinner.setOnItemSelectedListener(AddOffer.this);
                }
            }
        });

        // when the addMyItemButton is clicked add item from spinner to my chosen items list
        addMyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItems.add(myStringWithKey.getString());
                myChosenItemsList.add(myStringWithKey);
                myItemsAdapter.notifyItemInserted(myItems.size()-1);
            }
        });

        // when the addOtherUserItemButton is clicked add item from spinner to other user chosen items list
        addOtherUserItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherUserItems.add(otherUserStringWithKey.getString());
                otherUserChosenItemsList.add(otherUserStringWithKey);
                otherUserItemsAdapter.notifyItemInserted(otherUserItems.size()-1);
            }
        });

        // if opened on PostPage activity add the item to chosen items list
        ifFromPostAddToChosenList(otherUserItemsAdapter);

        // if opened from counter offer button set all previous offer fields
        ifCounterOfferSetPreviousOfferFields(otherUserItemsAdapter, myItemsAdapter);

        sendOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myChosenItemsList.isEmpty() && otherUserChosenItemsList.isEmpty()){
                    Toast.makeText(AddOffer.this,"Wybierz jakiś przedmiot", Toast.LENGTH_SHORT).show();
                } else if ((enterMyCashEditText.getText().toString().equals("")||enterMyCashEditText.getText().toString().equals("0")) && myChosenItemsList.isEmpty()){
                    Toast.makeText(AddOffer.this,"Wybierz swój przedmiot lub dodaj gotówkę", Toast.LENGTH_SHORT).show();
                } else if ((enterOtherUserCashEditText.getText().toString().equals("")||enterOtherUserCashEditText.getText().toString().equals("0")) && otherUserChosenItemsList.isEmpty()){
                    Toast.makeText(AddOffer.this,"Wybierz przedmiot odbiorcy lub dodaj gotówkę", Toast.LENGTH_SHORT).show();
                } else {
                      uploadFile();
                }
            }
        });
    }

    // if opened from counter offer button set all previous offer fields
    private void ifCounterOfferSetPreviousOfferFields(OfferAdapter otherUserItemsAdapter, OfferAdapter myItemsAdapter){
        if (documentId!=null){
            db.collection("offersObj").document(documentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Offer offer = documentSnapshot.toObject(Offer.class);
                    // set other user fields
                    for (StringWithKey senderItem : offer.getSenderItems()) {
                        otherUserItems.add(senderItem.getString());
                        otherUserChosenItemsList.add(senderItem);
                        otherUserItemsAdapter.notifyItemInserted(otherUserItems.size()-1);
                    }
                    // set my fields
                    for (StringWithKey receiverItem : offer.getReceiverItems()) {
                        myItems.add(receiverItem.getString());
                        myChosenItemsList.add(receiverItem);
                        myItemsAdapter.notifyItemInserted(myItems.size()-1);
                    }
                    enterMyCashEditText.setText(String.valueOf(offer.getReceiverCash()));
                    enterOtherUserCashEditText.setText(String.valueOf(offer.getSenderCash()));
                }
            });
        }
    }

    // if opened on PostPage activity add the item to chosen items list
    private void ifFromPostAddToChosenList(OfferAdapter otherUserItemsAdapter){
        if (itemId!=null){
            otherUserItems.add(itemTitle);
            otherUserChosenItemsList.add(new StringWithKey(itemTitle,itemId));
            otherUserItemsAdapter.notifyItemInserted(otherUserItems.size()-1);
        }
    }

    // upload file to the database
    private void uploadFile(){

        // set empty cash edit texts to 0
        if(enterMyCashEditText.getText().toString().equals("")){
            enterMyCashEditText.setText("0");
        }
        if(enterOtherUserCashEditText.getText().toString().equals("")){
            enterOtherUserCashEditText.setText("0");
        }

        // if the offer is new set offer status to sent and set documentId
        if (documentId==null){
            DocumentReference newOfferRef = db.collection("offersObj").document();
            documentId = newOfferRef.getId();
            offerStatus = "Wysłano";
        }

        // set new offer and update in database
        newOffer = new Offer(documentId,senderId,receiverId,myChosenItemsList,Integer.parseInt(enterMyCashEditText.getText().toString().trim()),otherUserChosenItemsList,Integer.parseInt(enterOtherUserCashEditText.getText().toString().trim()),offerStatus);
        Barter.updateOfferInDatabase(documentId,newOffer);

        Toast.makeText(AddOffer.this,"Oferta wysłana", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent( getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
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
        if ( parent.getId() == myItemsSpinner.getId() ) {
            myStringWithKey = (StringWithKey) parent.getItemAtPosition(position);
        } else if (parent.getId() == otherUserItemsSpinner.getId()){
            otherUserStringWithKey = (StringWithKey) parent.getItemAtPosition(position);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
