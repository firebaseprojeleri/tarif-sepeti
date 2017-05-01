package com.gelecegiyazanlar.tarifsepeti.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.adapters.ListAdapter;
import com.gelecegiyazanlar.tarifsepeti.listeners.AppListener;
import com.gelecegiyazanlar.tarifsepeti.listeners.FireListener;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;

import java.util.LinkedHashMap;

/**
 * Created by serdar on 30.07.2016
 */
public class FragmentOne extends Fragment {

    private static final String TAG = "FragmentOne";
    private ListView listView;
    public ListAdapter listAdapter;

private View fragmentOneView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        fragmentOneView = inflater.inflate(R.layout.fragment_one, container, false);

        listView = (ListView) fragmentOneView.findViewById(R.id.postsList);

        return fragmentOneView;
    }

    public void populateListView(final LinkedHashMap<String, RECIPE> blogPostsMap) {

        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists populateListView");

        if(listAdapter == null) {

            Log.d(TAG, "testMainUpdate initRecipesMapsAndLists populateListView if");

            listAdapter = new ListAdapter(getContext(), blogPostsMap);
            listView.setAdapter(listAdapter);

        }else{

            Log.d(TAG, "testMainUpdate initRecipesMapsAndLists populateListView else");

            listAdapter.update(blogPostsMap);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppListener appListener = (AppListener)getActivity();
                appListener.openFragmentTwo((String) blogPostsMap.keySet().toArray()[blogPostsMap.size() - position - 1]);

            }

        });

        FireListener fireListener = (FireListener) getContext();
        if(fireListener!= null){

            Log.i(TAG, "fire is not null");

            fireListener.listenShowProgressBar(false);

        }else{

            Log.i(TAG, "fire is null");


        }

    }

}
