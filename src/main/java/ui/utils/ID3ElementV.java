package ui.utils;

import java.util.LinkedList;

public class ID3ElementV {
    private String weather, temp, hum, wind, play;
    private LinkedList<String> id3FedElements;

    public ID3ElementV() {
    }

    public ID3ElementV(String weather, String temp, String hum, String wind, String play) {
        this.weather = weather;
        this.temp = temp;
        this.hum = hum;
        this.wind = wind;
        this.play = play;
    }

    public ID3ElementV(String play) {
        this.play = play;
    }

    public ID3ElementV(LinkedList<String> id3FedElements) {
        this.id3FedElements = id3FedElements;

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

    public LinkedList<String> getId3FedElements() {
        return id3FedElements;
    }

    public void setId3FedElements(LinkedList<String> id3FedElements) {
        this.id3FedElements = id3FedElements;
    }
}
