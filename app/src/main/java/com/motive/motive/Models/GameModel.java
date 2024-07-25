package com.motive.motive.Models;

import com.google.firebase.firestore.PropertyName;
import java.util.Arrays;
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

    public GameModel(String gameID, String hostID, double latitude, double longitude, int maxPlayers, String gameType) {
        this.gameID = gameID;
        this.hostID = hostID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxPlayers = maxPlayers;
        this.gameType = gameType;
        this.gameSize = 1;
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

    @PropertyName("beginner")
    public boolean isBeginner() {
        return beginner;
    }

    @PropertyName("beginner")
    public void setBeginner(boolean beginner) {
        this.beginner = beginner;
    }

    @PropertyName("intermediate")
    public boolean isIntermediate() {
        return intermediate;
    }

    @PropertyName("intermediate")
    public void setIntermediate(boolean intermediate) {
        this.intermediate = intermediate;
    }

    @PropertyName("expert")
    public boolean isExpert() {
        return expert;
    }

    @PropertyName("expert")
    public void setExpert(boolean expert) {
        this.expert = expert;
    }

    @PropertyName("male")
    public boolean isMale() {
        return male;
    }

    @PropertyName("male")
    public void setMale(boolean male) {
        this.male = male;
    }

    @PropertyName("female")
    public boolean isFemale() {
        return female;
    }

    @PropertyName("female")
    public void setFemale(boolean female) {
        this.female = female;
    }

    @PropertyName("neutral")
    public boolean isNeutral() {
        return neutral;
    }

    @PropertyName("neutral")
    public void setNeutral(boolean neutral) {
        this.neutral = neutral;
    }

    @PropertyName("age16")
    public boolean isAge16() {
        return age16;
    }

    @PropertyName("age16")
    public void setAge16(boolean age16) {
        this.age16 = age16;
    }

    @PropertyName("age17to36")
    public boolean isAge17to36() {
        return age17to36;
    }

    @PropertyName("age17to36")
    public void setAge17to36(boolean age17to36) {
        this.age17to36 = age17to36;
    }

    @PropertyName("age36")
    public boolean isAge36() {
        return age36;
    }

    @PropertyName("age36")
    public void setAge36(boolean age36) {
        this.age36 = age36;
    }

    public String getMandatoryItems() {
        return mandatoryItems;
    }

    public void setMandatoryItems(String mandatoryItems) {
        this.mandatoryItems = mandatoryItems;
    }

    public String getNotes() {
        return notes;
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

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
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

    @PropertyName("experienceAsString")
    public void setExperienceAsString(String experienceAsString) {
        this.beginner = experienceAsString.contains("Beginner");
        this.intermediate = experienceAsString.contains("Intermediate");
        this.expert = experienceAsString.contains("Expert");
    }

    @PropertyName("genderPreferenceAsString")
    public void setGenderPreferenceAsString(String genderPreferenceAsString) {
        this.male = genderPreferenceAsString.contains("Male");
        this.female = genderPreferenceAsString.contains("Female");
        this.neutral = genderPreferenceAsString.contains("Neutral");
    }

    @PropertyName("agePreferenceAsString")
    public void setAgePreferenceAsString(String agePreferenceAsString) {
        this.age16 = agePreferenceAsString.contains("16 and under");
        this.age17to36 = agePreferenceAsString.contains("17 to 36");
        this.age36 = agePreferenceAsString.contains("36+");
    }

    public String getExperienceAsString() {
        StringBuilder experience = new StringBuilder();
        if (this.beginner) experience.append("Beginner ");
        if (this.intermediate) experience.append("Intermediate ");
        if (this.expert) experience.append("Expert ");
        return experience.toString().trim();
    }

    public String getGenderPreferenceAsString() {
        StringBuilder gender = new StringBuilder();
        if (this.male) gender.append("Male ");
        if (this.female) gender.append("Female ");
        if (this.neutral) gender.append("Neutral ");
        return gender.toString().trim();
    }

    public String getAgePreferenceAsString() {
        StringBuilder age = new StringBuilder();
        if (this.age16) age.append("16 and under ");
        if (this.age17to36) age.append("17 to 36 ");
        if (this.age36) age.append("36+ ");
        return age.toString().trim();
    }
}
