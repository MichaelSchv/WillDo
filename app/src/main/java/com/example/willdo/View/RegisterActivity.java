package com.example.willdo.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;



public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText register_ET_email;
    private TextInputEditText register_ET_password;
    private MaterialButton register_BTN_register;
    private FirestoreManager firestoreManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        firestoreManager = new FirestoreManager();
        firestoreManager.initFSM();
        register_BTN_register.setOnClickListener(v->registerNewUser());
    }

    private void registerNewUser() {
        String email = register_ET_email.getText().toString().trim();
        String password = register_ET_password.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            firestoreManager.registerUser(email, password, new FirestoreManager.RegistrationCallback() {

                @Override
                public void onRegistrationSuccess() {
                    Toast.makeText(RegisterActivity.this, "Registration Succeeded!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onRegistrationFailed(String msg) {
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void findViews() {
        register_ET_email = findViewById(R.id.register_ET_email);
        register_ET_password = findViewById(R.id.register_ET_password);
        register_BTN_register = findViewById(R.id.register_BTN_register);
    }
}