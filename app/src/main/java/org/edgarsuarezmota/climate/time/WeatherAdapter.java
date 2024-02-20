package org.edgarsuarezmota.climate.time;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private Context context;
    private List<WeatherEntity.Forecast.ForecastDay.Hour> weatherList;

    public WeatherAdapter(Context context, List<WeatherEntity.Forecast.ForecastDay.Hour> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherEntity.Forecast.ForecastDay.Hour hour = weatherList.get(position);

        holder.txtTime.setText(hour.getTime());
        holder.txtCondition.setText(hour.getCondition().getText());
        holder.txtTemperature.setText(String.valueOf(hour.getTemp_c()) + " ºC");
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtCondition, txtTemperature;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.tv_hour);
            txtCondition = itemView.findViewById(R.id.tv_weather);
            txtTemperature = itemView.findViewById(R.id.tv_degrees);
        }
    }
}
