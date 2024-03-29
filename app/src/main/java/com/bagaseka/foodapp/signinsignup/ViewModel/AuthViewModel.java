package com.bagaseka.foodapp.signinsignup.ViewModel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bagaseka.foodapp.signinsignup.Repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    private AuthenticationRepository repository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthenticationRepository(application);
        userData = repository.getFirebaseUserMutableLiveData();
        loggedStatus = repository.getUserLoggedMutableLiveData();
    }

    public void register(View v, String email, String pass, String user){
        repository.register(v,email, pass, user);
    }
    public void signIn(View v, String email , String pass){
        repository.login(v, email, pass);
    }
    public void forgotPassword(View v, String email){
        repository.forgotPassword(v,email);
    }
    public void signOut(){
        repository.signOut();
    }

}
