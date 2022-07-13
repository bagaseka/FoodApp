package com.bagaseka.foodapp.component.adapter;

import android.annotation.SuppressLint;
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

import com.bagaseka.foodapp.component.ReviewDialog;
import com.bagaseka.foodapp.component.model.ChildItem;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListChildAdapter extends RecyclerView.Adapter<OrderListChildAdapter.ChildViewHolder> {

    private final ArrayList<ChildItem> items = new ArrayList<>();

    public OrderListChildAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitItem(ChildItem item) {
        if (item == null) return;

        boolean shouldAdd = true;
        boolean isUpdate = false;
        int indexUpdate = -1;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).checkId(item)) {
                items.get(i).update(item);
                isUpdate = true;
                shouldAdd = false;
                indexUpdate = i;
                break;
            }
        }

        if (shouldAdd) {
            items.add(item);
            notifyDataSetChanged();
        }

        if (isUpdate) {
            notifyItemChanged(indexUpdate, items.get(indexUpdate));
        }
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_history, parent, false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ChildItem childItem = items.get(position);

        if (childItem.isProgress()) {
            if (childItem.isReviewStatus()) {
                holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.Green));
                holder.statusReviewTV.setText("Completed");
                holder.reviewBtn.setVisibility(View.GONE);
            } else {
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

            holder.setFoodPrice(childItem.getHarga() * childItem.getCounter());
            holder.reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReviewDialog bottomSheetDialogFragment = new ReviewDialog(
                            childItem.getFoodID(),
                            childItem.getNama(),
                            childItem.getCounter(),
                            childItem.getHarga(),
                            childItem.getImage(),
                            childItem.getOrderId()
                    );

                    bottomSheetDialogFragment.show(
                            ((FragmentActivity) v.getContext())
                                    .getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                }
            });
        } else {
            holder.statusReviewLayout.getBackground().setTint(holder.itemView.getResources().getColor(R.color.colorPrimary));
            holder.statusReviewTV.setText("Ongoing");
            holder.reviewBtn.setVisibility(View.GONE);

            holder.setNameFood(childItem.getNama());
            holder.setFoodID(childItem.getFoodID());
            holder.setFoodPrice(childItem.getHarga());
            holder.setFoodCount(childItem.getCounter());
            holder.setImageFood(childItem.getImage());

            holder.setFoodPrice(childItem.getHarga() * childItem.getCounter());
        }
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

        public void setFoodPrice(int price) {
            foodPrice.setText(formatRupiah(Double.parseDouble(String.valueOf(price))));
        }

        private String formatRupiah(Double number){
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            return formatRupiah.format(number);
        }

        public void setImageFood(String image) {
            Glide.with(itemView.getContext())
                    .load(image)
                    .centerCrop()
                    .into(imageFood);
        }
    }
}
