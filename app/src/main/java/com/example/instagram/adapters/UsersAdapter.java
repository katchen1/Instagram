package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.Utils;
import com.example.instagram.activities.UserDetailActivity;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.databinding.ItemUserBinding;
import com.example.instagram.models.Comment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final String TAG = "UsersAdapter";
    private final Context context;
    private final List<ParseUser> users;

    /* Constructor takes the context and the list of comments in the recycler view. */
    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    /* Creates a view holder for the comment. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(context)));
    }

    /* Binds the comment at the passed in position to the view holder. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    /* Returns the number of comments in the list. */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /* Defines the view holder for a comment. */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemUserBinding binding;

        /* Constructor takes in a binding for the comment's view and sets its onClick listener. */
        public ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // If user clicks the comment's username or profile photo, navigate to the commenter's details.
            binding.getRoot().setOnClickListener(this);
        }

        /* Binds the comment's data to the view's components. */
        public void bind(ParseUser user) {
            try {
                String username = user.fetchIfNeeded().getString("username");
                binding.tvUsername.setText(username);
                ParseFile photo = user.fetchIfNeeded().getParseFile("photo");
                if (photo != null) {
                    Glide.with(context).load(photo.getUrl()).circleCrop().into(binding.ivProfileImage);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Parse exception", e);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("user", users.get(position));
                context.startActivity(intent);
            }
        }
    }
}