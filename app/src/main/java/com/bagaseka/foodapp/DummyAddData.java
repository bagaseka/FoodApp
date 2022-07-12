package com.bagaseka.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DummyAddData extends AppCompatActivity {
    private DocumentReference ref;
    private EditText nama, harga, desc, image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_add_data);

        nama = (EditText) findViewById(R.id.nama);
        harga = (EditText) findViewById(R.id.harga);
        desc = (EditText) findViewById(R.id.deskripsi);
        image = (EditText) findViewById(R.id.image);

        ref = FirebaseFirestore.getInstance().collection("Product").document();

        Button addData = findViewById(R.id.AddData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaFood = nama.getText().toString();
                int hargaFood = Integer.parseInt(harga.getText().toString());
                String descFood = desc.getText().toString();
                String imageFood = image.getText().toString();

                Map<String, Object> account = new HashMap<>();
                account.put("Nama", namaFood);
                account.put("Harga", hargaFood);
                account.put("Descrip", descFood);
                account.put("Image", imageFood);

                ref.set(account)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DummyAddData.this, "Add Data was success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(DummyAddData.this, "Add Data is Fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}