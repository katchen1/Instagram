package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.example.instagram.models.Post;
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
    }
}