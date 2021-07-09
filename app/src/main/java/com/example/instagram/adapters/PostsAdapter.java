package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ItemPostImageBinding;
import com.example.instagram.models.Post;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
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