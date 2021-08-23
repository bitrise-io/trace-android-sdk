package io.bitrise.trace.network;

import static java.lang.Thread.sleep;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.bitrise.trace.data.CrashTestDataProvider;
import io.bitrise.trace.data.storage.DataStorage;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Unit tests for {@link CrashSender}.
 */
public class CrashSenderTest {

  final NetworkCommunicator mockCommunicator = Mockito.mock(NetworkCommunicator.class);
  final DataStorage mockDataStorage = Mockito.mock(DataStorage.class);

  @Before
  public void setup() {
    NetworkClient.networkCommunicator = mockCommunicator;
  }

  @After
  public void tearDown() {
    NetworkClient.networkCommunicator = null;
  }

  @Test
  public void send_isSuccessful() throws InterruptedException {
    final CrashSender sender = new CrashSender(
        CrashTestDataProvider.createCrashRequestWithUuid("uuid1"), mockDataStorage);

    final Call<Void> mockedCall = Mockito.mock(Call.class);
    Mockito.when(mockCommunicator.sendCrash(any())).thenReturn(mockedCall);

    Mockito.doAnswer(invocation -> {
      Callback<Void> callback = invocation.getArgument(0, Callback.class);
      callback.onResponse(mockedCall, Response.success(null));
      return null;
    }).when(mockedCall).enqueue(any(Callback.class));

    // when i call send
    sender.send();

    // this happens asynchronously, and can take a few milliseconds to actually get called.
    sleep(100);

    // the request should be removed
    verify(mockDataStorage, times(1))
        .deleteCrashRequest("uuid1");
  }

  @Test
  public void send_isNotSuccessful() throws InterruptedException {
    final CrashSender sender = new CrashSender(
        CrashTestDataProvider.createCrashRequestWithUuid("uuid2"), mockDataStorage);

    final Call<Void> mockedCall = Mockito.mock(Call.class);
    Mockito.when(mockCommunicator.sendCrash(any())).thenReturn(mockedCall);

    Mockito.doAnswer(invocation -> {
      Callback<Void> callback = invocation.getArgument(0, Callback.class);
      final ResponseBody mockResponseBody = Mockito.mock(ResponseBody.class);
      callback.onResponse(mockedCall, Response.error(503, mockResponseBody));
      return null;
    }).when(mockedCall).enqueue(any(Callback.class));

    // when i call send
    sender.send();

    // this happens asynchronously, and can take a few milliseconds to actually get called.
    sleep(100);

    // the request should be removed
    verify(mockDataStorage, times(1))
        .updateCrashRequestSentAttemptCounter("uuid2");
  }

  @Test
  public void send_onFailure() throws InterruptedException {
    final CrashSender sender = new CrashSender(
        CrashTestDataProvider.createCrashRequestWithUuid("uuid3"), mockDataStorage);

    final Call<Void> mockedCall = Mockito.mock(Call.class);
    Mockito.when(mockCommunicator.sendCrash(any())).thenReturn(mockedCall);

    Mockito.doAnswer(invocation -> {
      Callback<Void> callback = invocation.getArgument(0, Callback.class);
      callback.onFailure(mockedCall, new IOException());
      return null;
    }).when(mockedCall).enqueue(any(Callback.class));

    // when i call send
    sender.send();

    // this happens asynchronously, and can take a few milliseconds to actually get called.
    sleep(100);

    // the request should be removed
    verify(mockDataStorage, times(1))
        .updateCrashRequestSentAttemptCounter("uuid3");
  }

}
