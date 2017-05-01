package com.gelecegiyazanlar.tarifsepeti.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gelecegiyazanlar.tarifsepeti.models.CategoryRowItem;
import com.gelecegiyazanlar.tarifsepeti.viewholders.CategoryViewHolder;

import java.util.List;

/**
 * Created by serdar on 22.08.2016
 */
public class CategoryAdapter extends BaseAdapter{

    private static final String TAG = "CategoryAdapter";
    private Context context;
    private List<CategoryRowItem> categoryRowItems;

    public CategoryAdapter(Context context, List<CategoryRowItem> categoryRowItems) {

        this.context = context;
        this.categoryRowItems = categoryRowItems;

    }

    @Override
    public int getCount() {
        return categoryRowItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(context, viewGroup);

        return categoryViewHolder.bindToRow(categoryRowItems, i);
    }

}
