package com.lumos.durshaltyut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    String user_type;
    private EditText email, pwd, pwd2, name;
    String sEmail, sName;
    String sPwd;
    String sPwd2;

    Button register;
    //database to access db
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView login = findViewById(R.id.login_textview);
        login.setClickable(true);

        register = findViewById(R.id.reg);
        //database

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mAuth = FirebaseAuth.getInstance();
        //getting user type
        Bundle bundle = getIntent().getExtras();
        user_type = bundle.getString("type");
        // setting heading
        TextView type = findViewById(R.id.user_type_heading);

        //signup process
//to check if user logged in

        firebaseAuth = FirebaseAuth.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(register.this, student_main.class);
                    startActivity(intent);
                    finish();
                }
            }


        };

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pwd = findViewById(R.id.pwd);
        pwd2 = findViewById(R.id.pwd2);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sName = name.getText().toString();
                sEmail = email.getText().toString();
                sPwd = pwd.getText().toString();
                sPwd2 = pwd2.getText().toString();
                registerUser();

            }
        });


        if (user_type.equals("student")) {
            type.setText("Learn with tyut.");
            Toast.makeText(this, "User is a student", Toast.LENGTH_LONG).show();


        } else if (user_type.equals("educator")) {
            type.setText("Teach with tyut.");
            Toast.makeText(this, "User is a tutor", Toast.LENGTH_LONG).show();


        } else {

            Toast.makeText(this, "User is an alien", Toast.LENGTH_LONG).show();

        }


    }

    private void registerUser() {


        if (sName.isEmpty()) {
            name.setError("Name is Required");
            name.requestFocus();
            return;
        }

        if (sEmail.isEmpty()) {
            email.setError("Email is Required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Please enter a valid email.");
            email.requestFocus();
            return;
        }
        if (sPwd.isEmpty()) {
            pwd.setError("Password is Required");
            pwd.requestFocus();
            return;
        }

        if (sPwd.length() < 6) {
            pwd.setError("Minimum length of password should be 6.");
            pwd.requestFocus();
            return;
        }


        if (!sPwd.equals(sPwd2)) {
            pwd2.setError("Passwords do not match");
            pwd2.requestFocus();
            return;
        }

        if (user_type.equals("student")) {
            mAuth.createUserWithEmailAndPassword(sEmail, sPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userinfo user = new userinfo(sName, sEmail);
                        String user_id = mAuth.getCurrentUser().getUid();
                        //  dbReference.child(user_id).setValue(user);
                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("Users").child("Learners").child(user_id);
                        current_user_db.setValue(user);
                        Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                        mAuth.signInWithEmailAndPassword(sEmail,sPwd);
                        Intent intent = new Intent(register.this, student_main.class);
                        intent.putExtra("type", "student");
                        intent.putExtra("name", sName);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Some Error occurred. Please try again.", Toast.LENGTH_SHORT).show();

                    }

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "User Already Registered. Please Log In.", Toast.LENGTH_SHORT).show();
                    } else {
                    }
                }


            });
        }

            if (user_type.equals("educator")) {
                mAuth.createUserWithEmailAndPassword(sEmail, sPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userinfo user = new userinfo(sName, sEmail);
                            String user_id = mAuth.getCurrentUser().getUid();
                            //  dbReference.child(user_id).setValue(user);
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("Users").child("Educator").child(user_id);
                            current_user_db.setValue(user);
                            FirebaseUser user_fb = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(sName).build();
                            user_fb.updateProfile(profileUpdates);

                            Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(register.this, educator_main.class);
                            intent.putExtra("type", "educator");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Some Error occurred. Please try again.", Toast.LENGTH_SHORT).show();

                        }

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "User Already Registered. Please Log In.", Toast.LENGTH_SHORT).show();
                        } else {
                        }
                    }


                });


            }

    }



        //incase user already has an account
        public void login_new(View view){

            if (user_type.equals("student")) {
                Intent i = new Intent(register.this, login.class);
                i.putExtra("type", "student");
                startActivity(i);

            } else if (user_type.equals("educator")) {
                Intent i = new Intent(register.this, login.class);
                i.putExtra("type", "educator");
                startActivity(i);


            }
        }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null  && user_type.equals("student")) {
            Intent intent = new Intent(register.this, student_main.class);
            startActivity(intent);
            finish();
        } else if (user != null  && user_type.equals("educator")) {
            Intent intent = new Intent(register.this, educator_main.class);
            startActivity(intent);
            finish();
        }  else {

        }
    }
    }
