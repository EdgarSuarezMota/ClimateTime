package org.edgarsuarezmota.climate.time;

import java.util.List;

public class WeatherEntity {
    private Forecast forecast;
    private Location location;
    private Current current;

    public static class Current {
        private double temp_c;  // Suponiendo que la temperatura es un valor double

        public double getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(double temp_c) {
            this.temp_c = temp_c;
        }

        @Override
        public String toString() {
            return "Current{" +
                    "temp_c=" + temp_c +
                    '}';
        }
    }

    public static class Location {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Forecast {
        private List<ForecastDay> forecastday;

        public static class ForecastDay {
            private List<Hour> hour;

            public static class Hour {
                private String time;
                private double temp_c;
                private Condition condition;

                public static class Condition {
                    private String text;

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    @Override
                    public String toString() {
                        return "Condition{" +
                                "text='" + text + '\'' +
                                '}';
                    }
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public double getTemp_c() {
                    return temp_c;
                }

                public void setTemp_c(double temp_c) {
                    this.temp_c = temp_c;
                }

                public Condition getCondition() {
                    return condition;
                }

                public void setCondition(Condition condition) {
                    this.condition = condition;
                }

                @Override
                public String toString() {
                    return "Hour{" +
                            "time='" + time + '\'' +
                            ", temp_c=" + temp_c +
                            ", condition=" + condition +
                            '}';
                }
            }

            public List<Hour> getHour() {
                return hour;
            }

            public void setHour(List<Hour> hour) {
                this.hour = hour;
            }

            @Override
            public String toString() {
                return "ForecastDay{" +
                        "hour=" + hour +
                        '}';
            }
        }

        public List<ForecastDay> getForecastday() {
            return forecastday;
        }

        public void setForecastday(List<ForecastDay> forecastday) {
            this.forecastday = forecastday;
        }

        @Override
        public String toString() {
            return "Forecast{" +
                    "forecastday=" + forecastday +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WeatherEntity{" +
                "forecast=" + forecast +
                ", location=" + location +
                ", current=" + current +
                '}';
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }



}
