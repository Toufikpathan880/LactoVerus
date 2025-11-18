package com.example.lactoverus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtPhone, edtPassword;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));
    }

    private void registerUser() {

        String fullName = edtFullName.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Enter name");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Enter email");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Enter phone");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Enter password");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String userId = auth.getCurrentUser().getUid();

                        DocumentReference ref = firestore.collection("Users").document(userId);

                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("email", email);
                        user.put("phone", phone);

                        ref.set(user)
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show());

                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }
}
