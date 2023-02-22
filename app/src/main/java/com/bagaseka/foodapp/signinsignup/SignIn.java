package com.bagaseka.foodapp.signinsignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bagaseka.foodapp.IntroScreen;
import com.bagaseka.foodapp.main.MainActivity;
import com.bagaseka.foodapp.main.recommended.TestRecommend;
import com.example.foodapp.R;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail,inputPassword;
    private TextView SignUp;
    private Button SignIn;
    private ImageButton Back;
    private AuthViewModel viewModel;
    private Intent moveIntent;
    private FirebaseAuth auth;
    private LinearLayoutCompat ForgotPass;
    private TextInputLayout emailLy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        autoLogin();
        initialization();
        Back.setOnClickListener(this);
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
                    viewModel.signIn(v,email , pass);
                }else if (email.isEmpty()){
                    setSnackbar(v, R.color.colorPrimary, "Email can not be empty");
                }else if (!email.isEmpty() && pass.isEmpty()){
                    setSnackbar(v, R.color.colorPrimary, "Password can not be empty");
                }
                break;

            case R.id.ForgotPass:
                moveIntent = new Intent(this, com.bagaseka.foodapp.forgot.ForgotPass.class);
                startActivity(moveIntent);
                overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;

            case R.id.SignUp:
                moveIntent = new Intent(this, SignUp.class);
                startActivity(moveIntent);
                overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                break;

            case R.id.back:
                moveIntent = new Intent(this, IntroScreen.class);
                startActivity(moveIntent);
                overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
                break;
        }
    }
    public void initialization(){
        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        SignUp = findViewById(R.id.SignUp);
        ForgotPass = findViewById(R.id.ForgotPass);
        SignIn = findViewById(R.id.SignIn);
        Back = findViewById(R.id.back);

        emailLy = findViewById(R.id.emailLy);
    }
    public void autoLogin(){
        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
//                    if (auth.getCurrentUser().isEmailVerified()){
//                        Intent moveIntent = new Intent(SignIn.this, MainActivity.class);
//                        startActivity(moveIntent);
//                        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
//                    }
                    Intent moveIntent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(moveIntent);
                    overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                }
            }
        });
    }
    public void setSnackbar(View v, int color, String text) {
        Snackbar snackbar = Snackbar.make(v, text,
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(
                ContextCompat.getColor(this, color));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}