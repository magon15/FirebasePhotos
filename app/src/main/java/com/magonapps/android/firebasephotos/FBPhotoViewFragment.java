package com.magonapps.android.firebasephotos;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gonzales on 11/4/2017.
 */

public class FBPhotoViewFragment extends Fragment {

    private static final String PHOTO_URI = "photouri";

    @BindView(R.id.view_image_view)
    ImageView mViewImage;

    public static FBPhotoViewFragment newInstance(String photoUri){
        FBPhotoViewFragment fbPhotoViewFragment = new FBPhotoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHOTO_URI,photoUri);
        fbPhotoViewFragment.setArguments(bundle);

        return fbPhotoViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view_layout,container,false);
        ButterKnife.bind(this,view);

        GlideApp.with(this)
                .load(Uri.parse((String) getArguments().getSerializable(PHOTO_URI)))
                .into(mViewImage);

        return view;
    }
}
