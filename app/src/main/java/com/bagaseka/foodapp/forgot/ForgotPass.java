package com.bagaseka.foodapp.forgot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.foodapp.R;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail;
    private Button send;
    private ImageButton back;
    private AuthViewModel viewModel;
    private FirebaseAuth auth;
    private Intent moveIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        inputEmail = findViewById(R.id.inputEmail);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        send = findViewById(R.id.Send);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back){
            moveIntent = new Intent(this, SignIn.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }else if(v.getId() == R.id.Send){
            viewModel.forgotPassword(inputEmail.getText().toString());
        }
    }
}