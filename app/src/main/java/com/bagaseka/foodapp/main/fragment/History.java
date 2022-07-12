package com.bagaseka.foodapp.main.fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bagaseka.foodapp.component.adapter.OrderListChildAdapter;
import com.bagaseka.foodapp.component.adapter.OrderListParentAdapter;
import com.bagaseka.foodapp.component.model.ChildItem;
import com.bagaseka.foodapp.component.model.ParentItem;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class History extends Fragment {

    private View v;
    private RecyclerView parentRecyclerViewItem;
    private FirebaseAuth auth;
    private String userID;
    private final OrderListParentAdapter parentAdapter = new OrderListParentAdapter();

    public History() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_history, container, false);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        parentRecyclerViewItem = v.findViewById(R.id.listOrderRv);
        parentRecyclerViewItem.setLayoutManager(new LinearLayoutManager(
                getContext(),RecyclerView.VERTICAL,false));

        ParentItemList(new MyCallbackParent() {
            @Override
            public void onCallback(List<ParentItem> parentItemList) {
                History.this.setRecyclerView(parentItemList);
            }
        });

        return v;
    }

    private void setRecyclerView(List<ParentItem> parentItemList) {
        parentAdapter.setParentItemList(parentItemList);
        parentRecyclerViewItem.setAdapter(parentAdapter);
    }

    private void ParentItemList(MyCallbackParent MyCallbackParent) {

        Query query = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .orderBy("MyOrderNumber", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                ArrayList<ParentItem> parentItemList = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult()) {
                    if (doc.getString("UserID").equals(userID)){
                        ParentItem item = new ParentItem();
                        String date = doc.getString("OrderID");
                        item.setDate(date);

                        parentItemList.add(item);
                    }
                }

                ChildItemList(new MyCallbackChild() {
                    @Override
                    public void onCallback(List<ChildItem> childItemList) {
                        OrderListChildAdapter childAdapter = new OrderListChildAdapter();
                        childAdapter.setChildItemList(childItemList);
                        parentAdapter.addChildAdapter(childAdapter);
                    }
                });
                MyCallbackParent.onCallback(parentItemList);
            }
        });
    }

    private void ChildItemList(MyCallbackChild myCallback) {
        Query queryMenu = FirebaseFirestore.getInstance()
                .collection("Pesanan")
                .whereEqualTo("UserID" , userID)
                .orderBy("MyOrderNumber", Query.Direction.DESCENDING);

        queryMenu.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Boolean progress;
                String key;
                if (value == null) {
                    return;
                }
                for (QueryDocumentSnapshot doc : value){
                    progress = doc.getBoolean("Status");
                    key = doc.getId();
                    Query queryOrderDataMenu = FirebaseFirestore.getInstance()
                            .collection("Pesanan")
                            .document(doc.getId())
                            .collection("Food");

                    Boolean finalProgress = progress;
                    String finalKey = key;
                    queryOrderDataMenu.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            ArrayList<String>  orderFoodID = new ArrayList<>();
                            ArrayList<String>  menuCount = new ArrayList<>();
                            ArrayList<Boolean>  statusReview = new ArrayList<>();

                            if (value == null) {
                                return;
                            }
                            for (QueryDocumentSnapshot doc : value){
                                orderFoodID.add(doc.getString("FoodID"));
                                menuCount.add(doc.getString("itemCount"));
                                statusReview.add(doc.getBoolean("StatusReview"));
                            }

                            if (orderFoodID.size() > 0){

                                Query queryFoodData = FirebaseFirestore.getInstance()
                                        .collection("Product")
                                        .whereIn("FoodID", orderFoodID);

                                queryFoodData.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        ArrayList<ChildItem> childItemList = new ArrayList<>();
                                        int index = 0;
                                        if (value == null) {
                                            return;
                                        }
                                        for (QueryDocumentSnapshot doc : value){
                                            ChildItem item = new ChildItem();

                                            int itemCount = Integer.valueOf(menuCount.get(index));
                                            Boolean reviewStat = statusReview.get(index);

                                            String nama = doc.getString("Nama");
                                            String harga = String.valueOf(doc.get("Harga"));
                                            String image = doc.getString("Image");
                                            String id = doc.getId();

                                            item.setReviewStatus(reviewStat);
                                            item.setProgress(finalProgress);
                                            item.setOrderId(finalKey);

                                            item.setCounter(itemCount);
                                            item.setFoodID(id);
                                            item.setNama(nama);

                                            item.setImage(image);
                                            item.setHarga(Integer.parseInt(harga));

                                            childItemList.add(item);
                                            index++;
                                        }
                                        myCallback.onCallback(childItemList);
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });


    }

    public interface MyCallbackChild {
        void onCallback(List<ChildItem> childItemList);
    }

    public interface MyCallbackParent {
        void onCallback(List<ParentItem> parentItemList);
    }
}

