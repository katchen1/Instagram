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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Utils;
import com.example.instagram.activities.LoginActivity;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ParseUser user; // the current user
    private List<Post> allPosts; // posts shown in the recycler view
    private PostsAdapter adapter; // adapter for the posts' recycler view
    private File photoFile; // file for the user's uploaded profile picture
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();

        // Set up adapter and layout of recycler view
        allPosts = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        adapter = new PostsAdapter(getContext(), allPosts, 1, this);
        binding.rvPosts.setLayoutManager(layoutManager);
        binding.rvPosts.setAdapter(adapter);
        queryPosts(0);

        // Endless scrolling
        binding.rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        });

        // Set up  the user's basic info
        binding.ivProfilePhoto.setOnClickListener(v -> launchCamera());
        ParseFile photo = user.getParseFile("photo");
        if (photo != null) {
            Glide.with(this).load(photo.getUrl()).circleCrop().into(binding.ivProfilePhoto);
        }
        binding.tvUsernameToolbar.setText(user.getUsername());
        binding.tvName.setText(user.getString("name"));
        binding.tvBio.setText(user.getString("bio"));
        binding.tvPostCount.setText(String.format(Locale.US, "%d", user.getInt("postCount")));
        binding.tvFollowerCount.setText(String.format(Locale.US, "%d", user.getInt("followerCount")));
        binding.tvFollowingCount.setText(String.format(Locale.US, "%d", user.getInt("followingCount")));
        binding.btnLogout.setOnClickListener(this::logoutOnClick);

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            allPosts.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
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

    /* After returning from the camera, set the profile photo to be the image taken. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (photoFile == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the image view and save the new profile photo to the Parse database
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivProfilePhoto.setImageBitmap(takenImage);
            user.put("photo", new ParseFile(photoFile));
            user.saveInBackground();
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

    /* Queries the posts 20 at a time. Skips the first skip items. */
    public void queryPosts(int skip) {
        binding.pbLoading.setVisibility(View.VISIBLE); // show the intermediate progress bar
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify type of data
        query.include(Post.KEY_USER); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()); // limit posts to current user's
        query.addDescendingOrder("createdAt"); // order posts by creation date
        query.findInBackground((posts, e) -> { // start async query for posts
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                binding.pbLoading.setVisibility(View.INVISIBLE); // hide the intermediate progress bar
                return;
            }

            // Save received posts to list and notify adapter of new data
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
            binding.pbLoading.setVisibility(View.INVISIBLE); // hide the intermediate progress bar
        });
    }

    /* When the user clicks log out button, log out and return to the login page. */
    public void logoutOnClick(View v) {
        ParseUser.logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
