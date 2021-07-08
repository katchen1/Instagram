package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentFeedBinding;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public abstract class PostsFragment extends Fragment {

    public static final String TAG = "FeedActivity";
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    FragmentFeedBinding binding;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        setLayoutManager();
        binding.rvPosts.setAdapter(adapter);
        queryPosts(0);

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            allPosts.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });

        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    protected void setLayoutManager() {
        return;
    }
    protected void queryPosts(int skip) {
        return;
    }
}