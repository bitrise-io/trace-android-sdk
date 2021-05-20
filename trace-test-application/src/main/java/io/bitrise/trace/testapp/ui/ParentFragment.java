package io.bitrise.trace.testapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import io.bitrise.trace.testapp.R;

/**
 * A Fragment that have a child Fragment. Is a {@link Fragment} class. Supports the same
 * functionality with {@link ParentDeprecatedFragment}.
 */
public class ParentFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull final LayoutInflater inflater,
                           @Nullable final ViewGroup container,
                           @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_parent, container, false);
  }

  @Override
  public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final Button showChildButton = view.findViewById(R.id.show_child_button);
    showChildButton.setOnClickListener(v -> {
      final NavHostFragment navHostFragment =
          (NavHostFragment) getChildFragmentManager()
              .findFragmentById(R.id.nav_host_fragment_fragment);
      final NavController navController = navHostFragment.getNavController();
      navController.navigate(R.id.child_fragment);
    });
  }
}
