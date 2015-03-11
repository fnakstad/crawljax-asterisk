package com.crawljax.plugins.eventfetcher;

import com.crawljax.core.CrawlerContext;
import com.crawljax.core.plugin.OnNewStatePlugin;
import com.crawljax.core.state.StateVertex;

import org.json.*;

public class EventFetcher implements OnNewStatePlugin {

	@Override
	public void onNewState(CrawlerContext context, StateVertex newState) {
		// TODO Auto-generated method stub
		try {
			Object events = context.getBrowser().executeJavaScript("return JSON.stringify(eventHandlers);");
			JSONArray arr = new JSONArray(events.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
