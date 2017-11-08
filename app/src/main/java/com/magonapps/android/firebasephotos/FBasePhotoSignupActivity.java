package com.magonapps.android.firebasephotos;

import android.support.v4.app.Fragment;

/**
 * Created by Gonzales on 10/28/2017.
 */

public class FBasePhotoSignupActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return FBasePhotoSignupFragment.newInstance();
    }
}
