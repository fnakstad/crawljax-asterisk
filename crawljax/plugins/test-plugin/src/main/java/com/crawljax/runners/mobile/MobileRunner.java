package com.crawljax.runners.mobile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;
import com.crawljax.core.configuration.ProxyConfiguration;
import com.crawljax.core.plugin.Plugin;
import com.crawljax.oraclecomparator.OracleComparator;
import com.crawljax.oraclecomparator.comparators.AttributeComparator;
import com.crawljax.oraclecomparator.comparators.PlainStructureComparator;
import com.crawljax.oraclecomparator.comparators.XPathExpressionComparator;
import com.crawljax.plugins.crawloverview.CrawlOverview;
import com.crawljax.plugins.eventfetcher.EventFetcher;
import com.google.common.collect.ImmutableList;

public final class MobileRunner {
	//private static final String URL = "https://www.google.co.jp";
	//private static final String URL = "http://stanford.edu/";
	//private static final String URL = "http://m.baidu.com";
	//private static final String URL = "https://www.goodreads.com";
	//private static final String URL = "https://www.wikia.com";
	//private static final String URL = "https://www.youtube.com";
	//private static final String URL = "https://www.pinterest.com/tomhynes/interspecies-buddying/";
	//private static final String URL = "http://demos.telerik.com/kendo-ui/touchevents/index/";
	//private static final String URL = "http://bomomo.com";
	//private static final String URL = "http://www.addyosmani.com/resources/canvasphoto/";
	//private static final String URL = "http://dan.forys.co.uk/experiments/mesmerizer";
	//private static final String URL = "http://modern-carpentry.com/workshop/html5/waveform/";
	//private static final String URL = "http://gifpaint.com/";
	//private static final String URL = "http://gartic.com.br/sketch/";
	//private static final String URL = "https://developer.cdn.mozilla.net/media/uploads/demos/r/a/ranjan_purbey/10bf08f0bbd6689475be65b4ae441bd9/slider_1395653448_demo_package/index.html";
	//private static final String URL = "https://developer.mozilla.org/en-US/demos/detail/memory-boost/launch";
	//private static final String URL = "https://mdn.mozillademos.org/en-US/docs/Web/Guide/Events/Touch_events$samples/Example?revision=716919";
	//private static final String URL = "https://maps.google.com";
	//private static final String URL = "http://www.webdevbreak.com/episodes/touch-gestures-hammerjs/demo";
	//private static final String URL = "http://cubiq.org/dropbox/SwipeView/demo/gallery/";
	//private static final String URL = "http://photoswipe.com/";
	
	//private static final String URL = "http://www.filamentgroup.com/examples/jqm-pagination/demo/";
	//private static final String URL = "https://crawl-event-test.herokuapp.com/hammer";
	//private static final String URL = "http://gather.ly/articles/feature";
	// http://pakastin.fi/cards/
	// http://lab.hakim.se/scroll-effects/mobile.html
	//private static final String URL = "http://slides.com/andreylisin/omaha-server#/6/1";
	// http://wordmap.co/
	//private static final String URL = "http://m.imgur.com";
	// m.imgur.com
	// http://www.140worldcup.com/
	//private static final String URL = "http://www.hotels.com/";
	//private static final String URL = "http://m.weather.com/weather/today/JAXX0085:1:JA";

	//private static final String URL = "http://touch.toyota.com/index.html";
	private static final String URL = "http://muro.deviantart.com/";
	//private static final String URL = "https://www.pinterest.com/tomhynes/interspecies-buddying/";
	//private static final String URL = "http://www.hotels.com/search.do?resolved-location=CITY%3A726784%3ASRS%3AUNKNOWN&destination-id=726784&q-destination=Tokyo,%20Japan&q-localised-check-in=2015-03-05&q-localised-check-out=2015-03-06&q-rooms=1&q-room-0-adults=1&q-room-0-children=0";
	
	// Funnyjunk.com...?
	// http://ifpaintingscouldtext.tumblr.com/?
	// mashable.com (This definitely has a swipe!)
	//private static final String URL = "http://mashable.com";
	
	private static final int MAX_DEPTH = 2;
	private static final int MAX_NUMBER_STATES = 3000;
	private static final long WAIT_TIME_AFTER_EVENT = 500;
	private static final long WAIT_TIME_AFTER_RELOAD = 500;
	
