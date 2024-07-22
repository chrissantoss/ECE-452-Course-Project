package com.motive.motive.Models;

import com.google.firebase.auth.FirebaseUser;

public class UserModel  {
//    private String username;
//    private String userId;
//    private String fcmToken;
    private String UserID;
    private GameModel hostingGame=null;


    public UserModel(String Uid) {
        this.UserID=Uid;
    }

//    public UserModel(String username,String userId) {
//        this.username = username;
//        this.userId = userId;
//    }

    public boolean isHostingGame(){return hostingGame==null;}

    public void setUserID(String userID) {
        UserID = userID;
    }
    public String  getUserID(){
        return this.UserID;
    }

    public void setHostingGame(GameModel hostingGame) {
        this.hostingGame = hostingGame;
    }

    public GameModel getHostingGame() {
        return this.hostingGame;
    }
}
