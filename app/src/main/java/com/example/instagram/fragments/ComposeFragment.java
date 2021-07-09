package com.example.instagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.instagram.Utils;
import com.example.instagram.databinding.FragmentComposeBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import java.io.File;

public class ComposeFragment extends Fragment {

    private final String TAG = "CaptureFragment";
    private FragmentComposeBinding binding;
    private File photoFile; // the new post's photo

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the button listeners
        binding.btnCaptureImage.setOnClickListener(v -> launchCamera());
        binding.btnSubmit.setOnClickListener(v -> {
            // Check if description is empty
            String description = binding.etDescription.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there's an image
            if (photoFile == null || binding.ivPostImage.getDrawable() == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the post to the database
            savePost(description, ParseUser.getCurrentUser(), photoFile);
        });
    }

    /* Launches the camera. */
    private void launchCamera() {
        // Create an intent to take picture and a file reference for future access
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /* After returning from the camera, load the taken image into a preview. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivPostImage.setImageBitmap(takenImage);
        } else {
            Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    /* Returns the File for a photo stored on disk given the fileName. */
    private File getPhotoFileUri() {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    /* Saves a post to the parse database. */
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        binding.pbLoading.setVisibility(View.VISIBLE); // show the intermediate progress bar
        Post post = new Post(currentUser, description, new ParseFile(photoFile));
        post.saveInBackground(e -> {
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Error while saving post", e);
                return;
            }

            // Post was successfully saved
            Toast.makeText(getContext(), "Post saved", Toast.LENGTH_SHORT).show();
            binding.etDescription.setText("");
            binding.ivPostImage.setImageResource(0);
            ParseUser.getCurrentUser().put("postCount", ParseUser.getCurrentUser().getInt("postCount") + 1);
            ParseUser.getCurrentUser().saveInBackground();
            binding.pbLoading.setVisibility(View.INVISIBLE); // hide the intermediate progress bar
        });
    }
}
