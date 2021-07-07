package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.parse.ParseFile;

public class PostDetailActivity extends AppCompatActivity {

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPostDetailBinding binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = getIntent().getParcelableExtra("post");

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivPostImage);
        }
        binding.tvDescription.setText(post.getDescription());
        binding.tvCreatedAt.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        binding.tvUsername.setText(post.getUser().getUsername());
    }
}