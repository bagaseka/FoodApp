package com.bagaseka.foodapp;

import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission_group.CAMERA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bagaseka.foodapp.main.recommended.TestRecommend;
import com.bagaseka.foodapp.signinsignup.SignUp;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.MainActivity;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.synnapps.carouselview.CarouselView;

public class IntroScreen extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        Button btnSignIn = findViewById(R.id.SignIn);
        Button btnSignUp = findViewById(R.id.SignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with camera functionality
                    // ...
                } else {
                    // permission was denied, close the app

                    finish();
                }
                return;
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
//                    if (auth.getCurrentUser().isEmailVerified()){
//                        Intent moveIntent = new Intent(IntroScreen.this, MainActivity.class);
//                        startActivity(moveIntent);
//                    }
                    Intent moveIntent = new Intent(IntroScreen.this, MainActivity.class);
                    startActivity(moveIntent);
                    overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                }
            }
        });
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
