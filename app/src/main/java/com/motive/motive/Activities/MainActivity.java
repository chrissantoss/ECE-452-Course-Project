package com.motive.motive.Activities;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.motive.motive.R;

import com.google.firebase.FirebaseApp;


//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        EdgeToEdge.enable(this);
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this); // Initialize Firebase
        //EdgeToEdge.enable(this); // Uncomment if needed

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Call finish() to close MainActivity
    }
}