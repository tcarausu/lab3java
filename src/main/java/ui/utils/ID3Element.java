package ui.utils;

public class ID3Element {
    private String weather, temp, hum, wind, play;

    public ID3Element() {
    }

    public ID3Element(String weather, String temp, String hum, String wind, String play) {
        this.weather = weather;
        this.temp = temp;
        this.hum = hum;
        this.wind = wind;
        this.play = play;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }
}
