/**
 *
 */
package edu.virginia.lib.camel.aggregation;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.StringSource;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.osgi.service.log.LogService;

/**
 * @author ajs6f
 * 
 */
public class SimpleXMLRecursiveMergeStrategy implements AggregationStrategy {

	private LogService log;

	private final static String XMLHEADER = "<\\?xml.+?\\?>";

	private final static Pattern XMLHEADERREGEXP = Pattern.compile(XMLHEADER);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org
	 * .apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		if (oldExchange == null)
			return newExchange;
		if (newExchange == null)
			return oldExchange;

		String oldXML = oldExchange.getIn().getBody(String.class);
		// remove XML declaration
		oldXML = XMLHEADERREGEXP.matcher(oldXML).replaceAll("");
		debug("oldXML = " + oldXML);
		String newXML = newExchange.getIn().getBody(String.class);
		// remove XML declaration
		newXML = XMLHEADERREGEXP.matcher(newXML).replaceAll("");
		debug("newXML = " + newXML);
		String mergedXML = "<wrapper>" + oldXML + newXML + "</wrapper>";
		oldExchange.getIn().setBody(new StringSource(mergedXML));

		return oldExchange;
	}
	
	private void debug (String msg) {
		log.log(LogService.LOG_DEBUG, msg);
	}
	
	public LogService getLog() {
		return log;
	}

	public void setLog(LogService log) {
		this.log = log;
	}

}