package com.motive.motive.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.motive.motive.Models.GameModel;
import com.motive.motive.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    private LatLng userLocation;
    private ArrayList<GameModel> games  = new ArrayList<GameModel>();
    private LatLng defaultLocation =  new LatLng(43.4723, -80.5449);
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        FirebaseFirestore.getInstance().collection("games").get().addOnSuccessListener(gamesDocuments ->{
            for (DocumentSnapshot gameDocument : gamesDocuments.getDocuments()) {

                GameModel game = gameDocument.toObject(GameModel.class);
                if (game != null) {
                    games.add(game);
                }
            }
        });

        Button openCreateGameFormButton = findViewById(R.id.openCreateGameFormButton);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            initializeMap();
        }



        openCreateGameFormButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, CreateGameActivity.class);
            intent.putExtra("defaultLocation", defaultLocation);
            intent.putExtra("userLocation", userLocation);
            startActivity(intent);
        });
    }

    private void initializeMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                map.setMyLocationEnabled(true);

                // Getting last known location
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Move camera to user's location
                        Toast.makeText(this, "Location found", Toast.LENGTH_LONG).show();
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    } else {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
                        Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show();
                    }
                    fetchGamesAndAddMarkers();
                });

                map.setOnMarkerClickListener(marker -> {
                    GameModel game = markerGameMap.get(marker);
                    if (game != null) {
                        showGameDetailsDialog(game);
                    }
                    return true;
                });
            });
        }
    }

    private void fetchGamesAndAddMarkers() {
        FirebaseFirestore.getInstance().collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    GameModel game = document.toObject(GameModel.class);
                    if (game != null) {
                        games.add(game);
                        LatLng gameLocation = new LatLng(game.getLatitude(), game.getLongitude());
                        map.addMarker(new MarkerOptions().position(gameLocation).title(game.getGameType()));
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching game data", e));
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
        Button joinGameButton = dialogView.findViewById(R.id.joinGameButton);

        gameTypeTextView.setText(game.getGameType());
        gameSizeTextView.setText(String.valueOf(game.getGameSize()));
        mandatoryItemsTextView.setText(game.getMandatoryItems());
        experienceTextView.setText(game.getExperienceAsString());
        genderTextView.setText(game.getGenderPreferenceAsString());
        ageTextView.setText(game.getAgePreferenceAsString());
        notesTextView.setText(game.getNotes());

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
                        Toast.makeText(HomePageActivity.this, "Successfully joined the game", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomePageActivity.this, "Failed to join the game", Toast.LENGTH_SHORT).show();
                        Log.e("ERR JOINING GAME", String.valueOf(task.getException()));
                    }
                });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

}

