package com.bagaseka.foodapp.component.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.ReviewItem;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;

import java.util.ArrayList;
import java.util.List;


public class ListCustomerReviewAdapter extends RecyclerView.Adapter<ListCustomerReviewAdapter.FoodViewHolder> {

    private final ArrayList<ReviewItem> models = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ReviewItem> models) {
        this.models.clear();
        this.models.addAll(models);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        ReviewItem model = models.get(position);

        holder.setNameUser(model.getName());
        holder.setDate(model.getDate());
        holder.setRate(model.getRating());
        holder.setReview(model.getReview());

        if (model.getImage() != null) {
            holder.setImageUser(model.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    protected class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameUser, reviewRate, reviewDate, reviewComment;
        ImageView imageUser;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameUser = itemView.findViewById(R.id.nameUser);
            imageUser = itemView.findViewById(R.id.imageUser);
            reviewRate = itemView.findViewById(R.id.reviewRate);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            reviewComment = itemView.findViewById(R.id.reviewComment);
        }

        public void setNameUser(String name) {
            nameUser.setText(name);
        }

        public void setRate(String name) {
            reviewRate.setText(name);
        }

        public void setDate(String name) {
            reviewDate.setText(name);
        }

        public void setReview(String name) {
            reviewComment.setText(name);
        }

        public void setImageUser(String image) {
            Glide.with(itemView.getContext())
                    .load(image)
                    .circleCrop()
                    .into(imageUser);
        }
    }
}

