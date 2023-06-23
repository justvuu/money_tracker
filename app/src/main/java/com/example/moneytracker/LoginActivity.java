package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    DBHelper DB;
    EditText usernameTxt, passwordTxt;
    Button loginBtn;
    TextView registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameTxt = findViewById(R.id.usernameEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);
        registerBtn = findViewById(R.id.registerNavBtn);

        DB = new DBHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                try {
                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                        Toast.makeText(loginBtn.getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        boolean check = DB.login(username, password);
                        if(check){
                            Toast.makeText(loginBtn.getContext(), "Success", Toast.LENGTH_SHORT).show();
                            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(loginBtn.getContext(), "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (Exception ex){
                    System.out.println(ex);
                }


            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                catch (Exception ex){
                    System.out.println(ex);
                }

            }
        });
    }
}