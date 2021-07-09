package com.example.instagram.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Utils;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    private final String TAG = "PostDetailActivity";
    private ActivityPostDetailBinding binding;
    private Post post; // the post whose details are shown
    private int position; // position of the post in the recycler view
    private List<Comment> allComments; // comments of the post
    private CommentsAdapter adapter; // adapter for the list of comments
    private Like likeByCurrentUser; // the like registered by the current user on the post, null if none

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show the post's basic information
        post = getIntent().getParcelableExtra("post");
        position = getIntent().getIntExtra("position", -1);
        binding.tvUsername.setText(post.getUser().getUsername());
        binding.tvDescription.setText(post.getDescription());
        binding.tvCreatedAt.setText(Utils.calculateTimeAgo(post.getCreatedAt()));

        // Show images
        Glide.with(this).load(post.getImage().getUrl()).into(binding.ivPostImage);
        ParseFile profileImage = post.getUser().getParseFile("photo");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(binding.ivProfileImage);
        }

        // If user clicks on the profile photo or username, navigate to the profile of the post's user
        View.OnClickListener listener = v -> {
            Intent intent = new Intent(PostDetailActivity.this, UserDetailActivity.class);
            intent.putExtra("user", post.getUser());
            startActivity(intent);
        };
        binding.ivProfileImage.setOnClickListener(listener);
        binding.tvUsername.setOnClickListener(listener);

        // Set up info and interaction for the post's likes and comments
        setupLikes();
        setupComments();
    }

    /* Everything related to the post's likes. */
    public void setupLikes() {
        // Show the number of likes
        binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes()));

        // Check if the post is liked by the current user
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class); // specify type of data
        query.whereEqualTo(Like.KEY_POST, post); // limit to the current post
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser()); // limit to the current user
        query.getFirstInBackground((like, e) -> { // start async query for likes
            // Fill in the image button according to the like status
            likeByCurrentUser = like; // store the user's like in a variable for future access
            if (e != null || like == null) {
                binding.btnLike.setImageResource(R.drawable.ufi_heart);
            } else {
                binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
            }
        });
    }

    /* When the user clicks on the like button, like or unlike the post. */
    public void btnLikeOnClick(View view) {
        // Like
        if (likeByCurrentUser == null) {
            binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
            Like like = new Like(ParseUser.getCurrentUser(), post);
            like.saveInBackground(e -> {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Error while saving like", e);
                    return;
                }

                // Like was successfully saved
                binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes() + 1));
                likeByCurrentUser = like;
                post.setNumLikes(post.getNumLikes() + 1); // increment post's number of likes
                post.saveInBackground();
            });
        }

        // Unlike
        else {
            binding.btnLike.setImageResource(R.drawable.ufi_heart);
            try {
                likeByCurrentUser.delete(); // delete the row from the database
                likeByCurrentUser = null;
                binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes() - 1));
                post.setNumLikes(post.getNumLikes() - 1); // decrement post's number of likes
                post.saveInBackground();
            } catch (ParseException e) {
                Log.e(TAG, "Parse exception", e);
            }
        }
    }

    /* Everything related to the post's comments. */
    public void setupComments() {
        // Set up adapter and layout of recycler view
        allComments = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new CommentsAdapter(this, allComments);
        binding.rvComments.setLayoutManager(layoutManager);
        binding.rvComments.setAdapter(adapter);
        queryComments(0);

        // Endless scrolling
        binding.rvComments.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryComments(allComments.size());
            }
        });
    }

    /* Queries the current post's comments 20 at a time. Skips the first skip items. */
    public void queryComments(int skip) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class); // specify type of data
        query.include(Comment.KEY_POST); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Comment.KEY_POST, post); // limit to the current post
        query.addDescendingOrder("createdAt"); // order comments by creation date
        query.findInBackground((comments, e) -> { // start async query for comments
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting comments", e);
                return;
            }

            // Save received comments to list and notify adapter of new data
            allComments.addAll(comments);
            adapter.notifyDataSetChanged();
        });
    }

    /* When the user clicks on the comment button, open a dialog to add a comment. */
    public void btnCommentOnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder.setTitle("New comment");

        // Set up the input
        final EditText input = new EditText(PostDetailActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // If the post button is clicked, create a new comment and add to the database
        builder.setPositiveButton("Post", (dialog, which) -> {
            String text = input.getText().toString();
            Comment comment = new Comment(ParseUser.getCurrentUser(), post, text);
            comment.saveInBackground(e -> {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    return;
                }

                // Comment was successfully saved
                allComments.add(0, comment);
                adapter.notifyItemInserted(0);
                dialog.cancel();
            });
        });

        // If the cancel button is clicked, return to the activity
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /* Passes the post back to the activity it came from. */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("post", post);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        System.out.println("ON BACK PRESSED");
        finish();
    }
}