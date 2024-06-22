package com.motive.motive.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.motive.motive.Models.GameModel;
import com.motive.motive.R;

public class CreateGameActivity extends AppCompatActivity {

    private EditText gameTypeInput;
    private EditText gameSizeInput;
    private EditText mandatoryItemsInput;
    private CheckBox experienceBeginner;
    private CheckBox experienceIntermediate;
    private CheckBox experienceExpert;
    private CheckBox genderMale;
    private CheckBox genderFemale;
    private CheckBox genderNeutral;
    private CheckBox age16Under;
    private CheckBox age17to36;
    private CheckBox age36Plus;
    private EditText notesInput;
    private Button createGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        gameTypeInput = findViewById(R.id.gameTypeInput);
        gameSizeInput = findViewById(R.id.gameSizeInput);
        mandatoryItemsInput = findViewById(R.id.mandatoryItemsInput);
        experienceBeginner = findViewById(R.id.experienceBeginner);
        experienceIntermediate = findViewById(R.id.experienceIntermediate);
        experienceExpert = findViewById(R.id.experienceExpert);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        genderNeutral = findViewById(R.id.genderNeutral);
        age16Under = findViewById(R.id.age16Under);
        age17to36 = findViewById(R.id.age17to36);
        age36Plus = findViewById(R.id.age36Plus);
        notesInput = findViewById(R.id.notesInput);
        createGameButton = findViewById(R.id.createGameButton);

        createGameButton.setOnClickListener(v -> createGame());
    }


    private void createGame() {
        String gameType = gameTypeInput.getText().toString();
        String gameSizeStr = gameSizeInput.getText().toString();
        String mandatoryItems = mandatoryItemsInput.getText().toString();
        String notes = notesInput.getText().toString();

        if (TextUtils.isEmpty(gameType) || TextUtils.isEmpty(gameSizeStr)) {
            Toast.makeText(this, "Game Type and Game Size are required", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
