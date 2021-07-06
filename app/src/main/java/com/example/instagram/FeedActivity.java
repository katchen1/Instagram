package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.ActivityFeedBinding;
import com.example.instagram.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedBinding binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts);

    }
}