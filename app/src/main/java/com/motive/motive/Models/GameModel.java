package com.motive.motive.Models;
import java.util.List;

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
    private List<String> participants;

    private int gameSize;
    private String startTime;
    private String endTime;


    public GameModel() {
        // Public no-arg constructor needed
    }

    public GameModel(String gameID, String hostID, double latitude, double longitude, int maxPlayers, String gameTyp) {
        this.gameID = gameID;
        this.hostID = hostID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxPlayers = maxPlayers;
        this.gameType = gameType;
        this.gameSize = 1;
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
    
    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    
    public String getExperienceAsString() {
        StringBuilder experience = new StringBuilder();
        if (beginner) experience.append("Beginner ");
        if (intermediate) experience.append("Intermediate ");
        if (expert) experience.append("Expert ");
        return experience.toString().trim();
    }

    public String getGenderPreferenceAsString() {
        StringBuilder gender = new StringBuilder();
        if (male) gender.append("Male ");
        if (female) gender.append("Female ");
        if (neutral) gender.append("Neutral ");
        return gender.toString().trim();
    }

    public String getAgePreferenceAsString() {
        StringBuilder age = new StringBuilder();
        if (age16) age.append("16 and under ");
        if (age17to36) age.append("17 to 36 ");
        if (age36) age.append("36+ ");
        return age.toString().trim();
    }

    public String getMandatoryItems() {
        return mandatoryItems;
    }

    public String getNotes() {
        return notes;
    } public int getGameSize() {
        return gameSize;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
