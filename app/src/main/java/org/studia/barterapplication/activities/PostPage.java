package org.studia.barterapplication.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.Report;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

/**
 * This is the class of postpage
 */
public class PostPage extends AppCompatActivity implements ReportDialog.ReportypeListener {

    TextView title, sellerName, city, features, price, dateAndCategory;
    Button addToFavouritesButton;
    Button visitSeller;
    Button sendOfferBtn;
    ImageButton report;
    Post currentPost;
    User currentUser;
    ImageView postImageView;
    boolean reportedBefore;
    private User currentSeller;
    private FirebaseFirestore db;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpage);

        db = FirebaseFirestore.getInstance();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = Barter.getCurrentUser();

        setCurrentPost();

        db.collection("usersObj").whereEqualTo(FieldPath.documentId(), currentPost.getSellerId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult() ) {
                    currentSeller = documentSnapshot.toObject(User.class);
                }
            }
        });

        setViews();

        setButtons();
    }

    /**
     * Gets current post from global class
     * */
    public void setCurrentPost()
    {
        currentPost = Barter.getCurrentPost();
    }

    /**Sets all text views according to properties of the post.
     *
     * */
    @SuppressLint("SetTextI18n")
    public void setViews()
    {
        price = (TextView) findViewById(R.id.postPrice);
        price.setText(String.valueOf(currentPost.getPrice()) + "zł");

        dateAndCategory = (TextView) findViewById(R.id.dateAndCategory);
        dateAndCategory.setText(String.valueOf(Barter.getDateFormat(currentPost.getTimestamp())) + "   |   " + currentPost.getCategory().getCategoryName());

        title = (TextView) findViewById(R.id.postTitle);
        title.setText(currentPost.getTitle());

        sellerName = (TextView) findViewById(R.id.postseller);
        sellerName.setText(currentPost.getSellerName());

        city = (TextView) findViewById(R.id.postschoolLesson);
        city.setText(currentPost.getVoivodeship() + " / "+ currentPost.getCity() );

        features = (TextView) findViewById(R.id.postFeatures);
        features.setText(currentPost.getDescription());

        postImageView = (ImageView) findViewById(R.id.postImageView);
        Picasso.get().load(currentPost.getPicture()).fit().into(postImageView);

    }

    /**
     * Sets all the buttons and add on click listener for them.
     *
     * */
    public void setButtons()
    {
        visitSeller = (Button) findViewById(R.id.GoSeller);

        //open seller page by taking its information from database
        visitSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.setCurrentSeller(currentSeller);
                Intent sellerPage = new Intent(getApplicationContext(), OtherUsersProfile.class);
                startActivity(sellerPage);
            }
        });

        addToFavouritesButton = (Button) findViewById(R.id.AddToFavourites);

        //add to favourites
        addToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( Barter.getCurrentUser().getFavourites().contains(currentPost) )
                {
                    // send a warning if user have already added this item to favourites
                    Toast.makeText(PostPage.this, "Już posiadasz ten przedmiot na liście ulubionych", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // updates users favourites list in database unless that item is already in the favourites list
                    Barter.getCurrentUser().addOfferToFavourites(currentPost);
                    Barter.updateUserInDatabase(Barter.getCurrentUser().getDocumentId(), Barter.getCurrentUser());

                    notifyUser();
                    Toast.makeText(PostPage.this, "Lista ulubionych aktualizowana", Toast.LENGTH_LONG).show();

                }
            }
        });

        sendOfferBtn = (Button) findViewById(R.id.sendOfferBtn);

        // send new offer
        sendOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getDocumentId().equals(currentPost.getSellerId())){
                    // send a warning if its your post page
                    Toast.makeText(PostPage.this, "Nie możesz wysłać oferty do samego siebie", Toast.LENGTH_SHORT).show();
                } else {
                    // makes a new offer with this item
                    Barter.setCurrentSeller(currentSeller);
                    Intent intent = new Intent(PostPage.this, AddOffer.class);
                    intent.putExtra("itemId",currentPost.getId());
                    intent.putExtra("itemTitle", currentPost.getTitle());
                    startActivity(intent);
                }
            }
        });



        //open new report dialog
        report = (ImageButton) findViewById(R.id.reportPost);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportedBefore = isReportedBefore();
                DialogFragment popUp = new ReportDialog();
                popUp.show(getSupportFragmentManager(), "ReportDialog");

            }
        });

    }

    /**
     * This method is called when user click on submit button in report dialog
     * */
    @Override
    public void positiveClicked(String[] reportTypes, int position) {

        //users can only report every post just once, so as to prevent spamming
        if (reportedBefore)
        {
            Toast.makeText(PostPage.this, "Już zgłosiłeś to ogłoszenie", Toast.LENGTH_LONG).show();
        }
        else {
            // add report to current post
            currentPost.report(reportTypes[position], position, Barter.getCurrentUser().getDocumentId());

            //and save it to database
            db.collection("postsObj").document(currentPost.getId()).update("reports", currentPost.getReports());
            Toast.makeText(PostPage.this, "Twoje zgłoszenie zostało zapisane", Toast.LENGTH_LONG).show();
            reportedBefore = true;
        }

    }

    /**This method checks whether user is already reported this post before
     * @return
     * */
    private boolean isReportedBefore()
    {
        for(Report r : currentPost.getReports())
        {
            if(r.getOwner().equals(Barter.getCurrentUser().getDocumentId())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void negativeClicked() {

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
     * This method informs user with a notificatiion when they add an item to their favourites list
     */
    private void notifyUser() {
        Intent resultIntent = new Intent(this, FavouritesActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 4,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Barter.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Lista ulubionych aktualizowana!")
                .setContentText("Nowy przedmiot został dodany do twojej listy ulubionych.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(4, mBuilder.build());
    }
}