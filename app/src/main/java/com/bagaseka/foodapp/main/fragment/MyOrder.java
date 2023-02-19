package com.bagaseka.foodapp.main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.adapter.OrderListChildAdapter;
import com.bagaseka.foodapp.component.model.ChildItem;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyOrder extends Fragment {

    private final String TAG = "MyOrder";
    private String userID;
    private final OrderListChildAdapter childAdapter = new OrderListChildAdapter();
    private ListenerRegistration keysListener = null;
    private Set<ListenerRegistration> descListener = new HashSet<>();
    private ListenerRegistration productsListener = null;
    public MyOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        RecyclerView parentRecyclerViewItem = view.findViewById(R.id.listOrderRv);
        parentRecyclerViewItem.setLayoutManager(new LinearLayoutManager(
                getContext(), RecyclerView.VERTICAL, false));

        parentRecyclerViewItem.setAdapter(childAdapter);
        getHistoryData(childAdapter::submitItem);

        return view;
    }

    private void getHistoryData(OnDataCallback callback) {
        getHistoryKeys((key, progress) -> getHistoryFoodDesc(key, (orderFoodId, menuCounts, statusReviews) -> {
            Log.d("TestCounter", menuCounts.toString());
            Log.d("TestStatusReviews", menuCounts.toString());

            getProducts(items -> {
                for (int i = 0; i < orderFoodId.size(); i++) {
                    for (int j = 0; j < items.size(); j++) {
                        if (items.get(j).getFoodID().equals(orderFoodId.get(i))) {
                            Log.d(TAG, "index: " + j + " orderFoodId: " + orderFoodId.get(i) + "products: " + items.get(i));
                            Log.d("TestCounter index", menuCounts.get(i));

                            ChildItem item = new ChildItem();

                            item.setFoodID(items.get(j).getFoodID());
                            item.setNama(items.get(j).getNama());
                            item.setImage(items.get(j).getImage());
                            item.setHarga(items.get(j).getHarga());
                            item.setOrderId(key);
                            item.setProgress(progress);
                            item.setCounter(Integer.parseInt(menuCounts.get(i)));
                            item.setReviewStatus(statusReviews.get(i));

                            callback.onCallback(item);
                            break;
                        }
                    }
                }
            });
        }));
    }
    private void getHistoryKeys(GetKeysCallback callback) {
        Query queryMenu = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .whereEqualTo("UserID", userID)
                .orderBy("MyOrderNumber", Query.Direction.DESCENDING);

        keysListener = queryMenu.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;

                for (QueryDocumentSnapshot doc : value) {
                    String key = doc.getId();
                    Boolean progress = doc.getBoolean("Status");

                    callback.keys(key, progress);
                }
            }
        });
    }
    private void getHistoryFoodDesc(String id, GetFoodDescCallback callback) {
        Query queryOrderDataMenu = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .document(id)
                .collection("Food");

        descListener.add(queryOrderDataMenu.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value == null) return;

                ArrayList<String> orderFoodID = new ArrayList<>();
                ArrayList<String> menuCount = new ArrayList<>();
                ArrayList<Boolean> statusReview = new ArrayList<>();

                for (QueryDocumentSnapshot doc : value) {
                    orderFoodID.add(doc.getString("FoodID"));
                    menuCount.add(doc.getString("itemCount"));
                    statusReview.add(doc.getBoolean("StatusReview"));
                }

                if (orderFoodID.isEmpty()) return;

                callback.data(
                        orderFoodID,
                        menuCount,
                        statusReview
                );
            }

        }));
    }
    private void getProducts(GetProductsCallback callback) {
        Query queryFoodData = FirebaseFirestore.getInstance()
                .collection("Product");

        productsListener = queryFoodData.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;

                ArrayList<ChildItem> childItemList = new ArrayList<>();

                for (int index = 0; index < value.size(); index++) {
                    ChildItem item = new ChildItem();

                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) value.getDocuments().get(index);

                    String id = doc.getId();
                    String nama = doc.getString("Nama");
                    String harga = String.valueOf(doc.get("Harga"));
                    String image = doc.getString("Image");

                    item.setFoodID(id);
                    item.setNama(nama);
                    item.setImage(image);
                    item.setHarga(Integer.parseInt(harga));

                    childItemList.add(item);
                }

                Log.d(TAG, "child: " + childItemList);

                callback.data(childItemList);
            }
        });
    }

    @Override
    public void onDestroyView() {
        keysListener.remove();
        productsListener.remove();

        if (!descListener.isEmpty()) {
            for (ListenerRegistration registration : descListener) {
                registration.remove();
            }
        }

        super.onDestroyView();
    }
    public interface OnDataCallback {
        void onCallback(ChildItem item);
    }
    private interface GetProductsCallback {
        void data(List<ChildItem> items);
    }
    private interface GetFoodDescCallback {
        void data(List<String> orderFoodId, List<String> menuCounts, List<Boolean> statusReviews);
    }
    private interface GetKeysCallback {
        void keys(String key, Boolean progress);
    }

}

