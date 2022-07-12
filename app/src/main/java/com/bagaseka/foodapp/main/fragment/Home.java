package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bagaseka.foodapp.component.adapter.ListFavoriteAdapter;
import com.bagaseka.foodapp.main.Favorite;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.component.adapter.ListMenuAdapter;
import com.bagaseka.foodapp.main.Cart;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.bagaseka.foodapp.main.SearchActivity;
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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView nFoodRecommendRv, nFoodFavoriteRv, nFoodExplore;
    private FirestoreRecyclerOptions<HomeMainList> options,favOption;
    private FirestoreRecyclerAdapter adapter, adapterRecommend,adapterFav;
    private ImageView cart;
    private TextView favoriteMenu,itemCountCart;
    private ConstraintLayout FavoriteLayout;
    private SearchView searchView;
    private List<String> menuID;
    private View v;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CarouselView promo;
    private String[] imageCarousel = {"https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Serba-Ekslusif-768x255.jpg","https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/03/Banner_Bagi-bagi-Semangat-Ramadan-768x256.jpg","https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Bebas-Ongkir-768x256.jpg","https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Kotak-Kejutan-768x224.jpg","https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Gajian-Ekstra-Ramadan-768x278.jpg"};

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        FavoriteLayout = v.findViewById(R.id.FavoriteLayout);
        String userID = auth.getUid();
        itemCountCart = v.findViewById(R.id.itemCountCart);
        searchView = v.findViewById(R.id.searchView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
        linearLayoutManager.setOrientation( LinearLayoutManager.VERTICAL );
        linearLayoutManager.setAutoMeasureEnabled( true );

        setCartItemCount(userID);

        auth = FirebaseAuth.getInstance();

        promo = (CarouselView) v.findViewById(R.id.promo);
        promo.setPageCount(imageCarousel.length);
        promo.setImageListener(imageListener);

        RecyclerView(userID);

        favoriteMenu= v.findViewById(R.id.favoriteMenu);
        favoriteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveWithDataIntent = new Intent(getContext(), Favorite.class);
                startActivity(moveWithDataIntent);
            }
        });

        cart = v.findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getContext(), Cart.class);
                startActivity(moveIntent);
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
                    .into(imageView);

        }
    };
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapterRecommend.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapterRecommend.stopListening();
    }

    public void RecyclerView(String userID){
        RecyclerViewInitial();

        Query query = FirebaseFirestore.getInstance()
                .collection("Product");

        options = new FirestoreRecyclerOptions.Builder<HomeMainList>()
                .setQuery(query, new SnapshotParser<HomeMainList>() {
                    @Override
                    public HomeMainList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        /*Write the process you want to do when taking a snapshot*/
                        String nama = snapshot.getString("Nama");
                        String harga = String.valueOf(snapshot.get("Harga"));
                        String image = snapshot.getString("Image");
                        String id = snapshot.getId();
                        HomeMainList List = new HomeMainList(nama,harga,image,id);
                        return List;
                    }
                }).build();

        Query queryFav = FirebaseFirestore.getInstance()
                .collection("Akun").document(userID).collection("Favorite");

        queryFav.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String>  MenuID = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    MenuID.add(doc.getString("FoodID"));
                }
                if (MenuID.size() > 0){
                    FavoriteLayout.setVisibility(View.VISIBLE);

                    Query favQuery = FirebaseFirestore.getInstance()
                            .collection("Product");

                    favOption = new FirestoreRecyclerOptions.Builder<HomeMainList>()
                            .setQuery(favQuery, new SnapshotParser<HomeMainList>() {
                                @Override
                                public HomeMainList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                    /*Write the process you want to do when taking a snapshot*/
                                    HomeMainList List = new HomeMainList();
                                    for (int i=0; i<MenuID.size();i++){
                                        if (MenuID.get(i).equals(snapshot.getId())){
                                            String nama = snapshot.getString("Nama");
                                            String harga = String.valueOf(snapshot.get("Harga"));
                                            String image = snapshot.getString("Image");
                                            String id = snapshot.getId();

                                            List.setFoodID(id);
                                            List.setNama(nama);
                                            List.setImage(image);
                                            List.setHarga(harga);
                                            List.setMenuID(MenuID);
                                        }
                                    }
                                    return List;
                                }
                            }).setLifecycleOwner(Home.this).build();

                    adapterFav = new ListFavoriteAdapter(favOption,R.layout.list_card);
                    nFoodFavoriteRv.setAdapter(adapterFav);
                }else{
                    FavoriteLayout.setVisibility(View.GONE);
                }
            }
        });

        adapter = new ListMenuAdapter(options,R.layout.list_card_horizontal);
        adapterRecommend = new ListMenuAdapter(options,R.layout.list_card_recommend);

        nFoodRecommendRv.setAdapter(adapterRecommend);
        nFoodExplore.setAdapter(adapter);
    }


    public void RecyclerViewInitial(){
        nFoodRecommendRv = v.findViewById(R.id.recommendFoodRv);
        nFoodFavoriteRv = v.findViewById(R.id.newFoodRv);
        nFoodExplore = v.findViewById(R.id.DrinkRv);

        nFoodRecommendRv.setHasFixedSize(true);
        //nFoodFavoriteRv.setHasFixedSize(true);

        nFoodRecommendRv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        nFoodFavoriteRv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        nFoodExplore.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

    }
    public void setCartItemCount(String userID){

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