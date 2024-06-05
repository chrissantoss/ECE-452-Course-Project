package com.motive.motive;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import org.w3c.dom.DocumentType;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }

    public static DocumentReference currentUserInfo() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
}
