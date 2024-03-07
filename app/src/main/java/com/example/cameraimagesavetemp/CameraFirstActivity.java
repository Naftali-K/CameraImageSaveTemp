package com.example.cameraimagesavetemp;

import static com.example.cameraimagesavetemp.MainActivity.permissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 *  https://youtu.be/CYRXXOM3aGI?si=lRIUil3qYARkCpfi - video lesson
 */

public class CameraFirstActivity extends AppCompatActivity {

    public static final int PERMISSION_REQ_CODE = 100;
    public static final int CAMERA_PERMISSION_REQ_CODE = 101;

    private ImageView imageView;
    private Button openCameraBtn;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_first);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setReferences();
        checkPermissions();

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
    }

    private void setReferences() {
        imageView = findViewById(R.id.image_view);
        openCameraBtn = findViewById(R.id.open_camera_btn);
    }

    private void checkPermissions() {
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_CODE);
            }
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQ_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        String fileName = "photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);

            currentPhotoPath = imageFile.getAbsolutePath();

            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.example.cameraimagesavetemp.fileprovider",
                    imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "onRequestPermissionsResult: All permission access.");
            } else {
                Log.d(MainActivity.TAG, "onRequestPermissionsResult: need ALL permission");
//                Toast.makeText(getBaseContext(), "Need Permissions", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CAMERA_PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "onRequestPermissionsResult: Camera permission access.");
                openCamera();
            } else {
                Log.d(MainActivity.TAG, "onRequestPermissionsResult: need CAMERA permission");
//                Toast.makeText(getBaseContext(), "Need Camera Permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

            Log.d(MainActivity.TAG, "onActivityResult: \tIMAGE --- \nBitMap: " + bitmap + "\nPhotoPath: " + currentPhotoPath);

            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}