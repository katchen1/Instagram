package com.example.instagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.Utils;
import com.example.instagram.activities.UserDetailActivity;
import com.example.instagram.databinding.ItemPostImageBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final String TAG = "PostsAdapter";
    private final Context context;
    private final List<Post> posts; // the list of posts displayed
    private final int mode; // 0 for feed, 1 for profile
    private final Fragment fragment; // need this for onActivityResult to work

    /* Constructor takes the context and the list of posts in the recycler view. */
    public PostsAdapter(Context context, List<Post> posts, int mode, Fragment fragment) {
        this.context = context;
        this.posts = posts;
        this.mode = mode;
        this.fragment = fragment;
    }

    /* Creates a view holder for the post. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mode == 0) { // feed - item post (linear layout)
            return new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(context)));
        } else { // profile - item post image (grid layout)
            return new ViewHolder(ItemPostImageBinding.inflate(LayoutInflater.from(context)));
        }
    }

    /* Binds the post at the passed in position to the view holder. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    /* Returns the number of posts in the list. */
    @Override
    public int getItemCount() {
        return posts.size();
    }

    /* Defines the view holder for a post. */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPostBinding itemPostBinding;
        private ItemPostImageBinding itemPostImageBinding;

        /* Constructor takes in a binding for the post's view and sets its onClick listener. */
        public ViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.itemPostBinding = binding;
            binding.getRoot().setOnClickListener(this);

            // If user clicks the post's username or profile image, navigate to the post's user details.
            View.OnClickListener listener = v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", posts.get(position).getUser());
                    context.startActivity(intent);
                }
            };
            binding.ivProfileImage.setOnClickListener(listener);
            binding.tvUsername.setOnClickListener(listener);

            // Set the action listeners
            binding.btnComment.setOnClickListener(this::btnCommentClicked);
        }

        /* Alternative constructor for grid layout. */
        public ViewHolder(ItemPostImageBinding binding) {
            super(binding.getRoot());
            this.itemPostImageBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        /* Binds the post's data to the view's components. */
        public void bind(Post post) {
            ImageView ivImage;
            if (mode == 0) { // linear layout
                ivImage = itemPostBinding.ivImage;
                itemPostBinding.tvDescription.setText(post.getDescription());
                itemPostBinding.tvUsername.setText(post.getUser().getUsername());
                itemPostBinding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes()));

                // Fill in the post user's profile image
                ParseFile profileImage = post.getUser().getParseFile("photo");
                if (profileImage != null) {
                    Glide.with(context).load(profileImage.getUrl()).circleCrop().into(itemPostBinding.ivProfileImage);
                }
            } else { // grid layout
                ivImage = itemPostImageBinding.ivImage;
            }

            // Load in the post's image
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
        }

        /* When a post is clicked, show its details in a new activity. */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post", posts.get(position)); // pass in the post
                intent.putExtra("position", position); // pass in the post's position in the list
                if (fragment != null) { // starting activity from a fragment
                    fragment.startActivityForResult(intent, Utils.POST_DETAIL_ACTIVITY_CODE);
                } else { // starting activity from an activity
                    ((Activity) context).startActivityForResult(intent, Utils.POST_DETAIL_ACTIVITY_CODE);
                }
            }
        }

        /* When the comment button is clicked, open a dialog for adding a comment. */
        public void btnCommentClicked(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("New comment");

            // Set up the input
            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // If the post button is clicked, create a new comment and add to the database
            builder.setPositiveButton("Post", (dialog, which) -> {
                String text = input.getText().toString();
                Comment comment = new Comment(ParseUser.getCurrentUser(), posts.get(getAdapterPosition()), text);
                comment.saveInBackground(e -> {
                    // Check for errors
                    if (e != null) {
                        Log.e(TAG, "Error while saving comment", e);
                        return;
                    }

                    // Comment was successfully saved
                    Toast.makeText(input.getContext(), "Comment added", Toast.LENGTH_SHORT).show();
                    dialog.cancel(); // close dialog
                    onClick(v); // navigate to the post detail activity to see the new comment
                });
            });

            // If the cancel button is clicked, return to the activity
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}