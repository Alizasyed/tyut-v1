package com.lumos.durshaltyut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {


    String user_type;
    TextInputEditText email, pwd;

    Button login;
    FirebaseAuth mAuth;
    TextInputLayout out1, out2;
    String sEmail, sPwd;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        out1 = findViewById(R.id.outline1);
        out2 = findViewById(R.id.outline2);


        Bundle bundle = getIntent().getExtras();
        user_type = bundle.getString("type");

        mAuth = FirebaseAuth.getInstance();

        email = (TextInputEditText) findViewById(R.id.email);
        pwd = findViewById(R.id.pwd);
        login = findViewById(R.id.login);
        //   TextView reg = findViewById(R.id.reg_textview);

        TextView type = findViewById(R.id.type);
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(login.this, student_main.class);
                    startActivity(intent);
                    finish();
                }
            }


        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sEmail = email.getText().toString().trim();
                sPwd = pwd.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    email.setError("Please enter a valid email.");
                    email.requestFocus();
                    return;
                }

                if (sEmail.isEmpty()) {
                    email.setError("Email is Required");
                    email.requestFocus();
                    return;
                }
                if (sPwd.isEmpty()) {
                    email.setError("Email is Required");
                    email.requestFocus();
                    return;
                }
                loginUser(user_type);

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        }


    private void updateUI(FirebaseUser user) {
        if (user != null  && user_type.equals("student")) {
            Intent intent = new Intent(login.this, student_main.class);
            startActivity(intent);
            finish();
        } else if (user != null  && user_type.equals("educator")) {
            Intent intent = new Intent(login.this, educator_main.class);
            startActivity(intent);
            finish();
        }  else {

        }
    }

    public void register_new(View view) {

        if (user_type.equals("student")) {
            Intent i = new Intent(login.this, register.class);
            i.putExtra("type", "student");
            startActivity(i);

        } else if (user_type.equals("educator")) {
            Intent i = new Intent(login.this, register.class);
            i.putExtra("type", "educator");
            startActivity(i);


        }
    }


    private void loginUser(String user_type) {

        this.user_type = user_type;

        if (user_type.equals("student")) {
            mAuth.signInWithEmailAndPassword(sEmail, sPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), student_main.class);
                        intent.putExtra("type", "student");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (user_type.equals("educator")) {

            mAuth.signInWithEmailAndPassword(sEmail, sPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), educator_main.class);
                        intent.putExtra("type", "educator");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }


}

