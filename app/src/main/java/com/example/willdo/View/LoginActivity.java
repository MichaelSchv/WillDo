package com.example.willdo.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    private TextInputEditText login_ET_email;
    private TextInputEditText login_ET_password;
    private MaterialButton login_BTN_login;
    private MaterialTextView login_MTV_register;

    private FirestoreManager firestoreManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        firestoreManager = new FirestoreManager();
        firestoreManager.initFSM();
        handleLoginButton();
        handleRegister();
    }

    private void handleLoginButton() {
        login_BTN_login.setOnClickListener(v->loginUser());
    }

    private void loginUser() {
        String email = login_ET_email.getText().toString().trim();
        String password = login_ET_password.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            firestoreManager.loginUser(email, password, new FirestoreManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationResult(int resultCode, Context context) {
                    switch (resultCode){
                        case FirestoreManager.LOGIN_SUCCESSFUL:
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ListsActivity.class);
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            intent.putExtra("used_id", currentUser.getUid());
                            startActivity(intent);
                            finish();


                        case FirestoreManager.LOGIN_FAILED_INVALID_CREDENTIALS:
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();

                            break;

                        case FirestoreManager.LOGIN_FAILED_UNKNOWN_ERROR:
                            Toast.makeText(context, "Login failed. Try again", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, this);

        }
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
        {
            Intent intent = new Intent(LoginActivity.this, ListsActivity.class);
            intent.putExtra("used_id", currentUser.getUid());
            startActivity(intent);
        }


    }
*/
    private void handleRegister() {
        login_MTV_register.setOnClickListener(v->{
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void findViews() {
        login_ET_email = findViewById(R.id.login_ET_email);
        login_ET_password = findViewById(R.id.login_ET_password);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_MTV_register = findViewById(R.id.login_MTV_register);
    }
}