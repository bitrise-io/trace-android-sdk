package io.bitrise.trace.data.resource;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.dto.DataValues;

import static io.bitrise.trace.data.dto.DataValues.app;
import static io.bitrise.trace.data.dto.DataValues.br;
import static io.bitrise.trace.data.dto.DataValues.build;
import static io.bitrise.trace.data.dto.DataValues.carrier;
import static io.bitrise.trace.data.dto.DataValues.device;
import static io.bitrise.trace.data.dto.DataValues.id;
import static io.bitrise.trace.data.dto.DataValues.locale;
import static io.bitrise.trace.data.dto.DataValues.network;
import static io.bitrise.trace.data.dto.DataValues.os;
import static io.bitrise.trace.data.dto.DataValues.platform;
import static io.bitrise.trace.data.dto.DataValues.rooted;
import static io.bitrise.trace.data.dto.DataValues.session;
import static io.bitrise.trace.data.dto.DataValues.type;
import static io.bitrise.trace.data.dto.DataValues.version;

/**
 * Enum class for storing the different {@link io.opencensus.proto.resource.v1.Resource} labels.
 */
public enum ResourceLabel {
    APPLICATION_VERSION_CODE(DataValues.getName(app, build)),
    APPLICATION_ID(DataValues.getName(br, app, id)),
    APPLICATION_VERSION_NAME(DataValues.getName(app, version)),
    APPLICATION_PLATFORM(DataValues.getName(app, platform)),
    DEVICE_CARRIER(DataValues.getName(device, carrier)),
    DEVICE_LOCALE(DataValues.getName(device, locale)),
    DEVICE_ID(DataValues.getName(device, id)),
    DEVICE_NETWORK(DataValues.getName(device, network)),
    DEVICE_OS(DataValues.getName(os, version)),
    DEVICE_ROOTED(DataValues.getName(device, rooted)),
    DEVICE_TYPE(DataValues.getName(device, type)),
    SESSION_ID(DataValues.getName(app, session, id));

    private final String name;

    ResourceLabel(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }
}
