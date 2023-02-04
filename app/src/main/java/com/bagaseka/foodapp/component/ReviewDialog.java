package com.bagaseka.foodapp.component;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private String nameFood,imageFood,idFood,userID,foodOrderKey;
    private int countMenu, priceFood;

    private TextView nameFoodTv,countMenuTv,priceFoodTv;
    private ImageView imageFoodImg;
    private RatingBar ratingBar;
    private EditText inputReview;
    private Button Submit;
    private FirebaseAuth auth;
    private OnSubmitListener listener;

    public ReviewDialog(String id, String name,
                        int count, int price, String imageFood,
                        String foodOrderKey){
        this.idFood = id;
        this.nameFood = name;
        this.countMenu = count;
        this.priceFood = price;
        this.imageFood = imageFood;
        this.foodOrderKey = foodOrderKey;
    };

    public ReviewDialog(){
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                com.google.android.material.bottomsheet.BottomSheetDialog dialog = (com.google.android.material.bottomsheet.BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_feedback,container,false);
        onInitViews(v);
        return v;
    }

    private void onInitViews(View v) {
        nameFoodTv = v.findViewById(R.id.nameFood);
        countMenuTv = v.findViewById(R.id.countMenu);
        priceFoodTv = v.findViewById(R.id.priceFood);
        imageFoodImg = v.findViewById(R.id.imageFood);
        ratingBar = v.findViewById(R.id.ratingBar);
        inputReview = v.findViewById(R.id.inputReview);
        Submit = v.findViewById(R.id.Submit);
        Submit.setOnClickListener(this);

        nameFoodTv.setText(nameFood);
        countMenuTv.setText(countMenu + " items");
        priceFoodTv.setText(String.valueOf(priceFood));

        Glide.with(this)
                .load(imageFood)
                .centerCrop()
                .into(imageFoodImg);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
    }

    @Override
    public void onClick(View v) {

        String currentDateTimeString = java.text.DateFormat.getDateInstance().format(new Date());

        CollectionReference addFeedback = FirebaseFirestore.getInstance()
                .collection("Feedback");

        if (v.getId() == R.id.Submit){

            if (ratingBar.getRating() == 0.0 ){
                //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
            }else{

                DocumentReference queryFoodID = FirebaseFirestore.getInstance()
                        .collection("Pesanan")
                        .document(foodOrderKey).collection("Food")
                        .document(idFood);

                queryFoodID.update("StatusReview", true);

                Map<String, Object> review = new HashMap<>();
                review.put("UserID", userID);
                review.put("FoodID", idFood);
                review.put("Date", currentDateTimeString);
                review.put("Rating", ratingBar.getRating());
                if (inputReview.getText().toString().isEmpty()){
                    review.put("Review", "-");
                }else{
                    review.put("Review", inputReview.getText().toString());
                }
                addFeedback.document().set(review);

                addRatingUserOnProduct(idFood,ratingBar.getRating());

                this.dismiss();
            }
        }
    }

    public void addRatingUserOnProduct(String foodID, float rating){
        DocumentReference addRatingOnProduct = FirebaseFirestore.getInstance()
                .collection("Product").document(foodID)
                .collection("Rating").document(userID);

        addRatingOnProduct.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Map<String, Object> addOwnRatingOnProduct = new HashMap<>();

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    double averageRating = 0;
                    int totalProduct = 1;
                    if (document.exists()) {
                        totalProduct = document.getLong("TotalOrder").intValue();
                        averageRating = (rating + document.getDouble("RatingAverage")) / document.getLong("TotalOrder").intValue();
                        addOwnRatingOnProduct.put("RatingAverage", averageRating);
                        addOwnRatingOnProduct.put("TotalOrder", totalProduct);
                        addRatingOnProduct.set(addOwnRatingOnProduct);
                    }else {
                        addOwnRatingOnProduct.put("RatingAverage", rating);
                        addOwnRatingOnProduct.put("TotalOrder", totalProduct);
                        addRatingOnProduct.set(addOwnRatingOnProduct);
                    }
                } else {
                    Log.d("Failure", "get failed with ", task.getException());
                }
            }
        });


    }

    public void updateList(OnSubmitListener listener){
        this.listener = listener;
    }

    public interface OnSubmitListener{
        void onclick();
    }

    public double cosineSimilarity(double[] A, double[] B) {
        if (A == null || B == null || A.length == 0 || B.length == 0 || A.length != B.length) {
            return 2;
        }

        double sumProduct = 0;
        double sumASq = 0;
        double sumBSq = 0;
        for (int i = 0; i < A.length; i++) {
            sumProduct += A[i]*B[i];
            sumASq += A[i] * A[i];
            sumBSq += B[i] * B[i];
        }
        if (sumASq == 0 && sumBSq == 0) {
            return 2.0;
        }
        return sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
    }
}
