package io.bitrise.trace.network;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import io.opencensus.proto.resource.v1.Resource;

/**
 * Abstract data class for requests sent to the backend with the {@link NetworkCommunicator}.
 */
public abstract class NetworkRequest {

  @SerializedName("resource")
  @NonNull
  Resource resource;

  @NonNull
  public Resource getResource() {
    return resource;
  }

  public void setResource(@NonNull final Resource resource) {
    this.resource = resource;
  }
}
