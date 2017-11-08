package com.magonapps.android.firebasephotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gonzales on 10/28/2017.
 */

public class FBasePhotoSignupFragment extends Fragment {

    private static final String TAG = "FBaseSignupFragment";
    private static final int PICK_FILE_REQUEST = 0;

    @BindView(R.id.signup_button)
    Button mSignupButton;

    @BindView(R.id.signup_email_text)
    EditText mUsernameSignup;

    @BindView(R.id.signup_user_password)
    EditText mSignupPassword;

    @BindView(R.id.signup_password_confirm)
    EditText mSignupPasswordConfirm;

    @BindView(R.id.signup_progress_icon)
    ProgressBar mProgressBarIcon;

    @BindView(R.id.signup_full_name_text)
    EditText mFullName;

    @BindView(R.id.signup_home_country_text)
    EditText mHomeCountryText;

    @BindView(R.id.signup_choose_profile_picture)
    CardView mCardSelectPicture;

    @BindView(R.id.signup_placeholder_image)
    ImageView mPlaceholderImage;

    @BindView(R.id.signup_text_picture_hint)
    TextView mTakePictureHintText;

    @BindView(R.id.signup_user_image_icon)
    ImageView mSignupUserIcon;

    @BindView(R.id.signup_password_icon)
    ImageView mSignupPasswordIcon;

    private FirebaseAuth mFirebaseAuthSignup;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Uri mProfilePictureUri;

    public static FBasePhotoSignupFragment newInstance(){
        return new FBasePhotoSignupFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuthSignup.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mFirebaseAuthSignup.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuthSignup = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG,"onAuthStateChanged:signed_in"+user.getEmail());
                }else{
                    Log.d(TAG,"onAuthStateChanged:user logged out");
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_firebase_photo_signup,container,false);
        ButterKnife.bind(this,mainView);

        Uri mLogoUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.placeholder_new);
        Uri mUserUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.user_icon);
        Uri mPasswordUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.password_icon);

        GlideApp.with(this)
                .load(mLogoUri)
                .override(140,140)
                .into(mPlaceholderImage);

        GlideApp.with(this)
                .load(mUserUri)
                .override(40,40)
                .into(mSignupUserIcon);

        GlideApp.with(this)
                .load(mPasswordUri)
                .override(40,40)
                .into(mSignupPasswordIcon);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mUsernameSignup.getText().toString().trim();
                final String password = mSignupPassword.getText().toString().trim();
                final String passwordConfirm = mSignupPasswordConfirm.getText().toString().trim();
                final String fullName = mFullName.getText().toString().trim();
                final String homeCountry = mHomeCountryText.getText().toString().trim();

                boolean isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if(!email.isEmpty()&&!password.isEmpty()&&!fullName.isEmpty()&&!homeCountry.isEmpty()&&!passwordConfirm.isEmpty()){
                    if(isValidEmail){
                        if(password.length()>=6) {
                            if (password.equals(passwordConfirm)) {
                                mProgressBarIcon.setVisibility(View.VISIBLE);
                                mProgressBarIcon.animate();

                                mFirebaseAuthSignup.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    String uriPath = "placeholder";
                                                    if(mProfilePictureUri!=null)
                                                        uriPath = mProfilePictureUri.toString();

                                                    ArrayList<String> test = new ArrayList<>();
                                                    test.add("hello");
                                                    test.add("testing");

                                                    FirebasePhotosUser firebasePhotosUser = new FirebasePhotosUser(fullName, homeCountry, uriPath, test);
                                                    Intent intent = FBasePhotoProfileActivity.newIntentSignup(FBasePhotoSignupFragment.this.getActivity(), firebasePhotosUser);
                                                    startActivity(intent);
                                                    mProgressBarIcon.setVisibility(View.GONE);
                                                    FBasePhotoSignupFragment.this.getActivity().finish();

                                                } else {
                                                    mProgressBarIcon.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            } else {
                                Snackbar.make(mainView,"Passwords don't match.",Snackbar.LENGTH_SHORT).show();
                            }
                        }else{
                            Snackbar.make(mainView,"Password must be at least 6 characters long",Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(mainView,"Not a valid e-mail address.",Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar.make(mainView,"There are empty fields.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mCardSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select a file"),PICK_FILE_REQUEST);
            }
        });

        return mainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE_REQUEST
                && resultCode == getActivity().RESULT_OK
                && data != null
                && data.getData()!=null){

            mProfilePictureUri = data.getData();

            GlideApp.with(FBasePhotoSignupFragment.this)
                    .load(mProfilePictureUri)
                    .into(mPlaceholderImage);



        }
    }
}
