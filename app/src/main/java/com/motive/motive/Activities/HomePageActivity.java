package com.motive.motive.Activities;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.AdvancedMarker;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
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
import com.motive.motive.data.GameClusterItem;
import com.motive.motive.data.GameClusterRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;

public class HomePageActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;

    private ClusterManager<GameClusterItem> mapClusterManager;
    private LatLng userLocation;
    private ArrayList<GameModel> games = new ArrayList<GameModel>();
    private LatLng defaultLocation = new LatLng(43.4723, -80.5449);
    private LatLng selectedLocation;
    private Map<GameClusterItem, GameModel> markerGameMap = new HashMap<>();
    private GameModel hostingGame;

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button openCreateGameFormButton = findViewById(R.id.openCreateGameFormButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

            mapClusterManager = new ClusterManager<GameClusterItem>(this, map);
            mapClusterManager.setRenderer(new GameClusterRenderer(this, map, mapClusterManager));
            map.setOnCameraIdleListener(mapClusterManager);
            map.setOnInfoWindowClickListener(mapClusterManager);

            mapClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<GameClusterItem>() {
                @Override
                public boolean onClusterClick(Cluster<GameClusterItem> cluster) {

                    Collection<GameClusterItem> gameClusterItems = cluster.getItems();
                    List<GameModel> games = new ArrayList<GameModel>();
                    for(GameClusterItem gameClusterItem:gameClusterItems) {

                        games.add( markerGameMap.get(gameClusterItem));

                    }
                    Log.i("clicked marker in cluster", "cluster size " + games.size());
                    if (!games.isEmpty()) {
                        showGameDetailsDialog(games);
                    }

                    return true;
                }
            });

            mapClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<GameClusterItem>() {
                @Override
                public boolean onClusterItemClick(GameClusterItem item) {
                    Log.i("clicked marker ", "cluster size " + item.getTitle());
                    GameModel game = markerGameMap.get(item);
                    Log.d("Firestore", "Marker clicked: " + game);
                    Log.d("Firestore", "Marker clicked: " + item.getTitle());
                    Log.d("Firestore", "Marker clicked: " + markerGameMap);

                    if (game != null) {
                        showGameDetailsDialog(listOf(game));
                    }
                    return false;

                }
            });
            mapClusterManager.cluster();

        });



    }

    private void fetchGamesAndAddMarkers() {
        games.clear();
        markerGameMap.clear();

        if(mapClusterManager != null){
            mapClusterManager.clearItems();
            mapClusterManager.cluster();
        }

        FirebaseFirestore.getInstance().collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    GameModel game = document.toObject(GameModel.class);
                    if (game != null) {
                        games.add(game);
                        LatLng gameLocation = new LatLng(game.getLatitude(), game.getLongitude());
                        GameClusterItem item = new GameClusterItem(gameLocation.latitude, gameLocation.longitude, game.getGameType(), game.getGameID());
                        markerGameMap.put(item, game);
                        mapClusterManager.addItem(item);
                        mapClusterManager.cluster();

                    }
                }
                Log.d("finished adding games", "Games: " +games.size());

            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching game data", e));

    }

    private void fetchHostingGame() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                            hostingGame = game;
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

    private void showGameDetailsDialog(List<GameModel> gameList) {
        if (gameList == null || gameList.isEmpty()) {
            return; // No games to show
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_details, null);
        builder.setView(dialogView);



        ImageButton nextButton = dialogView.findViewById(R.id.nextButton);
        ImageButton backButton = dialogView.findViewById(R.id.backButton);

        if (gameList.size() == 1) {
            nextButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
        }

        final int[] currentIndex = {0};
        updatePopupView(gameList.get(currentIndex[0]), dialogView);

        nextButton.setOnClickListener(v -> {
            currentIndex[0] = (currentIndex[0] + 1) % gameList.size();
            updatePopupView(gameList.get(currentIndex[0]), dialogView);
            Log.i("next", "current index" + currentIndex[0]);
        });

        backButton.setOnClickListener(v -> {
            currentIndex[0] = (currentIndex[0] - 1 + gameList.size()) % gameList.size();
            updatePopupView(gameList.get(currentIndex[0]), dialogView);
            Log.i("back", "current index" + currentIndex[0]);
        });



        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void updatePopupView(GameModel game, View dialogView){

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
                if (canJoinGame(game)) {
                    joinGame(game);
                } else {
                    Toast.makeText(HomePageActivity.this, "Cannot join a game while hosting another game at the same time.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean canJoinGame(GameModel game) {
        if (hostingGame == null) {
            return true;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date hostingEndTime = sdf.parse(hostingGame.getEndTime());
            Date gameStartTime = sdf.parse(game.getStartTime());

            if (hostingEndTime != null && gameStartTime != null) {
                return gameStartTime.after(hostingEndTime);
            }
        } catch (Exception e) {
            Log.e("HomePageActivity", "Error parsing date", e);
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
