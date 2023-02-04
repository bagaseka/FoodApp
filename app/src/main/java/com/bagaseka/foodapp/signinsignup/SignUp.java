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

import com.bagaseka.foodapp.AdminGenerateQrCode;
import com.example.foodapp.R;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText userEdit,emailEdit,passEdit,retypepassEdit;
    private AuthViewModel viewModel;
    private Button signUpBtn;
    private ImageButton back;
    private Intent moveIntent;
    private LinearLayoutCompat signin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    Intent moveIntent = new Intent(SignUp.this, SignIn.class);
                    startActivity(moveIntent);
                    overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                }
            }
        });

        userEdit = findViewById(R.id.inputName);
        emailEdit = findViewById(R.id.inputEmail);
        passEdit = findViewById(R.id.inputPassword);
        retypepassEdit = findViewById(R.id.retypePass);
        signUpBtn = findViewById(R.id.SignUp);
        signUpBtn.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        signin = findViewById(R.id.Login);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignUp){
            String email = emailEdit.getText().toString();
            String pass = passEdit.getText().toString();
            String user = userEdit.getText().toString();

            if (user.isEmpty()){
                setSnackbar(v, R.color.colorPrimary, "Username can not be empty");
            }else if (email.isEmpty()) {
                setSnackbar(v, R.color.colorPrimary, "Email can not be empty");
            }else if (pass.isEmpty()) {
                setSnackbar(v, R.color.colorPrimary, "Password can not be empty");
            }else if (!retypepassEdit.getText().toString().trim().equals(pass)) {
                setSnackbar(v, R.color.colorPrimary, "Password Not Match");
            }else{
                viewModel.register(v, email , pass , user);
            }
        }else if (v.getId() == R.id.back){
            moveIntent = new Intent(this, SignIn.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }else if (v.getId() == R.id.Login){
            moveIntent = new Intent(this, SignIn.class);
            startActivity(moveIntent);
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
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