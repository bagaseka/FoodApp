package com.bagaseka.foodapp.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bagaseka.foodapp.component.adapter.ListCartMenuAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.bagaseka.foodapp.main.fragment.order.Checkout;
import com.bagaseka.foodapp.main.fragment.order.SelectTable;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity implements View.OnClickListener {

    private Button CheckOut,MainButton;
    private TextView totalPrice,ppn;
    private ImageButton back;
    private ConstraintLayout noItem;
    private RecyclerView recyclerView;
    private LinearLayout layout1;
    private FirebaseAuth auth;
    private List<String> menuID,dataCount;
    String userID;

    private ListCartMenuAdapter listCartMenuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        totalPrice = findViewById(R.id.totalPrice);
        ppn = findViewById(R.id.ppn);
        noItem = findViewById(R.id.noItem);
        layout1 = findViewById(R.id.layout1);
        MainButton = findViewById(R.id.MainButton);
        MainButton.setOnClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        CheckOut = findViewById(R.id.Checkout);
        CheckOut.setOnClickListener(this);
        //-------------------------------------------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this,RecyclerView.VERTICAL,false));

        putAllDataIntoRecyclerView();

        checkSize();

    }

    public void checkSize(){
        if (totalPrice.getText() == "0"){
            CheckOut.setClickable(false);
            noItem.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void putAllDataIntoRecyclerView(){

        Query query = FirebaseFirestore.getInstance()
                .collection("Cart")
                .document(userID).collection("Food");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                menuID = new ArrayList<>();
                dataCount = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult()){
                    menuID.add(doc.getString("FoodID"));
                    dataCount.add(String.valueOf(doc.get(("itemCount"))));
                }

                if (menuID.size() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    noItem.setVisibility(View.GONE);
                    Query queryProduct = FirebaseFirestore.getInstance()
                            .collection("Product");

                    queryProduct.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<HomeMainList> coursesArrayList = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()){
                                for (int i = 0;i<menuID.size();i++) {
                                    if (menuID.get(i).equals(doc.getString("FoodID"))){
                                        int itemCount = Integer.parseInt(dataCount.get(i));
                                        String nama = doc.getString("Nama");
                                        String harga = String.valueOf(doc.get("Harga"));
                                        String image = doc.getString("Image");
                                        String numOrder = String.valueOf(doc.get("numOrder"));
                                        String id = doc.getString("FoodID");

                                        String totalItemPrice = String.valueOf(Integer.parseInt(harga));
                                        HomeMainList List = new HomeMainList();

                                        List.setNama(nama);
                                        List.setFoodID(id);
                                        List.setHarga(totalItemPrice);
                                        List.setImage(image);
                                        List.setCountOrder(numOrder);
                                        List.setItemCount(String.valueOf(itemCount));

                                        coursesArrayList.add(List);
                                        break;
                                    }
                                }
                            }
                            listCartMenuAdapter = new ListCartMenuAdapter(coursesArrayList);
                            recyclerView.setAdapter(listCartMenuAdapter);
                        }
                    });

                }else{
                    CheckOut.setClickable(false);
                    noItem.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back){
            finish();
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }else if(v.getId() == R.id.Checkout){
            finish();
            Intent intent = new Intent(Cart.this, SelectTable.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }else if (v.getId() == R.id.MainButton){
            Intent intent = new Intent(Cart.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }
    }
}