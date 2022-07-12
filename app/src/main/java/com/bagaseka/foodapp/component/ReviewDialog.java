package com.bagaseka.foodapp.component;

import android.os.Build;
import android.os.Bundle;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                com.google.android.material.bottomsheet.BottomSheetDialog dialog = (com.google.android.material.bottomsheet.BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0); // Remove this line to hide a dark background if you manually hide the dialog.
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

        DocumentReference addFeedback = FirebaseFirestore.getInstance()
                .collection("Feedback").document();

        if (v.getId() == R.id.Submit){

            if (Submit.getText().toString().equals("SetCompleted")){
                Toast.makeText(v.getContext(), "Slebew", Toast.LENGTH_SHORT).show();
            }else{
                DocumentReference queryFoodID = FirebaseFirestore.getInstance()
                        .collection("Pesanan")
                        .document(foodOrderKey).collection("Food")
                        .document(idFood);

                queryFoodID.update("StatusReview", true);

                Map<String, Object> cart = new HashMap<>();
                cart.put("UserID", userID);
                cart.put("FoodID", idFood);
                cart.put("Date", currentDateTimeString);
                cart.put("Rating", ratingBar.getRating());
                if (inputReview.getText().toString().isEmpty()){
                    cart.put("Review", "-");
                }else{
                    cart.put("Review", inputReview.getText().toString());
                }

                addFeedback.set(cart);

            }

            this.dismiss();

        }

    }

    public void updateList(OnSubmitListener listener){
        this.listener = listener;
    }

    public interface OnSubmitListener{
        void onclick();
    }
}
