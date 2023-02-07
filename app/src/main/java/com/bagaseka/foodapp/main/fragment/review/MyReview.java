package com.bagaseka.foodapp.main.fragment.review;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bagaseka.foodapp.component.adapter.ListCustomerReviewAdapter;
import com.bagaseka.foodapp.component.adapter.ListMyReviewAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class MyReview extends AppCompatActivity implements View.OnClickListener {

    private ImageButton back;
    private RecyclerView myReviewRV;
    private ListMyReviewAdapter adapter;
    private String userID;
    private FirebaseAuth auth;

    private ListenerRegistration dataReviewRegistration = null;
    private ListenerRegistration dataAllReviewsRegistration = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        myReviewRV = findViewById(R.id.myReviewRV);
        myReviewRV.setHasFixedSize(true);
        myReviewRV.setLayoutManager(new LinearLayoutManager(MyReview.this, RecyclerView.VERTICAL, false));

        adapter = new ListMyReviewAdapter();
        myReviewRV.setAdapter(adapter);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

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

    private void getUserData(List<ReviewItem> items, GetFeedbackCallback callback) {

        FirebaseFirestore.getInstance()
                .collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) return;

                        ArrayList<ReviewItem> newItems = new ArrayList<>(items);
                        Log.d("itemLength2",String.valueOf(newItems.size()));
                        for (int i = 0; i < newItems.size(); i++) {
                            for (QueryDocumentSnapshot doc : value) {
                                String foodID = doc.getString("FoodID");
                                if (newItems.get(i).getFoodID().equals(foodID)) {
                                    String name = doc.getString("Nama");
                                    String image = doc.getString("Image");

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
                .whereEqualTo("UserID", userID)
                .orderBy("Date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) return;

                        ArrayList<ReviewItem> items = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            ReviewItem item = new ReviewItem();

                            String foodID = doc.getString("FoodID");
                            String date = doc.getString("Date");
                            String rating = String.valueOf(doc.get("Rating"));
                            String review = doc.getString("Review");

                            item.setFoodID(foodID);
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