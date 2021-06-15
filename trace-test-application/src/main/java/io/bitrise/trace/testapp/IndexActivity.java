package io.bitrise.trace.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import io.bitrise.trace.TraceSdk;
import io.bitrise.trace.testapp.exception.ExceptionActivity;
import io.bitrise.trace.testapp.network.NetworkActivity;
import io.bitrise.trace.testapp.ui.MainActivity;

/**
 * Entry point for the app.
 */
public class IndexActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_index);

    TraceSdk.setDebugEnabled(true);

    final Button btnUiTests = findViewById(R.id.btn_ui_tests);
    final Button btnNetworkTests = findViewById(R.id.btn_network_tests);
    final Button btnExceptionTests = findViewById(R.id.btn_exception_tests);

    btnUiTests.setOnClickListener(
        v -> startActivity(new Intent(IndexActivity.this, MainActivity.class)));

    btnNetworkTests.setOnClickListener(
        v -> startActivity(new Intent(IndexActivity.this, NetworkActivity.class)));

    btnExceptionTests.setOnClickListener(
        v -> startActivity(new Intent(IndexActivity.this, ExceptionActivity.class)));
  }
}