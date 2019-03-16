package com.ronan.carpanion.util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIDService extends FirebaseInstanceIdService
{
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser currentUser;

    @Override
    public void onTokenRefresh()
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("REFRESH TOKEN", refreshedToken);

        database = FirebaseDatabase.getInstance();

//        if(auth.getCurrentUser() != null)
//        {
//            ref = database.getReference(auth.getCurrentUser().getUid());
//            ref.child("pushToken").setValue(refreshedToken);
//        }
    }
}