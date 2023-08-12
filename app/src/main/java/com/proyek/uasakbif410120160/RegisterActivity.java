package com.proyek.uasakbif410120160;
/**
 * NIM : 10120160
 * Nama : Rendi Julianto
 * Kelas : IF-4
 */
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText emailRegister;
    EditText passwordRegister;
    Button Register;
    TextView Login;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }
        emailRegister = findViewById(R.id.et_email);
        passwordRegister = findViewById(R.id.et_password);
        Login = findViewById(R.id.tvLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        Register = findViewById(R.id.btn_register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailRegister.getText().toString().trim();
                String password = passwordRegister.getText().toString().trim();
                boolean isEmptyFields = false;
                if(TextUtils.isEmpty(email)) {
                    isEmptyFields = true;
                    emailRegister.setError("Field ini tidak boleh kosong!");
                }
                if(TextUtils.isEmpty(password)) {
                    isEmptyFields = true;
                    passwordRegister.setError("Field ini tidak boleh kosong!");
                }
                if(!isEmptyFields) {
                    Register.setText("Loading...");
                    Register.setEnabled(false);
                    emailRegister.setEnabled(false);
                    passwordRegister.setEnabled(false);
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Register.setText("Daftar");
                            Register.setEnabled(true);
                            emailRegister.setEnabled(true);
                            passwordRegister.setEnabled(true);
                            if (task.isSuccessful()) {
                                emailRegister.setText("");
                                passwordRegister.setText("");
                                Toast.makeText(RegisterActivity.this, "Berhasil Melakukan Pendaftaran", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }


}