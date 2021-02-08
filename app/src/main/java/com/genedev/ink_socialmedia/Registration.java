package com.genedev.ink_socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.genedev.ink_socialmedia.RegistrationUtils.RegistrationFragmentUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

public class Registration extends Activity {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Registration.this, RegistrationFragmentUtils.class);
                Registration.this.startActivity(i);
                Registration.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
