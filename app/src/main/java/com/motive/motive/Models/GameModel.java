package com.motive.motive.Models;

public class GameModel {
    private String gameID;

    private String hostID;

    private String[] playerIDs;

    private Number maxPlayers;

    private String gameType;

    private Number numPlayers;


    public GameModel(String gameID, String hostID, Number maxPlayers, String gameType){
        this.gameID = gameID;
        this.hostID = hostID;
        this.maxPlayers = maxPlayers;
        this.gameType = gameType;
    }
}
