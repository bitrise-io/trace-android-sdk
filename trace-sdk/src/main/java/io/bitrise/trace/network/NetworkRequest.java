package io.bitrise.trace.network;

import androidx.annotation.NonNull;

import io.opencensus.proto.resource.v1.Resource;

/**
 * Abstract data class for requests sent to the backend with the {@link NetworkCommunicator}.
 */
public abstract class NetworkRequest {

    @NonNull
    Resource resource;

    @NonNull
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
