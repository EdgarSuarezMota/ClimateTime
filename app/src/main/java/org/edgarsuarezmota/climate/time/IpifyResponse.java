package org.edgarsuarezmota.climate.time;

import com.google.gson.annotations.SerializedName;

public class IpifyResponse {

    @SerializedName("ip")
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }
}

