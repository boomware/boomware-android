package com.boomware.sample;

import android.app.Application;

import com.boomware.sdk.Boomware;


public class App extends Application {

    public static final String API_KEY = "97b0763d2c6220225bb997cad7b07d79:40c8d8d42beb9c307c56d1e817a9ba56:4bfdf6e7f72fe5b3";

    public void onCreate(){
        super.onCreate();

        //Initialize Boomware instance (key from Account on https://boomware.com)
        Boomware.initialize(this, API_KEY);
    }
}
