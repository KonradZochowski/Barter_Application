package org.studia.barterapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import org.studia.barterapplication.R;

/**
 * A class for the Email Verification Check Page
 */
public class EmailVerificationCheckActivity extends AppCompatActivity {

    // Properties
    Button checkBtn;
    Button resendBtn;
    Button goToLoginBtn;
    FirebaseAuth mAuth;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        checkBtn = findViewById(R.id.checkVerificationBtn);
        resendBtn = findViewById( R.id.resendCodeBtn);
        goToLoginBtn = findViewById( R.id.goToLoginPageBtn);

        mAuth = FirebaseAuth.getInstance();

        // Sets ClickListener object for Check button.
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // updates user information
                mAuth.getCurrentUser().reload();

                // checks whether the email address of the current user is verified or not.
                // if the email is verified, direct user to Successful Email Verification Page.
                if ( mAuth.getCurrentUser().isEmailVerified() ) {
                    Toast.makeText(getApplicationContext(), "Zalogowano z powodzeniem", Toast.LENGTH_SHORT).show();
                    startActivity( new Intent( getApplicationContext(), SuccessfulVerificationActivity.class));
                    finish();
                }
                // otherwise, show an error message.
                else {
                    Toast.makeText(getApplicationContext(), "Zweryfikuj sw??j adres e-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sets ClickListener object for Resend button.
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // updates user information
                mAuth.getCurrentUser().reload();

                // sends verification email
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // if the task is successful, show a validation message.
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Nowy e-mail weryfikacyjny zosta?? wys??any.", Toast.LENGTH_SHORT).show();
                        }
                        // otherwise, show an error message.
                        else {
                            Toast.makeText(getApplicationContext(), "Spr??buj ponownie za chwil??.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Sets ClickListener object for Go to Login Page button.
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // directs user to login page.
                startActivity( new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}