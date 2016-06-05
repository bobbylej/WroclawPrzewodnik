package com.guide.przewo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

/*
 *
 * Aktywność bazowa, dziedziczą po niej wszystkie aktywności.
 *
 */
public class BaseActivity extends ActionBarActivity
{
    protected CharSequence mTitle;

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#53749D")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }
}
