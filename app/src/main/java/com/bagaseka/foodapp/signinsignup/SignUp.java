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
import android.widget.ImageButton;

import com.example.foodapp.R;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
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
                }
            }
        });

        userEdit = findViewById(R.id.inputName);
        emailEdit = findViewById(R.id.inputEmail);
        passEdit = findViewById(R.id.inputPassword);
        retypepassEdit = findViewById(R.id.retypePass);
        signUpBtn = findViewById(R.id.SignIn);
        signUpBtn.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        signin = findViewById(R.id.Login);
        signin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignIn){
            if (!retypepassEdit.getText().toString().trim().equals(passEdit.getText().toString().trim())){
                retypepassEdit.setError("Password Not Match");

            }else{
                String email = emailEdit.getText().toString();
                String pass = passEdit.getText().toString();
                String user = userEdit.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()){
                    viewModel.register(email , pass , user);
                }
            }
        }else if (v.getId() == R.id.back){
            moveIntent = new Intent(this, SignIn.class);
            startActivity(moveIntent);
        }else if (v.getId() == R.id.Login){
            moveIntent = new Intent(this, SignIn.class);
            startActivity(moveIntent);
        }
    }


}