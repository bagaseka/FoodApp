package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bagaseka.foodapp.main.Favorite;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.fragment.review.MyReview;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class User extends Fragment {

    private AuthViewModel viewModel;
    private ConstraintLayout favoriteLayout,myReview,Logout;
    private TextView verifEmail,nameUser,emailUser;
    private FirebaseAuth auth;
    private ShapeableImageView imageUser;

    private ListenerRegistration dataUserRegistration = null;

    public User() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        auth = FirebaseAuth.getInstance();
        nameUser = v.findViewById(R.id.nameUser);
        imageUser = v.findViewById(R.id.imageUser);
        emailUser = v.findViewById(R.id.emailUser);
        emailUser.setText(auth.getCurrentUser().getEmail());

        dataUserRegistration = FirebaseFirestore.getInstance()
                .collection("Akun").document(auth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) return;

                        nameUser.setText(value.getString("name"));

                        if (!value.getString("image").equals("null")){
                            Glide.with(getContext())
                                    .load(value.getString("image"))
                                    .centerCrop()
                                    .into(imageUser);
                        }
                    }
                });

        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);

        verifEmail = v.findViewById(R.id.verifEmail);
        if (auth.getCurrentUser().isEmailVerified()){
            verifEmail.setVisibility(View.GONE);
        }else{
            verifEmail.setVisibility(View.VISIBLE);
        }
        verifEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getCurrentUser().sendEmailVerification();
                Toast.makeText(getContext(), "Register was success", Toast.LENGTH_SHORT).show();
            }
        });

        myReview = v.findViewById(R.id.myReview);
        myReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyReview.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            }
        });

        favoriteLayout = v.findViewById(R.id.Favorite);
        favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Favorite.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            }
        });
        Logout = v.findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Intent moveIntent2 = new Intent(getActivity(), SignIn.class);
                startActivity(moveIntent2);
                requireActivity().overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        if (dataUserRegistration != null) dataUserRegistration.remove();
        super.onDestroy();
    }
}