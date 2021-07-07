package io.bitrise.trace.plugin.network;

import javax.inject.Singleton;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Holds the list of network calls for the symbol collector endpoint.
 */
@Singleton
public interface SymbolCollectorCommunicator {
  @Multipart
  @POST("symbols/android")
  Call<ResponseBody> uploadMappingFile(
      @Header("app_version") String appVersion,
      @Header("Authorization") String token,
      @Header("build_version") String buildId,
      @Part("body") RequestBody body,
      @Part MultipartBody.Part file);
}
