package com.guide.przewo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.guide.przewo.DataAccess.App;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.ObjectType;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.SearchCriteria;
import com.guide.przewo.Models.SearchResult;
import com.guide.przewo.Models.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class SearchActivity extends BaseActivity
{
	ListView searchResults;
	List<SearchResult> allTags = new ArrayList<SearchResult>();
	List<SearchResult> results = new ArrayList<SearchResult>();
	ArrayAdapter adapter;
	static private List<Integer> supportedTypes = Arrays.asList(1,2,3,4,5,6,8,10,11,13,18);

	private int getPlaceIcon(int type)
	{
		String token;
		if (supportedTypes.contains(type))
		{
			token = "place_type" + type;
		}
		else
		{
			token = "marker";
		}

		int resId = getApplicationContext().getResources().getIdentifier(token, "drawable", getApplicationContext().getPackageName());

		return resId;
	}

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.overridePendingTransition(R.anim.slide_top_left_in, R.anim.slide_bottom_right_out);

		setContentView(R.layout.activity_search);

        mTitle = "Czego szukasz?";
		searchResults = ((ListView)findViewById(R.id.results));

		((EditText)findViewById(R.id.searchInput)).requestFocus();

		for(Type type : Place.AllPlacesTypes)
		{
			int pic = getPlaceIcon(type.id);
			Log.d(type.name, pic + " ");
			allTags.add(new SearchResult(type.name, ObjectType.TAG, pic));
		}

		for(Category category : Event.AllEventCategories)
		{
			allTags.add(new SearchResult(category.name, ObjectType.TAG, R.drawable.events));
		}

		results.addAll(allTags);

		SharedPreferences sharedPref;
		sharedPref = getSharedPreferences(getResources().getString(R.string.main_store), 0);

		searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				SearchResult clickedObject = results.get(position);

				SharedPreferences sharedPref;
				sharedPref = getSharedPreferences(getResources().getString(R.string.main_store), 0);

				switch (clickedObject.objectType)
				{
					case PLACE:
						RadarManager.isRadarBlocked = true;
						sharedPref.edit().putInt("openPlace", ((Place) clickedObject.object).id).commit();
						finish();
						break;

					case EVENT:
						RadarManager.isRadarBlocked = true;
						sharedPref.edit().putInt("openPlace", ((Event) clickedObject.object).idPlace).commit();
						finish();
						break;

					case ROUTE:
						sharedPref.edit().putInt("showRoute", ((Route) clickedObject.object).id).commit();
						finish();
						break;

					case TAG:
						((EditText)findViewById(R.id.searchInput)).setText(clickedObject.tag);
						break;
				}
			}
		});


		adapter = new SearchResultAdapter(this, results);
		searchResults.setAdapter(adapter);

		((EditText)findViewById(R.id.searchInput)).addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				results.clear();
				if (s.length() > 1)
				{
					SearchCriteria searchCriteria = new SearchCriteria(s.toString().toLowerCase(), false, false);
					Collection<SearchResult> newResults = ((App) getApplicationContext()).searchFor(searchCriteria);
					results.addAll(newResults);
				}
				else
				{
					results.addAll(allTags);
				}

				adapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});
	}

	@Override
	public void onBackPressed()
	{
		if (((EditText)findViewById(R.id.searchInput)).getText().length() > 0)
		{
			((EditText)findViewById(R.id.searchInput)).setText("");
		}
		else
		{
			super.onBackPressed();
		}
	}


}
