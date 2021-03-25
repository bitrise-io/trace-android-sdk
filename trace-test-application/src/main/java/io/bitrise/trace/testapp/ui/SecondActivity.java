package io.bitrise.trace.testapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import io.bitrise.trace.testapp.R;

/**
 * Similar to {@link MainActivity}, this Activity can launch a new Activity, named {@link ThirdActivity} and can
 * navigate to {@link ParentFragment}.
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setupView();
    }

    /**
     * Sets up the view and the click listeners.
     */
    private void setupView() {
        final Button secondActivityButton = findViewById(R.id.third_activity_button);
        secondActivityButton.setOnClickListener(v -> {
            final Intent intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        });

        final Button parentFragmentButton = findViewById(R.id.fragment_parent_button);
        parentFragmentButton.setOnClickListener(v -> {
            final NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity);
            final NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.parent_fragment);
        });
    }
}
