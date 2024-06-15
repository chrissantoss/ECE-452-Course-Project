package com.motive.motive.Activities;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.motive.motive.R;

public class RegisterActivity extends AppCompatActivity {
    EditText userNameInput;
    EditText password;
    EditText confirmPassword;
    Button registerBtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameInput = findViewById(R.id.userNameInput);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerBtn = findViewById(R.id.register);

        if (userNameInput == null || password == null || registerBtn == null) {
            Log.e("LoginActivity", "One or more views are null!");
        }
        registerBtn.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            registerEmailPass();
        });

    }

    void registerEmailPass() {
        String email = this.userNameInput.getText().toString();
        String pass = this.password.getText().toString();
        String conf_pass = this.confirmPassword.getText().toString();
        Log.e("Register", email + " " + pass);

        if (email.isEmpty() || pass.isEmpty() || conf_pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(conf_pass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Register", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Register", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}