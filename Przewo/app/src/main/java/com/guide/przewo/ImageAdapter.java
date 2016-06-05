package com.guide.przewo;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guide.przewo.DataAccess.App;
import com.guide.przewo.DataAccess.Outsource.DownloadImageTask;
import com.guide.przewo.Models.Place;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MateuszAntczak on 2015-06-26.
 */
public class ImageAdapter extends PagerAdapter {
    Context context;
    SharedPreferences sharedPref;
    List<String> photos;

    ImageAdapter(Context context){
        this.context=context;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.main_store), 0);
        int placeId = sharedPref.getInt(context.getResources().getString(R.string.current_marker),-1);
        photos = new ArrayList<>();
        if(placeId!=-1){
            Place place = ((App)context.getApplicationContext()).getLocalRepository().getPlaceDetails(placeId);
            Log.e("Wczytano do zdjecia: ", place.name);
            photos = place.photos;
        }
    }
    @Override
    public int getCount() {
        return photos.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        //imageView.setImageDrawable(LoadImageFromWebOperations(photos.get(position)));
        Log.e(photos.get(position),"  uriiii");
        new DownloadImageTask(imageView).execute(photos.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
