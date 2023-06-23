package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private TextView loginBtn, usernameTxt, passwordTxt, retypePasswordTxt;
    Button signupBtn;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameTxt = findViewById(R.id.usernameEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        retypePasswordTxt = findViewById(R.id.retypePasswordEditText);
        loginBtn = findViewById(R.id.loginNavBtn);
        signupBtn = findViewById(R.id.signupButton);
        DB = new DBHelper(this);


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                String retypePassword = retypePasswordTxt.getText().toString();
                try {
                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(retypePassword)) {
                        Toast.makeText(signupBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                    }
                    else if(!password.equals(retypePassword)){
                        Toast.makeText(signupBtn.getContext(), "The password is not match", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        User user = new User(username, password);
                        long id = DB.register(user);
                        if (id != -1) {
                            // Registration successful
                            Toast.makeText(signupBtn.getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // Registration failed
                            Toast.makeText(signupBtn.getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (Exception ex){
                    System.out.println(ex);
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}