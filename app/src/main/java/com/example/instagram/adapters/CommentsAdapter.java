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
import com.example.instagram.models.Comment;
import com.parse.ParseException;
import com.parse.ParseFile;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final String TAG = "CommentsAdapter";
    private final Context context;
    private final List<Comment> comments;

    /* Constructor takes the context and the list of comments in the recycler view. */
    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    /* Creates a view holder for the comment. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(context)));
    }

    /* Binds the comment at the passed in position to the view holder. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    /* Returns the number of comments in the list. */
    @Override
    public int getItemCount() {
        return comments.size();
    }

    /* Defines the view holder for a comment. */
    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCommentBinding binding;

        /* Constructor takes in a binding for the comment's view and sets its onClick listener. */
        public ViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // If user clicks the comment's username or profile photo, navigate to the commenter's details.
            View.OnClickListener listener = v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", comments.get(position).getUser());
                    context.startActivity(intent);
                }
            };
            binding.ivProfileImage.setOnClickListener(listener);
            binding.tvUsername.setOnClickListener(listener);
        }

        /* Binds the comment's data to the view's components. */
        public void bind(Comment comment) {
            binding.tvText.setText(comment.getText());
            try {
                String username = comment.getUser().fetchIfNeeded().getString("username");
                binding.tvUsername.setText(username);
                binding.tvTimestamp.setText(Utils.calculateTimeAgo(comment.getCreatedAt()));
                ParseFile photo = comment.getUser().fetchIfNeeded().getParseFile("photo");
                if (photo != null) {
                    Glide.with(context).load(photo.getUrl()).circleCrop().into(binding.ivProfileImage);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Parse exception", e);
            }
        }
    }
}