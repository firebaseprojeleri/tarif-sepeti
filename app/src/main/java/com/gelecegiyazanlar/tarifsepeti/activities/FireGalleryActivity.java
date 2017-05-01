package com.gelecegiyazanlar.tarifsepeti.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.adapters.ImageAdapter;
import com.gelecegiyazanlar.tarifsepeti.models.MyImage;
import com.gelecegiyazanlar.tarifsepeti.utils.FireUtils;
import com.gelecegiyazanlar.tarifsepeti.utils.MyFileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireGalleryActivity extends AppCompatActivity {

    private List<MyImage> imgData = new ArrayList<>();
    private Activity activity;
    private String TAG = "FireGalleryActivity";
    private ImageAdapter imageAdapter;
    private GridView gridView;
    private View selectedView;
    private MyImage clickedImage;
    private Menu optionMenu;
    private ProgressBar toolbarProgressBar;
    private enum MenuState{HIDE, SHOW};
    private MenuState menuState = MenuState.SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_gallery);

        activity = this;

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        toolbarProgressBar = (ProgressBar) findViewById(R.id.toolbarProgreesBar);

        getSupportActionBar().setTitle(R.string.fire_gallery);

        gridView = (GridView)findViewById(R.id.image_list);

    }

    private void populateGalleryView(DataSnapshot dataSnapshot) {

        imgData.clear();

        for (DataSnapshot dt : dataSnapshot.getChildren()) {

            MyImage mi = dt.getValue(MyImage.class);
            imgData.add(mi);

        }

        if (imageAdapter == null) {

            imageAdapter = new ImageAdapter(this, imgData);
            gridView.setAdapter(imageAdapter);

        } else {

            imageAdapter.notifyDataSetChanged();

        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                clickedImage = imgData.get(i);
                setViewUnSelected(selectedView);
                setViewSelected(view);
                selectedView = view;

                if(clickedImage != null){

                    prepareOptionsMenuItem(optionMenu, R.id.option_1, R.drawable.ic_done_white_24dp, true);

                    prepareOptionsMenuItem(optionMenu, R.id.option_2, R.drawable.ic_delete_white_24dp, true);

                }

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        optionMenu = menu;

        getMenuInflater().inflate(R.menu.fire_gallery_menu, menu);

        if (menuState == MenuState.HIDE) {

            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);

        }else if(menuState == MenuState.SHOW){

            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(true);

        }

        FireUtils.getImagesDbRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                populateGalleryView(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.option_1:

                Intent intentData = new Intent();
                intentData.putExtra("imgName", clickedImage.getImgName());
                intentData.putExtra("imgLink", clickedImage.getImgLink());
                activity.setResult(activity.RESULT_OK, intentData);
                activity.finish(); // ends current activity


                break;
            case R.id.option_2:

                showProgressBar(true);


                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete record");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        dialog.dismiss();

                        FireUtils.getImagesFolderRef().child(clickedImage.getImgName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {


                            @Override
                            public void onSuccess(Void aVoid) {

                                showProgressBar(true);

                                Toast.makeText(FireGalleryActivity.this, "image successfully deleted", Toast.LENGTH_SHORT).show();

                            }

                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(FireGalleryActivity.this, "image could not be deleted", Toast.LENGTH_SHORT).show();

                            }

                        });

                        FireUtils.getImagesDbRef().child(MyFileUtils.getFileName(clickedImage.getImgName())).removeValue();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProgressBar(boolean b) {

        if(b){

            menuState = MenuState.HIDE;
            invalidateOptionsMenu();
            toolbarProgressBar.setVisibility(View.VISIBLE);

        }else{

            menuState = MenuState.SHOW;
            invalidateOptionsMenu();
            toolbarProgressBar.setVisibility(View.GONE);

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        prepareOptionsMenuItem(menu, R.id.option_1, R.drawable.ic_done_white_24dp, false);

        prepareOptionsMenuItem(menu, R.id.option_2, R.drawable.ic_delete_white_24dp, false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void prepareOptionsMenuItem(Menu menu, int itemId, int drawableId, boolean enabled) {

        MenuItem item = menu.findItem(itemId);
        Drawable icon = getResources().getDrawable(drawableId);

        if (!enabled) {

            icon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            item.setEnabled(enabled);
            item.setIcon(icon);

        }else{

            item.setEnabled(enabled);
            item.setIcon(icon);

        }

    }

    private void setViewUnSelected(View v) {

        if (v != null) {

            v.setBackgroundColor(getResources().getColor(R.color.image_list_item_background));

            int imgDefaultPadding = getResources().getDimensionPixelSize(R.dimen.image_list_item_default_padding);

            v.setPadding(imgDefaultPadding, imgDefaultPadding, imgDefaultPadding, imgDefaultPadding);

        }

    }

    private void setViewSelected(View v) {

        if (v != null) {

            v.setBackgroundColor(getResources().getColor(R.color.image_list_item_background_highlight));

            int imgSelectedPadding = getResources().getDimensionPixelSize(R.dimen.image_list_item_selected_padding);

            v.setPadding(imgSelectedPadding, imgSelectedPadding, imgSelectedPadding, imgSelectedPadding);

        }

    }

}
