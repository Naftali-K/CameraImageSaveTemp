package com.example.cameraimagesavetemp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TestCode";
    public static final String[] permissions = new String[] {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    public static final String FILE_PROVIDER = "com.example.cameraimagesavetemp.fileprovider";

    private Button cameraFirstBtn, cameraSecondBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();

        cameraFirstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CameraFirstActivity.class);
                startActivity(intent);
            }
        });

        cameraSecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CameraSecondActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setReferences() {
        cameraFirstBtn = findViewById(R.id.camera_first_btn);
        cameraSecondBtn = findViewById(R.id.camera_second_btn);
    }
}