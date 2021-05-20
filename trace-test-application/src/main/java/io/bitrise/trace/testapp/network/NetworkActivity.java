package io.bitrise.trace.testapp.network;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import io.bitrise.trace.testapp.R;

/**
 * Shows the networking related UI tests.
 */
public class NetworkActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_network);

    final Button btnOkhttp = findViewById(R.id.btn_network_okhttp);
    final Button btnUrlConnection = findViewById(R.id.btn_network_urlconnection);

    btnOkhttp.setOnClickListener(v -> {
      startActivity(new Intent(NetworkActivity.this, OkHttpActivity.class));
    });

    btnUrlConnection.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(NetworkActivity.this, UrlConnectionActivity.class));
      }
    });
  }
}