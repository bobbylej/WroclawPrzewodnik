package com.guide.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;


public class StartActivity extends ActionBarActivity
{


	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		ImageButton logo = (ImageButton) findViewById(R.id.logo);
		logo.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				runRadar(v);
			}
		});

		//close splash screen after 0.5s
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				startActivity(new Intent(StartActivity.this, BaseActivityFragments.class));
				finish();
			}
		}, 500);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
	}


	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_start, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public void onBackPressed()
	{
		Log.d("CDA", "onBackPressed Called");
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
		startActivity(intent);
		finish();
		System.exit(0);
	}

	public void runRadar(View view)
	{
		Intent intent = new Intent(this, BaseActivityFragments.class);
		startActivity(intent);
		finish();
	}
}
