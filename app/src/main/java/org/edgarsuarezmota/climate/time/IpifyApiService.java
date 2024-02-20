package org.edgarsuarezmota.climate.time;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IpifyApiService {

    @GET("?format=json")
    Call<IpifyResponse> getIpAddress();
}

