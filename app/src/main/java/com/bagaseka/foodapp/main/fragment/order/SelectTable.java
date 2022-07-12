package com.bagaseka.foodapp.main.fragment.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.foodapp.R;

public class SelectTable extends AppCompatActivity {

    public static final String FOOD_ID = "food_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_table);

        String foodID = getIntent().getStringExtra(FOOD_ID);
    }
}