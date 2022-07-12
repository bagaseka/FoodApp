package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bagaseka.foodapp.main.Favorite;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.fragment.review.MyReview;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;

public class User extends Fragment {

    private AuthViewModel viewModel;
    private ConstraintLayout favoriteLayout,myReview,Logout;

    public User() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);

        myReview = v.findViewById(R.id.myReview);
        myReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyReview.class);
                startActivity(intent);
            }
        });

        favoriteLayout = v.findViewById(R.id.Favorite);
        favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Favorite.class);
                startActivity(intent);
            }
        });
        Logout = v.findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Intent moveIntent2 = new Intent(getActivity(), SignIn.class);
                startActivity(moveIntent2);
            }
        });

        return v;
    }
}