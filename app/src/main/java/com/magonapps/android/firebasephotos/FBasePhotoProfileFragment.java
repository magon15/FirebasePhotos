package com.magonapps.android.firebasephotos;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gonzales on 10/28/2017.
 */

public class FBasePhotoProfileFragment extends Fragment {

    private FirebaseAuth mFirebaseAuthProfile;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private RecyclerView mPhotoRecyclerView;
    private PhotoListViewAdapter mPhotoListViewAdapter;
    private FirebasePhotosUser mFirebasePhotosUser;
    private ChildEventListener mPhotoChildListener;

    private ArrayList<String> photoUrls = new ArrayList<>();

    private static final String TAG = "ProfileFragment";
    private static final int FILE_UPLOAD = 0;

    @BindView(R.id.profile_picture_circle)
    ImageView mProfilePicture;

    @BindView(R.id.profile_name_text)
    TextView mProfileUserName;

    @BindView(R.id.profile_location_text)
    TextView mHomeLocation;

    @BindView(R.id.profile_sign_out)
    Button mButtonSignOut;

    @BindView(R.id.profile_upload_fab)
    FloatingActionButton mUploadPhotoButton;

    private static final String FIREBASE_PHOTOS_USER = "profile_fragment_user";
    private static final String IS_SIGN_UP = "issignup";

    public static FBasePhotoProfileFragment newInstanceSignup(FirebasePhotosUser photoUser){
        Bundle args = new Bundle();
        args.putSerializable(FIREBASE_PHOTOS_USER,photoUser);
        args.putBoolean(IS_SIGN_UP,true);

        FBasePhotoProfileFragment profileFragment = new FBasePhotoProfileFragment();
        profileFragment.setArguments(args);

        return profileFragment;

    }

    public static FBasePhotoProfileFragment newInstanceLogin(){
        Bundle args = new Bundle();
        args.putBoolean(IS_SIGN_UP,false);

        FBasePhotoProfileFragment profileFragment = new FBasePhotoProfileFragment();
        profileFragment.setArguments(args);

        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuthProfile = FirebaseAuth.getInstance();



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firebase_photo_profile,container,false);
        ButterKnife.bind(this,view);

        final FirebaseUser firebaseUser = mFirebaseAuthProfile.getCurrentUser();

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.profile_photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));

        mPhotoListViewAdapter = new PhotoListViewAdapter();
        mPhotoRecyclerView.setAdapter(mPhotoListViewAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("user_photo_urls");
        DatabaseReference mUploadedPictures = mDatabaseReference.child(firebaseUser.getUid()+"/photoFileNames");

        mUploadedPictures.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photoUrls = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    photoUrls.add((String) snapshot.getValue());
                    Log.d(TAG, (String) snapshot.getValue());
                }

                mPhotoListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(getArguments().getBoolean(IS_SIGN_UP)){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");


            mFirebasePhotosUser = (FirebasePhotosUser) getArguments().getSerializable(FIREBASE_PHOTOS_USER);
            String localFullName = mFirebasePhotosUser.getFullName();
            String localLocation = mFirebasePhotosUser.getHomeCountry();
            final Uri photoUri = Uri.parse(mFirebasePhotosUser.getProfilePicUri());

            mProfileUserName.setText(localFullName);
            mHomeLocation.setText(localLocation);

            mDatabaseReference.child(firebaseUser.getUid()).setValue(mFirebasePhotosUser);

            mStorageReference = FirebaseStorage.getInstance().getReference("users");
            StorageReference imageRef = mStorageReference.child(firebaseUser.getUid()+"/profile_pic/profilePicture.jpg");
            imageRef.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Log.d(TAG,"upload success");
                        }
                    });


            GlideApp.with(FBasePhotoProfileFragment.this)
                    .load(photoUri)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(mProfilePicture);

        }else{
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        mFirebasePhotosUser = dataSnapshot.child(firebaseUser.getUid()).getValue(FirebasePhotosUser.class);

                    mProfileUserName.setText(mFirebasePhotosUser.getFullName());
                    mHomeLocation.setText(mFirebasePhotosUser.getHomeCountry());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mStorageReference = FirebaseStorage.getInstance().getReference("users");
            StorageReference imageRef = mStorageReference.child(firebaseUser.getUid()+"/profile_pic/profilePicture.jpg");

            GlideApp.with(FBasePhotoProfileFragment.this)
                    .load(imageRef)
                    .circleCrop()
                    .into(mProfilePicture);

        }





        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuthProfile.signOut();
                Intent intent = new Intent(FBasePhotoProfileFragment.this.getActivity(),FBasePhotoLoginActivity.class);
                startActivity(intent);
                FBasePhotoProfileFragment.this.getActivity().finish();
            }
        });

        mUploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select a file"),FILE_UPLOAD);

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILE_UPLOAD
                && resultCode == getActivity().RESULT_OK
                && data != null
                && data.getData()!=null){

            String dateTime = Long.toString(new Date().getTime());
            final FirebaseUser firebaseUser = mFirebaseAuthProfile.getCurrentUser();

            mStorageReference = FirebaseStorage.getInstance().getReference("users");
            final StorageReference imageRef = mStorageReference.child(firebaseUser.getUid()+"/uploaded_images/" + dateTime + ".jpg");
            imageRef.putFile(data.getData())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG,"upload success");

                            mDatabaseReference = FirebaseDatabase.getInstance().getReference("user_photo_urls");
                            DatabaseReference mUploadedPictures = mDatabaseReference.child(firebaseUser.getUid()+"/photoFileNames");
//
                            mUploadedPictures.push().setValue(taskSnapshot.getDownloadUrl().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    });
        }
    }

    class PhotoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mProfileImageView;
        private Uri mPhotoUri;
        private int position;

        public PhotoListViewHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_single_image);
            mPhotoUri = Uri.parse("android.resource://com.magonapps.android.firebasephotos/"+R.drawable.mugshot);
            itemView.setOnClickListener(this);
        }

        private void onBindViewHolder(int position){
            this.position = position;
            GlideApp.with(FBasePhotoProfileFragment.this)
            .load(photoUrls.get(position))
                    .placeholder(R.drawable.unnamed)
                    .into(mProfileImageView);

        }

        @Override
        public void onClick(View v) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                        FBasePhotoProfileFragment.this.getActivity(),
                        mProfileImageView,
                        mProfileImageView.getTransitionName()
                ).toBundle();


                Intent intent = FBPhotoViewActivity.newIntent(FBasePhotoProfileFragment.this.getActivity(),photoUrls.get(this.position));
                startActivity(intent,bundle);

            }
        }
    }

    private class PhotoListViewAdapter extends RecyclerView.Adapter<PhotoListViewHolder>{

        @Override
        public PhotoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_single_image,null);
            return new PhotoListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoListViewHolder holder, int position) {
            holder.onBindViewHolder(position);
        }

        @Override
        public int getItemCount() {
            return photoUrls.size();
        }
    }
}
