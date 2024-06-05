package com.motive.motive;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText userNameInpu;
    EditText password;
    EditText confirmPassword;
    Button login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameInpu = findViewById(R.id.userNameInput);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener((v)-> {
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}