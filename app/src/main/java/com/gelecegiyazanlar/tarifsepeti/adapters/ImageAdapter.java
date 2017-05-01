package com.gelecegiyazanlar.tarifsepeti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.MyImage;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
   private Context mContext;
   private List<MyImage> images;



   // Constructor
   public ImageAdapter(Context c, List<MyImage> imgData) {

      mContext = c;
      this.images = imgData;

   }
   
   public int getCount() {
      return images.size();
   }

   public Object getItem(int position) {
      return null;
   }

   public long getItemId(int position) {
      return 0;
   }
   
   // create a new ImageView for each item referenced by the Adapter
   public View getView(int position, View convertView, ViewGroup parent) {

      LayoutInflater layoutInflater = LayoutInflater.from(mContext);

      ImageView customImageView;

      if (convertView == null) {

         customImageView = (ImageView) layoutInflater.inflate(R.layout.image_list_item, parent, false).findViewById(R.id.item_img);


      }
      else 
      {
         customImageView = (ImageView) convertView;
      }

      Glide.with(mContext).

              load(images.get(position)
                        .getImgLink())
                        .crossFade()
                        .centerCrop()
                        .into(customImageView);

      return customImageView;
   }

}