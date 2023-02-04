package com.bagaseka.foodapp.main.fragment.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EditUser extends AppCompatActivity {

    EditText inputName,birthDate;
    ImageView imageUser;
    Spinner gender;
    Button update;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        inputName = findViewById(R.id.inputName);
        birthDate = findViewById(R.id.birthDate);
        imageUser = findViewById(R.id.imageUser);
        gender = findViewById(R.id.gender);
        update = findViewById(R.id.update);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void updateData(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection("Akun").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                inputName.setText(value.getString("name"));
                if (!value.getString("image").equals("null")){
                    Glide.with(EditUser.this)
                            .load(value.getString("image"))
                            .centerCrop()
                            .into(imageUser);
                }

            }
        });
    }
}