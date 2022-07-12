package com.bagaseka.foodapp.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListCartMenuAdapter extends RecyclerView.Adapter<ListCartMenuAdapter.ViewHolder> {

    private ArrayList<HomeMainList> cartData;
    private View rootView;
    private int finalTotal = 0;
    private int subtotal = 0;
    private TextView textviewTotalExpense;
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getUid();

    public ListCartMenuAdapter(ArrayList<HomeMainList> cartData) {
        this.cartData = cartData;
    }

    @NonNull
    @Override
    public ListCartMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_cart, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        textviewTotalExpense = (TextView) rootView.findViewById(R.id.totalPrice);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCartMenuAdapter.ViewHolder holder, int position) {
        HomeMainList cart = cartData.get(position);
        holder.setNameFood(cart.getNama());
        holder.setPriceFood(cart.getHarga());
        holder.setImageFood(cart.getImage());
        holder.setFoodID(cart.getFoodID());
        holder.setItemCount(cart.getItemCount());
        holder.positionItem(position);
        holder.setPriceFood(String.valueOf(Integer.parseInt(cart.getHarga()) * Integer.parseInt(cart.getItemCount())));

        subtotal = subtotal + Integer.parseInt(cart.getItemCount()) * Integer.parseInt(cart.getHarga());

        holder.count = Integer.parseInt(cart.getItemCount());
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.count++;
                int sub = Integer.parseInt(cart.getHarga()) * holder.count;
                holder.setPriceFood(String.valueOf(sub));
                holder.changePrice(holder.count
                        ,Integer.parseInt(cart.getHarga())
                        ,true);
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.count--;
                if (holder.count < 1){
                    DocumentReference Ref = FirebaseFirestore.getInstance()
                            .collection("Cart").document(userID)
                            .collection("Food").document(holder.idFood);
                    Ref.delete();

                    notifyItemRemoved(holder.positionItem);
                    cartData.remove(holder.positionItem);
                }else{
                    int sub = Integer.parseInt(cart.getHarga()) * holder.count;
                    holder.setPriceFood(String.valueOf(sub));
                    holder.changePrice(holder.count
                            ,Integer.parseInt(cart.getHarga())
                            ,false);
                }
            }
        });

        finalTotal = subtotal;

        textviewTotalExpense.setText(String.valueOf(finalTotal));
    }

    @Override
    public int getItemCount() {
        return cartData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameFood,priceFood,itemCount,countReview,rateReview;
        ImageView imageFood,plus,minus,delete;
        String idFood;
        int count = 0,positionItem = 0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            priceFood = itemView.findViewById(R.id.priceFood);
            imageFood = itemView.findViewById(R.id.imageFood);
            delete = itemView.findViewById(R.id.delete);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            itemCount = itemView.findViewById(R.id.count);
            rateReview = itemView.findViewById(R.id.rateReview);
            countReview = itemView.findViewById(R.id.countReview);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference Ref;
                    Ref = FirebaseFirestore.getInstance()
                            .collection("Cart").document(userID)
                            .collection("Food").document(idFood);
                    Ref.delete();

                    notifyItemRemoved(positionItem);
                    cartData.remove(positionItem);
                }
            });
        }

        public void positionItem(int position){
            positionItem = position;
        }
        public void setNameFood(String name){
            nameFood.setText(name);
        }
        public void setPriceFood(String price){
            priceFood.setText(price);
        }
        public void setImageFood(String image){
            Glide.with(itemView.getContext())
                    .load(image)
                    .centerCrop()
                    .into(imageFood);
        }
        public void setFoodID(String id){
            idFood = id;
        }
        public void setItemCount(String count){
            itemCount.setText(count);
        }
        public void changePrice(int count, int price, boolean increment){

            setItemCount(String.valueOf(count));

            if (increment){
                subtotal  = subtotal + price;
            }else{
                if (count >= 1){
                    subtotal  = subtotal - price;
                }
            }

            if (count < 1){
                minus.setClickable(false);
            }else{
                minus.setClickable(true);
            }

            FirebaseFirestore.getInstance()
                    .collection("Cart").document(userID)
                    .collection("Food").document(idFood)
                    .update("itemCount", count);

            finalTotal = subtotal;
            textviewTotalExpense.setText(String.valueOf(subtotal));

        }
        public void setRating(String idFood){

            Query dataReviewQuery = FirebaseFirestore.getInstance()
                    .collection("Feedback")
                    .whereEqualTo("FoodID", idFood);

            dataReviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    int counter = 0;
                    float totalRating = 0;
                    String ratingValue = "0";

                    if (!value.isEmpty()){
                        for (QueryDocumentSnapshot doc : value){

                            totalRating = totalRating + doc.getLong("Rating").floatValue();
                            counter++;

                        }

                        ratingValue = String.valueOf(totalRating/counter);
                    }
                    rateReview.setText(ratingValue);
                    countReview.setText(counter + " Orders");
                }
            });
        }

    }
}
