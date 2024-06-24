package com.motive.motive.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.motive.motive.Models.GameModel;
import com.motive.motive.R;

public class CreateGameActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    private Button createGameButton;

    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

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
        createGameButton = findViewById(R.id.createGameButton);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        createGameButton.setOnClickListener(v -> createGame());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });

        LatLng defaultLocation = new LatLng(43.4723, -80.5449);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

        int gameSize = Integer.parseInt(gameSizeStr);
        boolean beginner = experienceBeginner.isChecked();
        boolean intermediate = experienceIntermediate.isChecked();
        boolean expert = experienceExpert.isChecked();
        boolean male = genderMale.isChecked();
        boolean female = genderFemale.isChecked();
        boolean neutral = genderNeutral.isChecked();
        boolean age16 = age16Under.isChecked();
        boolean age17to36 = age17to36Input.isChecked();
        boolean age36 = age36Plus.isChecked();

        String gameID = FirebaseFirestore.getInstance().collection("games").document().getId();
        double latitude = selectedLocation != null ? selectedLocation.latitude : 43.4723;
        double longitude = selectedLocation != null ? selectedLocation.longitude : -80.5449;
        GameModel game = new GameModel(gameID, "hostID", latitude, longitude, gameSize, gameType);
        game.setExperience(beginner, intermediate, expert);
        game.setGenderPreference(male, female, neutral);
        game.setAgePreference(age16, age17to36, age36);
        game.setMandatoryItems(mandatoryItems);
        game.setNotes(notes);

        FirebaseFirestore.getInstance().collection("games").document(gameID)
                .set(game)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateGameActivity.this, "Game Created Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateGameActivity.this, "Failed to create game", Toast.LENGTH_SHORT).show();
                            Log.e("ERR CREATING GAME", String.valueOf(task.getException()));
                        }
                    }
                });
    }
}
