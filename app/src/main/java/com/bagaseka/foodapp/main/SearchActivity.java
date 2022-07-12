package com.bagaseka.foodapp.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bagaseka.foodapp.component.adapter.ListMenuAdapter;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_search;
    ImageButton back;
    private RecyclerView nFoodSeachRv;
    private FirestoreRecyclerOptions<HomeMainList> options;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference reference = firestore.collection("Product");
    private FirestoreRecyclerAdapter adapterSearch;
    private Query q;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        edt_search = findViewById(R.id.edt_search);
        nFoodSeachRv = findViewById(R.id.nFoodSeachRv);
        nFoodSeachRv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                q = reference.orderBy("Nama")
                        .startAt(s.toString().trim()).endAt(s.toString().trim() + "\uf8ff");
                showAdapter(q);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    public void showAdapter(Query query){
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
                }).setLifecycleOwner(SearchActivity.this).build();

        adapterSearch = new ListMenuAdapter(options,R.layout.list_card_search);
        nFoodSeachRv.setAdapter(adapterSearch);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back){
            finish();
        }
    }
}