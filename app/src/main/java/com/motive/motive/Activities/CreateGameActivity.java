package com.motive.motive.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.motive.motive.Models.GameModel;
import com.motive.motive.R;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Nullable;

import com.google.android.gms.maps.model.Marker;


public class CreateGameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Spinner gameTypeDropDown;
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
    private Map<Marker, GameModel> markerGameMap = new HashMap<>();

    private Spinner dateSpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        // Initialize UI components
        gameTypeDropDown = findViewById(R.id.gameTypeDropdown);
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

        // Initialize the new spinners
        dateSpinner = findViewById(R.id.dateSpinner);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Populate spinners
        populateDateSpinner();
        populateTimeSpinners(startTimeSpinner);
        populateTimeSpinners(endTimeSpinner);

        createGameButton.setOnClickListener(v -> createGame());
        fetchGamesAndAddMarkers();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.game_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        gameTypeDropDown.setAdapter(adapter);

    }


   @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });

        googleMap.setOnMarkerClickListener(marker -> {
            GameModel game = markerGameMap.get(marker);
            if (game != null) {
                showGameDetailsDialog(game);
            }
            return true;
        });

        LatLng userLocation = getIntent().getExtras().getParcelable("userLocation");
        LatLng defaultLocation = getIntent().getExtras().getParcelable("defaultLocation");

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                (userLocation != null) ? userLocation : defaultLocation,
                15));
    }


    private void fetchGamesAndAddMarkers() {
        FirebaseFirestore.getInstance().collection("games")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(CreateGameActivity.this, "Failed to fetch games", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        markerGameMap.clear();
                        googleMap.clear();
                        for (QueryDocumentSnapshot document : value) {
                            GameModel game = document.toObject(GameModel.class);
                            LatLng location = new LatLng(game.getLatitude(), game.getLongitude());
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title(game.getGameType()));
                            markerGameMap.put(marker, game);
                        }
                    }
                });
    }

    private void showGameDetailsDialog(GameModel game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_details, null);
        builder.setView(dialogView);

    TextView gameTypeTextView = dialogView.findViewById(R.id.gameTypeTextView);
    TextView gameSizeTextView = dialogView.findViewById(R.id.gameSizeTextView);
    TextView mandatoryItemsTextView = dialogView.findViewById(R.id.mandatoryItemsTextView);
    TextView experienceTextView = dialogView.findViewById(R.id.experienceTextView);
    TextView genderTextView = dialogView.findViewById(R.id.genderTextView);
    TextView ageTextView = dialogView.findViewById(R.id.ageTextView);
    TextView notesTextView = dialogView.findViewById(R.id.notesTextView);
    TextView participantsTextView = dialogView.findViewById(R.id.participantsTextView);
    Button joinGameButton = dialogView.findViewById(R.id.joinGameButton);

    gameTypeTextView.setText(game.getGameType());
    gameSizeTextView.setText(String.valueOf(game.getGameSize()));
    mandatoryItemsTextView.setText(game.getMandatoryItems());
    experienceTextView.setText(game.getExperienceAsString());
    genderTextView.setText(game.getGenderPreferenceAsString());
    ageTextView.setText(game.getAgePreferenceAsString());
    notesTextView.setText(game.getNotes());
    participantsTextView.setText("Participants: " + (game.getParticipants() != null ? game.getParticipants().size() : 0));

        joinGameButton.setOnClickListener(v -> joinGame(game));

    AlertDialog dialog = builder.create();
    dialog.show();
}


    private void joinGame(GameModel game) {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
        String currentUserID = currentUser.getUid();

        FirebaseFirestore.getInstance().collection("games").document(game.getGameID())
            .update("participants", FieldValue.arrayUnion(currentUserID))
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateGameActivity.this, "Successfully joined the game", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateGameActivity.this, "Failed to join the game", Toast.LENGTH_SHORT).show();
                    Log.e("ERR JOINING GAME", String.valueOf(task.getException()));
                }
            });
    } else {
        Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
    }
}

    private void populateDateSpinner() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) { // next 7 days
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
    }

    private void populateTimeSpinners(Spinner spinner) {
        List<String> times = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        for (int i = 0; i < 48; i++) { // 30-minute intervals
            times.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void scheduleGameDeletion(String gameID, String endTime) {
        // Calculate the difference between the current time and the end time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            long endTimeMillis = sdf.parse(endTime).getTime();
            long delay = endTimeMillis - System.currentTimeMillis();

            new android.os.Handler().postDelayed(() -> {
                FirebaseFirestore.getInstance().collection("games").document(gameID).delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Game Deletion", "Game deleted successfully");
                            } else {
                                Log.e("Game Deletion", "Failed to delete game", task.getException());
                            }
                        });
            }, delay);
        } catch (Exception e) {
            Log.e("Game Deletion", "Failed to parse end time", e);
        }
    }


    private void createGame() {
        String gameType = gameTypeDropDown.toString();
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String hostID = currentUser.getUid();
        String gameID = FirebaseFirestore.getInstance().collection("games").document().getId();
        double latitude = selectedLocation != null ? selectedLocation.latitude : 43.4723;
        double longitude = selectedLocation != null ? selectedLocation.longitude : -80.5449;

        // Capture start and end times
        String date = dateSpinner.getSelectedItem().toString();
        String startTime = startTimeSpinner.getSelectedItem().toString();
        String endTime = endTimeSpinner.getSelectedItem().toString();

        // Error Checking
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime24Hour = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfTime12Hour = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String currentDateStr = sdfDate.format(calendar.getTime());
        String currentTimeStr = sdfTime24Hour.format(calendar.getTime());
        String selectedStartDateStr = date + " " + startTime;
        String selectedEndDateStr = date + " " + endTime;

        try {
            Date currentDate = sdfDate.parse(currentDateStr);
            Date selectedDate = sdfDate.parse(date);
            Date currentTime = sdfTime24Hour.parse(currentTimeStr);
            Date startTimeDate = sdfTime12Hour.parse(startTime);
            Date endTimeDate = sdfTime12Hour.parse(endTime);

            // Log current and selected dates and times
            Log.d("CreateGame", "Current Date: " + currentDateStr);
            Log.d("CreateGame", "Selected Date: " + date);
            Log.d("CreateGame", "Current Time: " + currentTimeStr);
            Log.d("CreateGame", "Start Time: " + startTime);
            Log.d("CreateGame", "End Time: " + endTime);

            if (selectedDate.before(currentDate)) {
                Toast.makeText(this, "Start date cannot be before the current date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDate.equals(currentDate) && startTimeDate.before(currentTime)) {
                Toast.makeText(this, "Selected time cannot be before the current time on the same date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endTimeDate.before(startTimeDate)) {
                Toast.makeText(this, "End time cannot be before the start time", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
            return;
        }


        GameModel game = new GameModel(gameID, hostID, latitude, longitude, gameSize, gameType);
        game.setExperience(beginner, intermediate, expert);
        game.setGenderPreference(male, female, neutral);
        game.setAgePreference(age16, age17to36, age36);
        game.setMandatoryItems(mandatoryItems);
        game.setNotes(notes);
        game.setStartTime(date + " " + startTime);
        game.setEndTime(date + " " + endTime);

        FirebaseFirestore.getInstance().collection("games").document(gameID)
                .set(game)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Add the host as a participant
                        FirebaseFirestore.getInstance().collection("games").document(gameID)
                                .update("participants", FieldValue.arrayUnion(hostID))
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(CreateGameActivity.this, "Game Created Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(CreateGameActivity.this, "Failed to add host as participant", Toast.LENGTH_SHORT).show();
                                        Log.e("ERR ADDING HOST", String.valueOf(task1.getException()));
                                    }
                                });

                        // Schedule deletion of the game after the end time
                        scheduleGameDeletion(gameID, date + " " + endTime);
                    } else {
                        Toast.makeText(CreateGameActivity.this, "Failed to create game", Toast.LENGTH_SHORT).show();
                        Log.e("ERR CREATING GAME", String.valueOf(task.getException()));
                    }
                });
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

}
