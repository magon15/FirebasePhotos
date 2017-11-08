package com.magonapps.android.firebasephotos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Gonzales on 11/4/2017.
 */

public class FBPhotoViewActivity extends SingleFragmentActivity {

    private static final String PHOTO_URI = "photouri";

    public static Intent newIntent(Context context, String mPhotoUri){
        Intent intent = new Intent(context,FBPhotoViewActivity.class);
        intent.putExtra(PHOTO_URI,mPhotoUri);

        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String photoUriString = getIntent().getStringExtra(PHOTO_URI);
        return FBPhotoViewFragment.newInstance(photoUriString);
    }
}
