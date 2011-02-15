package com.rss.widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class RssService extends IntentService{

	private String tag = "RssService";
	private RssFeed rssFeed;
    private static final String URL = "http://feeds.feedburner.com/RealMadrid_pl";

	
	public RssService() {
		super("RssService");
		Log.v(tag, "RssService()");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(tag, "onHandleIntent call");
		
		rssFeed = getRSS(URL);
		showFeed();
		
	}

	private void showFeed() {
		Log.v("show FEED", "---------------");
		Log.v("Feed Title", rssFeed.getRssFeedTitle());
		
		RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.rss_layout);
		remoteView.setTextViewText(R.id.bdw_w_name, rssFeed.getRssFeedTitle());

		
		List<RssItem> allItem = rssFeed.getAllItem();
		
		StringBuilder sb = new StringBuilder();
		
		for (RssItem rssItem : allItem) {
			sb.append(rssItem.getRssItemTitle()).append(" * ");
		}
		Log.v("item", sb.toString());
		
		remoteView.setTextViewText(R.id.bdw_w_days, sb.toString());
		
		
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		ComponentName componentName = new ComponentName(this, RssWidget.class);
	
		appWidgetManager.updateAppWidget(componentName, remoteView);
		
		Intent intent = new Intent(this, ShowRssItemInList.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		remoteView.setOnClickPendingIntent(R.id.bdw_w_date, pendingIntent);
		appWidgetManager.updateAppWidget(componentName, remoteView); // TEGO MI BRAKOWA�O !!!!!!!!!!! POCZYTAJ CO TO !!!!!!
		
	}

	private RssFeed getRSS(String url2) {
		Log.v(tag, "getRSS call");
		
		RssHandler rssHandler = null;
		try {
			
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser newSAXParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = newSAXParser.getXMLReader();
			
			rssHandler = new RssHandler();
			xmlReader.setContentHandler(rssHandler);

			URL url = new URL(url2);
			InputStream openStream = url.openStream();
			InputSource input = new InputSource(openStream);
			
			xmlReader.parse(input);
		
		
		} catch (ParserConfigurationException e) {
			Log.e("Error", "ParserConfigurationException ");
			e.printStackTrace();
		} catch (SAXException e) {
			Log.e("Error", "SAXException");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			Log.e("Error", "MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Error", "IOException ");
			e.printStackTrace();
		}
		
		Log.v(tag +" RssFeed ()", "OK");
		return rssHandler.getFeed();
	}

	


}
