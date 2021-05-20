package io.bitrise.trace.testapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import io.bitrise.trace.testapp.R;

/**
 * Main Activity for the test application. Can launch {@link SecondActivity} and navigate to
 * {@link ParentFragment}.
 */
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupView();
  }

  /**
   * Sets up the view and the click listeners.
   */
  private void setupView() {
    final Button secondActivityButton = findViewById(R.id.second_activity_button);
    secondActivityButton.setOnClickListener(v -> {
      final Intent intent = new Intent(this, SecondActivity.class);
      startActivity(intent);
    });

    final Button parentFragmentButton = findViewById(R.id.fragment_parent_button);
    parentFragmentButton.setOnClickListener(v -> {
      final NavHostFragment navHostFragment =
          (NavHostFragment) getSupportFragmentManager()
              .findFragmentById(R.id.nav_host_fragment_activity);
      final NavController navController = navHostFragment.getNavController();
      navController.navigate(R.id.parent_fragment);
    });
  }
}
