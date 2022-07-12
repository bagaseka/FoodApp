package com.bagaseka.foodapp.signinsignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bagaseka.foodapp.main.MainActivity;
import com.example.foodapp.R;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail,inputPassword;
    private TextView SignUp;
    private Button SignIn;
    private AuthViewModel viewModel;
    private Intent moveIntent;
    private FirebaseAuth auth;
    private LinearLayoutCompat ForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();

        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    if (auth.getCurrentUser().isEmailVerified()){
                        Intent moveIntent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(moveIntent);
                    }
                }
            }
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        SignUp = findViewById(R.id.SignUp);
        ForgotPass = findViewById(R.id.ForgotPass);

        SignIn = findViewById(R.id.SignIn);

        SignUp.setOnClickListener(this);
        ForgotPass.setOnClickListener(this);
        SignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SignIn:
                String email = inputEmail.getText().toString();
                String pass = inputPassword.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()){
                    viewModel.signIn(email , pass);
                }
                break;

            case R.id.ForgotPass:
                moveIntent = new Intent(this, com.bagaseka.foodapp.forgot.ForgotPass.class);
                startActivity(moveIntent);
                break;

            case R.id.SignUp:
                moveIntent = new Intent(this, SignUp.class);
                startActivity(moveIntent);
                break;
        }
    }
}