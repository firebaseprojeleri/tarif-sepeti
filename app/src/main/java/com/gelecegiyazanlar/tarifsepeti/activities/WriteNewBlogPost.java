package com.gelecegiyazanlar.tarifsepeti.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.listeners.FireListener;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.gelecegiyazanlar.tarifsepeti.utils.FireUtils;
import com.gelecegiyazanlar.tarifsepeti.utils.ImageFileUtils;
import com.gelecegiyazanlar.tarifsepeti.utils.MyFileUtils;
import com.gelecegiyazanlar.tarifsepeti.utils.PermissionUtils;
import com.gelecegiyazanlar.tarifsepeti.utils.UCropUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

public class WriteNewBlogPost extends AppCompatActivity implements FireListener{

    private String TAG = "WriteNewBlogPost";
    private Context context;
    private EditText editTextTitle;
    private EditText editTextBody;
    private ImageView postImage;
    private int PICK_IMAGE_REQUEST_FROM_DEVICE = 1001;
    private int PICK_IMAGE_REQUEST_FROM_FIRE = 1002;
    private Uri postImageUri = null;
    private String imgLinkFromFire;
    private String imgNameFromFire;
    private Menu popupMenu;
    private AutoCompleteTextView autoCompleteTextView;
    private enum ImgFrom {EMPTY, DEVICE, FIREDB, EDITED}
    private ImgFrom imgSource = ImgFrom.EMPTY;
    private Activity activity;
    private ProgressBar toolbarProgressBar;
    private enum MenuState{HIDE, SHOW};
    private MenuState menuState = MenuState.SHOW;
    private Button tarifEkleButton;
    private String category = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_blog_post);

        context = this;
        activity = this;

        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);

        toolbarProgressBar = (ProgressBar) findViewById(R.id.toolbarProgreesBar);

        getSupportActionBar().setTitle(R.string.write_new_blog_post);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextBody = (EditText) findViewById(R.id.editTextBody);
        postImage = (ImageView) findViewById(R.id.postImage);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        List<String> categoriesList = FireUtils.getCategoriesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

        //read write izin yoksa iste
        if(!PermissionUtils.checkRecordReadWriteExternalPermission(activity))PermissionUtils.requestRecordAudioPermission(WriteNewBlogPost.this);

        tarifEkleButton = (Button)findViewById(R.id.tarifEkleButton);
        tarifEkleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(PermissionUtils.checkRecordReadWriteExternalPermission(activity))sendBlogPostToFire(postImageUri);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.write_new_blogpost_menu, menu);
        popupMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.option_1:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_FROM_DEVICE);

                break;
            case R.id.option_2:

                Intent i = new Intent(this, FireGalleryActivity.class);
                startActivityForResult(i, PICK_IMAGE_REQUEST_FROM_FIRE);

                break;
            case R.id.option_3:

                Uri sourceUri;

                if (imgSource == ImgFrom.DEVICE) {

                    sourceUri = postImageUri;
                    UCropUtils.openUCropWithUri(activity, sourceUri);

                } else if (imgSource == ImgFrom.FIREDB) {

                    FireUtils.downloadImageAndOpenUCrop(activity, imgNameFromFire);

                }

                break;
            case R.id.option_4:

                if(PermissionUtils.checkRecordReadWriteExternalPermission(activity))sendBlogPostToFire(postImageUri);

                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //------------------------------------------------//
        //gets image from gallery and sets the image view //
        //------------------------------------------------//
        if (requestCode == PICK_IMAGE_REQUEST_FROM_DEVICE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imageUri = data.getData();
            Glide
                    .with(WriteNewBlogPost.this)
                    .load(imageUri)
                    .centerCrop()
                    .into(postImage);

            postImageUri = imageUri;
            imgSource = ImgFrom.DEVICE;
            Log.i(TAG, "picked gallery imageUri: " + imageUri);

        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            Uri resultUri = UCrop.getOutput(data);
            postImageUri = resultUri;
            imgSource = ImgFrom.EDITED;
            Log.i(TAG, "uCrop resultUri: " + resultUri);

            Glide
                    .with(WriteNewBlogPost.this)
                    .load(postImageUri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postImage);

        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(data);
            Log.i(TAG, "cropError: " + cropError);

        }

        //-------------------------------------------------//
        //gets image from firebase and sets the image view //
        //-------------------------------------------------//
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_FROM_FIRE) {

            imgNameFromFire = data.getStringExtra("imgName");
            imgLinkFromFire = data.getStringExtra("imgLink");

            if (imgNameFromFire != null) {

                Log.i(TAG, "imgName : " + imgNameFromFire);
                Log.i(TAG, "imgLinkFromFire : " + imgLinkFromFire);

                Glide
                        .with(WriteNewBlogPost.this)
                        .load(imgLinkFromFire)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(postImage);

                imgSource = ImgFrom.FIREDB;

            }

        }

        if (imgSource != ImgFrom.EMPTY) {

            popupMenu.getItem(2).setEnabled(true);
            popupMenu.getItem(3).setEnabled(true);

        }

        Log.i(TAG, "onActivityResult");

    }

    /**
     * Uploads the picked image to firebase
     */
    private void sendBlogPostToFire(Uri postImageUri) {

        final String title = editTextTitle.getText().toString().trim();
        final String body = editTextBody.getText().toString().trim();
        category=autoCompleteTextView.getText().toString().trim();

        if(!allTextUtilsIsEmpty(title, body, category)) {

            showProgressBar(true);

            final File imageFile;

            if (imgSource == ImgFrom.EDITED) {

                imageFile = new File(postImageUri.getEncodedPath());
                FireUtils.uploadImageAndPushAll(context, imageFile, title, body, category);

            } else if (imgSource == ImgFrom.DEVICE) {

                imageFile = new File(ImageFileUtils.getImagePath(WriteNewBlogPost.this, postImageUri));

                FireUtils.uploadImageAndPushAll(context, imageFile, title, body, category);

            } else if (imgSource == ImgFrom.FIREDB) {

                FireUtils.pushAll(context, new RECIPE(title, body, imgLinkFromFire, category));

            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MyFileUtils.trimCache(WriteNewBlogPost.this);

    }

    private Boolean allTextUtilsIsEmpty(String title, String body, String category){

        Boolean rtrn = false;

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(body) || TextUtils.isEmpty(category)){

            Toast.makeText(context, "Lütfen boş alanları doldurun!", Toast.LENGTH_SHORT).show();

            rtrn = true;

        }

        return rtrn;
    }

    public void clear() {

        popupMenu.getItem(2).setEnabled(false);
        popupMenu.getItem(3).setEnabled(false);
        postImage.setImageResource(android.R.color.transparent);
        editTextTitle.setText("");
        editTextBody.setText("");
        autoCompleteTextView.setText("");

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
            clear();
            finish();

        }

    }

    @Override
    public void listenShowProgressBar(Boolean b) {

        showProgressBar(b);

    }

}