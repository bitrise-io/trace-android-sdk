package io.bitrise.trace.plugin.network;

import javax.inject.Singleton;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Holds the list of network calls for the symbol collector endpoint.
 */
@Singleton
public interface SymbolCollectorCommunicator {
  @POST("symbols/android")
  Call<ResponseBody> uploadMappingFile(
      @Header("Authorization") String token,
      @Header("build_version") String buildCode,
      @Header("app_version") String versionName,
      @Body RequestBody body);
}
