package com.example.instagram.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.models.Post;
import com.example.instagram.adapters.PostsAdapter;
import com.parse.ParseQuery;

public class FeedFragment extends PostsFragment {

    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void setLayoutManager() {
        // Set up adapter and layout of recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new PostsAdapter(getActivity(), allPosts, 0);
        binding.rvPosts.setLayoutManager(layoutManager);

        // Endless scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);
    }

    @Override
    public void queryPosts(int skip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify type of data
        query.include(Post.KEY_USER); // include data referred by user key
        query.setSkip(skip); // skip the first skip items
        query.setLimit(20); // limit query to 20 items
        query.addDescendingOrder("createdAt"); // order posts by creation date
        query.findInBackground((posts, e) -> { // start async query for posts
            // Check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            // Save received posts to list and notify adapter of new data
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}