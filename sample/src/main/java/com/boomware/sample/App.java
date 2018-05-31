package com.boomware.sample;

import android.app.Application;

import com.boomware.sdk.Boomware;


public class App extends Application {


    public void onCreate(){
        super.onCreate();

        //Initialize Boomware instance (key from Account on https://boomware.com)
        Boomware.initialize(this, "<your:api:jey>");
    }
}
