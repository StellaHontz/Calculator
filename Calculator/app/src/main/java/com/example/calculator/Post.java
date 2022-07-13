package com.example.calculator;

import com.google.gson.annotations.SerializedName;

public class Post {
    //We only want result String
    @SerializedName("result")
    String result;

    @SerializedName("date")
    String date;

    public Post(String result, String date) {
        this.result = result;
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
