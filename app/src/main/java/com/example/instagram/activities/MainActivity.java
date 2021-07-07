package com.example.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.fragments.CaptureFragment;
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.Post;
import com.example.instagram.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new FeedFragment();
        final Fragment fragment2 = new CaptureFragment();
        final Fragment fragment3 = new ProfileFragment();

        // handle navigation selection
        binding.bottomNavigation.setOnNavigationItemSelectedListener(
                item -> {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.action_feed:
                            fragment = fragment1;
                            break;
                        case R.id.action_capture:
                            fragment = fragment2;
                            break;
                        case R.id.action_profile:
                        default:
                            fragment = fragment3;
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                    return true;
                });
        // Set default selection
        binding.bottomNavigation.setSelectedItemId(R.id.action_feed);


//        setSupportActionBar(binding.toolbar);
//        if (getSupportActionBar() != null) getSupportActionBar().setTitle("");
//
//        binding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCamera();
//            }
//        });
//
//        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String description = binding.etDescription.getText().toString();
//                if (description.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT);
//                    return;
//                }
//                if (photoFile == null || binding.ivPostImage.getDrawable() == null) {
//                    Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ParseUser currentUser = ParseUser.getCurrentUser();
//                savePost(description, currentUser, photoFile);
//            }
//        });
    }

//    private void launchCamera() {
//        // create Intent to take a picture and return control to the calling application
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Create a fFile reference to access to future access
//        photoFile = getPhotoFileUri(photoFileName);
//
//        // wrap File object into a content provider
//        // required for API >= 24
//        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
//        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
//
//        // If you call startActivityForRestuos() using an intent that no app can handle, your app will crash.
//        // So as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            // Start the image capture intent to take photo
//            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        }
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // by this point we have the camera photo on disk
//                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                // RESIZE BITMAP, see section below
//                // Load the taken image into a preview
//                binding.ivPostImage.setImageBitmap(takenImage);
//            } else { // Result was a failure
//                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    // Returns the File for a photo stored on disk given the fileName
//    private File getPhotoFileUri(String fileName) {
//        // Get safe storage directory for photos
//        // Use 'getExternalFilesDir' on Context to access package-specific directories.
//        // This way, we don't need to request external read/write runtime permissions.
//        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
//            Log.d(TAG, "failed to create directory");
//        }
//
//        // Return the file target for the photo based on filename
//        return new File(mediaStorageDir.getPath() + File.separator + fileName);
//    }
//
//    private void savePost(String description, ParseUser currentUser, File photoFile) {
//        Post post = new Post();
//        post.setDescription(description);
//        post.setImage(new ParseFile(photoFile));
//        post.setUser(currentUser);
//        post.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Error while saving", e);
//                    Toast.makeText(MainActivity.this, "Error while saving!", Toast.LENGTH_SHORT);
//                }
//                Log.i(TAG, "Post save was successful!");
//                binding.etDescription.setText("");
//                binding.ivPostImage.setImageResource(0);
//            }
//        });
//    }
//
//    public void logoutOnClick(View v) {
//        ParseUser.logOut();
//        Intent i = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(i);
//    }
//
//    public void feedOnClick(View v) {
//        Intent i = new Intent(MainActivity.this, FeedFragment.class);
//        startActivity(i);
//    }

//    private void queryPosts() {
//        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
//        query.include(Post.KEY_USER);
//        query.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> posts, ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Issue with getting posts", e);
//                    return;
//                }
//                for (Post post: posts) {
//                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
//                }
//            }
//        });
//    }
}