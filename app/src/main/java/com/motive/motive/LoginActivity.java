package com.motive.motive;
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
//import com.google.firebase.auth.SignInMethodQueryResult;
//import com.google.firebase.auth.UserRecord;
//import com.google.firebase.firestore.DocumentSnapshot;

//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.DocumentSnapshot;
public class LoginActivity extends AppCompatActivity {
    EditText userNameInput;
    EditText password;
    Button loginBtn;
    Button registerBtn;
    UserModel userModel;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameInput = findViewById(R.id.userNameInput);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);

        if (userNameInput == null || password == null || loginBtn == null) {
            Log.e("LoginActivity", "One or more views are null!");
        }
        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(v -> {
            signInEmailPass();
        });
    }

    void signInEmailPass() {
        String email = this.userNameInput.getText().toString();
        String pass = this.password.getText().toString();
        Log.e("Login", email + " " + pass);
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success");
                    FirebaseUser user = auth.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}