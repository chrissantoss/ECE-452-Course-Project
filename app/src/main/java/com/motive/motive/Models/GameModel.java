package com.motive.motive.Models;

public class GameModel {
    private String gameID;
    private String hostID;
    private double latitude;
    private double longitude;
    private int maxPlayers;
    private String gameType;
    private boolean beginner;
    private boolean intermediate;
    private boolean expert;
    private boolean male;
    private boolean female;
    private boolean neutral;
    private boolean age16;
    private boolean age17to36;
    private boolean age36;
    private String mandatoryItems;
    private String notes;

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
    // Include setters for the additional fields

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

    public void setExperience(boolean beginner, boolean intermediate, boolean expert) {
        this.beginner = beginner;
        this.intermediate = intermediate;
        this.expert = expert;
    }

    public void setGenderPreference(boolean male, boolean female, boolean neutral) {
        this.male = male;
        this.female = female;
        this.neutral = neutral;
    }

    public void setAgePreference(boolean age16, boolean age17to36, boolean age36) {
        this.age16 = age16;
        this.age17to36 = age17to36;
        this.age36 = age36;
    }

    public void setMandatoryItems(String mandatoryItems) {
        this.mandatoryItems = mandatoryItems;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Additional getters and setters for new fields
}
