package com.genedev.ink_socialmedia;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;


public class Login extends Activity {

    private TextInputLayout userName, passWord;
    private Button login, register;
    private Snackbar snackbar;
    private String email,pass;
    private ProgressDialog progressDialog;
    private static int TIME_OUT = 3000;
    private View v;

    private HelperUtils helperUtils;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);

        v = findViewById(R.id.act_login);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Registration.class);
                startActivity(i);
                Login.this.finish();
            }
        });

        if (mAuth.getCurrentUser()!=null){
            mAuth.signOut();
        }
        else{
            emptyFields("Login your account");
        }
        checkConnection();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Snackbar.make(v, "You are connected through a wifi", Snackbar.LENGTH_SHORT).show();

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Snackbar.make(v, "You are connected through a data", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(v, "Connection unavailable", Snackbar.LENGTH_LONG).setAction("Retry", (new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }));
            login.setEnabled(false);
            register.setEnabled(false);
        }
    }

    private void emptyFields(String str){
        Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
    }

    private void loginUser(){

        email = userName.getEditText().getText().toString().trim();
        pass = passWord.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            emptyFields("Email is required");
            userName.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emptyFields("Enter a valid email address");
            userName.requestFocus();
            return;
        }
        else if (pass.isEmpty()){
            emptyFields("Password is required");
            passWord.requestFocus();
            return;
        }
        else if (passWord.getEditText().length()<8){
            emptyFields("Password must be 8 characters");
            passWord.requestFocus();
            return;
        }

        else{
            signInWithEmailPassword();
            progressDialog = new ProgressDialog(Login.this,R.style.AlertDialogTheme);
            progressDialog.setMessage("Logging in");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, TIME_OUT);
        }

    }
    private void signInWithEmailPassword(){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            emptyFields("Successfully logged in");
                            mAuth.getCurrentUser();
                            Intent mainIntent = new Intent(Login.this, Home.class);
                            Login.this.startActivity(mainIntent);
                            Login.this.finish();

                        } else {
                            progressDialog.dismiss();
                            emptyFields("Invalid account credentials");
                        }
                    }
                });
    }
}

