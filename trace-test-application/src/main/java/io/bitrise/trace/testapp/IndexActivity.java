package io.bitrise.trace.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.bitrise.trace.TraceSdk;
import io.bitrise.trace.testapp.network.NetworkActivity;
import io.bitrise.trace.testapp.ui.MainActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        TraceSdk.setDebugEnabled(true);

        Button btnUiTests = findViewById(R.id.btn_ui_tests);
        Button btnNetworkTests = findViewById(R.id.btn_network_tests);

        btnUiTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, MainActivity.class));
            }
        });

        btnNetworkTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, NetworkActivity.class));
            }
        });
    }
}