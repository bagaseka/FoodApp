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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

//        String currentDateTimeString = java.text.DateFormat.getDateInstance().format(new Date());
//
//        CollectionReference addFeedback = FirebaseFirestore.getInstance()
//                .collection("Feedback");
//
//        if (v.getId() == R.id.Submit){
//
//            if (ratingBar.getRating() == 0.0 ){
//                //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
//            }else{
//
//                DocumentReference queryFoodID = FirebaseFirestore.getInstance()
//                        .collection("Pesanan")
//                        .document(foodOrderKey).collection("Food")
//                        .document(idFood);
//
//                queryFoodID.update("StatusReview", true);
//
//                Map<String, Object> review = new HashMap<>();
//                review.put("UserID", userID);
//                review.put("FoodID", idFood);
//                review.put("Date", currentDateTimeString);
//                review.put("Rating", ratingBar.getRating());
//                if (inputReview.getText().toString().isEmpty()){
//                    review.put("Review", "-");
//                }else{
//                    review.put("Review", inputReview.getText().toString());
//                }
//                addFeedback.document().set(review);
//
//                this.dismiss();
//            }
//        }
        testAmbilData();
    }

    public void testAmbilData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dataRef = db.collection("Feedback");

        dataRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Map<String, Float>> allData  = new HashMap<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String userId = snapshot.getString("UserID");
                        String foodId = snapshot.getString("FoodID");
                        float rating = snapshot.getLong("Rating").floatValue();

                        Map<String, Float> userData = allData.get(userId);
                        if (userData == null) {
                            userData = new HashMap<>();
                            allData.put(userId, userData);
                        }
                        userData.put(foodId, rating);
                    }
                    prosesData(allData);


                    // Proses data
                } else {
                    // Handle error
                }

            }
        });
    }

    public void prosesData(Map<String, Map<String, Float>> allData){
        for (Map.Entry<String, Map<String, Float>> outerEntry : allData.entrySet()) {
            Map<String, Float> a = outerEntry.getValue();
            for (Map.Entry<String, Map<String, Float>> innerEntry : allData.entrySet()) {
                Map<String, Float> b = innerEntry.getValue();
                double similarity = cosineSimilarity(a, b);
                Log.d("Similarity", "Similarity: " + similarity);
            }
        }

//        String targetOuterKey = "a";
//        String targetInnerKey = "a";
//        float sum = 0;
//        int count = 0;
//        for (Map.Entry<String, Map<String, Float>> outerEntry : allData.entrySet()) {
//            String outerKey = outerEntry.getKey();
//            Map<String, Float> innerMap = outerEntry.getValue();
//            Log.d("OuterKey", outerKey);
//            for (Map.Entry<String, Float> innerEntry : innerMap.entrySet()) {
//                String innerKey = innerEntry.getKey();
//                Float value = innerEntry.getValue();
//                Log.d("InnerKey", innerKey);
//                Log.d("Value", value.toString());
//                if (outerKey.equals(targetOuterKey) && innerKey.equals(targetInnerKey)) {
//                    sum += value;
//                    count++;
//                }
//            }
//        }
//        float average = sum / count;
//        Log.d("Average", "Average: " + average);

        //Log Data
//        for (Map.Entry<String, Map<String, Float>> outerEntry : allData.entrySet()) {
//            String outerKey = outerEntry.getKey();
//            Map<String, Float> innerMap = outerEntry.getValue();
//            for (Map.Entry<String, Float> innerEntry : innerMap.entrySet()) {
//                String innerKey = innerEntry.getKey();
//                Float value = innerEntry.getValue();
//                Log.i("TAG", outerKey + " - " + innerKey + " : " + value);
//            }
//        }

    }
    private double cosineSimilarity(Map<String, Float> a, Map<String, Float> b) {
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (Map.Entry<String, Float> entry : a.entrySet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dotProduct += entry.getValue() * b.getOrDefault(entry.getKey(), 0.0f);
            }
            magnitudeA += Math.pow(entry.getValue(), 2);
        }

        for (Map.Entry<String, Float> entry : b.entrySet()) {
            magnitudeB += Math.pow(entry.getValue(), 2);
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        if (magnitudeA != 0.0 && magnitudeB != 0.0) {
            return dotProduct / (magnitudeA * magnitudeB);
        } else {
            return 0.0;
        }
    }

    public void updateList(OnSubmitListener listener){
        this.listener = listener;
    }

    public interface OnSubmitListener{
        void onclick();
    }

    static Double calculateWeightedAverage(Map<Double, Integer> map) throws ArithmeticException {
        double num = 0;
        double denom = 0;
        for (Map.Entry<Double, Integer> entry : map.entrySet()) {
            num += entry.getKey() * entry.getValue();
            denom += entry.getValue();
        }

        return num / denom;
    }

}
