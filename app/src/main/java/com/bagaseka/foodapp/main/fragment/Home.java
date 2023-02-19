package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.adapter.ListFavoriteAdapter;
import com.bagaseka.foodapp.component.adapter.ListMenuAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.bagaseka.foodapp.main.Cart;
import com.bagaseka.foodapp.main.Favorite;
import com.bagaseka.foodapp.main.SearchActivity;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView nFoodRecommendRv, nFoodFavoriteRv, nFoodExplore;
    private FirestoreRecyclerOptions<HomeMainList> options, recOptions;
    private FirestoreRecyclerAdapter adapter, adapterRecommend;
    private ImageView cart;
    private TextView favoriteMenu, itemCountCart;
    private ConstraintLayout FavoriteLayout,reccomendLayout;
    private SearchView searchView;
    private LinearLayout filterRice, filterRamen, filterAppetizer, filterDessert, filterDrink;
    private View v;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CarouselView promo;
    private String[] imageCarousel = {"https://firebasestorage.googleapis.com/v0/b/foodappta.appspot.com/o/Banner1-01.png?alt=media&token=081469a1-3611-47b0-83dd-25354835b969"};
    private ListFavoriteAdapter listFavoriteAdapter;
    private String userID;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

        FavoriteLayout = v.findViewById(R.id.FavoriteLayout);
        reccomendLayout = v.findViewById(R.id.reccomendLayout);
        userID = auth.getUid();
        itemCountCart = v.findViewById(R.id.itemCountCart);
        searchView = v.findViewById(R.id.searchView);
        filterRice = v.findViewById(R.id.filterRice);
        filterRamen = v.findViewById(R.id.filterRamen);
        filterAppetizer = v.findViewById(R.id.filterAppetizer);
        filterDessert = v.findViewById(R.id.filterDessert);
        filterDrink = v.findViewById(R.id.filterDrink);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setAutoMeasureEnabled(true);
        setCartItemCount(userID);
        auth = FirebaseAuth.getInstance();
        promo = (CarouselView) v.findViewById(R.id.promo);
        promo.setPageCount(imageCarousel.length);
        promo.setImageListener(imageListener);
        RecyclerViewInitial();
        setRecyclerViewAllMenu("Ramen");
        setRecyclerViewRecommend();
        setRecyclerviewFavorite(userID);
        favoriteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveWithDataIntent = new Intent(getContext(), Favorite.class);
                startActivity(moveWithDataIntent);
                requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            }
        });
        filterRice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewAllMenu("Rice");
            }
        });
        filterRamen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewAllMenu("Ramen");
            }
        });
        filterAppetizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewAllMenu("Appetizer");
            }
        });
        filterDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewAllMenu("Dessert");
            }
        });
        filterDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewAllMenu("Drink");
            }
        });
        cart = v.findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getContext(), Cart.class);
                startActivity(moveIntent);
                requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(moveIntent);
            }
        });
        return v;
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getContext())
                    .load(imageCarousel[position])
                    .centerCrop()
                    .into(imageView);

        }
    };
    public void setRecyclerViewRecommend() {
            FirebaseFirestore.getInstance()
                    .collection("Recommend").document(userID)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException error) {

                            if (document.exists()){
                                reccomendLayout.setVisibility(View.VISIBLE);
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
                                                String id = snapshot.getId();
                                                String numOrder = String.valueOf(snapshot.get("numOrder"));
                                                HomeMainList List = new HomeMainList(nama, harga, image, id, numOrder);
                                                return List;
                                            }
                                        }).setLifecycleOwner(Home.this).build();

                                adapterRecommend = new ListMenuAdapter(options, R.layout.list_card_recommend,getActivity());
                                nFoodRecommendRv.setAdapter(adapterRecommend);
                            }else{
                                reccomendLayout.setVisibility(View.GONE);
                            }
                        }
                    });

    }
    public void setRecyclerViewAllMenu(String kategori) {

        Query query = FirebaseFirestore.getInstance()
                .collection("Product")
                .whereEqualTo("Kategori", kategori);

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
                }).setLifecycleOwner(Home.this).build();

        adapter = new ListMenuAdapter(options, R.layout.list_card_horizontal,getActivity());
        nFoodExplore.setAdapter(adapter);
    }
    public void setRecyclerviewFavorite(String userID){
        Query queryFav = FirebaseFirestore.getInstance()
                .collection("Akun").document(userID).collection("Favorite");

        queryFav.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> menuID = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    menuID.add(doc.getString("FoodID"));
                }
                if (menuID.size() > 0) {
                    FavoriteLayout.setVisibility(View.VISIBLE);

                    Query favQuery = FirebaseFirestore.getInstance()
                            .collection("Product");

                    favQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<HomeMainList> favArrayList = new ArrayList<>();
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
                                        List.setMenuID(menuID);
                                        List.setCountOrder(numOrder);

                                        favArrayList.add(List);
                                        break;
                                    }
                                }
                            }
                            listFavoriteAdapter = new ListFavoriteAdapter(favArrayList,R.layout.list_card,getActivity());
                            nFoodFavoriteRv.setAdapter(listFavoriteAdapter);
                        }
                    });

                } else {
                    FavoriteLayout.setVisibility(View.GONE);
                }
            }
        });
    }
    public void RecyclerViewInitial() {
        nFoodRecommendRv = v.findViewById(R.id.recommendFoodRv);
        nFoodFavoriteRv = v.findViewById(R.id.newFoodRv);
        nFoodExplore = v.findViewById(R.id.DrinkRv);
        favoriteMenu = v.findViewById(R.id.favoriteMenu);

        nFoodRecommendRv.setHasFixedSize(true);
        //nFoodFavoriteRv.setHasFixedSize(true);

        nFoodRecommendRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        nFoodFavoriteRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        nFoodExplore.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }
    public void setCartItemCount(String userID) {

        Query query = FirebaseFirestore.getInstance()
                .collection("Cart").document(userID).collection("Food");

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
}