	private static final Logger LOG = LoggerFactory.getLogger(MobileRunner.class);

	/**
	 * Entry point
	 */
	public static void main(String[] args) throws IOException {
		CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(URL);
		//builder.crawlRules().insertRandomDataInInputForms(true);

		try {
			builder.setOutputDirectory(getOutputDir(URL));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		// This may cause state changes unprovoked by event handlers
		builder.crawlRules().insertRandomDataInInputForms(false);
		
		
		// Proxy stuff
		//builder.setProxyConfig(ProxyConfiguration.manualProxyOn("127.0.0.1", 8003));

		builder.setProxyConfig(ProxyConfiguration.manualProxyOn("127.0.0.1", 8080));
		builder.setUseEventHandlers(true);
		builder.setUseMobileUserAgent(false);
		
		builder.crawlRules().click("a");
		builder.crawlRules().click("button");
		
		// limit the crawling scope
		//builder.setMaximumStates(MAX_NUMBER_STATES);
		builder.setUnlimitedStates();
		//builder.setUnlimitedCrawlDepth();
		builder.setMaximumDepth(MAX_DEPTH);
		builder.setMaximumRunTime(2, TimeUnit.HOURS);
		//builder.setUnlimitedRuntime();

		builder.setBrowserConfig(new BrowserConfiguration(BrowserType.FIREFOX));
		
		// Telerik Kendo UI
		//builder.crawlRules().addOracleComparator(new OracleComparator("AttributeComparator", new AttributeComparator("data-uid")));
		
		// bomomo.com
		//builder.crawlRules().addOracleComparator(new OracleComparator("XPathExpressionComparator", new XPathExpressionComparator("//IFRAME")));
		
		// gifpaint.com
		//builder.crawlRules().dontClick("a").withAttribute("id", "login");
		
		// Hammer.js demo
		//builder.crawlRules().dontClick("a").withAttribute("href", "http://www.webdevbreak.com/episodes/touch-gestures-hammerjs");
		
		// google.co.jp
		//builder.crawlRules().addOracleComparator(new OracleComparator("AttributeComparator", new AttributeComparator("value", "href")));
		
		// weather.com
		//builder.crawlRules().dontClick("button").withText("Continue to full site");
		//builder.crawlRules().addOracleComparator(new OracleComparator("AttributeComparator", new AttributeComparator("href", "id", "mw-variation-1", "mw-variation-2", "mw-variation-3", "mw-variation-4", "mw-variation-5", "mw-variation-6", "mw-variation-7", "mw-variation-8", "mw-variation-9")));
		
		// hotels.com
		//builder.crawlRules().addOracleComparator(new OracleComparator("XPathExpressionComparator", new XPathExpressionComparator("//IFRAME", "//div[contains(@class,\"widget-urgency-inner\")]")));
		
		// slides.com
		//builder.crawlRules().addOracleComparator(new OracleComparator("XPathExpressionComparator", new XPathExpressionComparator("//META")));
		
		// touch.toyota.com
		//builder.crawlRules().addOracleComparator(new OracleComparator("XPathExpressionComparator", new XPathExpressionComparator("//IFRAME")));
		
		// muro.deviantart.com
		builder.crawlRules().addOracleComparator(new OracleComparator("AttributeComparator", new AttributeComparator("value", "layerid")));
		
		// Mashable
		//builder.crawlRules().addOracleComparator(new OracleComparator("XPathExpressionComparator", new XPathExpressionComparator("//IFRAME", "//TIME", "//a[contains(@class,\"num\")]")));
		//builder.crawlRules().addOracleComparator(new OracleComparator("AttributeComparator", new AttributeComparator("data-sessionlink", "data-shares", "id")));
		
		builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
		builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);
		
		builder.addPlugin(new CrawlOverview());

		builder.crawlRules().setInputSpec(getInputSpecification());

		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		crawljax.call();
	}

	private static InputSpecification getInputSpecification() {
		InputSpecification input = new InputSpecification();
		//input.field("gbqfq").setValue("Crawljax");
		return input;
	}
	
	private static File getOutputDir(String url) throws URISyntaxException {
		URI uri = new URI(url); 
		String timestamp = (new Timestamp((new Date()).getTime())).toString();
		File output = new File("out/" + uri.getHost() + "-" + timestamp);
		
		return output;
	}

	private MobileRunner() {
		// Utility class
	}
}
