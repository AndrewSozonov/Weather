package ru.geekbrains.task5;

public class CityCard {

    private String city;
    private float temperature;
    private String icon;

    public CityCard(String city, float temperature, String icon) {
        this.city = city;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getCity() {
        return city;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }
}
