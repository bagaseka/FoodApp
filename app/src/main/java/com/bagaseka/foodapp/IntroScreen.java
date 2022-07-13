package com.bagaseka.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bagaseka.foodapp.signinsignup.SignUp;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.MainActivity;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.synnapps.carouselview.CarouselView;

public class IntroScreen extends AppCompatActivity implements View.OnClickListener {
    CarouselView customCarouselView;
    private AuthViewModel viewModel;
    private FirebaseAuth auth;

    String[] sampleNetworkImageURLs = {
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image1&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image2&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image3&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image4&txt=350%C3%97150&w=350&h=150",
            "https://placeholdit.imgix.net/~text?txtsize=15&txt=image5&txt=350%C3%97150&w=350&h=150"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    if (auth.getCurrentUser().isEmailVerified()){
                        Intent moveIntent = new Intent(IntroScreen.this, MainActivity.class);
                        startActivity(moveIntent);
                    }
                }
            }
        });

        Button btnSignIn = findViewById(R.id.SignIn);
        Button btnSignUp = findViewById(R.id.SignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignIn){
            Intent moveIntent = new Intent(IntroScreen.this, SignIn.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }else if (v.getId() == R.id.SignUp){
            Intent moveIntent = new Intent(IntroScreen.this, SignUp.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
    }
}
