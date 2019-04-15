package com.pccoedevelopers.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity{

    private EditText mEmailFromLogin;
    private EditText mPasswordFromLogin;
    private Button mSigninButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Button mGotoSignupButton;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailFromLogin = findViewById(R.id.email_from_login);
        mPasswordFromLogin = findViewById(R.id.password_from_login);
        mSigninButton = findViewById(R.id.signin_button);
        mGotoSignupButton = findViewById(R.id.goto_signup_button);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){

                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        mGotoSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gotoSignupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                gotoSignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoSignupIntent);

            }
        });

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Signing in...");
                mProgress.show();
                startSignin();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    private void startSignin(){

        String email = mEmailFromLogin.getText().toString();
        String password = mPasswordFromLogin.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            mProgress.dismiss();

            Toast.makeText(this, "Email and Password fields cannot be empty!", Toast.LENGTH_LONG).show();

        }

        else if (TextUtils.getTrimmedLength(password) < 8 ){
            mProgress.dismiss();
            mPasswordFromLogin.setError("Password should not be less than 8 characters!");
        }

        else {



            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Problem Signing In!", Toast.LENGTH_LONG).show();
                    }

                    else {
                        mProgress.dismiss();
                    }

                }
            });

        }

    }
}

