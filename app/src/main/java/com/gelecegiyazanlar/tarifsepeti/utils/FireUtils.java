package com.gelecegiyazanlar.tarifsepeti.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.listeners.FireListener;
import com.gelecegiyazanlar.tarifsepeti.models.RECIPE;
import com.gelecegiyazanlar.tarifsepeti.models.Category;
import com.gelecegiyazanlar.tarifsepeti.models.MyImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serdar on 05.08.2016
 */
public class FireUtils {

    //Firebase Database json names
    public static final String TAG = "FireUtils";
    public static final String RECIPES = "recipes";
    public static final String STARS = "stars";
    public static final String IMAGES = "images";
    public static final String CATEGORIES = "categories";
    public static final String USERS = "users";

    //Firebase Storage url and file names
    public static final String APP_STORAGE_BUCKED_URL = "gs://tarif-sepeti-e4ed7.appspot.com";
    public static final String IMAGES_FILE = "images";

    public static String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getDbRef(){

        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getImagesDbRef(){

        return FirebaseDatabase.getInstance().getReference().child(IMAGES).child(getUid());
    }

    public static DatabaseReference getBlogPostsDbRef(){

        return FirebaseDatabase.getInstance().getReference().child(RECIPES);
    }

    public static DatabaseReference getUsersDbRef(){

        return FirebaseDatabase.getInstance().getReference().child(USERS);
    }

    public static DatabaseReference getCurrentUserDbRef(){

        return FirebaseDatabase.getInstance().getReference().child(USERS).child(getUid());
    }

    public static DatabaseReference getCategoriesDbRef() {

        return FirebaseDatabase.getInstance().getReference().child(CATEGORIES);
    }

    public static StorageReference getStorageRef(){

        return FirebaseStorage.getInstance().getReferenceFromUrl(APP_STORAGE_BUCKED_URL);
    }

    public static StorageReference getImagesFolderRef(){

        return FirebaseStorage.getInstance().getReferenceFromUrl(APP_STORAGE_BUCKED_URL).child(IMAGES).child(getUid());
    }

    public static StorageReference getImageFileRef(String imageName){

        return FirebaseStorage.getInstance().getReferenceFromUrl(APP_STORAGE_BUCKED_URL).child(IMAGES).child(imageName);
    }

    public static void pushAll(final Context context, RECIPE RECIPE) {

        setCategory(RECIPE.getCategory());

        getBlogPostsDbRef().push().setValue(RECIPE).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, "yemek tarifinizi başarıyla eklediniz", Toast.LENGTH_SHORT).show();

                FireListener fireListener = (FireListener) context;
                if(fireListener!= null)
                    fireListener.listenShowProgressBar(false);

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                FireListener fireListener = (FireListener) context;
                if(fireListener!= null)
                    fireListener.listenShowProgressBar(false);

                Toast.makeText(context, "RECIPE eklenemedi", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public static void setMyImageValue(MyImage imageToRealtimeDb) {

        String filePath = MyFileUtils.getFileName(imageToRealtimeDb.getImgName()).toLowerCase();

        Log.i(TAG, "filePath :" + filePath);

        getImagesDbRef().child(filePath).setValue(imageToRealtimeDb).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                Log.i(TAG, "setMyImageValue success");

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i(TAG, "setMyImageValue unsuccess");

            }

        });

    }

    /** Sends RECIPE to firebase database*/
    public static void pushBlogPostAndMyImage(Context context, MyImage imageToRealtimeDb, String title, String body, String category) {

        setMyImageValue(imageToRealtimeDb);

        pushAll(context, new RECIPE(title, body, imageToRealtimeDb.getImgLink(), category));

    }

    public static void uploadImageAndPushAll(final Context context, final File imageFile, final String title, final String body, final String category) {

        setCategory(category);
        InputStream stream = null;

        try {

            stream = new FileInputStream(imageFile);
            UploadTask uploadTask = FireUtils.getImageFileRef(imageFile.getName()).putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.i(TAG, "image could not uploaded");

                    FireListener fireListener = (FireListener) context;
                    if(fireListener!= null)
                        fireListener.listenShowProgressBar(false);

                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.i(TAG, "image uploaded");


                    Uri storedImageUrl = taskSnapshot.getDownloadUrl();
                    if (storedImageUrl != null) {

                        pushBlogPostAndMyImage(context, new MyImage(imageFile.getName(), storedImageUrl.toString()), title, body, category);

                    }

                    FireListener fireListener = (FireListener) context;
                    if(fireListener!= null){

                        Log.i(TAG, "fire is not null");

                        fireListener.listenShowProgressBar(false);

                    }else{

                        Log.i(TAG, "fire is null");


                    }

                }

            });

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

    }

    private static void setCategory(String category) {

        Category blogCategory = new Category(category);
        getCategoriesDbRef().child(category).setValue(blogCategory);

    }

    public static List<String> getCategoriesList() {

        final List<String> categoriesList = new ArrayList<>();

        FireUtils.getCategoriesDbRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    try {

                        Category category = data.getValue(Category.class);
                        categoriesList.add(category.getName());

                    }catch (DatabaseException d){

                        d.printStackTrace();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return categoriesList;
    }

    public static void downloadImageAndOpenUCrop(final Activity activity, String imageName) {

        try {

            final File localImageFile = File.createTempFile("downloaded_images", "png");
            FireUtils.getImageFileRef(imageName).getFile(localImageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    UCropUtils.openUCropWithUri(activity, Uri.fromFile(localImageFile));

                    FireListener fireListener = (FireListener) activity;
                    if(fireListener!= null)
                        fireListener.listenShowProgressBar(false);

                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {

                    FireListener fireListener = (FireListener) activity;
                    if(fireListener!= null)
                        fireListener.listenShowProgressBar(false);

                }

            });

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
