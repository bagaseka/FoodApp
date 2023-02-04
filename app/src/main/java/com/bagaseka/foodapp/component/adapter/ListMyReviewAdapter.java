package com.bagaseka.foodapp.component.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.ReviewItem;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListMyReviewAdapter extends RecyclerView.Adapter<ListMyReviewAdapter.FoodViewHolder>{

    private final ArrayList<ReviewItem> models = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ReviewItem> models) {
        this.models.clear();
        this.models.addAll(models);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListMyReviewAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListMyReviewAdapter.FoodViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_myreview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListMyReviewAdapter.FoodViewHolder holder, int position) {
        ReviewItem model = models.get(position);

        holder.setNameFood(model.getName());
        holder.setDate(model.getDate());
        holder.setRate(Float.parseFloat(model.getRating()));
        holder.setReview(model.getReview());

        if (model.getImage() != null) {
            holder.setImageFood(model.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    protected class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameFood, reviewDate, reviewComment;
        ImageView imageFood;
        RatingBar ratingBar;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            imageFood = itemView.findViewById(R.id.imageFood);
            reviewDate = itemView.findViewById(R.id.date);
            reviewComment = itemView.findViewById(R.id.review);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void setNameFood(String name) {
            nameFood.setText(name);
        }

        public void setRate(Float rate) {
            ratingBar.setRating(rate);
        }

        public void setDate(String date) {
            reviewDate.setText(date);
        }

        public void setReview(String review) {

            if (review.equals("-")){
                reviewComment.setText("No Review");
            }else{
                reviewComment.setText(review);
            }
        }

        public void setImageFood(String image) {
            Glide.with(itemView.getContext())
                    .load(image)
                    .into(imageFood);
        }
    }
}
