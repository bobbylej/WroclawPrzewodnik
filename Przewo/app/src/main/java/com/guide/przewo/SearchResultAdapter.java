package com.guide.przewo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guide.przewo.Models.ObjectType;
import com.guide.przewo.Models.SearchResult;

import java.util.List;

class SearchResultAdapter extends ArrayAdapter<SearchResult>
{
	List<SearchResult> results;

	public SearchResultAdapter(Context context, List<SearchResult> results)
	{
		super(context, 0, results);
		this.results = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		SearchResult result = (SearchResult) results.get(position);
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_result, parent, false);
		}

		ImageView img = (ImageView) convertView.findViewById(R.id.image);
		TextView name = (TextView) convertView.findViewById(R.id.name);

		img.setImageResource(result.getImage());
		name.setText(result.getName());

		TextView hint = (TextView) convertView.findViewById(R.id.hint);
		TextView time = (TextView) convertView.findViewById(R.id.time);

		if (result.objectType != ObjectType.TAG)
		{
			time.setVisibility(View.VISIBLE);
			hint.setVisibility(View.VISIBLE);
			if (result.hint.equals("") == false)
			{
				hint.setText(result.hint);
			} else
			{
				hint.setText("");
			}

			if (result.time.equals("") == false)
			{
				if (hint.getText().equals(""))
				{
					hint.setText(result.time);
					time.setText("");
				} else
				{
					time.setText(result.time);
				}
			} else
			{
				time.setText("");
			}
		}
		else
		{
			time.setVisibility(View.GONE);
			hint.setVisibility(View.GONE);
		}

		return convertView;
	}
}