package com.swiftkaydevelopment.testing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnconnect;  //this button takes user to Listview activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checks to make sure application signature hasnt changed ie: app was recompiled
        ApkSecurityManager sec = new ApkSecurityManager(MainActivity.this);
        sec.verifyApk();

        btnconnect = (Button) findViewById(R.id.btnconnect);

        btnconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.swiftkaydevelopment.testing.DRAGGABLEEXAMPLEACTIVITY");
                startActivity(i);
                finish();
            }
        });
    }
}
