package com.bagaseka.foodapp.signinsignup.Repository;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.bagaseka.foodapp.main.MainActivity;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationRepository {
    private Application application;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private MutableLiveData<Boolean> userLoggedMutableLiveData;
    private FirebaseAuth auth;
    private DocumentReference ref;

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedMutableLiveData() {
        return userLoggedMutableLiveData;
    }

    public AuthenticationRepository(Application application){
        this.application = application;
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLoggedMutableLiveData = new MutableLiveData<>();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
        }
    }

    public void register(View v, String email , String pass , String user){
        auth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if (task.isSuccessful()){
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
                    //auth.getCurrentUser().sendEmailVerification();

                    ref = FirebaseFirestore.getInstance().collection("Akun").document(auth.getUid().toString());

                    Map<String, Object> account = new HashMap<>();
                    account.put("name", user);
                    account.put("image", "null");

                    ref.set(account)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        setSnackbar(v, R.color.Green, "Register was success!");
                                    }else{
                                        setSnackbar(v, R.color.colorPrimary, "Register is Fail!");
                                    }
                                }
                            });
                }
            }
        });
    }

    public void login(String email , String pass){

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
//                    if (auth.getCurrentUser().isEmailVerified()){
//                        firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
//                    }else{
//                        Toast.makeText(application, "Your Email Need Verification, Please Check Your Email!", Toast.LENGTH_SHORT).show();
//                    }
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());

                }
            }
        });
    }

    public void forgotPassword(View v, String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setSnackbar(v, R.color.Green, "Reset Password Was Sent To Email, Please Check Your Email!");
                }
            }
        });
    }

    public void signOut(){
        auth.signOut();
        userLoggedMutableLiveData.postValue(true);
    }

    public void setSnackbar(View v, int color, String text) {
        Snackbar snackbar = Snackbar.make(v, text,
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(
                ContextCompat.getColor(v.getContext(), color));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
