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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ListFavoriteAdapter extends RecyclerView.Adapter<ListFavoriteAdapter.ViewHolder>{

    private ArrayList<HomeMainList> favData;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getUid();
    private Context context;
    private View rootView;
    private ConstraintLayout FavoriteLayout;
    private int layout;
    private DocumentReference Ref;
    private Activity mActivity;

    public ListFavoriteAdapter(ArrayList<HomeMainList> favData, int layout,Activity mActivity) {
        this.favData = favData;
        this.layout = layout;
        this.mActivity=mActivity;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeMainList favorite = favData.get(position);
        holder.setNameFood(favorite.getNama());
        holder.setPriceFood(favorite.getHarga());
        holder.setImageFood(favorite.getImage());
        holder.setFoodID(favorite.getFoodID());
        holder.setRating(favorite.foodID);
        holder.setFavorite(favorite.foodID);
        holder.setOrderCount(favorite.getCountOrder());

        holder.favoriteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.favoriteFood.isSelected()) {

                    Ref = FirebaseFirestore.getInstance()
                            .collection("Akun").document(userID)
                            .collection("Favorite").document(holder.idFood);

                    Ref.delete();
                    holder.setSnackbar(v, R.color.colorPrimary, "Deleted from favorite");
                    holder.favoriteFood.setSelected(false);

                    favData.remove(holder.getAbsoluteAdapterPosition());
                    notifyItemRemoved(holder.getAbsoluteAdapterPosition());


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameFood, priceFood, itemCount, countOrder, rateReview;
        ImageView imageFood, favoriteFood;
        String idFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            priceFood = itemView.findViewById(R.id.priceFood);
            imageFood = itemView.findViewById(R.id.imageFood);
            itemCount = itemView.findViewById(R.id.count);
            rateReview = itemView.findViewById(R.id.rateReview);
            countOrder = itemView.findViewById(R.id.countReview);
            favoriteFood = itemView.findViewById(R.id.favoriteFood);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent moveWithDataIntent = new Intent(v.getContext(), FoodDetail.class);
                    moveWithDataIntent.putExtra(FoodDetail.FOOD_ID, idFood);
                    v.getContext().startActivity(moveWithDataIntent);
                    mActivity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                }
            });



        }

        public void setNameFood(String name) {
            nameFood.setText(name);
        }

        public void setPriceFood(String price) {
            priceFood.setText(formatRupiah(Double.parseDouble(price)));
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

        public void setOrderCount(String numOrder){countOrder.setText(numOrder + " Orders");}

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
