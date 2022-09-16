package org.studia.barterapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A class for the Register Page
 */
public class SignUpActivity extends AppCompatActivity  {

    // Properties
    EditText mEmail;
    EditText mUserName;
    EditText mPassword;
    Button mSignUpButton;
    Button mGoBackToLogin;

    private ArrayList<String> userNames;
    private ArrayList<String> emails;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    User user;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        mEmail = (EditText) findViewById(R.id.mailAdressEditText);
        mUserName = (EditText) findViewById(R.id.userNameEditText);
        mPassword = (EditText) findViewById(R.id.passwordEditText);
        mSignUpButton = (Button) findViewById(R.id.signUpBtn);
        mGoBackToLogin = (Button) findViewById(R.id.goBackToLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Create two arraylist for the usernames and feedbacks
        userNames = new ArrayList<>();
        emails = new ArrayList<>();

        // Pull the data about usernames and e-mails using a for loop
        db.collection("usersObj").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful() ) {
                    for ( DocumentSnapshot documentSnapshot : task.getResult() ) {
                        emails.add(documentSnapshot.getString("email"));
                        userNames.add(documentSnapshot.getString("userName"));
                    }
                }
            }
        });

        // Sets ClickListener object for Sign Up button.
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Sets ClickListener object for Back to Login button.
        mGoBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * Validates all of the input, registers a new user with the given email,
     * username and password and saves the user's data to database.
     */
    private void registerUser() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String userName = mUserName.getText().toString().trim();

        // if the email field is empty, show an error message.
        if (email.isEmpty() ) {
            mEmail.setError("Wprowadź swój adres e-mail");
            mEmail.requestFocus();
            return;
        }
        else {
            mEmail.setError( null);
        }

        // if the username field is empty, show an error message.
        if (userName.isEmpty() ) {
            mUserName.setError("Wprowadź swoją nazwę użytkownika");
            mUserName.requestFocus();
            return;
        }
        else {
            mUserName.setError( null);
        }

        // if the password field is empty, show an error message.
        if (password.isEmpty() ) {
            mPassword.setError("Wprowadź swoje hasło");
            mPassword.requestFocus();
            return;
        }
        else {
            mPassword.setError( null);
        }

        // if the email address is not valid, show an error message.
        if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            mEmail.setError("Wprowadź poprawny adres e-mail!");
            mEmail.requestFocus();
            return;
        }
        else {
            mEmail.setError( null);
        }

        // if the email address is already used, show an error message.
        if ( emails.contains(email) ) {
            mEmail.setError("Adres e-mail jest zajęty!");
            mEmail.requestFocus();
            return;
        }
        else {
            mEmail.setError( null);
        }

        // if the user name is already used, show an error message.
        if ( userNames.contains(userName) ) {
            mUserName.setError("Login jest zajęty!");
            mUserName.requestFocus();
            return;
        }
        else {
            mEmail.setError( null);
        }

        // if the password is less than 6 chars, show an error message
        if (password.length() < 6) {
            mPassword.setError("Wprowadź hasło o minimalnej długości 6 znaków");
            mPassword.requestFocus();
            return;
        }
        else {
            mEmail.setError( null);
        }

        // create a user with the given email and password and add the user's data to database.
        mAuth.createUserWithEmailAndPassword( email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // if task is successful, send an email verification link.
                if ( task.isSuccessful()) {

                    FirebaseUser fUser = mAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // if the sending process successful, create a new user, add the data to database and transfer the data to the verification activity.
                            if (task.isSuccessful()) {
                                user = new User( userName,email ,mAuth.getUid());
                                Barter.setCurrentUser(user);


                                db.collection("usersObj").document(mAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                                Intent i =  new Intent( getApplicationContext(), EmailVerificationCheckActivity.class);
                                i.putExtra("email",email);
                                i.putExtra("password", password);
                                startActivity(i);
                                finish();
                            }
                            // otherwise, show an error message.
                            else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                // otherwise, show an error message.
                else {
                    Toast.makeText(SignUpActivity.this, "Błąd przy rejestracji! Spróbuj ponownie!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
