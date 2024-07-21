package com.motive.motive.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.motive.motive.Models.GameModel;
import com.motive.motive.R;

public class EditGameActivity extends AppCompatActivity {

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
    private CheckBox age17to36Input;
    private CheckBox age36Plus;
    private EditText notesInput;
    private Button saveChangesButton;
    private Button deleteGameButton;

    private MapView mapView;

    private String gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        // Initialize UI components
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
        age17to36Input = findViewById(R.id.age17to36);
        age36Plus = findViewById(R.id.age36Plus);
        notesInput = findViewById(R.id.notesInput);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteGameButton = findViewById(R.id.deleteGameButton);
        mapView = findViewById(R.id.mapView);

        gameID = getIntent().getStringExtra("gameID");

        // Fetch and display current game details
        fetchGameDetails(gameID);

        saveChangesButton.setOnClickListener(v -> saveChanges());
        deleteGameButton.setOnClickListener(v -> deleteGame());
    }

    private void fetchGameDetails(String gameID) {
        FirebaseFirestore.getInstance().collection("games").document(gameID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    GameModel game = documentSnapshot.toObject(GameModel.class);
                    if (game != null) {
                        gameTypeInput.setText(game.getGameType());
                        gameSizeInput.setText(String.valueOf(game.getMaxPlayers()));
                        mandatoryItemsInput.setText(game.getMandatoryItems());
                        setExperienceCheckBoxes(game.getExperienceAsString());
                        setGenderCheckBoxes(game.getGenderPreferenceAsString());
                        setAgeCheckBoxes(game.getAgePreferenceAsString());
                        notesInput.setText(game.getNotes());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditGameActivity.this, "Failed to fetch game details", Toast.LENGTH_SHORT).show();
                    Log.e("ERR FETCHING GAME", e.getMessage());
                });
    }

    private void setExperienceCheckBoxes(String experience) {
        experienceBeginner.setChecked(experience.contains("Beginner"));
        experienceIntermediate.setChecked(experience.contains("Intermediate"));
        experienceExpert.setChecked(experience.contains("Expert"));
    }

    private void setGenderCheckBoxes(String gender) {
        genderMale.setChecked(gender.contains("Male"));
        genderFemale.setChecked(gender.contains("Female"));
        genderNeutral.setChecked(gender.contains("Neutral"));
    }

    private void setAgeCheckBoxes(String age) {
        age16Under.setChecked(age.contains("16 and under"));
        age17to36Input.setChecked(age.contains("17 to 36"));
        age36Plus.setChecked(age.contains("36+"));
    }


    private void saveChanges() {
        String gameType = gameTypeInput.getText().toString();
        int gameSize = Integer.parseInt(gameSizeInput.getText().toString());
        String mandatoryItems = mandatoryItemsInput.getText().toString();
        boolean beginner = experienceBeginner.isChecked();
        boolean intermediate = experienceIntermediate.isChecked();
        boolean expert = experienceExpert.isChecked();
        boolean male = genderMale.isChecked();
        boolean female = genderFemale.isChecked();
        boolean neutral = genderNeutral.isChecked();
        boolean age16 = age16Under.isChecked();
        boolean age17to36 = age17to36Input.isChecked();
        boolean age36 = age36Plus.isChecked();
        String notes = notesInput.getText().toString();

        FirebaseFirestore.getInstance().collection("games").document(gameID)
                .update(
                        "gameType", gameType,
                        "maxPlayers", gameSize,
                        "mandatoryItems", mandatoryItems,
                        "beginner", beginner,
                        "intermediate", intermediate,
                        "expert", expert,
                        "male", male,
                        "female", female,
                        "neutral", neutral,
                        "age16", age16,
                        "age17to36", age17to36,
                        "age36", age36,
                        "notes", notes
                )
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Game updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update game", Toast.LENGTH_SHORT).show();
                        Log.e("ERR UPDATING GAME", String.valueOf(task.getException()));
                    }
                });
    }
    private void deleteGame() {
        FirebaseFirestore.getInstance().collection("games").document(gameID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Game deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete game", Toast.LENGTH_SHORT).show();
                        Log.e("ERR DELETING GAME", String.valueOf(task.getException()));
                    }
                });
    }

}
