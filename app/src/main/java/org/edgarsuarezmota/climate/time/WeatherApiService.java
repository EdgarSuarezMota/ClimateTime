package org.edgarsuarezmota.climate.time;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("forecast.json")
    Call<WeatherEntity> getWeather(
            @Query("key") String apiKey,
            @Query("q") String city,
            @Query("lang") String language,
            @Query("days") int numberOfDays
    );
}
