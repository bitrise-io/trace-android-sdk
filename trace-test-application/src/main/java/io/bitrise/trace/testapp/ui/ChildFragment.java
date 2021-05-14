package io.bitrise.trace.testapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.bitrise.trace.testapp.R;

/**
 * A {@link Fragment} class, used as a child of {@link ParentFragment}.
 */
public class ChildFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(@NonNull final LayoutInflater inflater,
                           @Nullable final ViewGroup container,
                           @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_child, container, false);
  }
}
