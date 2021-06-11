package io.bitrise.trace.testapp.exception;

import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.SessionManager;
import io.bitrise.trace.testapp.R;
import okhttp3.OkHttpClient;

/**
 * Activity that will throw exceptions causing the app to crash.
 */
public class ExceptionActivity extends AppCompatActivity {

  /**
   * The {@link SessionManager} to control session starts and ends.
   */
  private final SessionManager sessionManager = ApplicationSessionManager.getInstance();

  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exception);
    setupView();
  }

  /**
   * Sets up the initial state of the view.
   */
  private void setupView() {
    final SwitchCompat sessionSwitch = findViewById(R.id.switch_session);
    setSessionState(sessionManager.getActiveSession() != null);
    sessionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setSessionState(isChecked));

    final Button exceptionInAppButton = findViewById(R.id.btn_exception_app);
    final Button exceptionInDependencyButton = findViewById(R.id.btn_exception_dependency);
    final Button exceptionInDependencyRethrownButton =
        findViewById(R.id.btn_exception_dependency_rethrow);
    final Button exceptionInMainThreadButton = findViewById(R.id.btn_exception_main_thread);
    final Button exceptionInThreadButton = findViewById(R.id.btn_exception_thread);

    exceptionInAppButton.setOnClickListener(v -> throwExceptionInApp());
    exceptionInDependencyButton.setOnClickListener(v -> throwExceptionInDependency());
    exceptionInDependencyRethrownButton.setOnClickListener(v -> rethrowExceptionFromDependency());
    exceptionInMainThreadButton.setOnClickListener(v -> throwExceptionInMainThread());
    exceptionInThreadButton.setOnClickListener(v -> throwExceptionInThread());
  }

  /**
   * Sets the state of the session for the {@link #sessionManager}.
   *
   * @param started {@code true} to start the session, {@code false} to stop.
   */
  private void setSessionState(final boolean started) {
    if (started) {
      sessionManager.startSession();
    } else {
      sessionManager.stopSession();
    }
  }

  /**
   * Method for throwing and exception in the applications code.
   */
  private void throwExceptionInApp() {
    throwException("App is not happy!");
  }

  /**
   * Method to throw an exception from a dependency's code.
   */
  private void throwExceptionInDependency() {
    throwExceptionInOkHttp();
  }

  /**
   * Catches and rethrows an exception.
   */
  private void rethrowExceptionFromDependency() {
    try {
      throwExceptionInOkHttp();
    } catch (final NullPointerException npe) {
      throw new UnsupportedOperationException("Exception caught and rethrown!", npe);
    }
  }

  /**
   * Throws an exception in the main thread. Should be called from the main thread.
   */
  private void throwExceptionInMainThread() {
    throwException("Main thread is not happy!");
  }

  /**
   * Creates a new thread and throws an exception.
   */
  private void throwExceptionInThread() {
    new Thread() {
      @Override
      public void run() {
        super.run();
        throwException(String.format("Thread with name %s is not happy",
            Thread.currentThread().getName()));
      }
    }.start();
  }

  /**
   * Throws an {@link UnsupportedOperationException}.
   *
   * @param extra adds this text to the message of the exception.
   */
  private void throwException(@NonNull final String extra) {
    throw new UnsupportedOperationException("We are having a bad time here! " + extra);
  }

  /**
   * Throws an exception in the code of OkHttp.
   */
  private void throwExceptionInOkHttp() {
    new OkHttpClient.Builder().socketFactory(null).build();
  }
}
