package com.bagaseka.foodapp.main.fragment.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bagaseka.foodapp.component.adapter.ListCartMenuAdapter;
import com.bagaseka.foodapp.component.adapter.ListCheckOutAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.Cart;
import com.bagaseka.foodapp.main.MainActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkout extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView checkoutRV;
    private TextView subPrice,ppn,total,moreFood;
    private Button Order;
    private ImageButton back;
    private FirebaseAuth auth ;
    private List<String> menuID, dataCount;
    private String userID;
    private ListCheckOutAdapter listCheckOutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        checkoutRV = findViewById(R.id.orderList);
        subPrice = findViewById(R.id.subPrice);
        moreFood = findViewById(R.id.MoreFood);
        ppn = findViewById(R.id.ppn);
        total = findViewById(R.id.total);
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        moreFood.setOnClickListener(this);

        checkoutRV.setLayoutManager(new LinearLayoutManager(Checkout.this,RecyclerView.VERTICAL,false));

        Order = findViewById(R.id.BtnOrder);

        setDataIntoRecyclerview();

    }

    public void setDataIntoRecyclerview(){
        Query queryCartData = FirebaseFirestore.getInstance()
                .collection("Cart")
                .document(userID).collection("Food");

        queryCartData.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                menuID = new ArrayList<>();
                dataCount = new ArrayList<>();

                for (QueryDocumentSnapshot doc : value) {
                    menuID.add(doc.getString("FoodID"));
                    dataCount.add(String.valueOf(doc.get("itemCount")));
                }
                if (menuID.size() > 0){
                    checkoutRV.setVisibility(View.VISIBLE);
                    Query queryProduct = FirebaseFirestore.getInstance()
                            .collection("Product");

                    queryProduct.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            int itemCount = 1;
                            ArrayList<HomeMainList> foodData = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()){
                                for (int i = 0;i<menuID.size();i++) {
                                    if (menuID.get(i).equals(doc.getString("FoodID"))){
                                        itemCount = Integer.parseInt(dataCount.get(i));
                                        String nama = doc.getString("Nama");
                                        String harga = String.valueOf(doc.get("Harga"));
                                        String image = doc.getString("Image");
                                        String id = doc.getString("FoodID");

                                        String totalItemPrice = String.valueOf(Integer.parseInt(harga));
                                        HomeMainList List = new HomeMainList();

                                        List.setNama(nama);
                                        List.setFoodID(id);
                                        List.setHarga(totalItemPrice);
                                        List.setImage(image);
                                        List.setItemCount(String.valueOf(itemCount));

                                        foodData.add(List);
                                        break;
                                    }
                                }
                            }
                            listCheckOutAdapter = new ListCheckOutAdapter(foodData);
                            checkoutRV.setAdapter(listCheckOutAdapter);
                        }
                    });

                    Order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderProgres();
                            finish();
                            Intent intent = new Intent(Checkout.this, MainActivity.class);
                            intent.putExtra("orderSuccess", Boolean.valueOf(true));
                            startActivity(intent);

                        }
                    });
                }else{
                    checkoutRV.setVisibility(View.GONE);
                }
            }
        });
    }

    public void orderProgres(){

        String currentDateTimeString = java.text.DateFormat.getDateInstance().format(new Date());

        FirebaseFirestore.getInstance()
                .collection("Cart").document(userID).collection("Food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot doc : value) {

                    menuID.add(doc.getString("FoodID"));
                    dataCount.add(String.valueOf(doc.get("itemCount")));

                }
            }
        });

        DocumentReference addOrderData = FirebaseFirestore.getInstance()
                .collection("Pesanan").document();

        Query getSize = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .whereEqualTo("UserID", userID);

        getSize.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 1;

                for (DocumentSnapshot doc : task.getResult()){
                    i++;
                }

                Map<String, Object> dataPerson = new HashMap<>();
                dataPerson.put("TableID", "SgdXfy32n1");
                dataPerson.put("Status", true);
                dataPerson.put("UserID", userID);
                dataPerson.put("MyOrderNumber", i);
                dataPerson.put("Date",currentDateTimeString);
                dataPerson.put("OrderID", addOrderData.getId());

                addOrderData.set(dataPerson);
            }
        });


        CollectionReference addFoodData = FirebaseFirestore.getInstance()
                .collection("Pesanan").document(addOrderData.getId())
                .collection("Food");

        for (int i=0;i<menuID.size();i++){

            Map<String, Object> order = new HashMap<>();
            order.put("FoodID",menuID.get(i));
            order.put("itemCount", dataCount.get(i));
            order.put("StatusReview",false);

            addFoodData.document(menuID.get(i)).set(order);

            FirebaseFirestore.getInstance()
                    .collection("Cart").document(userID)
                    .collection("Food").document(menuID.get(i))
                    .delete();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(Checkout.this, Cart.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back){
            finish();
            Intent intent = new Intent(Checkout.this, Cart.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        } else if (v.getId() == R.id.MoreFood){
            finish();
            Intent intent = new Intent(Checkout.this, MainActivity.class);
            startActivity(intent);
        }
    }
}