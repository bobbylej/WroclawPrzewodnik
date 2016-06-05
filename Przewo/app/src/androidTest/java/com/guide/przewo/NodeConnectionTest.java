package com.guide.przewo;

import com.guide.przewo.DataAccess.NodeAccess;
import com.loopj.android.http.JsonHttpResponseHandler;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.json.JSONObject;

public class NodeConnectionTest extends TestCase
{

	public void setUp() throws Exception
	{
		super.setUp();
	}

	public void nodeConnectionTest() throws Exception
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("");
		System.out.println("Start testu node");
		nodeAccess.get("/places/0" + 0, null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				System.out.println("Sukces testu node");
			}
		});
		System.out.println("Koniec testu node");
	}
}