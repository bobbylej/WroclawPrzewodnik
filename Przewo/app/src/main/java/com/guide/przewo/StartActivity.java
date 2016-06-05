package com.guide.przewo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * Okno startowe aplikacji
 */
public class StartActivity extends Activity
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
				nextActivity();
			}
		});

		//close splash screen after 0.5s
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
            {
                public void run()
                {
				nextActivity();
			}
		}, 500);
	}

	public void nextActivity()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
