package com.guide.przewo;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guide.przewo.Models.Event;

/*
 *
 * Fragment reprezentujący wydarzenie
 *
 */
public class EventFragment extends Fragment
{
	View view;
	Event event;
	public EventFragment()
	{
	}

	public EventFragment(Event event)
	{
		this.event = event;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_event, container, false);

		if (event != null)
		{
			TextView eventDescription = (TextView)view.findViewById(R.id.eDesc);
			TextView eventPlace = (TextView)view.findViewById(R.id.ePlace);
			TextView eventDistance = (TextView)view.findViewById(R.id.eDistance);
			TextView eventStart = (TextView)view.findViewById(R.id.eStart);
			TextView eventStop = (TextView)view.findViewById(R.id.eStop);

			eventDescription.setText(event.description);
			eventPlace.setText("W: "+ event.place.name);
			eventDistance.setText(event.place.getDistanceInHumarFormat());
			eventStart.setText(event.getDateInHumanFormat());
			eventStop.setText(event.getEndDateInHumanFormat());
		}
		else
		{
			Log.d("Błąd", "Próba wypełnienia szczegółów eventu przez null");
		}

		return view;
	}
}
