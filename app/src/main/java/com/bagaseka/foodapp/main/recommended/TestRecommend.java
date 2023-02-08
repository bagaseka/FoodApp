package com.bagaseka.foodapp.main.recommended;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bagaseka.foodapp.component.adapter.ListMenuAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.example.foodapp.R;
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

public class TestRecommend extends AppCompatActivity {

    private AuthViewModel viewModel;
    private String userID;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView nFoodRecommendRv;
    private Button logout,generate;
    private FirestoreRecyclerOptions<HomeMainList> options;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recommend);

        logout = findViewById(R.id.logout);
        generate = findViewById(R.id.generate);
        userID = auth.getUid();

        RecyclerViewInitial();
        setRecommended(userID);

        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Intent moveIntent2 = new Intent(TestRecommend.this, SignIn.class);
                startActivity(moveIntent2);
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query feedbackQuery = FirebaseFirestore.getInstance()
                        .collection("Feedback")
                        .whereEqualTo("UserID", userID)
                        .orderBy("FoodID");

                Query otherFeedback = FirebaseFirestore.getInstance()
                        .collection("Feedback")
                        .whereNotEqualTo("UserID",userID)
                        .orderBy("FoodID");

                feedbackQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> menuID = new ArrayList<>();
                        int[] myRating = new int[0];
                        int count = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            menuID.add(doc.getString("FoodID"));
                            myRating[count] = Integer.parseInt(String.valueOf(doc.get("Rating")));
                            count++;
                        }
                        otherFeedback.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                List<String> otherUserID = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : value) {
                                    String otherMenuID = doc.getString("FoodID");
                                    for (String menu : menuID){
                                        if (menu.equals(otherMenuID)){

                                        }
                                    }
                                }

                                List<String> menuOtherID = new ArrayList<>();
                                int[] otherRating = new int[0];
                                int countOther = 0;
                                for (QueryDocumentSnapshot doc : value) {
                                    if (menuID.get(countOther).equals(doc.getString("FoodID"))){
                                        menuOtherID.add(countOther,doc.getString("FoodID"));
                                        otherRating[countOther] = Integer.parseInt(String.valueOf(doc.get("Rating")));
                                    }else{
                                        countOther++;
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void RecyclerViewInitial(){
        nFoodRecommendRv = findViewById(R.id.recommendFoodRv);
        nFoodRecommendRv.setLayoutManager(new LinearLayoutManager(TestRecommend.this, RecyclerView.VERTICAL,false));
    }

    public void setRecommended(String userID){

        FirebaseFirestore.getInstance()
                .collection("Recommend").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();
                        List<String> foodRec = (List<String>) document.get("FoodID");

                        Query query = FirebaseFirestore.getInstance()
                                .collection("Product")
                                .whereIn("FoodID",foodRec);

                        options = new FirestoreRecyclerOptions.Builder<HomeMainList>()
                                .setQuery(query, new SnapshotParser<HomeMainList>() {
                                    @Override
                                    public HomeMainList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                        /*Write the process you want to do when taking a snapshot*/
                                        String nama = snapshot.getString("Nama");
                                        String harga = String.valueOf(snapshot.get("Harga"));
                                        String image = snapshot.getString("Image");
                                        String numOrder = String.valueOf(snapshot.get("numOrder"));
                                        String id = snapshot.getId();
                                        HomeMainList List = new HomeMainList(nama, harga, image, id, numOrder);
                                        return List;
                                    }
                                }).setLifecycleOwner(TestRecommend.this).build();

                        adapter = new ListMenuAdapter(options, R.layout.list_card_vertical,TestRecommend.this);
                        nFoodRecommendRv.setAdapter(adapter);
                    }
                });
    }
}