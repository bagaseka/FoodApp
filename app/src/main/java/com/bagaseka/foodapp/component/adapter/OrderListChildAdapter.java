package com.bagaseka.foodapp.component.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.ChildItem;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.ReviewDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListChildAdapter extends RecyclerView.Adapter<OrderListChildAdapter.ChildViewHolder> implements ReviewDialog.OnSubmitListener {

    private List<ChildItem> childItemList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setChildItemList(List<ChildItem> childItemList) {
        this.childItemList = childItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_order_completed, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);

        if (childItem.isProgress()){
            if (childItem.isReviewStatus()){
                holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.Green));
                holder.statusReviewTV.setText("Completed");
                holder.reviewBtn.setVisibility(View.GONE);
            }else{
                holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.colorPrimary));
                holder.statusReviewTV.setText("Completed");
                holder.reviewBtn.setVisibility(View.VISIBLE);
                holder.reviewBtn.setText("Review our Menu");
            }
            holder.setNameFood(childItem.getNama());
            holder.setFoodID(childItem.getFoodID());
            holder.setFoodPrice(childItem.getHarga());
            holder.setFoodCount(childItem.getCounter());
            holder.setImageFood(childItem.getImage());
            holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialogFragment bottomSheetDialogFragment =
                            new ReviewDialog(
                                    childItem.getFoodID(),
                                    childItem.getNama(),
                                    childItem.getCounter(),
                                    childItem.getHarga(),
                                    childItem.getImage(),
                                    childItem.getOrderId()
                            );

                    ((ReviewDialog) bottomSheetDialogFragment).updateList(OrderListChildAdapter.this);

                    bottomSheetDialogFragment.show(
                            ((FragmentActivity)v.getContext())
                                    .getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                }
            });
        }else{

            holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.colorPrimary));
            holder.statusReviewTV.setText("Ongoing");
            holder.reviewBtn.setVisibility(View.GONE);

            holder.setNameFood(childItem.getNama());
            holder.setFoodID(childItem.getFoodID());
            holder.setFoodPrice(childItem.getHarga());
            holder.setFoodCount(childItem.getCounter());
            holder.setImageFood(childItem.getImage());

        }
    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onclick() {
        notifyDataSetChanged();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView nameFood, foodCount, foodPrice, statusReviewTV;
        String idFood;
        ImageView imageFood;
        Button reviewBtn;
        RelativeLayout statusReviewLayout;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            foodCount = itemView.findViewById(R.id.itemCount);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            imageFood = itemView.findViewById(R.id.imageFood);
            reviewBtn = itemView.findViewById(R.id.btnReview);

            statusReviewLayout = itemView.findViewById(R.id.statusReviewLayout);
            statusReviewTV = itemView.findViewById(R.id.statusReviewTV);
        }

        public void setNameFood(String name) {
            nameFood.setText(name);
        }
        public void setFoodCount(int counter) {
            foodCount.setText(String.valueOf(counter) + " items");
        }
        public void setFoodID(String id) {
            idFood = id;
        }
        public void setFoodPrice(int price){foodPrice.setText("IDR "+String.valueOf(price));}
        public void setImageFood(String image){
            Glide.with(itemView.getContext())
                    .load(image)
                    .centerCrop()
                    .into(imageFood);
        }
    }
}
