package io.bitrise.trace.testapp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.testapp.R;

/**
 * A Fragment that have a child Fragment. Is a deprecated {@link Fragment} class. Supports the
 * same functionality with {@link ParentFragment}.
 */
public class ParentDeprecatedFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.deprecated_fragment_parent, container, false);
  }

  @Override
  public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final Button showChildButton = view.findViewById(R.id.show_child_button);
    showChildButton.setOnClickListener(v -> {
      getFragmentManager().beginTransaction()
                          .add(R.id.deprecated_fragment_frame, new ChildDeprecatedFragment())
                          .commit();
    });
  }
}
