package com.motive.motive.Models;

public class GameModel {
    private String gameID;
    private String hostID;
    private double latitude;
    private double longitude;
    private int maxPlayers;
    private String gameType;
    private int numPlayers;

    public GameModel() {
        // Public no-arg constructor needed
    }

    public GameModel(String gameID, String hostID, double latitude, double longitude, int maxPlayers, String gameType) {
        this.gameID = gameID;
        this.hostID = hostID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxPlayers = maxPlayers;
        this.gameType = gameType;
    }

    // Getters and Setters
    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
