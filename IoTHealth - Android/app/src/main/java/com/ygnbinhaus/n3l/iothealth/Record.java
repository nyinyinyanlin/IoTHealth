package com.ygnbinhaus.n3l.iothealth;

public class Record {
    public String date;
    public Double heartrate;
    public Double oxygen;
    public Double temperature;
    public Double humidity;
    public Double latitude;
    public Double longitude;
    public String comment;

    public Record(String date, Double heartrate, Double oxygen, Double temperature, Double humidity, Double latitude, Double longitude, String comment) {
        this.date = date;
        this.heartrate = heartrate;
        this.oxygen = oxygen;
        this.temperature = temperature;
        this.humidity = humidity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public Double getHeartrate() {
        return heartrate;
    }

    public Double getOxygen() {
        return oxygen;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getComment() {
        return comment;
    }
}
