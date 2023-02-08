package com.bagaseka.foodapp.main.fragment.review;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.adapter.ListCustomerReviewAdapter;
import com.bagaseka.foodapp.component.model.ReviewItem;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CustomerReview extends AppCompatActivity implements View.OnClickListener {

    private ImageButton back;
    private RecyclerView recyclerView;
    private ListCustomerReviewAdapter adapter;
    private String foodID;
    private TextView rating, counterReviewOrder;

    private ListenerRegistration dataReviewRegistration = null;
    private ListenerRegistration dataAllReviewsRegistration = null;

    public static final String FOOD_ID = "food_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_review);
        foodID = getIntent().getStringExtra(FOOD_ID);

        rating = findViewById(R.id.reviewRate);
        counterReviewOrder = findViewById(R.id.counterReviewOrder);

        recyclerView = findViewById(R.id.CustomerReviewRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerReview.this, RecyclerView.VERTICAL, false));
        adapter = new ListCustomerReviewAdapter();
        recyclerView.setAdapter(adapter);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        getAllRating();

        getAllFeedback(new GetFeedbackCallback() {
            @Override
            public void data(List<ReviewItem> items) {
                getUserData(items, new GetFeedbackCallback() {
                    @Override
                    public void data(List<ReviewItem> items) {
                        adapter.setData(items);
                    }
                });
            }
        });
    }

    private void getAllRating(){
        Query dataReviewQuery = FirebaseFirestore.getInstance()
                .collection("Feedback")
                .whereEqualTo("FoodID", foodID);

        dataReviewRegistration = dataReviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int counter = 0;
                float totalRating = 0;
                for (QueryDocumentSnapshot doc : value) {

                    totalRating = totalRating + doc.getLong("Rating").floatValue();
                    counter++;

                }
                rating.setText(String.valueOf(totalRating / counter));
                counterReviewOrder.setText(counter + " Times Order");
            }
        });
    }

    private void getUserData(List<ReviewItem> items, GetFeedbackCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("Akun")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) return;

                        ArrayList<ReviewItem> newItems = new ArrayList<>(items);

                        for (int i = 0; i < newItems.size(); i++) {
                            for (QueryDocumentSnapshot doc : value) {
                                String userId = doc.getId();
                                Log.d("User id",doc.getId());
                                Log.d("User id review",newItems.get(i).getUserID());
                                if (newItems.get(i).getUserID().equals(userId)) {
                                    String name = doc.getString("name");
                                    String image = doc.getString("image");

                                    newItems.get(i).setName(name);
                                    newItems.get(i).setImage(image);
                                }
                            }
                        }

                        callback.data(newItems);
                    }
                });
    }

    private void getAllFeedback(GetFeedbackCallback callback) {
        dataAllReviewsRegistration = FirebaseFirestore.getInstance()
                .collection("Feedback")
                .whereEqualTo("FoodID", foodID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) return;

                        ArrayList<ReviewItem> items = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            ReviewItem item = new ReviewItem();

                            String userId = doc.getString("UserID");
                            String date = doc.getString("Date");
                            String rating = String.valueOf(doc.get("Rating"));
                            String review = doc.getString("Review");

                            item.setUserID(userId);
                            item.setDate(date);
                            item.setRating(rating);
                            item.setReview(review);

                            items.add(item);
                        }

                        callback.data(items);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (dataReviewRegistration != null) dataReviewRegistration.remove();
        if (dataAllReviewsRegistration != null) dataAllReviewsRegistration.remove();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
                break;
        }
    }

    private interface GetFeedbackCallback {
        void data(List<ReviewItem> items);
    }
}