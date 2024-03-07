package com.example.cameraimagesavetemp;

import static com.example.cameraimagesavetemp.MainActivity.permissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * https://youtu.be/6rXP96DU1VA?si=yFOb2rnWTuYBydMR - lesson video
 */
public class CameraSecondActivity extends AppCompatActivity {

    public static final int PERMISSION_REQ_CODE = 100;
    public static final int CAMERA_PERMISSION_REQ_CODE = 101;
    public static final int CAMERA_REQ = 102;

    private ImageView imageView;
    private Button openCameraBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_second);
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
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQ_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQ);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher();
            finish();
        }
        return super.onOptionsItemSelected(item);
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

        if (requestCode == CAMERA_REQ && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");

            imageView.setImageBitmap(bitmap);

            saveImageToGallery(bitmap);
        }
    }

    private void saveImageToGallery(Bitmap imageBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            sendBroadcast(mediaScanIntent);

            Toast.makeText(this, "Image Saved Successfully.", Toast.LENGTH_SHORT).show();
            Log.d(MainActivity.TAG, "saveImageToGallery: Image Uri: " + Uri.fromFile(imageFile).toString());
        } catch (FileNotFoundException e) {
            Log.d(MainActivity.TAG, "saveImageToGallery: Error FileOutputStream: " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.d(MainActivity.TAG, "saveImageToGallery: Error flush, close: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}