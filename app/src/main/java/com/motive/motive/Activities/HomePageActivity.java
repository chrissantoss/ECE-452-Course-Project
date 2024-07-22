package com.motive.motive.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.motive.motive.Models.GameModel;
import com.motive.motive.Models.UserModel;
import com.motive.motive.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;

public class HomePageActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    private LatLng userLocation;
    private ArrayList<GameModel> games = new ArrayList<GameModel>();
    private LatLng defaultLocation = new LatLng(43.4723, -80.5449);
    private LatLng selectedLocation;
    private Map<Marker, GameModel> markerGameMap = new HashMap<>();
//    private GameModel hostingGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button openCreateGameFormButton = findViewById(R.id.openCreateGameFormButton);
        fetchGamesAndAddMarkers();
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

        fetchHostingGame();
    }
    @Override
    protected void onStart() {
        super.onStart();
        fetchGamesAndAddMarkers();
        Log.d("onStart update games", "Games: " +games.size());
    }
    private void initializeMap() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

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
                    Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show();
                }
            });

            fetchGamesAndAddMarkers();
            map.setOnMarkerClickListener(marker -> {
                GameModel game = markerGameMap.get(marker);
                Log.d("Firestore", "Marker clicked: " + game);
                Log.d("Firestore", "Marker clicked: " + marker);
                Log.d("Firestore", "Marker clicked: " + markerGameMap);

                if (game != null) {
                    showGameDetailsDialog(game);
                }
                return false;
            });
        });

        FirebaseFirestore.getInstance().collection("games").get();

    }

    private void fetchGamesAndAddMarkers() {
        games.clear();
        markerGameMap.clear();
        FirebaseFirestore.getInstance().collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    GameModel game = document.toObject(GameModel.class);
                    if (game != null) {
                        games.add(game);
                        LatLng gameLocation = new LatLng(game.getLatitude(), game.getLongitude());
                        Marker marker = map.addMarker(new MarkerOptions().position(gameLocation).title(game.getGameType()));
                        markerGameMap.put(marker, game);
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching game data", e));
    }

    private void fetchHostingGame() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserModel userModel = new UserModel(currentUser.getUid());
        if (currentUser != null) {
            String hostID = currentUser.getUid();
            FirebaseFirestore.getInstance().collection("games")
                    .whereEqualTo("hostID", hostID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                GameModel game = document.toObject(GameModel.class);
                                if (game != null) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                        Date currentDate = new Date();
                                        Date gameEndTime = sdf.parse(game.getEndTime());

                                        if (gameEndTime != null && gameEndTime.after(currentDate)) {
                                            userModel.setHostingGame(game);
                                            storeUserModelInDatabase(userModel);
                                            break;
                                        }
                                    } catch (Exception e) {
                                        Log.e("HomePageActivity", "Error parsing date", e);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching hosting game data", e));
        }
    }

    private void storeUserModelInDatabase(UserModel userModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userModel.getUserID())
                .set(userModel)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "UserModel successfully written!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error writing UserModel", e));
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(game.getHostID())) {
            joinGameButton.setText("Edit Game");
            joinGameButton.setOnClickListener(v -> editGame(game));
        } else {
            joinGameButton.setText("Join Game");
            joinGameButton.setOnClickListener(v -> {
                Log.d("OverHERE", "currentUser: " +"helloo");
                if (canJoinGame(game)) {
                    joinGame(game);
                } else {
                    Toast.makeText(HomePageActivity.this, "Cannot join a game while hosting another game at the same time.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    private boolean canJoinGame(GameModel game) {
//        if (hostingGame == null) {
//            return true;
//        }
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//            Date hostingEndTime = sdf.parse(hostingGame.getEndTime());
//            Date gameStartTime = sdf.parse(game.getStartTime());
//
//            if (hostingEndTime != null && gameStartTime != null) {
//                return gameStartTime.after(hostingEndTime);
//            }
//        } catch (Exception e) {
//            Log.e("HomePageActivity", "Error parsing date", e);
//        }
//
//        return false;
//    }

    private boolean canJoinGame(GameModel game) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("canJoinGame", "User not authenticated");
            return false;
        }

        Log.d("CanJoinGame", "currentUser: " +currentUser.getUid());
        String userID = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            DocumentSnapshot documentSnapshot = db.collection("users").document(userID).get().getResult();
            if (documentSnapshot.exists()) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                Log.e("CanJoinGame", "SavedUser: " + userModel.getHostingGame().getGameType());

                if (userModel != null) {
                    GameModel hostingGame = userModel.getHostingGame();
                    if (hostingGame != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date currentDate = new Date();
                        Date hostingEndTime = sdf.parse(hostingGame.getEndTime());

                        if (hostingEndTime != null && hostingEndTime.before(currentDate)) {
                            // Hosting game has expired, set it to null
                            userModel.setHostingGame(null);
                            storeUserModelInDatabase(userModel);
                            hostingGame = null; // Update the local variable to reflect the change
                        }

                        if (hostingGame == null) {
                            return true; // Hosting game is null after the check
                        } else {
                            Date gameStartTime = sdf.parse(game.getStartTime());
                            if (gameStartTime != null) {
                                return gameStartTime.after(hostingEndTime);
                            }
                        }
                    } else {
                        return true; // User is not hosting any game, they can join the game
                    }
                }
            }
        } catch (Exception e) {
            Log.e("canJoinGame", "Error fetching user data or parsing date", e);
        }

        return false;
    }

    private void editGame(GameModel game) {
        Intent intent = new Intent(this, EditGameActivity.class);
        intent.putExtra("gameID", game.getGameID());
        startActivity(intent);
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
