package com.bagaseka.foodapp.main.fragment.review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bagaseka.foodapp.IntroScreen;
import com.bagaseka.foodapp.component.model.ReviewItem;
import com.bagaseka.foodapp.signinsignup.SignUp;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.adapter.ListCustomerReviewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class CustomerReview extends AppCompatActivity implements View.OnClickListener {

    private ImageButton back;
    private RecyclerView recyclerView;
    private FirestoreRecyclerOptions<ReviewItem> options;
    private FirestoreRecyclerAdapter adapter;
    private String foodID,userID;
    private FirebaseAuth auth;
    private TextView rating,counterReviewOrder;

    public static final String FOOD_ID = "food_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_review);
        foodID = getIntent().getStringExtra(FOOD_ID);
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        rating = findViewById(R.id.reviewRate);
        counterReviewOrder = findViewById(R.id.counterReviewOrder);

        recyclerView = findViewById(R.id.CustomerReviewRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerReview.this, RecyclerView.VERTICAL,false));

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        Query userData = FirebaseFirestore.getInstance()
                .collection("Feedback");

        Query dataReviewQuery = FirebaseFirestore.getInstance()
                .collection("Feedback")
                .whereEqualTo("FoodID", foodID);

        dataReviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int counter = 0;
                float totalRating = 0;
                for (QueryDocumentSnapshot doc : value){

                    totalRating = totalRating + doc.getLong("Rating").floatValue();
                    counter++;

                }
                rating.setText(String.valueOf(totalRating/counter));
                counterReviewOrder.setText(counter + " Times Order");
            }
        });

        userData.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value == null) return;
                List<String> userID = new ArrayList<>();
                List<String> foodID = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value){
                    userID.add(doc.getString("UserID"));
                    foodID.add(doc.getString("FoodID"));
                    //LoadDataIntoRecyclerview(foodID,nameUser,imageUser);
                }

            }
        });


    }

    public void LoadDataIntoRecyclerview(String foodID, String name, String image){
        Query customerReview = FirebaseFirestore.getInstance()
                .collection("Feedback")
                .whereEqualTo("FoodID", foodID);

        options = new FirestoreRecyclerOptions.Builder<ReviewItem>()
                .setQuery(customerReview, new SnapshotParser<ReviewItem>() {
                    @NonNull
                    @Override
                    public ReviewItem parseSnapshot(@NonNull DocumentSnapshot snapshot) {

                        ReviewItem List = new ReviewItem();

                        List.setUserID(snapshot.getString("UserID"));
                        List.setName(name);
                        List.setDate(snapshot.getString("Date"));
                        List.setRating(String.valueOf(snapshot.get("Rating")));
                        List.setReview(snapshot.getString("Review"));
                        List.setImage(image);

                        return List;
                    }
                }).setLifecycleOwner(CustomerReview.this).build();

        adapter = new ListCustomerReviewAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}