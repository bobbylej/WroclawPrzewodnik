package com.guide.przewo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.guide.przewo.Models.Route;

import java.text.DecimalFormat;
import java.util.List;

class RouteListAdapter extends ArrayAdapter<Route>
{
	SharedPreferences.Editor editor;
	private GoogleApiClient mGoogleApiClient;
	private FragmentManager fragmentManager;
	List<Route> routes;

	public RouteListAdapter(Context context, List<Route> routes, FragmentManager fragmentManager, SharedPreferences.Editor editor)
	{
		super(context, 0, routes);
		this.routes = routes;
		this.fragmentManager = fragmentManager;
		this.editor = editor;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Route result = (Route) routes.get(position);
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_result, parent, false);
		}

		ImageView img = (ImageView) convertView.findViewById(R.id.image);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView hint = (TextView) convertView.findViewById(R.id.hint);
		TextView time = (TextView) convertView.findViewById(R.id.time);

		img.setImageResource(R.drawable.routes);
		name.setText(result.name);

		if (result == null || result.placesIds == null || result.placesIds.isEmpty())
		{
			hint.setText("");
			time.setText("");
			return convertView;
		}

		DecimalFormat four = new DecimalFormat("#0.0");

		hint.setText("Około " + four.format(result.tour_time) + " h");
		time.setText(result.description);

		View toolbar = convertView.findViewById(R.id.details);
		((RelativeLayout.LayoutParams) toolbar.getLayoutParams()).bottomMargin = -50;
		toolbar.setVisibility(View.GONE);

		ImageView walk = (ImageView) convertView.findViewById(R.id.walking);
		walk.setTag(result);
		ImageView drive = (ImageView) convertView.findViewById(R.id.driving);
		drive.setTag(result);

		walk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Route chosenRoute = (Route) (((ImageView) v).getTag());
				editor.putInt(String.valueOf(R.string.current_route), chosenRoute.id);
				editor.putInt("showRoute", chosenRoute.id);
				editor.putInt("walking", 1);
				editor.commit();
				fragmentManager.popBackStack();
//				((MainActivity) getContext()).showRoute(chosenRoute.id, true);
				((MainActivity) getContext()).onResume();
			}
		});

		drive.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Route chosenRoute = (Route) (((ImageView) v).getTag());
				editor.putInt(String.valueOf(R.string.current_route), chosenRoute.id);
				editor.putInt("showRoute", chosenRoute.id);
				editor.putInt("walking", 0);
				editor.commit();
				fragmentManager.popBackStack();
//				((MainActivity) getContext()).showRoute(chosenRoute.id, true);
				((MainActivity) getContext()).onResume();
			}
		});

		return convertView;
	}
}