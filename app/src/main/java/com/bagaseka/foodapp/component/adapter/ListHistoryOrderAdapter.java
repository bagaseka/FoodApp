package com.bagaseka.foodapp.component.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.ChildItem;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.ReviewDialog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ListHistoryOrderAdapter extends FirestoreRecyclerAdapter<ChildItem, ListHistoryOrderAdapter.FoodViewHolder> {

    public ListHistoryOrderAdapter(@NonNull FirestoreRecyclerOptions<ChildItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull ChildItem model) {
        if (model.isProgress()){
            if (model.isReviewStatus()){
                holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.Green));
                holder.statusReviewTV.setText("Completed");
                holder.reviewBtn.setVisibility(View.GONE);
            }else{
                holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.colorPrimary));
                holder.statusReviewTV.setText("Completed");
                holder.reviewBtn.setVisibility(View.VISIBLE);
                holder.reviewBtn.setText("Review our Menu");
            }
            holder.setNameFood(model.getNama());
            holder.setFoodID(model.getFoodID());
            holder.setFoodPrice(model.getHarga());
            holder.setFoodCount(model.getCounter());
            holder.setImageFood(model.getImage());
            holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialogFragment bottomSheetDialogFragment =
                            new ReviewDialog(
                                    model.getFoodID(),
                                    model.getNama(),
                                    model.getCounter(),
                                    model.getHarga(),
                                    model.getImage(),
                                    model.getOrderId()
                            );
                    bottomSheetDialogFragment.show(
                            ((FragmentActivity)v.getContext())
                                    .getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                }
            });
        }else{

            holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.colorPrimary));
            holder.statusReviewTV.setText("Ongoing");
            holder.reviewBtn.setVisibility(View.GONE);

            holder.setNameFood(model.getNama());
            holder.setFoodID(model.getFoodID());
            holder.setFoodPrice(model.getHarga());
            holder.setFoodCount(model.getCounter());
            holder.setImageFood(model.getImage());

        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_order_completed, parent, false);

        return new FoodViewHolder(view);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView nameFood, foodCount, foodPrice, statusReviewTV;
        String idFood;
        ImageView imageFood;
        Button reviewBtn;
        RelativeLayout statusReviewLayout;

        public FoodViewHolder(@NonNull View itemView) {
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
