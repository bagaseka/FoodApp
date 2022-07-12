package com.bagaseka.foodapp.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bagaseka.foodapp.main.fragment.review.CustomerReview;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
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

import java.util.HashMap;
import java.util.Map;

public class FoodDetail extends AppCompatActivity implements View.OnClickListener {

    public static final String FOOD_ID = "food_id";

    private ImageView imageDetail,back, fav,cart;
    private TextView nameDetail,rateReview,countReview,priceDetail,descripDetail,review,itemCountCart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private Button addToCart;
    private FirebaseAuth auth;
    private String foodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodID = getIntent().getStringExtra(FOOD_ID);
        docRef = db.collection("Product").document(foodID);

        auth = FirebaseAuth.getInstance();

        imageDetail = findViewById(R.id.imageDetail);
        nameDetail = findViewById(R.id.nameDetail);
        rateReview = findViewById(R.id.rateReview);
        countReview = findViewById(R.id.countReview);
        priceDetail = findViewById(R.id.priceDetail);
        cart = findViewById(R.id.cart);
        descripDetail = findViewById(R.id.descripDetail);
        addToCart = findViewById(R.id.addToCart);
        itemCountCart = findViewById(R.id.itemCountCart);
        review = findViewById(R.id.review);
        back = findViewById(R.id.back);
        fav = findViewById(R.id.fav);
        String userID = auth.getUid();
        setCartItemCount(userID);
        addToCart.setOnClickListener(this);
        review.setOnClickListener(this);
        back.setOnClickListener(this);
        fav.setOnClickListener(this);
        cart.setOnClickListener(this);

        setRatingReview(foodID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();

                    Glide.with(FoodDetail.this)
                            .load(document.getString("Image"))
                            .centerCrop()
                            .into(imageDetail);
                    nameDetail.setText(document.getString("Nama"));

                    setRating(document.getString("FoodID"));

                    priceDetail.setText("Rp"+ String.valueOf(document.get("Harga")));
                    descripDetail.setText(document.getString("Descrip"));

                    FirebaseFirestore.getInstance()
                            .collection("Akun").document(userID)
                            .collection("Favorite").document(document.getId())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (document.getId().equals(task.getResult().getString("FoodID"))){
                                fav.setSelected(true);
                            }else{
                                fav.setSelected(false);
                            }
                        }
                    });
                } else {
                    Toast.makeText(FoodDetail.this, "Cached get failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        String userID = auth.getUid();
        String mFoodID = getIntent().getStringExtra(FOOD_ID);
        DocumentReference addCart = FirebaseFirestore.getInstance()
                .collection("Cart").document(userID)
                .collection("Food").document(mFoodID);

        if (v.getId() == R.id.addToCart){

            DocumentReference finalAddCart = addCart;
            addCart.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String itemCount;
                    int itemCount2;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (mFoodID.equals(document.getString("FoodID"))){
                            itemCount = String.valueOf(document.get("itemCount"));
                            itemCount2 = Integer.parseInt(itemCount) + 1;
                            finalAddCart.update("itemCount", itemCount2);
                            snackbar(v, R.color.Green,"Success add to cart");
                        }else{
                            Map<String, Object> cart = new HashMap<>();
                            cart.put("FoodID", mFoodID);
                            cart.put("itemCount", 1);
                            finalAddCart.set(cart)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                snackbar(v, R.color.Green,"Success add to cart");
                                            }else{

                                            }
                                        }
                                    });
                        }
                    }
                }
            });


        }else if (v.getId() == R.id.back){
            finish();
        } else if (v.getId() == R.id.fav){
            if (fav.isSelected()){

                addCart = FirebaseFirestore.getInstance()
                        .collection("Akun").document(userID)
                        .collection("Favorite").document(mFoodID);

                addCart.delete();
                snackbar(v, R.color.colorPrimary,"Deleted from favorite");
                fav.setSelected(false);

            }else{

                addCart = FirebaseFirestore.getInstance()
                        .collection("Akun").document(userID)
                        .collection("Favorite").document(mFoodID);
                Map<String, Object> Favorite = new HashMap<>();
                Favorite.put("FoodID", mFoodID);
                addCart.set(Favorite);
                snackbar(v, R.color.Green,"Success add to favorite");
                fav.setSelected(true);
            }
        }
        else if (v.getId() == R.id.review){
            String foodID = getIntent().getStringExtra(FOOD_ID);
            Intent moveWithDataIntent = new Intent(v.getContext(), CustomerReview.class);
            moveWithDataIntent.putExtra(FoodDetail.FOOD_ID, foodID);
            startActivity(moveWithDataIntent);
        }else if (v.getId() == R.id.cart){
            Intent moveIntent = new Intent(FoodDetail.this, Cart.class);
            startActivity(moveIntent);
        }
    }
    public void setCartItemCount(String userID){

        Query query = FirebaseFirestore.getInstance()
                .collection("Cart").document(userID).collection("Food");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int totalItemCount = 0;
                for (QueryDocumentSnapshot doc : value) {

                    totalItemCount += Integer.parseInt(String.valueOf(doc.get("itemCount")));

                }

                itemCountCart.setText(String.valueOf(totalItemCount));
            }
        });
    }
    public void setRatingReview(String idFood){
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
    public void snackbar(View v, int color, String text){
        Snackbar snackbar = Snackbar.make(v, text,
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(
                ContextCompat.getColor(FoodDetail.this, color));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
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
