package DataAccess;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class NodeAccess
{
	private final String url;
	private AsyncHttpClient client;

	public NodeAccess(String url)
	{
		this.url = url;
		client = new AsyncHttpClient();
	}

	public RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		return client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		return client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private String getAbsoluteUrl(String relativeUrl)
	{
		return url + relativeUrl;
	}
}
