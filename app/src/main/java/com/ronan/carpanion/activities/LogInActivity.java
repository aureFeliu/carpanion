package com.ronan.carpanion.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.User;

public class LogInActivity extends Activity
{
    private EditText email, password;
    private TextView logoText;
    private FirebaseAuth auth;
    private Button loginButton, signUpButton;
    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database;
    private User user;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        token = FirebaseInstanceId.getInstance().getToken();
        //Log.d("LOGIN REFRESH TOKENS", token);

        if(auth.getCurrentUser() != null)
        {
            database = firebaseDatabase.getReference("user").child(auth.getCurrentUser().getUid());
            database.child("pushToken").setValue(token);

            startActivity(new Intent(LogInActivity.this, MapsActivity.class));
            finish();
        }

        setContentView(R.layout.activity_log_in);

        progressDialog = new ProgressDialog(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/circular-bold.otf");

        logoText = (TextView) findViewById(R.id.logoText);
        logoText.setTypeface(typefaceBold);
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(typeface);
        password = (EditText) findViewById(R.id.password);
        password.setTypeface(typeface);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setTypeface(typeface);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setTypeface(typeface);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressDialog.setMessage("Logging In");
                progressDialog.show();

                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();

                if(TextUtils.isEmpty(inputEmail))
                {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(inputPassword))
                {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(!task.isSuccessful())
                        {
                            progressDialog.hide();
                            Toast.makeText(LogInActivity.this, "Log In failed, try again", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.hide();
                            database = firebaseDatabase.getReference("user").child(auth.getCurrentUser().getUid());
                            database.child("pushToken").setValue(token);
                            startActivity(new Intent(LogInActivity.this, MapsActivity.class));
                        }
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
                //startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });
    }
}
