package com.bagaseka.foodapp.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.HomeMainList;
import com.bagaseka.foodapp.main.FoodDetail;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListFavoriteAdapter extends FirestoreRecyclerAdapter<HomeMainList, ListFavoriteAdapter.ViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getUid();
    private Context context;
    private View rootView;
    private ConstraintLayout FavoriteLayout;
    private int layout;

    public ListFavoriteAdapter(@NonNull FirestoreRecyclerOptions<HomeMainList> options, int layout) {
        super(options);
        this.layout = layout;
    }


    @NonNull
    @Override
    public ListFavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        FavoriteLayout = (ConstraintLayout) rootView.findViewById(R.id.FavoriteLayout);

        return new ListFavoriteAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListFavoriteAdapter.ViewHolder holder, int position, @NonNull HomeMainList favorite) {

        if (favorite.getFoodID() != null && !favorite.getFoodID().isEmpty()){
            holder.setNameFood(favorite.getNama());
            holder.setPriceFood(favorite.getHarga());
            holder.setImageFood(favorite.getImage());
            holder.setFoodID(favorite.getFoodID());
            holder.setRating(favorite.foodID);
            holder.setFavorite(favorite.foodID);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameFood, priceFood, itemCount, countReview, rateReview;
        ImageView imageFood, favoriteFood;
        String idFood;
        DocumentReference Ref;
        //int positionItem = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            priceFood = itemView.findViewById(R.id.priceFood);
            imageFood = itemView.findViewById(R.id.imageFood);
            itemCount = itemView.findViewById(R.id.count);
            rateReview = itemView.findViewById(R.id.rateReview);
            countReview = itemView.findViewById(R.id.countReview);
            favoriteFood = itemView.findViewById(R.id.favoriteFood);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent moveWithDataIntent = new Intent(v.getContext(), FoodDetail.class);
                    moveWithDataIntent.putExtra(FoodDetail.FOOD_ID, idFood);
                    v.getContext().startActivity(moveWithDataIntent);
                }
            });

            favoriteFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoriteFood.isSelected()) {

                        Ref = FirebaseFirestore.getInstance()
                                .collection("Akun").document(userID)
                                .collection("Favorite").document(idFood);

                        Ref.delete();
                        setSnackbar(v, R.color.colorPrimary, "Deleted from favorite");
                        favoriteFood.setSelected(false);

                    } else {

                        Ref = FirebaseFirestore.getInstance()
                                .collection("Akun").document(userID)
                                .collection("Favorite").document(idFood);
                        Map<String, Object> Favorite = new HashMap<>();
                        Favorite.put("FoodID", idFood);
                        Ref.set(Favorite);
                        setSnackbar(v, R.color.Green, "Success add to favorite");
                        favoriteFood.setSelected(true);
                    }
                }
            });

        }
//
//        public void positionItem(int position) {
//            positionItem = position;
//        }

        public void setNameFood(String name) {
            nameFood.setText(name);
        }

        public void setPriceFood(String price) {
            priceFood.setText(price);
        }

        public void setImageFood(String image) {
            Glide.with(itemView.getContext())
                    .load(image)
                    .centerCrop()
                    .into(imageFood);
        }

        public void setFavorite(String idFood) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userID = auth.getUid();

            FirebaseFirestore.getInstance()
                    .collection("Akun").document(userID)
                    .collection("Favorite").document(idFood)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (idFood.equals(task.getResult().getString("FoodID"))) {
                        favoriteFood.setSelected(true);
                    } else {
                        favoriteFood.setSelected(false);
                    }
                }
            });
        }

        public void setFoodID(String id) {
            idFood = id;
        }

        public void setRating(String idFood) {

            Query dataReviewQuery = FirebaseFirestore.getInstance()
                    .collection("Feedback")
                    .whereEqualTo("FoodID", idFood);

            dataReviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    int counter = 0;
                    float totalRating = 0;
                    String ratingValue = "0";

                    if (!value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {

                            totalRating = totalRating + doc.getLong("Rating").floatValue();
                            counter++;

                        }

                        ratingValue = String.valueOf(totalRating / counter);
                    }
                    rateReview.setText(ratingValue);
                    countReview.setText(counter + " Orders");
                }
            });
        }

        public void setSnackbar(View v, int color, String text) {
            Snackbar snackbar = Snackbar.make(v, text,
                    Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), color));
            snackbar.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

}
