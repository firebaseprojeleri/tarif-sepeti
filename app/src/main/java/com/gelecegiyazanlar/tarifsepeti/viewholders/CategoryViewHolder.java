package com.gelecegiyazanlar.tarifsepeti.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.CategoryRowItem;
import com.gelecegiyazanlar.tarifsepeti.widgets.CanaroTextView;

import java.util.List;

/**
 * Created by serdar on 30.07.2016
 */
public class CategoryViewHolder {

    private static final String TAG = "CategoryViewHolder";
    public CanaroTextView categoryName;
    public CanaroTextView categoryNo;

    private View mItemView;
    private Context mContext;

    public CategoryViewHolder(Context context, ViewGroup parent) {

        this.mContext = context;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        this.mItemView = layoutInflater.inflate(R.layout.category_row, parent, false);

        categoryName = (CanaroTextView) mItemView.findViewById(R.id.category_name);

        categoryNo = (CanaroTextView) mItemView.findViewById(R.id.category_items_no);

    }

    public View bindToRow(List<CategoryRowItem> categoryRowItems, int position) {

        categoryName.setText(categoryRowItems.get(position).name.toUpperCase());
        categoryNo.setText(categoryRowItems.get(position).no.toUpperCase());

        return mItemView;
    }

}
