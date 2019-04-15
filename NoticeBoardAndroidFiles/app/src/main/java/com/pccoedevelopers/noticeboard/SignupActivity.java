package com.pccoedevelopers.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PatternMatcher;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText mNameFromSignup;
    private EditText mErpIdFromSignup;
    private EditText mEmailFromSignup;
    private EditText mPasswordFromSignup, mConfirmPasswordFromSignup;
    private Button mSignupButton;
    private Button mGotoLoginButton;
    private ProgressDialog mProgress;
    private DatabaseReference mDbRefBeta;

    private String canSignUp;
    private String emailInFb;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        canSignUp = "0";

        mAuth = FirebaseAuth.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        mNameFromSignup = findViewById(R.id.name_from_signup);
        mErpIdFromSignup = findViewById(R.id.erp_id_from_signup);
        mEmailFromSignup = findViewById(R.id.email_from_signup);
        mPasswordFromSignup = findViewById(R.id.password_from_signup);
        mSignupButton = findViewById(R.id.signup_button);
        mGotoLoginButton = findViewById(R.id.goto_login_button);
        mConfirmPasswordFromSignup = findViewById(R.id.confirm_password_from_signup);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = mNameFromSignup.getText().toString().trim();
                final String erpId = mErpIdFromSignup.getText().toString().trim();
                final String email = mEmailFromSignup.getText().toString().trim();
                final String password = mPasswordFromSignup.getText().toString().trim();
                final String confirmPassword = mConfirmPasswordFromSignup.getText().toString().trim();

                mProgress.setMessage("Creating Account...");
                mProgress.show();

                mDatabaseRef.child("Can-Sign-Up").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(erpId.toUpperCase()) || dataSnapshot.hasChild(erpId.toLowerCase()) ){

                                canSignUp = "1";

                        }

                        startSignUp(name, erpId, email, password, confirmPassword);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SignupActivity.this, "Cannot Connect!", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

        mGotoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public void startSignUp(final String name, final String erpId, final String email, final String password, final String confirmPassword){

        String erpSubString = "";


        if (TextUtils.getTrimmedLength(erpId) == 14){

            erpSubString = erpId.substring(0,8) + erpId.substring(9);

        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(erpId) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), "Please fill all the details before Signing Up", Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.getTrimmedLength(erpId) != 14){
            mProgress.dismiss();
            mErpIdFromSignup.setError("Erp Id Format is not valid!");
        }

        else if (Character.isDigit(erpId.charAt(8)) || !TextUtils.isDigitsOnly(erpSubString)){
            mProgress.dismiss();
            mErpIdFromSignup.setError("Erp Id Format is not valid!");
        }

        else if (!validate(email)){
            mProgress.dismiss();
            mEmailFromSignup.setError("Not a valid email address!");
        }

        else if (TextUtils.getTrimmedLength(password) < 8 ){
            mProgress.dismiss();
            mPasswordFromSignup.setError("Password should not be less than 8 characters!");
        }

        else if (!password.equals(confirmPassword)){
            mProgress.dismiss();
            mConfirmPasswordFromSignup.setError("This does not match with the above password");
        }

        else if (!canSignUp.equals("1")){
            mProgress.dismiss();
            mErpIdFromSignup.setError("This Erp Id is not registered to the email address you have provided!");
        }

        else {

            canSignUp = "0";

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseRef.child("Users").child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("erp_id").setValue(erpId);
                        mDatabaseRef.child("Users").child("students").child(user_id).setValue(mAuth.getCurrentUser().getEmail());
                        mProgress.setMessage("Signed Up");
                        mProgress.dismiss();
                    }

                    else {
                        Toast.makeText(SignupActivity.this, "Error: Couldn't Sign Up", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

}
