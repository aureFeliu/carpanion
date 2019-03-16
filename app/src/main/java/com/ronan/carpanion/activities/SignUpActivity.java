package com.ronan.carpanion.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.User;

public class SignUpActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView password;
    private Spinner userType;
    private Button signUpButton;
    private Button selectProfileButton;

    private ProgressDialog progressDialog;
    private ProgressDialog userProgressDialog;

    private String profileImageURL = "https://firebasestorage.googleapis.com/v0/b/carpanionfirebase.appspot.com/o/Profile_Images%2F301565099?alt=media&token=e35a684d-0436-4241-87b4-329ab56f5376";
    private int userTypeSelected;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        progressDialog = new ProgressDialog(this);
        userProgressDialog = new ProgressDialog(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/circular-book.otf");

        firstName = (TextView) findViewById(R.id.firstName);
        firstName.setTypeface(typeface);
        lastName = (TextView) findViewById(R.id.lastName);
        lastName.setTypeface(typeface);
        email = (TextView) findViewById(R.id.email);
        email.setTypeface(typeface);
        password = (TextView) findViewById(R.id.password);
        password.setTypeface(typeface);
        userType = (Spinner) findViewById(R.id.userType);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setTypeface(typeface);
        selectProfileButton = (Button) findViewById(R.id.selectProfileButton);
        selectProfileButton.setTypeface(typeface);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.userType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);

        userType.setOnItemSelectedListener(this);

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                userProgressDialog.setMessage("Creating User");
                userProgressDialog.show();

                String inputEmail = email.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(inputEmail))
                {
                    Toast.makeText(getApplicationContext(), "Enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(inputPassword))
                {
                    Toast.makeText(getApplicationContext(), "Enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(inputPassword.length() < 6)
                {
                    Toast.makeText(getApplicationContext(), "Password too short, minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (!task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            userProgressDialog.dismiss();
                            user = auth.getCurrentUser();

                            System.out.println("SIGN UP ACTIVITY TOKEN: " + token);
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(firstName.getText().toString(), lastName.getText().toString(), profileImageURL, userTypeSelected, 0, token);
                            databaseReference.child("user").child(userID).setValue(user);

                            startActivity(new Intent(SignUpActivity.this, MapsActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

        selectProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
    {
        switch (pos)
        {
            case 0:
                userTypeSelected = 0;
                break;
            case 1:
                userTypeSelected = 1;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            progressDialog.setMessage("Uploading Image");
            progressDialog.show();
            Uri uri = data.getData();

            StorageReference ref = storageReference.child("Profile_Images").child(uri.getLastPathSegment());
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    progressDialog.dismiss();
                    Uri imageURL = taskSnapshot.getMetadata().getDownloadUrl();
                    profileImageURL = imageURL.toString();

                    Toast.makeText(SignUpActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
