package com.stanfordflipside.adam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ArticleActivity extends ListActivity {

	ArrayList<HashMap<String, String>> mylist;
	JSONObject json;

	boolean indicator;
	
	public static final int NUM_ELEMENTS=4;
	public static final int ID_OFFSET=0;
	public static final int TITLE_OFFSET=1;
	public static final int TYPE_OFFSET=2;
	public static final int PHOTO_OFFSET=3;
	public static final String ARTICLE_SELECTED="articleSelected";
	public String[]nameList;
	public int nameSize;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*indicator=false;
		DownloadTask downloader=new DownloadTask();
		downloader.execute(null, null, null);*/
		

		json=getJSONfromURL("http://stanfordflipside.com/iphone-feed");
		
		nameSize=0;


		//while(indicator==false)
			//;

		try{
			//Get the element that holds the earthquakes ( JSONArray )
			//mylist = new ArrayList<HashMap<String, String>>();
						
			
			JSONArray names=json.names();
			nameList=new String[names.length()*NUM_ELEMENTS];
			nameSize=names.length();
			
			
				//Loop the Array
				
			for(int i=0;i <names.length();i++){

				String current=names.getString(i);
				int currentInt=new Integer(current);
				JSONObject article=json.getJSONObject(current);
				//JSONArray article=json.getJSONArray(names.getString(i));



				//not using a map so I can get access to the list adapter
				//HashMap<String, String> map = new HashMap<String, String>();
				
				int currentIndex=(currentInt-1)*NUM_ELEMENTS;//where are we in the array
				
				nameList[currentIndex+ID_OFFSET]=article.getString("id");
				
								
				nameList[currentIndex+TITLE_OFFSET]=article.getString("title");
				nameList[currentIndex+TYPE_OFFSET]=article.getString("type");
				nameList[currentIndex+PHOTO_OFFSET]=article.getString("photo");
				Log.v("test", article.getString("title"));
				
								

			}
		}catch(JSONException e)        {
			Log.e("log_tag", "Error parsing data "+e.toString());
		}
		
		
		String[] actualArray=new String[nameSize];
		for(int x=0;x<nameSize;x++)
		{
			actualArray[x]=nameList[x*NUM_ELEMENTS+TITLE_OFFSET];
		}
		ListAdapter adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, actualArray);
		setListAdapter(adapter);


	}
	
	protected void onListItemClick(ListView l, View v, int position, long rowId) {
		super.onListItemClick(l, v, position, rowId);
		
		int articleID=new Integer(nameList[(int) (rowId*NUM_ELEMENTS+ID_OFFSET)]);
		String url="http://stanfordflipside.com/?p="+articleID+"&iphone=1";
		
		String articleType=nameList[(int) (rowId*NUM_ELEMENTS+TYPE_OFFSET)];
		if(articleType.equals("Video"))
		{
			String youTubeURL=getFullLink(url, "http://www.youtube.com/embed/", "\" frameborder=\"0\"");
			 Uri parsed=Uri.parse("http://www.youtube.com/v/"+youTubeURL);		   
			   startActivity(new Intent(Intent.ACTION_VIEW, parsed));
		}
		else		
		{
		Intent intent=new Intent(this, ViewArticle.class);
		
		intent.putExtra(ARTICLE_SELECTED, url);
		startActivity(intent);
		}
	}
	
	
private String getFullLink(String url, String urlStart, String urlStop) {
		// TODO Auto-generated method stub
	InputStream is = null;
	
	//http post
	try{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		is = entity.getContent();

	}catch(Exception e){
		Log.e("log_tag", "Error in http connection "+e.toString());
	}

	//convert response to string
	try{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"),8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
			
		}
		is.close();
		
		int startIndex=sb.indexOf(urlStart);
		int stopIndex=sb.indexOf(urlStop);
		
		String identifier=sb.substring(startIndex+urlStart.length(), stopIndex);
		return identifier;
		
		
	}catch(Exception e){
		Log.e("log_tag", "Error converting result "+e.toString());
	}
		return null;
	}

//I found the skeleton of this method on an online help site (I think StackOverflow, but I can't remember
	public static JSONObject getJSONfromURL(String url){

		//initialize
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;

		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}

		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				
			}
			is.close();
			
			result=sb.toString();
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		//try parse the string to a JSON object
		try{
			jArray = new JSONObject(result);
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}

		return jArray;
	}
		

	private class DownloadTask extends AsyncTask<Void, Void, Integer>
	{

		ProgressDialog dialog;

		protected void onPreExecute()
		{
			int stringId=R.string.Loading;
			String message=getString(stringId);
			dialog=ProgressDialog.show(ArticleActivity.this, "", message, true);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
		}

		protected Integer doInBackground(Void... url) {
			// TODO Auto-generated method stub
			json=getJSONfromURL("http://stanfordflipside.com/iphone-feed");		

			//indicator=true;
			return new Integer(1);
		}


		@Override
		protected void onPostExecute(Integer result)
		{
			indicator=true;
			dialog.dismiss();
			return;
		}

	}



}