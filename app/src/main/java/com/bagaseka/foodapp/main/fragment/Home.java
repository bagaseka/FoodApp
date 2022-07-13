package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.os.Bundle;
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
    private FirestoreRecyclerOptions<HomeMainList> options, favOption, recOptions;
    private FirestoreRecyclerAdapter adapter, adapterRecommend, adapterFav;
    private ImageView cart;
    private TextView favoriteMenu, itemCountCart;
    private ConstraintLayout FavoriteLayout;
    private SearchView searchView;
    private LinearLayout filterRice, filterRamen, filterAppetizer, filterDessert, filterDrink;
    private View v;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CarouselView promo;
    private String[] imageCarousel = {"https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Serba-Ekslusif-768x255.jpg", "https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/03/Banner_Bagi-bagi-Semangat-Ramadan-768x256.jpg", "https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Bebas-Ongkir-768x256.jpg", "https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Kotak-Kejutan-768x224.jpg", "https://ecs7-tokopedia-net.cdn.ampproject.org/i/s/ecs7.tokopedia.net/blog-tokopedia-com/uploads/2020/04/Banner_Gajian-Ekstra-Ramadan-768x278.jpg"};

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
                    .into(imageView);

        }
    };

    public void setRecyclerViewRecommend() {
        Query queryRecommend = FirebaseFirestore.getInstance()
                .collection("Product");

        recOptions = new FirestoreRecyclerOptions.Builder<HomeMainList>()
                .setQuery(queryRecommend, new SnapshotParser<HomeMainList>() {
                    @Override
                    public HomeMainList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        /*Write the process you want to do when taking a snapshot*/
                        String nama = snapshot.getString("Nama");
                        String harga = String.valueOf(snapshot.get("Harga"));
                        String image = snapshot.getString("Image");
                        String id = snapshot.getId();
                        HomeMainList List = new HomeMainList(nama, harga, image, id);
                        return List;
                    }
                }).setLifecycleOwner(Home.this).build();

        adapterRecommend = new ListMenuAdapter(options, R.layout.list_card_recommend);
        nFoodRecommendRv.setAdapter(adapterRecommend);
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
                        String id = snapshot.getId();
                        HomeMainList List = new HomeMainList(nama, harga, image, id);
                        return List;
                    }
                }).setLifecycleOwner(Home.this).build();

        adapter = new ListMenuAdapter(options, R.layout.list_card_horizontal);
        nFoodExplore.setAdapter(adapter);
    }

    public void setRecyclerviewFavorite(String userID) {
        Query queryFav = FirebaseFirestore.getInstance()
                .collection("Akun").document(userID).collection("Favorite");

        queryFav.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> MenuID = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    MenuID.add(doc.getString("FoodID"));
                }
                if (MenuID.size() > 0) {
                    FavoriteLayout.setVisibility(View.VISIBLE);

                    Query favQuery = FirebaseFirestore.getInstance()
                            .collection("Product")
                            .whereIn("FoodID", MenuID);

                    favOption = new FirestoreRecyclerOptions.Builder<HomeMainList>()
                            .setQuery(favQuery, new SnapshotParser<HomeMainList>() {
                                @Override
                                public HomeMainList parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                    /*Write the process you want to do when taking a snapshot*/
                                    HomeMainList List = new HomeMainList();
                                    String nama = snapshot.getString("Nama");
                                    String harga = String.valueOf(snapshot.get("Harga"));
                                    String image = snapshot.getString("Image");
                                    String id = snapshot.getId();

                                    List.setFoodID(id);
                                    List.setNama(nama);
                                    List.setImage(image);
                                    List.setHarga(harga);
                                    List.setMenuID(MenuID);
                                    return List;
                                }
                            }).setLifecycleOwner(Home.this).build();

                    adapterFav = new ListFavoriteAdapter(favOption, R.layout.list_card);
                    nFoodFavoriteRv.setAdapter(adapterFav);
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