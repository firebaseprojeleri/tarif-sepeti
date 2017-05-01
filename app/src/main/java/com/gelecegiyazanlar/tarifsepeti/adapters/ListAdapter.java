package com.gelecegiyazanlar.tarifsepeti.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.gelecegiyazanlar.tarifsepeti.viewholders.ListViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<RECIPE> implements Filterable
{

    public static final int FILTER_TITLE = 1;
    public static final int FILTER_CATEGORY = 2;
    private LinkedHashMap<String, RECIPE> mBlogPostsMap;

    private static final String TAG = "ListAdapter";
    private Filter valueFilter;
    private LinkedHashMap<String, RECIPE> fullMap;
    private LinkedHashMap<String, RECIPE> filterMap;

    public ListAdapter(Context context, LinkedHashMap<String, RECIPE> blogPostsMap) {
        super(context, R.layout.custom_blog_row, new ArrayList<>(blogPostsMap.values()));

        this.mBlogPostsMap = blogPostsMap;
        this.fullMap = new LinkedHashMap<>(blogPostsMap);
        this.filterMap = new LinkedHashMap<>(blogPostsMap);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListViewHolder listViewHolder = new ListViewHolder(getContext(), parent);

        return listViewHolder.bindToBlogPost(mBlogPostsMap, position);
    }

    @Override
    public int getCount() {
        return mBlogPostsMap.size();
    }

    public Filter getMyFilter(int filterNo){

        switch (filterNo){

            case FILTER_TITLE:

//                if (valueFilter == null) {

                    valueFilter = new TitleValueFilter();

//                }

                break;
            case FILTER_CATEGORY:

//                if (valueFilter == null) {

                  valueFilter = new CategoryValueFilter();

//                }

                break;

        }

        return valueFilter;
    }

    public void update(LinkedHashMap<String, RECIPE> blogPostsMap) {

        this.mBlogPostsMap = blogPostsMap;
        notifyDataSetChanged();

    }

    private class TitleValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            filterMap = new LinkedHashMap<>(fullMap);
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                Log.d(TAG, "constraint: " + constraint.toString());

                for (Map.Entry<String, RECIPE> e : fullMap.entrySet()) {

                    Log.d(TAG, "constraint: " + e.getValue().getTitle() + ", " + e.getValue().getCategory());

                    //başlık ve kategoride filtreler
                    if (!(e.getValue().getTitle().toUpperCase().contains(constraint.toString().toUpperCase()))) {

                        filterMap.remove(e.getKey());

                    } else {

                        Log.d(TAG, "constraint sonuc: " + e.getValue().getTitle() + ", " + e.getValue().getCategory());

                        Log.i(TAG, "sonuc: " + e.getValue().getTitle());

                    }

                }
                results.count = filterMap.size();
                results.values = filterMap;

            } else {

                results.count = fullMap.size();
                results.values = fullMap;

            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            mBlogPostsMap = new LinkedHashMap<>(filterMap);
            notifyDataSetChanged();

        }

    }

        private class CategoryValueFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                filterMap = new LinkedHashMap<>(fullMap);
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {

                    Log.d(TAG, "constraint: " + constraint.toString());

                    for(Map.Entry<String, RECIPE> e : fullMap.entrySet()) {

                        Log.d(TAG, "constraint: " + e.getValue().getTitle() + ", " +   e.getValue().getCategory());

                        //başlık ve kategoride filtreler
                        if (!(e.getValue().getCategory().toUpperCase().contains(constraint.toString().toUpperCase())) && !(constraint.toString().toUpperCase().equals("TÜM TARİFLER".toUpperCase()))) {

                            filterMap.remove(e.getKey());

                        }else{

                            Log.d(TAG, "constraint sonuc: " + e.getValue().getTitle() + ", " +   e.getValue().getCategory());

                            Log.i(TAG, "sonuc: " + e.getValue().getTitle());

                        }

                    }
                    results.count = filterMap.size();
                    results.values = filterMap;

                } else {

                    results.count = fullMap.size();
                    results.values = fullMap;

                }
                return results;

            }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            mBlogPostsMap = new LinkedHashMap<>(filterMap);
            notifyDataSetChanged();

        }

    }

}
