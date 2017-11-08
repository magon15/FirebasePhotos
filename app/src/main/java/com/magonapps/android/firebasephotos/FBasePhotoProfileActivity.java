package com.magonapps.android.firebasephotos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gonzales on 10/28/2017.
 */

public class FBasePhotoProfileActivity extends SingleFragmentActivity {

    private static final String IS_SIGN_UP = "issignup";
    private static final String FIREBASE_PHOTOS_USER = "firebase_photos_user";

    private FirebasePhotosUser mFirebasePhotosUser;

    private static final String TAG = "ProfileActivity";

    public static Intent newIntentSignup(Context context, FirebasePhotosUser photosUser){
        Intent intent = new Intent(context, FBasePhotoProfileActivity.class);
        intent.putExtra(FIREBASE_PHOTOS_USER,photosUser);
        intent.putExtra(IS_SIGN_UP,true);

        return intent;
    }

    public static Intent newIntentLogin(Context context){
        Intent intent = new Intent(context, FBasePhotoProfileActivity.class);
        intent.putExtra(IS_SIGN_UP,false);

        return intent;
    }


    @Override
    protected Fragment createFragment() {
        if(getIntent().getBooleanExtra(IS_SIGN_UP,true) ){
            mFirebasePhotosUser = (FirebasePhotosUser) getIntent().getSerializableExtra(FIREBASE_PHOTOS_USER);
            return FBasePhotoProfileFragment.newInstanceSignup(mFirebasePhotosUser);
        }else{
            return FBasePhotoProfileFragment.newInstanceLogin();
        }
    }
}
