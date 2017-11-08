package com.magonapps.android.firebasephotos;

import android.net.Uri;
import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzales on 10/31/2017.
 */

public class FirebasePhotosUser implements Serializable {
    private String mFullName;
    private String mHomeCountry;
    private String mProfilePicUri;
    private List<String> mPhotoFileNames;

    public List<String> getPhotoFileNames() {
        return mPhotoFileNames;
    }

    public void setPhotoFileNames(List<String> photoFileNames) {
        mPhotoFileNames = photoFileNames;
    }

    public FirebasePhotosUser() {
    }

    public FirebasePhotosUser(String fullName, String homeCountry, String profilePicUri, List<String> photoFileNames) {
        mFullName = fullName;
        mHomeCountry = homeCountry;
        mProfilePicUri = profilePicUri;
        mPhotoFileNames = photoFileNames;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getHomeCountry() {
        return mHomeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        mHomeCountry = homeCountry;
    }

    public String getProfilePicUri() {
        return mProfilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        mProfilePicUri = profilePicUri;
    }
}
