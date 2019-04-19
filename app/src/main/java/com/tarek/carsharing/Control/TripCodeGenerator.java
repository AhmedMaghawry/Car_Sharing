package com.tarek.carsharing.Control;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.tarek.carsharing.Control.Constants.URL_CODE;

public class TripCodeGenerator extends AsyncTask<String, Void, Void> {

    private onAction callback;


    public TripCodeGenerator(onAction callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(URL_CODE);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("GET");
            InputStream inputStream = httpConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("dodo", line);
            callback.onFinish(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
