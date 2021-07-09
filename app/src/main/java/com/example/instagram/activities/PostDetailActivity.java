package com.example.instagram.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.adapters.PostsAdapter;
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

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    Post post;
    List<Comment> comments;
    protected CommentsAdapter adapter;
    ActivityPostDetailBinding binding;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = getIntent().getParcelableExtra("post");

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivPostImage);
        }
        binding.tvDescription.setText(post.getDescription());
        binding.tvCreatedAt.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        binding.tvUsername.setText(post.getUser().getUsername());
        Glide.with(this).load(post.getUser().getParseFile("photo").getUrl()).circleCrop().into(binding.ivProfileImage);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, UserDetailActivity.class);
                intent.putExtra("user", post.getUser());
                startActivity(intent);
            }
        };
        binding.ivProfileImage.setOnClickListener(listener);
        binding.tvUsername.setOnClickListener(listener);

        // Display like info
        binding.tvLikeInfo.setText(post.getNumLikes() + " likes");
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class); // specify type of data
        query.setLimit(1); // limit query to 20 items
        query.whereEqualTo(Like.KEY_POST, post); // limit comments to current post's
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground((likes, e) -> { // start async query for posts
            // Check for errors
            if (e != null) {
                Log.e("ERROR", "Issue with getting posts", e);
                return;
            }
            if (likes.size() >= 1) binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
            else binding.btnLike.setImageResource(R.drawable.ufi_heart);
        });

        // Handling comments
        comments = new ArrayList<>();
        setLayoutManager();
        binding.rvComments.setAdapter(adapter);
        queryComments(0);

        // Commenting on the post
        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                builder.setTitle("Add comment");

                // Set up the input
                final EditText input = new EditText(PostDetailActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        Comment comment = new Comment();
                        comment.setText(text);
                        comment.setUser(ParseUser.getCurrentUser());
                        comment.setPost(post);
                        comment.saveInBackground(e -> {
                            // Check for errors
                            if (e != null) {
                                e.printStackTrace();
                                Toast.makeText(PostDetailActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                            }

                            // Post was successfully saved
                            Toast.makeText(PostDetailActivity.this, "Comment saved!", Toast.LENGTH_SHORT).show();
                            comments.add(0, comment);
                            adapter.notifyItemInserted(0);
                            dialog.cancel();
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // Liking the post
        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<Like> query = ParseQuery.getQuery(Like.class); // specify type of data
                query.setLimit(1); // limit query to 20 items
                query.whereEqualTo(Like.KEY_POST, post); // limit comments to current post's
                query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
                query.findInBackground((likes, e) -> { // start async query for posts
                    // Check for errors
                    if (e != null) {
                        Log.e("ERROR", "Issue with getting posts", e);
                        return;
                    }

                    // User hasn't liked the post
                    if (likes.isEmpty()) {
                        binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
                        Like like = new Like();
                        like.setPost(post);
                        like.setUser(ParseUser.getCurrentUser());
                        like.saveInBackground(e1 -> {
                            // Check for errors
                            if (e1 != null) {
                                e1.printStackTrace();
                                Toast.makeText(PostDetailActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                            }

                            // Post was successfully saved
                            post.setNumLikes(post.getNumLikes() + 1);
                            post.saveInBackground(e2 -> {
                                // Check for errors
                                if (e2 != null) {
                                    e2.printStackTrace();
                                    Toast.makeText(PostDetailActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Post was successfully saved
                                Toast.makeText(PostDetailActivity.this, "Post saved!", Toast.LENGTH_SHORT).show();
                                binding.tvLikeInfo.setText(post.getNumLikes() + " likes");
                            });
                            Toast.makeText(PostDetailActivity.this, "Like saved!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    // User has already liked the post
                    else {
                        binding.btnLike.setImageResource(R.drawable.ufi_heart);
                        try {
                            likes.get(0).delete();
                            Toast.makeText(PostDetailActivity.this, "Deleted like!", Toast.LENGTH_SHORT).show();
                            post.setNumLikes(post.getNumLikes() - 1);
                            post.saveInBackground(e1 -> {
                                // Check for errors
                                if (e1 != null) {
                                    e1.printStackTrace();
                                    Toast.makeText(PostDetailActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Post was successfully saved
                                Toast.makeText(PostDetailActivity.this, "Post saved!", Toast.LENGTH_SHORT).show();
                                binding.tvLikeInfo.setText(post.getNumLikes() + " likes");
                            });
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    protected void setLayoutManager() {
        // Set up adapter and layout of recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new CommentsAdapter(this, comments);
        binding.rvComments.setLayoutManager(layoutManager);

        // Endless scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryComments(comments.size());
            }
        };
        binding.rvComments.addOnScrollListener(scrollListener);
    }

    public void queryComments(int skip) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class); // specify type of data
        query.include(Comment.KEY_POST); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.whereEqualTo(Comment.KEY_POST, post); // limit comments to current post's
        query.addDescendingOrder("createdAt"); // order posts by creation date
        query.findInBackground((cmts, e) -> { // start async query for posts
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            // Save received posts to list and notify adapter of new data
            comments.addAll(cmts);
            adapter.notifyDataSetChanged();
        });
    }
}