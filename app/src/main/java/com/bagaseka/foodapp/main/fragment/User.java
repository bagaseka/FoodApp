package com.bagaseka.foodapp.main.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bagaseka.foodapp.main.Favorite;
import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.bagaseka.foodapp.main.fragment.review.MyReview;
import com.bagaseka.foodapp.signinsignup.SignIn;
import com.bagaseka.foodapp.signinsignup.ViewModel.AuthViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class User extends Fragment implements View.OnClickListener {

    private AuthViewModel viewModel;
    private ConstraintLayout favoriteLayout,myReview,Logout,changePassword;
    private TextView verifEmail,nameUser,emailUser;
    private FirebaseAuth auth;
    private ImageView imageUser;
    private ListenerRegistration dataUserRegistration = null;

    public User() {
        // Required empty public constructor
    }

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        uploadImage(result);
                    }
                }
            });

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

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("Akun").document(auth.getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    nameUser.setText(documentSnapshot.getString("name"));
                    // Check if the value is present in the document data
                    if (documentSnapshot.contains("image")) {
                        Object myValue = documentSnapshot.get("image");
                        if (myValue != null) {
                            Glide.with(getContext())
                                    .load(myValue)
                                    .circleCrop()
                                    .into(imageUser);
                        } else {
                            Glide.with(getContext())
                                    .load("https://firebasestorage.googleapis.com/v0/b/foodappta.appspot.com/o/man.png?alt=media&token=d2aec9d6-06ac-4c64-acd3-38d3b5c24351")
                                    .circleCrop()
                                    .into(imageUser);
                            }
                    } else {
                        // The value is not present in the document data
                    }
                } else {
                    // The document does not exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
            }
        });

        viewModel = new ViewModelProvider(this ).get(AuthViewModel.class);

        verifEmail = v.findViewById(R.id.verifEmail);
        if (auth.getCurrentUser().isEmailVerified()){
            verifEmail.setVisibility(View.GONE);
        }else{
            verifEmail.setVisibility(View.VISIBLE);
        }
        verifEmail.setOnClickListener(this);
        myReview = v.findViewById(R.id.myReview);
        myReview.setOnClickListener(this);
        favoriteLayout = v.findViewById(R.id.Favorite);
        favoriteLayout.setOnClickListener(this);
        Logout = v.findViewById(R.id.logout);
        Logout.setOnClickListener(this);
        imageUser.setOnClickListener(this);

        changePassword = v.findViewById(R.id.changePasswordLayout);
        changePassword.setOnClickListener(this);

        return v;
    }

    // This method uploads the image to Firebase Storage and Firestore
    private void uploadImage(Uri imageUri) {
        // Get a reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Get a reference to the location where the image will be stored in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register a listener to track the upload progress
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            }
        });

        // Register a listener to get the download URL of the uploaded image
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Get the download URL of the uploaded image
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    addImageToFirestore(downloadUri);
                }
            }
        });
    }

    // This method adds the image URL to Firestore
    private void addImageToFirestore(Uri imageUrl) {
        // Get a reference to the Firestore collection where the images are stored
        DocumentReference imageDoc = FirebaseFirestore.getInstance()
                .collection("Akun").document(auth.getUid());

        // Create a map with the image data
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("image", imageUrl.toString());

        // Set the data of the new document
        imageDoc.update(imageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // The image URL has been added to Firestore
                        //Log.d(TAG, "Image URL added to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        //Log.e(TAG, "Error adding image URL to Firestore", e);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (dataUserRegistration != null) dataUserRegistration.remove();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageUser){
            mGetContent.launch("image/*");

        }else if(v.getId() == R.id.Favorite){
            Intent intent = new Intent(getActivity(), Favorite.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }else if (v.getId() == R.id.myReview){
            Intent intent = new Intent(getActivity(), MyReview.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }else if (v.getId() == R.id.logout){
            viewModel.signOut();
            Intent moveIntent2 = new Intent(getActivity(), SignIn.class);
            startActivity(moveIntent2);
            requireActivity().overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
        }else if (v.getId() == R.id.verifEmail){
            auth.getCurrentUser().sendEmailVerification();
            Toast.makeText(getContext(), "Register was success", Toast.LENGTH_SHORT).show();
        }else if (v.getId() == R.id.changePasswordLayout){
            FirebaseUser currentUser = auth.getCurrentUser();
            auth.sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setSnackbar(v, R.color.Green, "Check On Your Email");
                            } else {
                                setSnackbar(v, R.color.colorPrimary, "Reset Password Is Error");
                            }
                        }
                    });

        }
    }
    public void setSnackbar(View v, int color, String text) {
        Snackbar snackbar = Snackbar.make(v, text,
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(
                ContextCompat.getColor(getContext(), color));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}