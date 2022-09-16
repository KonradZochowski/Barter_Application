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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import org.studia.barterapplication.R;

/**
 * A class for the Forgot Password Page
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // Properties
    EditText mEmail;
    Button mSendResetEmailBtn;
    Button mGoBackToLoginBtn;

    FirebaseAuth mAuth;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = (EditText) findViewById(R.id.emailAddressEditTextF);
        mSendResetEmailBtn = (Button) findViewById(R.id.sendResetEmailButton);
        mGoBackToLoginBtn = (Button) findViewById(R.id.goBackToLoginButton);

        mAuth = FirebaseAuth.getInstance();

        // Sets ClickListener object for Back to Login button.
        mGoBackToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        // Sets ClickListener object for Send Password Reset Link button.
        mSendResetEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();

                // if the email field is empty, show an error message.
                if (email.isEmpty() ) {
                    mEmail.setError("Wprowadź swój adres e-mail");
                    mEmail.requestFocus();
                    return;
                }
                else {
                    mEmail.setError( null);
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

                // if the account exists, it sends a password reset link to given email.
                mAuth.sendPasswordResetEmail( email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // if the sending reset email task is successful, directs user to login page.
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Link do resetowania hasła został wysłany na adres e-mail: " + email, Toast.LENGTH_LONG).show();
                            startActivity( new Intent(getApplicationContext(), LoginActivity.class));
                            finish();

                        }
                        else{
                            // otherwise, show an error message.
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }
        });
    }


}