package com.magonapps.android.firebasephotos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gonzales on 10/25/2017.
 */

public class FBasePhotoLoginFragment extends Fragment{

    private FirebaseAuth mFirebaseAuthLogin;
    private ProgressBar mProgressDialog;
    private SharedPreferences mPreferences = null;

    private static final String TAG = "FBasePhotoLoginFragment";

    @BindView(R.id.signup_email_text)
    EditText mUserText;

    @BindView(R.id.signup_user_password)
    EditText mUserPassword;

    @BindView(R.id.login_progress_icon)
    ProgressBar mProgressIcon;

    @BindView(R.id.login_button)
    Button mButtonLogin;

    @BindView(R.id.user_image_icon)
    ImageView mLoginUserIcon;

    @BindView(R.id.password_icon)
    ImageView mLoginPassIcon;

    @BindView(R.id.login_icon_image)
    ImageView mMainLogoImage;

    private void login(final View view) {

        String email = mUserText.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();

        if(!email.isEmpty() && !password.isEmpty()) {

            boolean isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (isValidEmail) {

                mProgressIcon.setVisibility(View.VISIBLE);
                mProgressIcon.animate();

                mFirebaseAuthLogin.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = FBasePhotoProfileActivity.newIntentLogin(FBasePhotoLoginFragment.this.getActivity());
                                    startActivity(intent);
                                    FBasePhotoLoginFragment.this.getActivity().finish();
                                } else {
                                    mProgressIcon.setVisibility(View.GONE);
                                    Snackbar.make(view,"There's a problem signing in.",Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(getActivity(), "Not a valid e-mail address.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "E-mail or password must not be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.login_button_signup)
    void signup(){
        Intent intent = new Intent(this.getActivity(),FBasePhotoSignupActivity.class);
        startActivity(intent);
        Log.d(TAG,"hello");
    }

    public static FBasePhotoLoginFragment newInstance(){
        return new FBasePhotoLoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuthLogin = FirebaseAuth.getInstance();
        if(mFirebaseAuthLogin.getCurrentUser()!=null){
            Intent intent = FBasePhotoProfileActivity.newIntentLogin(getActivity());
            startActivity(intent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_firebase_photo_login,container,false);
        ButterKnife.bind(this,view);

        Uri mLogoUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.firebase_image_icon);
        Uri mUserUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.user_icon);
        Uri mPasswordUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.password_icon);

        GlideApp.with(FBasePhotoLoginFragment.this)
                .load(mLogoUri)
                .override(120,120)
                .into(mMainLogoImage);

        GlideApp.with(FBasePhotoLoginFragment.this)
                .load(mUserUri)
                .override(40,40)
                .into(mLoginUserIcon);

        GlideApp.with(FBasePhotoLoginFragment.this)
                .load(mPasswordUri)
                .override(40,40)
                .into(mLoginPassIcon);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(view);
            }
        });

        return view;
    }

    private class LoadImagesTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

}
