package com.bagaseka.foodapp.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bagaseka.foodapp.component.adapter.ListCartMenuAdapter;
import com.bagaseka.foodapp.component.adapter.ListFavoriteAdapter;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.adapter.ListMenuAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favorite extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView nFoodFavoriteRv;
    private FirebaseAuth auth;
    private ImageButton back;
    private ImageView cart;
    private TextView notFound,itemCountCart;

    private ListFavoriteAdapter listFavoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        auth = FirebaseAuth.getInstance();
        notFound = findViewById(R.id.notFound);
        itemCountCart = findViewById(R.id.itemCountCart);
        cart = findViewById(R.id.cart);
        cart.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        String userID = auth.getUid();
        RecyclerViewInitial();
        setCartItemCount(userID);

        Query queryFav = FirebaseFirestore.getInstance()
                .collection("Akun").document(userID)
                .collection("Favorite");

        queryFav.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String>  menuID = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    menuID.add(doc.getString("FoodID"));
                }
                if (menuID.size() > 0){

                    nFoodFavoriteRv.setVisibility(View.VISIBLE);
                    Query favQuery = FirebaseFirestore.getInstance()
                            .collection("Product");

                    favQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<HomeMainList> menuFavList = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()){
                                for (int i = 0;i<menuID.size();i++) {
                                    if (menuID.get(i).equals(doc.getString("FoodID"))){
                                        String nama = doc.getString("Nama");
                                        String harga = String.valueOf(doc.get("Harga"));
                                        String image = doc.getString("Image");
                                        String numOrder = String.valueOf(doc.get("numOrder"));
                                        String id = doc.getId();
                                        HomeMainList List = new HomeMainList();

                                        List.setFoodID(id);
                                        List.setNama(nama);
                                        List.setImage(image);
                                        List.setHarga(harga);
                                        List.setCountOrder(numOrder);
                                        List.setMenuID(menuID);

                                        menuFavList.add(List);
                                        break;
                                    }
                                }
                            }
                            listFavoriteAdapter = new ListFavoriteAdapter(
                                    menuFavList,R.layout.list_card_horizontal,Favorite.this
                            );
                            nFoodFavoriteRv.setAdapter(listFavoriteAdapter);
                        }
                    });
                }else{
                    nFoodFavoriteRv.setVisibility(View.GONE);
                }
            }
        });

    }


    public void RecyclerViewInitial(){
        nFoodFavoriteRv = findViewById(R.id.favoriteMenuRv);
        nFoodFavoriteRv.setLayoutManager(new LinearLayoutManager(Favorite.this,RecyclerView.VERTICAL,false));
    }

    public void setCartItemCount(String userID){

        Query query = FirebaseFirestore.getInstance()
                .collection("Akun").document(userID).collection("Cart");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int totalItemCount = 0;
                for (QueryDocumentSnapshot doc : value) {

                    totalItemCount += Integer.parseInt(String.valueOf(doc.get("itemCount")));

                }

                itemCountCart.setText(String.valueOf(totalItemCount));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }else if (v.getId() == R.id.cart) {
            Intent moveIntent = new Intent(v.getContext(), Cart.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
    }
}