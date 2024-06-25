package com.motive.motive.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull; f 
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

            for (GameModel game : games) {
                Log.d("Firestore", "Adding marker at " +game.getLatitude()+", "+ game.getLongitude());
                LatLng gameLocation = new LatLng(game.getLatitude(), game.getLongitude());
                map.addMarker(new MarkerOptions().position(gameLocation).title(game.getGameType()));
            }



        });

        FirebaseFirestore.getInstance().collection("games").get();

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

