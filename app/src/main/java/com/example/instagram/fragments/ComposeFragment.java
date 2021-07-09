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
import com.example.instagram.databinding.FragmentComposeBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import java.io.File;

public class ComposeFragment extends Fragment {

    FragmentComposeBinding binding;
    public static final String TAG = "CaptureFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if (activity != null) {

            binding.btnCaptureImage.setOnClickListener(v -> launchCamera());

            binding.btnSubmit.setOnClickListener(v -> {
                String description = binding.etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(activity, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || binding.ivPostImage.getDrawable() == null) {
                    Toast.makeText(activity, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            });
        }
    }

    /* Launches the camera. */
    private void launchCamera() {
        // Create an intent to take picture and a file reference for future access
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(activity, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        /* If you call startActivityForResult() using an intent that no app can handle, your app
         * will crash. So as long as the result is not null, it's safe to use the intent. */
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                binding.ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Returns the File for a photo stored on disk given the fileName. */
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    /* Saves a post to the parse database. */
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(e -> {
            // Check for errors
            if (e != null) {
                Toast.makeText(activity, "Error while saving!", Toast.LENGTH_SHORT).show();
            }

            // Post was successfully saved
            Toast.makeText(activity, "Post saved!", Toast.LENGTH_SHORT).show();
            binding.etDescription.setText("");
            binding.ivPostImage.setImageResource(0);
        });
    }
}