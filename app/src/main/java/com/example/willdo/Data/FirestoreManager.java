package com.example.willdo.Data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.willdo.Model.List;
import com.example.willdo.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class FirestoreManager {
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int LOGIN_FAILED_INVALID_CREDENTIALS = 2;
    public static final int LOGIN_FAILED_UNKNOWN_ERROR = 3;

    private FirebaseAuth mAuth;

    public void initFSM() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateList(List list, final AddItemCallback callback) {
        String listId = list.getID();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not authenticated."));
            return;
        }
        db.collection("users").document(user.getUid()).collection("lists").document(listId).set(list).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    callback.onSuccess();
                else
                    callback.onFailure(task.getException());
            }
        });
    }
    public Task<Void> deleteList(List list){
        String listId = list.getID();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        return db.collection("users").document(user.getUid()).collection("lists")
                .document(listId).delete();
    }

    public void registerUser(String email, String password, RegistrationCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    User user = new User(firebaseUser.getUid(), email);
                    db.collection("users").document(firebaseUser.getUid())
                            .set(user)
                            .addOnCompleteListener(unused -> callback.onRegistrationSuccess())
                            .addOnFailureListener(e -> callback.onRegistrationFailed("Failed to create new user"));
                }
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    callback.onRegistrationFailed("Invalid email or password");
                } catch (FirebaseAuthUserCollisionException e) {
                    callback.onRegistrationFailed("Email already in use");
                } catch (Exception e) {
                    callback.onRegistrationFailed("Registration failed. Try again");
                }
            }
        });
    }

    public void loginUser(String email, String password, AuthenticationCallback callback, Context context) {
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onAuthenticationResult(LOGIN_SUCCESSFUL, context);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                callback.onAuthenticationResult(LOGIN_FAILED_INVALID_CREDENTIALS, context);
                            } catch (Exception e) {
                                callback.onAuthenticationResult(LOGIN_FAILED_UNKNOWN_ERROR, context);
                            }
                        }
                    }
                });
    }

    public void fetchUserLists(String userId, FetchListsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("lists")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null ) {
                            callback.onListsFetched(new ArrayList<>());
                        } else {
                            processFetchedLists(value, callback);
                        }
                    }
                });
    }

    private void processFetchedLists(QuerySnapshot queryDocumentSnapshots, FetchListsCallback callback) {
        java.util.List<List> userLists = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
            List list = documentSnapshot.toObject(List.class);
            list.setID(documentSnapshot.getId());
            userLists.add(list);
        }
        callback.onListsFetched(userLists);
    }

    public String createNewList(String listName, Context context,FirebaseAuth firebaseAuth, CreateListCallback callback){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            callback.onCreateFailed(new Exception("No current user"));
            return null;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newListRef = db.collection("users").document(user.getUid()).
                collection("lists").document();

        List newList = new List();
        newList.setTitle(listName);
        newList.setID(newListRef.getId());
        newList.addParticipant(user.getEmail());


        newListRef.set(new HashMap<String,Object>(){{
            put("title", newList.getTitle());
            put("participants", newList.getParticipants());
            put("completedItemsCount", newList.getCompletedItemsCount());
            put("items", newList.getItems());
        }})
                .addOnSuccessListener(e->callback.onListCreated(context, newList))
                .addOnFailureListener(e->callback.onCreateFailed(e));
        return newListRef.getId();
    }

    public interface AuthenticationCallback {
        void onAuthenticationResult(int resultCode, Context context);
    }

    public interface RegistrationCallback{
        void onRegistrationSuccess();
        void onRegistrationFailed(String msg);
    }

    public interface FetchListsCallback{
        void onListsFetched(java.util.List<List> lists);
        void onFetchFailed(Exception e);
    }

    public interface CreateListCallback{
        void onListCreated(Context context, List list);
        void onCreateFailed(Exception e);
    }

    public interface AddItemCallback {
        void onSuccess();

        void onFailure(Exception e);
    }
    public interface RemoveItemCallBack{
        void onSuccessDelete();
    }
}

