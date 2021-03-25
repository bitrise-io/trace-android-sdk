package io.bitrise.trace.testapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.bitrise.trace.testapp.R;

/**
 * Similar to {@link MainActivity} and {@link SecondActivity}, but this is a {@link Activity} and not
 * {@link AppCompatActivity} to test the deprecated {@link android.app.Fragment} behaviour.
 * Can navigate to {@link ParentDeprecatedFragment}.
 */
public class ThirdActivity extends Activity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setupView();
    }

    /**
     * Sets up the view and the click listeners.
     */
    private void setupView() {
        final Button parentFragmentButton = findViewById(R.id.fragment_parent_button);
        parentFragmentButton.setOnClickListener(v -> getFragmentManager().beginTransaction()
                                                                         .add(R.id.deprecated_activity_frame,
                                                                                 new ParentDeprecatedFragment())
                                                                         .commit());
    }
}
