package com.bagaseka.foodapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminGenerateQrCode extends AppCompatActivity {

    private EditText inputNameTable;
    private Button generateQR,downloadQR;
    private ImageView imageQR;
    private MultiFormatWriter writer = new MultiFormatWriter();
    Bitmap bitmap;
    String name, id;
    FirebaseStorage storage;
    StorageReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_generate_qr_code);

        inputNameTable = findViewById(R.id.inputNameTable);
        generateQR = findViewById(R.id.generateQR);
        imageQR = findViewById(R.id.imageQR);
        downloadQR = findViewById(R.id.downloadQR);

        storage = FirebaseStorage.getInstance();
        mReference = storage.getReference();

        DocumentReference addTable = FirebaseFirestore.getInstance()
                .collection("Meja").document();


        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name =  inputNameTable.getText().toString();
                id = addTable.getId();
                try {
                    BitMatrix bitMatrix = writer.encode(id, BarcodeFormat.QR_CODE,600,600);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageQR.setImageBitmap(bitmap);

                    Map<String, Object> Table = new HashMap<>();
                    Table.put("TableID", id);
                    Table.put("TableName", name);

                    addTable.set(Table);

                    downloadQR.setVisibility(View.VISIBLE);

                } catch (WriterException e) {
                    Toast.makeText(AdminGenerateQrCode.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        downloadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = getImageUri(AdminGenerateQrCode.this, bitmap, id);
                StorageReference storageReference = mReference.child("table/"+ name + ".jpg");
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(AdminGenerateQrCode.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminGenerateQrCode.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }
}