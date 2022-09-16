package org.studia.barterapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.User;

/**
 * This page helps user to change his*her profile information.
 */
public class EditProfile extends AppCompatActivity {

    User currentUser;
    Button confirmButton;
    EditText telephone;
    TextView username;


    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = Barter.getCurrentUser();


        setEditTexts();

        setButtons();

    }

    /**
     * This methods sets the buttons on the page
     * */
    void setButtons() {
        confirmButton = (Button) findViewById(R.id.confirmButton);

        //when user click on confirm, information will be uploaded to database
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentUser.setPhoneNumber(telephone.getText().toString());

                Barter.updateUserInDatabase(currentUser.getDocumentId(), currentUser);

                //open new page
                Intent profile = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(profile);

            }
        });
    }

    /**
     * This method initializes and sets the texts on the edit text
     * */
    void setEditTexts()
    {
        username = (TextView) findViewById(R.id.username);
        telephone = (EditText) findViewById(R.id.telephone);

        username.setText(currentUser.getUserName().toString());
        telephone.setText(currentUser.getPhoneNumber().toString());

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
                Intent barterIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(barterIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}