package com.genedev.ink_socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;

import com.genedev.ink_socialmedia.Utils.HelperUtils;
import com.google.firebase.auth.FirebaseAuth;


public class Splashscreen extends Activity {
    private static int TIME_OUT = 1000;

    private HelperUtils helperUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null){
                    Intent i = new Intent(Splashscreen.this,Home.class);
                    startActivity(i);
                    Splashscreen.this.finish();
                }
                else{
                    mAuth.signOut();
                    Intent i = new Intent(Splashscreen.this,Login.class);
                    startActivity(i);
                    Splashscreen.this.finish();
                }

                //FirebaseUser currentUser = mAuth.getCurrentUser();
                //updateUI(currentUser);
            }
        }, TIME_OUT);
    }
}
