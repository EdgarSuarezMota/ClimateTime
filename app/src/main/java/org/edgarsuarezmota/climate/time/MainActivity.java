package org.edgarsuarezmota.climate.time;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static String deviceLanguage = null;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String IPIFY_BASE_URL = "https://api.ipify.org/";
    private static final String API_KEY = "3d5da8ba18264dcfb3f192447232111";
    private static Double lat = null;
    private static String ip = null;
    private static Double lon = null;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private RecyclerView recyclerView;
    private WeatherApiService weatherApiService;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private Context context = this;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener el formato de hora del dispositivo móvil
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
        setContentView(R.layout.activity_main);

        // Obtener la hora actual
        String currentTime = getCurrentTime(is24HourFormat);

        // Cambiar el fondo según el rango de horas
        changeBackgroundBasedOnTime(currentTime, is24HourFormat);
        deviceLanguage = obtenerIdiomaDispositivo();
        // Verificar la conexión a Internet
        if (!isInternetAvailable()) {
            showProgressBar();
            showNoInternetAlert();

        } else {
            showProgressBar();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Verificar permisos
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permisos ya concedidos, obtener la ubicación
                    // Verificar si la ubicación está activada
                    if (isLocationEnabled()) {
                        // Si la ubicación está activada, continuar con el resto del código
                        obtenerUbicacion(context);
                    } else {
                        // Si la ubicación no está activada, mostrar alerta
                        showLocationSettingsAlert();
                    }

                } else {
                    // Solicitar permisos
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            } else {
                if (!isInternetAvailable()) {
                    showProgressBar();
                    showNoInternetAlert();

                } else {
                    obtenerDireccionIP();
                }
            }
        }
        BottomNavigationView bar = findViewById(R.id.bottom_navigation);
        // Establece el elemento seleccionado en el BottomNavigationView
        bar.getMenu().findItem(R.id.page_1).setChecked(true);
        bar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                // Obtén el ID del elemento seleccionado
                int itemId = item.getItemId();

                if (itemId == R.id.page_2) {
                    intent = new Intent(context, Manana.class);
                    // Inicia la actividad
                    startActivity(intent);
                    finish();
                } else if (itemId == R.id.page_3) {
                    intent = new Intent(context, Dia2.class);
                    // Inicia la actividad
                    startActivity(intent);
                    finish();
                }

                // Devuelve true para indicar que el evento fue manejado
                return true;
            }
        });


    }

    private void makeApiCall() {
        recyclerView = findViewById(R.id.rv_hours);

        // Configuración de Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        // Crear una instancia de WeatherApiService
        weatherApiService = retrofit.create(WeatherApiService.class);
        // Lista para almacenar los resultados de las solicitudes
        List<WeatherEntity.Forecast.ForecastDay.Hour> allWeatherData = new ArrayList<>();

        Call<WeatherEntity> call;
        if (ip == null) {
            call = weatherApiService.getWeather(API_KEY, lat + "," + lon, deviceLanguage, 1);
        } else {
            call = weatherApiService.getWeather(API_KEY, ip, deviceLanguage, 1);
        }
        call.enqueue(new Callback<WeatherEntity>() {
            @Override
            public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                if (response.isSuccessful()) {
                    WeatherEntity weatherEntity = response.body();
                    Log.i("WeatherEntity", weatherEntity.toString());
                    // Agregar los datos necesarios
                    if (weatherEntity != null && weatherEntity.getForecast() != null && weatherEntity.getForecast().getForecastday() != null && !weatherEntity.getForecast().getForecastday().isEmpty() && weatherEntity.getForecast().getForecastday().get(0).getHour() != null) {
                        // Obtener la lista de horas condicionalmente
                        List<WeatherEntity.Forecast.ForecastDay.Hour> hours = null;
                        hours = weatherEntity.getForecast().getForecastday().get(0).getHour();
                        // Convertir la cadena de fecha y hora a solo hora y agregarla a la lista
                        for (WeatherEntity.Forecast.ForecastDay.Hour hour : hours) {
                            String originalTime = hour.getTime();
                            String extractedHour = extractHourFromDateTime(originalTime);
                            hour.setTime(extractedHour);
                        }

                        // Agregar los datos a la lista principal
                        allWeatherData.addAll(hours);
                        Log.i("hours", hours.toString());

                        configureRecyclerView(allWeatherData);
                    } else {
                        Log.e("Retrofit", "Respuesta vacía o estructura incorrecta");
                    }
                } else {
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherEntity> call, Throwable t) {
                Log.e("Retrofit", "Error en la solicitud a la API", t);
            }
        });
    }

    private void configureRecyclerView(List<WeatherEntity.Forecast.ForecastDay.Hour> weatherList) {
        // Configurar el LinearLayoutManager y el adaptador
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Crear el adaptador y asignarlo al RecyclerView
        weatherAdapter = new WeatherAdapter(this, weatherList);
        recyclerView.setAdapter(weatherAdapter);
        hideProgressBar();
    }

    // Método para extraer la hora de una cadena de fecha y hora
    private String extractHourFromDateTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = inputFormat.parse(dateTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateParsing", "Error al analizar la fecha y hora", e);
            return "";
        }
    }

    private void getCurrentWeatherInfo(WeatherApiService weatherApiService) {
        // Configuración de Retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Log.i("numero", "aaa");
        // Crear una instancia de WeatherApiService
        weatherApiService = retrofit.create(WeatherApiService.class);
        deviceLanguage = obtenerIdiomaDispositivo();
        Call<WeatherEntity> currentWeatherCall;
        if (ip == null) {
            currentWeatherCall = weatherApiService.getWeather(API_KEY, lat + "," + lon, deviceLanguage, 1);
        } else {
            currentWeatherCall = weatherApiService.getWeather(API_KEY, ip, deviceLanguage, 1);
        }
        Log.d("hhhh", "Latitud: " + lat + ", Longitud: " + lon);

        currentWeatherCall.enqueue(new Callback<WeatherEntity>() {
            @Override
            public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                if (response.isSuccessful()) {
                    WeatherEntity weatherEntity = response.body();

                    // Verificar que la entidad y la información actual no sean nulas
                    if (weatherEntity != null && weatherEntity.getCurrent() != null) {
                        String location = weatherEntity.getLocation().getName();
                        double temperature = weatherEntity.getCurrent().getTemp_c();

                        // Actualizar los TextViews con la información actual
                        updateCurrentWeatherViews(location, temperature);
                        Log.e("Retrofit", weatherEntity.toString());
                    } else {
                        Log.e("Retrofit", "Respuesta vacía o estructura incorrecta para la información actual");
                    }
                } else {
                    Log.e("Retrofit", "Error al obtener la información actual: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherEntity> call, Throwable t) {
                Log.e("Retrofit", "Error en la solicitud de información actual a la API", t);
            }
        });
    }

    private void updateCurrentWeatherViews(String location, double temperature) {
        // Actualizar los TextViews con la información actual
        TextView tvLocation = findViewById(R.id.tv_location);
        TextView tvTemperature = findViewById(R.id.tv_degrees_current);

        tvLocation.setText(location);
        tvTemperature.setText(temperature + " °C");
    }

    // Manejar la respuesta del usuario a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permisos concedidos, obtener la ubicación
                    if (isLocationEnabled()) {
                        // Si la ubicación está activada, continuar con el resto del código
                        obtenerUbicacion(context);
                    } else {
                        // Si la ubicación no está activada, mostrar alerta
                        showLocationSettingsAlert();
                    }

                } else {
                    // Manejar el caso donde se niegan los permisos
                    Log.d("Permiso", "Permiso de ubicación denegado");
                    obtenerDireccionIP();

                }
                return;
            }
            // Manejar otros casos de solicitud de permisos si es necesario
        }
    }


    private void obtenerUbicacion(Context context) {
        if (!isInternetAvailable()) {
            showProgressBar();
            showNoInternetAlert();

        } else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            weatherApiService = retrofit.create(WeatherApiService.class);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d("hhhhh", "Latitud: " + location.getLatitude() + ", Longitud: " + location.getLongitude());
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        stopLocationUpdates();
                        if (!isInternetAvailable()) {
                            showProgressBar();
                            showNoInternetAlert();

                        } else {
                            getCurrentWeatherInfo(weatherApiService);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isInternetAvailable()) {
                                    showProgressBar();
                                    showNoInternetAlert();

                                } else {
                                    // Coloca aquí tu código de solicitud a la API con un retraso de 5 segundos
                                    makeApiCall();
                                }

                            }
                        }, 4000);
                    }
                }
            }
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates(context, fusedLocationClient, locationCallback);
        } else {
            // Manejar la situación cuando los permisos no están concedidos
        }
    }

    private void startLocationUpdates(Context context, FusedLocationProviderClient fusedLocationClient, LocationCallback locationCallback) {
        try {
            LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(5000).setFastestInterval(1000);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            Log.e("Location", "Excepción de seguridad al iniciar la actualización de ubicación", e);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback);
                Log.d("Location", "Actualizaciones de ubicación detenidas");
            } catch (Exception e) {
                Log.e("Location", "Error al detener las actualizaciones de ubicación", e);
            }
        }
    }


    // Método para obtener el idioma del dispositivo
    private String obtenerIdiomaDispositivo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Obtener el idioma directamente desde la configuración del dispositivo
            return getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            // Versiones anteriores a Nougat
            return getResources().getConfiguration().locale.getLanguage();
        }
    }

    public void obtenerDireccionIP() {

        if (!isInternetAvailable()) {
            showProgressBar();
            showNoInternetAlert();

        } else {


            // Configuración de Retrofit para la API de ipify
            Retrofit retrofit = new Retrofit.Builder().baseUrl(IPIFY_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            // Crear una instancia de IpifyApiService
            IpifyApiService ipifyApiService = retrofit.create(IpifyApiService.class);

            // Hacer la llamada a la API
            Call<IpifyResponse> call = ipifyApiService.getIpAddress();
            call.enqueue(new Callback<IpifyResponse>() {
                @Override
                public void onResponse(Call<IpifyResponse> call, Response<IpifyResponse> response) {
                    if (response.isSuccessful()) {
                        IpifyResponse ipifyResponse = response.body();
                        if (ipifyResponse != null) {
                            ip = ipifyResponse.getIpAddress();
                            if (!isInternetAvailable()) {
                                showProgressBar();
                                showNoInternetAlert();

                            } else {
                                getCurrentWeatherInfo(weatherApiService);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isInternetAvailable()) {
                                        showProgressBar();
                                        showNoInternetAlert();

                                    } else {
                                        // Coloca aquí tu código de solicitud a la API con un retraso de 5 segundos
                                        makeApiCall();
                                    }
                                }
                            }, 4000);
                            // Haz algo con la dirección IP obtenida
                            System.out.println("Dirección IP: " + ip);
                        }
                    } else {
                        // Manejar errores en la respuesta
                        System.out.println("Error en la respuesta de la API: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<IpifyResponse> call, Throwable t) {
                    // Manejar errores de la solicitud
                    System.out.println("Error en la solicitud a la API: " + t.getMessage());
                }
            });
        }
    }

    // Función para mostrar un ProgressBar y ocultar otros elementos
    private void showProgressBar() {
        // Obtén referencias a los elementos que deseas ocultar
        // Puedes ajustar esto según la estructura de tu diseño
        RecyclerView recyclerView = findViewById(R.id.rv_hours);
        TextView locationTextView = findViewById(R.id.tv_location);
        TextView temperatureTextView = findViewById(R.id.tv_degrees_current);
        View view = findViewById(R.id.view);
        // Obtén una referencia al ProgressBar que deseas mostrar
        ProgressBar progressBar = findViewById(R.id.progressBar);
        view.setVisibility(View.GONE);
        // Oculta los elementos que no deseas mostrar
        recyclerView.setVisibility(View.GONE);
        locationTextView.setVisibility(View.GONE);
        temperatureTextView.setVisibility(View.GONE);

        // Muestra el ProgressBar
        progressBar.setVisibility(View.VISIBLE);
    }

    // Función para ocultar el ProgressBar y mostrar otros elementos
    private void hideProgressBar() {
        // Obtén referencias a los elementos que ocultaste
        RecyclerView recyclerView = findViewById(R.id.rv_hours);
        TextView locationTextView = findViewById(R.id.tv_location);
        TextView temperatureTextView = findViewById(R.id.tv_degrees_current);
        View view = findViewById(R.id.view);
        // Obtén una referencia al ProgressBar que mostraste
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Oculta el ProgressBar
        progressBar.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        // Muestra los elementos que ocultaste
        recyclerView.setVisibility(View.VISIBLE);
        locationTextView.setVisibility(View.VISIBLE);
        temperatureTextView.setVisibility(View.VISIBLE);
    }

    // Función para verificar si la ubicación está activada
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Función para mostrar una alerta si la ubicación no está activada
    private void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Tienes que activar la ubicación");

        // Configurar el botón para abrir la configuración de ubicación
        alertDialog.setPositiveButton("Ir a configuración", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // Configurar el botón para cerrar la aplicación
        alertDialog.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Mostrar la alerta
        alertDialog.show();
    }

    // Método para verificar la conexión a Internet
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    // Método para mostrar una alerta cuando no hay conexión a Internet
    private void showNoInternetAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Error de conexión");
        alertDialog.setMessage("No hay conexión a Internet. Por favor, verifica tu conexión y vuelve a intentarlo.");

        // Configurar el botón para cerrar la aplicación
        alertDialog.setPositiveButton("Cerrar", (dialog, which) -> finish());

        // Mostrar la alerta
        alertDialog.show();
    }


    private void changeBackgroundBasedOnTime(String currentTime, boolean is24HourFormat) {
        try {
            // Seleccionar el formato de la hora
            String formatPattern = is24HourFormat ? "HH:mm" : "hh:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, Locale.getDefault());
            Date currentDate = sdf.parse(currentTime);

            // Imprimir la hora actual para verificar
            Log.d("Hora actual", sdf.format(currentDate));

            // Definir rangos de horas para cambiar el fondo
            Date morningStart = null;
            Date afternoonStart = null;
            Date eveningStart = null;

            // Parsear las horas en formato de 12 horas
            if (!is24HourFormat) {
                // Si es formato de 12 horas, añadir "AM" o "PM" según sea necesario
                morningStart = sdf.parse("07:00 AM");
                afternoonStart = sdf.parse("06:00 PM");
                eveningStart = sdf.parse("09:00 PM");
            } else {
                // Si es formato de 24 horas, se pueden utilizar las horas directamente
                morningStart = sdf.parse("07:00");
                afternoonStart = sdf.parse("18:00");
                eveningStart = sdf.parse("21:00");
            }


            // Imprimir las horas de inicio para verificar
            Log.d("Hora de la mañana", sdf.format(morningStart));
            Log.d("Hora de la tarde", sdf.format(afternoonStart));
            Log.d("Hora de la noche", sdf.format(eveningStart));

            // Definir rangos de horas y nombres de archivos para cambiar el fondo
            String imageName;
            if (is24HourFormat) {
                imageName = (currentDate.after(morningStart) && currentDate.before(afternoonStart)) ? "fondo1" :
                        (currentDate.after(afternoonStart) && currentDate.before(eveningStart)) ? "fondo2" : "fondo3";
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

                // Imprimir la hora del día para verificar
                Log.d("Hora del día", String.valueOf(hourOfDay));

                imageName = (hourOfDay >= 5 && hourOfDay < 18) ? "fondo1" :
                        (hourOfDay >= 18 && hourOfDay < 21) ? "fondo2" : "fondo3";
            }

            // Imprimir el nombre de la imagen seleccionada
            Log.d("Imagen seleccionada", imageName);

            // Obtener la referencia al ConstraintLayout con id "day"
            ImageView dayLayout = findViewById(R.id.test);

            // Verificar que la referencia no sea nula
            if (dayLayout == null) {
                Log.e("Error", "La referencia de la vista ImageView es nula");
                return;
            }

            int imageResource = getResources().getIdentifier(imageName, "drawable", this.getPackageName());

            // Establece el fondo del ConstraintLayout
            dayLayout.setImageResource(imageResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getCurrentTime(boolean is24HourFormat) {
        // Obtener la hora actual en el formato seleccionado
        String formatPattern = is24HourFormat ? "HH:mm" : "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, Locale.getDefault());
        return sdf.format(new Date());
    }
}

