package com.gelecegiyazanlar.tarifsepeti.viewholders;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.gelecegiyazanlar.tarifsepeti.utils.FireUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.LinkedHashMap;

/**
 * Created by serdar on 30.07.2016
 */
public class ListViewHolder {

    private static final String TAG = "listViewHolder";
    public ImageView imageViewBlogImage;
    public TextView textViewBlogTitle;
    public ImageView starView;
    public TextView numStarsView;

    private View mItemView;
    private Context mContext;

    public ListViewHolder(Context context, ViewGroup parent) {

        this.mContext = context;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        this.mItemView = layoutInflater.inflate(R.layout.custom_blog_row, parent, false);

        imageViewBlogImage = (ImageView) mItemView.findViewById(R.id.imageViewBlogImage);
        textViewBlogTitle = (TextView) mItemView.findViewById(R.id.textViewBlogTitle);
        numStarsView = (TextView) mItemView.findViewById(R.id.post_num_stars);
        starView = (ImageView) mItemView.findViewById(R.id.star);

    }

    public View bindToBlogPost(LinkedHashMap<String, RECIPE> blogPostsMap, int position) {

        int size = blogPostsMap.size();
        int newPos = size - position - 1;

        if(size > 0 && newPos >= 0) {

            final String recipeKey = (String) blogPostsMap.keySet().toArray()[newPos];
            final RECIPE recipe = (RECIPE) blogPostsMap.values().toArray()[newPos];

            if (recipe.stars.containsKey(FireUtils.getUid())) {

                starView.setImageResource(R.drawable.ic_star_white_24dp);

            } else {

                starView.setImageResource(R.drawable.ic_star_border_white_24dp);

            }

            View.OnClickListener st = new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.i(TAG, "star cliked bp: " + recipeKey + " ,  " + recipe.getTitle());

                    updateStars(recipeKey, recipe);

                }

            };

            View.OnClickListener tt = new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }

            };


            Glide
                    .with(mContext)
                    .load(recipe.getImgLink())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewBlogImage);

            textViewBlogTitle.setText(recipe.getTitle());
            textViewBlogTitle.setOnClickListener(tt);
            starView.setOnClickListener(st);
            numStarsView.setText(String.valueOf(recipe.starCount));

        }

        return mItemView;
    }

    private void updateStars(String pKey, RECIPE p) {

        if (p.stars.containsKey(FireUtils.getUid())) {

            // Unstar the post and remove self from stars
            p.starCount = p.starCount - 1;
            p.stars.remove(FireUtils.getUid());

        } else {

            // Star the post and add self to stars
            p.starCount = p.starCount + 1;
            p.stars.put(FireUtils.getUid(), true);

        }
        FireUtils.getBlogPostsDbRef().child(pKey).setValue(p);

    }

}
