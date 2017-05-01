package com.gelecegiyazanlar.tarifsepeti.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.adapters.CategoryAdapter;
import com.gelecegiyazanlar.tarifsepeti.adapters.ListAdapter;
import com.gelecegiyazanlar.tarifsepeti.fragments.FragmentOne;
import com.gelecegiyazanlar.tarifsepeti.fragments.FragmentTwo;
import com.gelecegiyazanlar.tarifsepeti.listeners.AppListener;
import com.gelecegiyazanlar.tarifsepeti.listeners.FireListener;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.gelecegiyazanlar.tarifsepeti.models.Category;
import com.gelecegiyazanlar.tarifsepeti.models.CategoryRowItem;
import com.gelecegiyazanlar.tarifsepeti.models.User;
import com.gelecegiyazanlar.tarifsepeti.utils.FireUtils;
import com.gelecegiyazanlar.tarifsepeti.widgets.CanaroTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Created by Dmytro Denysenko on 5/4/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, AppListener, FireListener{

    private static final String TAG = "MainActivity";
    private static final long RIPPLE_DURATION = 250;

    private Toolbar toolbar;
    private FrameLayout root;
    private View contentHamburger;
    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;
    private LinearLayout admin_group,sign_out;
    private boolean guillotineIsOpen = false;
    private GuillotineAnimation guillotineAnimation;
    private ProgressBar toolbarProgressBar;
    private enum MenuState{HIDE, SHOW};
    private MenuState menuState = MenuState.SHOW;
    private FragmentOne fragmentOne;
    private User currentUser;
    private CanaroTextView profileText;
    private ListView categoriesListView;
    private boolean isOpenFragmentTwo = false;
    private boolean isClickedGuilloItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "debug setcontent");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        root = (FrameLayout)findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);

        toolbarProgressBar = (ProgressBar) findViewById(R.id.toolbarProgreesBar);

        showProgressBar(true);
        mFirebaseAuth=FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser == null){

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }else{

            openFragmentOne();

        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);

        root.addView(guillotineMenu);
        sign_out=(LinearLayout) guillotineMenu.findViewById(R.id.sign_out);
        categoriesListView = (ListView) guillotineMenu.findViewById(R.id.categories_list);

        profileText = (CanaroTextView)guillotineMenu.findViewById(R.id.profile_text);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {

                    @Override
                    public void onGuillotineOpened() {
                        guillotineIsOpen = true;

                    }

                    @Override
                    public void onGuillotineClosed() {
                        guillotineIsOpen = false;

                        if(isOpenFragmentTwo && isClickedGuilloItem){

                            onBackPressed();
                            isOpenFragmentTwo = false;                                                                isClickedGuilloItem = false;

                        }

                    }

                })
                .build();

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickedGuilloItem = true;
                mFirebaseAuth.signOut();
                guillotineAnimation.close();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        admin_group = (LinearLayout) guillotineMenu.findViewById(R.id.admin_group);
        admin_group.setOnClickListener(this);

        Intent searchIntent = getIntent();

        if(Intent.ACTION_SEARCH.equals(searchIntent.getAction())){

            String query = searchIntent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();

        }

        FireUtils.getBlogPostsDbRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange normal: " + dataSnapshot);

                initRecipesMapsAndLists(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    //[initRecipesMapsAndLists metodu başladı]
    /**Tarifleri HashMap'a atar kategorileri getirir*/
    private void initRecipesMapsAndLists(DataSnapshot dataSnapshot) {

        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists ***********************başlangıç");

        LinkedHashMap<String, RECIPE> recipesMap = new LinkedHashMap<>();

        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists recipesMap oluşturuldu");

        for(DataSnapshot data: dataSnapshot.getChildren()){

            Log.d(TAG, "testMainUpdate initRecipesMapsAndLists data for döngüsü");

            try {

                RECIPE recipe = data.getValue(RECIPE.class);
                recipesMap.put(data.getKey(), recipe);
                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists recipe: " + recipe.getTitle() + "    pushKey: " + data.getKey());

            }catch (DatabaseException d){

                d.printStackTrace();
                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists recipe çekilirken DatabaseException: " + d.getMessage());

            }

        }

        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists fragmentOne.populateListView(recipesMap) recipesMap.size(): " + recipesMap.size());
        fragmentOne.populateListView(recipesMap);

        final List<String> categories = new ArrayList<>();
        final List<RECIPE> recipes = new ArrayList<>(recipesMap.values());
        final List<CategoryRowItem> categoryRowItems = new ArrayList<>();//guillotine menü üzerindeki kategori elemanları

        //kategoriler çekiliyor
        FireUtils.getCategoriesDbRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                categories.clear();
                categoryRowItems.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Log.d(TAG, "testMainUpdate initRecipesMapsAndLists kategoriler çekiliyor for döngüsü");

                    try {



                        Category category = data.getValue(Category.class);
                        categories.add(category.getName());

                        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists kategoriler çekiliyor for döngüsü category: " + category.getName());

                    } catch (DatabaseException d) {

                        d.printStackTrace();
                        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists kategoriler çekiliyor for döngüsü DatabaseException: " + d.getMessage());

                    }

                }

                int no = 0;
                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists no for döngüsü başlıyacak no: " + no);
                for (int i = 0; i < categories.size(); i++) {

                    for (int j = 0; j < recipes.size(); j++) {

                        if (categories.get(i).equals(recipes.get(j).getCategory())) {

                            no++;
                            Log.d(TAG, "testMainUpdate initRecipesMapsAndLists no for döngüsü no: " + no);

                        }

                    }

                    categoryRowItems.add(new CategoryRowItem(categories.get(i), String.valueOf(no)));
                    no = 0;

                }

                categoryRowItems.add(0, new CategoryRowItem("TÜM TARİFLER", String.valueOf(recipes.size())));

                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists CategoryAdapter oluşturulacak categoryRowItems.size(): " + categoryRowItems.size());
                CategoryAdapter categoryAdapter = new CategoryAdapter(MainActivity.this, categoryRowItems);

                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists categoryAdapter bağlanacak");
                categoriesListView.setAdapter(categoryAdapter);
                Log.d(TAG, "testMainUpdate initRecipesMapsAndLists categoryAdapter bağlandı");
                categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        isClickedGuilloItem = true;
                        if(fragmentOne != null)fragmentOne.listAdapter.getMyFilter(ListAdapter.FILTER_CATEGORY).filter(categoryRowItems.get(position).name);
                        //onBackPressed();
                        guillotineAnimation.close();

                    }

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        Log.d(TAG, "testMainUpdate initRecipesMapsAndLists ***********************bitiş");

    }
    //[initRecipesMapsAndLists metodu bitti]

    @Override
    protected void onResume() {
        super.onResume();

        if(mUser == null){

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }else{

            FireUtils.getCurrentUserDbRef().addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    buildCurrentUser(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    }

    private void buildCurrentUser(DataSnapshot dataSnapshot) {

            try {

                String currentUserUid = dataSnapshot.getKey();
                User user = dataSnapshot.getValue(User.class);
                if(currentUserUid.equals(FireUtils.getUid())){

                    currentUser = user;

                    profileText.setText(currentUser.getUsername().toUpperCase());

                }

            }catch (DatabaseException d){

                d.printStackTrace();

            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (fragmentOne != null) {

                    Filter filter = fragmentOne.listAdapter.getMyFilter(ListAdapter.FILTER_TITLE);

                    if (filter != null) filter.filter(newText);

                }

                return false;
            }


        });

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if(!guillotineIsOpen) {

            super.onBackPressed();

        }else{

            guillotineAnimation.close();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.admin_group:

                startActivity(new Intent(MainActivity.this, WriteNewBlogPost.class));
                isClickedGuilloItem = true;
                guillotineAnimation.close();

                break;

        }

    }

    public void openFragmentOne(){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragmentOne = new FragmentOne();

        transaction.add(R.id.container, fragmentOne);
        ////transaction.addToBackStack("fragmentOne");
        transaction.commit();

    }

    @Override
    public void openFragmentTwo(String blogPostKey) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        FragmentTwo fragmentTwo = new FragmentTwo();
        Bundle bundle = new Bundle();
        bundle.putString("blogPostKey", blogPostKey);
        fragmentTwo.setArguments(bundle);

        transaction.add(R.id.container, fragmentTwo);
        transaction.addToBackStack("fragmentTwo");
        transaction.commit();
        isOpenFragmentTwo = true;

    }

    @Override
    public void listenShowProgressBar(Boolean b) {

        showProgressBar(b);

    }

    private void showProgressBar(boolean b) {

        if(b){

            toolbarProgressBar.setVisibility(View.VISIBLE);

        }else{

            toolbarProgressBar.setVisibility(View.GONE);

        }

    }
}
