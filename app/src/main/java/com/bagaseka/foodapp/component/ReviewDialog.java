package com.bagaseka.foodapp.component;

import static com.google.common.collect.Sets.union;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
        getDataFromFirestore();
    }

    //---------------------------------------------------------

    public void getDataFromFirestore(){
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
                }

            }
        });
    }

    public static void logMap(Map<String, Float> map , String TAG) {
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + " : " + entry.getValue());
        }
    }
    //--------------

    public static Map<String, Float> filterMapA(Map<String, Float> mapA, Map<String, Float> mapB) {
        Map<String, Float> filteredMap = new HashMap<>();

        Set<String> keySetA = mapA.keySet();
        Set<String> keySetB = mapB.keySet();

        for (String keyA : keySetA) {
            if (keySetB.contains(keyA)) {
                filteredMap.put(keyA, mapA.get(keyA));
            }
        }

        return filteredMap;
    }
    public static Map<String, Float> filterMapB(Map<String, Float> mapA, Map<String, Float> mapB) {
        Map<String, Float> filteredMap = new HashMap<>();

        Set<String> keySetA = mapA.keySet();
        Set<String> keySetB = mapB.keySet();

        for (String keyB : keySetB) {
            if (keySetA.contains(keyB)) {
                filteredMap.put(keyB, mapA.get(keyB));
            }
        }

        return filteredMap;
    }

    //--------------

    public void prosesData(Map<String, Map<String, Float>> Data){

        Map<String, Double> weighted = new HashMap<>();

        //Cosine Similarity Clear
        for (Map.Entry<String, Map<String, Float>> outerEntry : Data.entrySet()) {
            if (outerEntry.getKey().equals(userID)) {
                Map<String, Float> a = outerEntry.getValue();
                for (Map.Entry<String, Float> entry1 : a.entrySet()) {
                    Log.d("AAAAMap a", entry1.getKey() + " : " + entry1.getValue());
                }
                for (Map.Entry<String, Map<String, Float>> innerEntry : Data.entrySet()) {
                    if (!innerEntry.getKey().equals(userID)){
                        Map<String, Float> b = innerEntry.getValue();

                        double similarity = cosineSimilarity(a, b);
                        weighted.put(innerEntry.getKey(),similarity);

                        Log.d("AAAASimilarity", innerEntry.getKey() + " Similarity: " + similarity);

                        for (Map.Entry<String, Float> entry : b.entrySet()) {
                            Log.d("AAAAMap b", "ID " + innerEntry.getKey()+" | " + entry.getKey() + " : " + entry.getValue());
                        }

                    }
                }
            }
        }
        //-----------------------

        ArrayList<String> foodid = new ArrayList<>();
        for (Map.Entry<String, Map<String, Float>> outerEntry : Data.entrySet()){
            if (outerEntry.getKey().equals(userID)) {
                Map<String, Float> a = outerEntry.getValue();
                for (Map.Entry<String, Float> entry1 : a.entrySet()) {
                    Log.d("AAAAMap a", entry1.getKey() + " : " + entry1.getValue());
                    foodid.add(entry1.getKey());
                }
            }
        }
        getDataPredict(foodid,weighted);
    }

    public void getDataPredict(ArrayList<String> exceptID, Map<String, Double> weighted){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dataRef = db.collection("Feedback");

        dataRef.whereNotIn("FoodID",exceptID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Map<String, Double>> allData  = new HashMap<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String userId = snapshot.getString("UserID");
                        String foodId = snapshot.getString("FoodID");
                        Double rating = snapshot.getLong("Rating").doubleValue();

                        Map<String, Double> userData = allData.get(foodId);
                        if (userData == null) {
                            userData = new HashMap<>();
                            allData.put(foodId, userData);
                        }
                        userData.put(userId, rating);
                    }

                    // Iterate through allData to view its contents
                    for (Map.Entry<String, Map<String, Double>> outerMap : allData.entrySet()) {
                        Log.d("123TAG", "Outer Key: " + outerMap.getKey());

                        //----
                        Map<String, Double> values = new HashMap<>();
                        for (Map.Entry<String, Double> innerMap : outerMap.getValue().entrySet()) {
                            Log.d("123TAG", "Inner Key: " + innerMap.getKey() + " Value: " + innerMap.getValue());
                            String key = innerMap.getKey();
                            Double value = innerMap.getValue();
                            values.put(key,value);
                        }


                        Log.d("123TAG", "Final: " + calculate(values,weighted));

                    }

                    for (Map.Entry<String, Double> entry : weighted.entrySet()) {
                        Log.d("123TAG_weight", "Key: " + entry.getKey() + " Value: " + entry.getValue());
                    }

                    //`----------------------------



                }
            }
        });
    }

    private double cosineSimilarity(Map<String, Float> a, Map<String, Float> b) {
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        Map<String, Float> a1 = filterMapA(a,b);
        Map<String, Float> b1 = filterMapB(b,a);

        logMap(a1, "MapaA");
        logMap(b1, "MapaB");

        for (Map.Entry<String, Float> entry : a1.entrySet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dotProduct += entry.getValue() * b1.getOrDefault(entry.getKey(), 0.0f);
            }
            magnitudeA += Math.pow(entry.getValue(), 2);
        }

        for (Map.Entry<String, Float> entry : b1.entrySet()) {
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

    public static Double calculate(Map<String, Double> values, Map<String, Double> weights) {
        double numerator = 0;
        double denominator = 0;

        for (Map.Entry<String, Double> innerEntry : values.entrySet()) {
            String key = innerEntry.getKey();
            Double value = innerEntry.getValue();
            Double weight = weights.get(key);

            numerator += value * weight;
            denominator += weight;
        }

        return (numerator / denominator);
    }

}
