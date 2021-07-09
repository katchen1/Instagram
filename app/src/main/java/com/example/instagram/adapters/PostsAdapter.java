package com.example.instagram.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.activities.UserDetailActivity;
import com.example.instagram.databinding.ItemPostImageBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;
    int mode;

    /* Constructor takes the context and the list of posts in the recycler view. */
    public PostsAdapter(Context context, List<Post> posts, int mode) {
        this.context = context;
        this.posts = posts;
        this.mode = mode;
    }

    /* Creates a view holder for the post. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mode == 0) { // Feed - item post (linear layout)
            return new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(context)));
        } else { // Profile - item post image (grid layout)
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
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, UserDetailActivity.class);
                        intent.putExtra("user", posts.get(position).getUser());
                        context.startActivity(intent);
                    }
                }
            };
            binding.ivProfileImage.setOnClickListener(listener);
            binding.tvUsername.setOnClickListener(listener);

            // Commenting on the post
            binding.btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Add comment");

                    // Set up the input
                    final EditText input = new EditText(context);
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
                            comment.setPost(posts.get(getAdapterPosition()));
                            comment.saveInBackground(e -> {
                                // Check for errors
                                if (e != null) {
                                    e.printStackTrace();
                                    Toast.makeText(input.getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                                }

                                // Post was successfully saved
                                Toast.makeText(input.getContext(), "Comment saved!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                Intent intent = new Intent(context, PostDetailActivity.class);
                                intent.putExtra("post", posts.get(getAdapterPosition()));
                                context.startActivity(intent);
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
        }

        /* Alternative constructor for profile fragment. */
        public ViewHolder(ItemPostImageBinding binding) {
            super(binding.getRoot());
            this.itemPostImageBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        /* Binds the post's data to the view's components. */
        public void bind(Post post) {
            ImageView ivImage;
            if (mode == 0) {
                itemPostBinding.tvDescription.setText(post.getDescription());
                itemPostBinding.tvUsername.setText(post.getUser().getUsername());
                itemPostBinding.tvLikeInfo.setText(post.getNumLikes() + " likes");
                ivImage = itemPostBinding.ivImage;
                Glide.with(context).load(post.getUser().getParseFile("photo").getUrl()).circleCrop().into(itemPostBinding.ivProfileImage);
            } else {
                ivImage = itemPostImageBinding.ivImage;
            }
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
                intent.putExtra("post", posts.get(position));
                context.startActivity(intent);
            }
        }
    }
}