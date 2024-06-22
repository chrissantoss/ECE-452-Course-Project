package com.motive.motive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.motive.motive.R;

public class HomePageActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button openCreateGameFormButton = findViewById(R.id.openCreateGameFormButton);
        openCreateGameFormButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, CreateGameActivity.class);
            startActivity(intent);
        });
    }
}

