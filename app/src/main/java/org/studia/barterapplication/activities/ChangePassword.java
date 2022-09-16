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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.studia.barterapplication.R;

/**
 * A class for the Change Password Page
 */
public class ChangePassword extends AppCompatActivity {

    // Properties
    TextView mOldPassword;
    TextView mNewPassword;
    TextView mNewPasswordAgain;

    Button passwordConfirmBtn;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    /**
     * This method is in all pages which creates the top menu
     * @param menu
     * @return true
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
                Intent barterIntent = new Intent(getApplicationContext(), MainActivity.class);
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
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_barter_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOldPassword = (EditText) findViewById(R.id.oldPasswordEditText);
        mNewPassword = (EditText) findViewById(R.id.newPasswordEditText);
        mNewPasswordAgain = (EditText) findViewById(R.id.newPasswordAgainEditText);
        passwordConfirmBtn = (Button) findViewById(R.id.passwordConfirmBtn);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Sets ClickListener object for Confirm button.
        passwordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call changePassword method.
                changePassword();
            }
        });
    }

    /**
     * Validates all of the input and changes the password.
     */
    private void changePassword(){
        String email = mUser.getEmail();
        String oldPassword = mOldPassword.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();
        String newPasswordAgain = mNewPasswordAgain.getText().toString().trim();

        // if the old password field has fewer characters than 6 characters, show an error message.
        if (oldPassword.length() < 6) {
            mOldPassword.setError("Wprowadź swoje hasło");
            mOldPassword.requestFocus();
            return;
        }

        // if the new password field is empty, show an error message.
        if (newPassword.isEmpty() ) {
            mNewPassword.setError("Wprowadź swoje nowe hasło");
            mNewPassword.requestFocus();
            return;
        }

        // if the new password field has fewer characters than 6 characters, show an error message.
        if (newPassword.length() < 6) {
            mNewPassword.setError("Wprowadź hasło o minimalnej długości 6 znaków");
            mNewPassword.requestFocus();
            return;
        }

        // if the user enter the same password for old password field and new
        // password field, show an error message.
        if ( oldPassword.equals(newPassword)) {
            mNewPassword.setError("Wprowadź swoje nowe hasło");
            mNewPassword.requestFocus();
            return;
        }

        // if the second new password field is empty, show an error message.
        if (newPasswordAgain.isEmpty() ) {
            mNewPasswordAgain.setError("Wprowadź swoje hasło");
            mNewPasswordAgain.requestFocus();
            return;
        }

        // if the new password fields do not match, show an error message.
        if( !(newPassword.equals(newPasswordAgain)) ) {
            mNewPasswordAgain.setError("Hasła się różnią");
            mNewPasswordAgain.requestFocus();
        }
        // otherwise, check the old password.
        else {
            // reauthenticates the user using the entered old password.
            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword);

            mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // if the old password is correct, update the password.
                    if(task.isSuccessful()){
                        mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // if this task fails, show an error message.
                                if(!task.isSuccessful()){
                                    Toast.makeText(ChangePassword.this, "Coś poszło nie tak, spróbuj ponownie później", Toast.LENGTH_SHORT).show();
                                }
                                // otherwise, direct user to SuccessfulPasswordChange activity.
                                else {
                                    Toast.makeText(ChangePassword.this, "Hasło zmienione z powodzeniem", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), SuccessfulPasswordChange.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                    // otherwise, show an error message.
                    else {
                        Toast.makeText(ChangePassword.this, "Obecne hasło jest nieprawidłowe", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}