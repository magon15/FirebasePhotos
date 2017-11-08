package com.magonapps.android.firebasephotos;

import android.support.v4.app.Fragment;

/**
 * Created by Gonzales on 10/25/2017.
 */

public class FBasePhotoLoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return FBasePhotoLoginFragment.newInstance();
    }
}
