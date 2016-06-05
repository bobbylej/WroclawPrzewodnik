package com.guide.przewo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.guide.przewo.DataAccess.App;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.DataAccess.NodeAccess;
import com.guide.przewo.Models.ObjectFactory;
import com.guide.przewo.Models.Transport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Śbyzby on 2015-06-28.
 */
public class TransportManager
{
    private final LocalRepository localRepository;
    MainActivity mainActivity;

    public TransportManager(MainActivity ma)
    {
        this.mainActivity = ma;
        localRepository =  ((App)mainActivity.getApplicationContext()).getLocalRepository();
    }

    public void updateTransportFromServer()
    {
        NodeAccess nodeAccess;
        nodeAccess = new NodeAccess("http://pasazer.mpk.wroc.pl");
        Log.d("Transport update", "Start");

        RequestParams requestParams = new RequestParams();
        requestParams.add("busList[bus][]", "130");

        nodeAccess.post("/position.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ObjectFactory objectFactory = new ObjectFactory();
                Log.d("dupaa", "ech");
                List<Transport> transportList = (List<Transport>) objectFactory.<Transport>getObjectFromJson(response, new TypeToken<List<Transport>>() {}.getType());
                localRepository.updateTransport(transportList);
            }
        });
    }

    public void drawTransportInCircle()
    {
        List<Transport> transportList = (List<Transport>) localRepository.getTransport();
        Log.d("transporty", transportList.size()+"");
        for(Transport t: transportList)
        {
            Bitmap b = BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), R.drawable.marker);
            Log.d("bitmap", b.toString());
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(t.x, t.y)).title(t.name).icon(BitmapDescriptorFactory.fromBitmap(b));
            mainActivity.getMap().addMarker(markerOptions);
        }
    }
}
