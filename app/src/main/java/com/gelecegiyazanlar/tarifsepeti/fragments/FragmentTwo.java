package com.gelecegiyazanlar.tarifsepeti.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gelecegiyazanlar.tarifsepeti.utils.FireUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by serdar on 30.07.2016
 */
public class FragmentTwo extends Fragment{

    private static final String TAG = "FragmentOne";

    private DatabaseReference mDatabase;
    private DatabaseReference mBlogPost;
    private ImageView imageViewBlogImage;
    private TextView textViewBlogTitle;
    private TextView textViewBlogBody;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        View fragmentTwoView = inflater.inflate(R.layout.fragment_two, container, false);

        Bundle bundle = getArguments();
        String mBlogPostKey = bundle.getString("blogPostKey");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mBlogPost = mDatabase.child(FireUtils.RECIPES).child(mBlogPostKey);

        imageViewBlogImage = (ImageView) fragmentTwoView.findViewById(R.id.imageViewBlogImage);
        textViewBlogTitle = (TextView) fragmentTwoView.findViewById(R.id.textViewBlogTitle);
        textViewBlogBody = (TextView) fragmentTwoView.findViewById(R.id.textViewBlogBody);

        return fragmentTwoView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        mBlogPost.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                RECIPE recipe = dataSnapshot.getValue(RECIPE.class);

                Glide
                        .with(getContext())
                        .load(recipe.getImgLink())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewBlogImage);

                textViewBlogTitle.setText(recipe.getTitle());
                textViewBlogBody.setText(recipe.getBody());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }

        });

    }

}
