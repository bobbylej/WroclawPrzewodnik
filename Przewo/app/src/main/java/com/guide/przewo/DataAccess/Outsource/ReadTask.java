package com.guide.przewo.DataAccess.Outsource;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Adamczyk Mateusz on 2015-04-03.
 */
public class ReadTask extends AsyncTask<String, Void, String> {

    GoogleMap googleMap;
    ParserTask parserTask;

    public ReadTask(GoogleMap googleMap) {
        this.googleMap = googleMap;
        parserTask = new ParserTask(googleMap);
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            HttpConnection http = new HttpConnection();
            data = http.readUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        parserTask.execute(result);
    }

    public void stopRoute() {
        parserTask.stopRoute();
    }
}
