/**
 * 
 */
package edu.virginia.lib.ole.esb.aggregation;

import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author ajs6f
 * 
 */
public class SimpleXMLRecursiveMergeStrategy implements AggregationStrategy {

	private final static Log log = LogFactory
			.getLog(SimpleXMLRecursiveMergeStrategy.class);

	private static TransformerFactory tFactory = TransformerFactory
			.newInstance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org
	 * .apache.camel.Exchange, org.apache.camel.Exchange)
	 */
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		try {
			if (oldExchange == null)
				return newExchange;
			if (newExchange == null)
				return oldExchange;
			log.debug("oldExchange.in.body class = "
					+ oldExchange.getIn().getBody().getClass().getName());
			log.debug("newExchange.in.body class = "
					+ newExchange.getIn().getBody().getClass().getName());

			Source oldXMLsource = oldExchange.getIn().getBody(Source.class);
			DOMResult oldXMLdomResult = new DOMResult();
			tFactory.newTransformer().transform(oldXMLsource, oldXMLdomResult);
			Document oldXMLdoc = (Document) oldXMLdomResult.getNode();

			StreamResult oldXMLstreamResult = new StreamResult();
			StringWriter oldXMLdebugString = new StringWriter();
			oldXMLstreamResult.setWriter(oldXMLdebugString);
			tFactory.newTransformer().transform(oldXMLsource,
					oldXMLstreamResult);
			log.debug("oldXML = " + oldXMLdebugString.getBuffer().toString());

			Source newXMLsource = newExchange.getIn().getBody(Source.class);
			DOMResult newXMLdomResult = new DOMResult();
			tFactory.newTransformer().transform(newXMLsource, newXMLdomResult);
			Element newXMLdoc = (Element) newXMLdomResult
					.getNode().getFirstChild();

			StreamResult newXMLstreamResult = new StreamResult();
			StringWriter newXMLdebugString = new StringWriter();
			newXMLstreamResult.setWriter(newXMLdebugString);
			tFactory.newTransformer().transform(newXMLsource,
					newXMLstreamResult);
			log.debug("newXML = " + newXMLdebugString.getBuffer().toString());
			oldXMLdoc.adoptNode(newXMLdoc);
			oldXMLdoc.getFirstChild().appendChild(newXMLdoc);

			StreamResult oldXMLnewstreamResult = new StreamResult();
			StringWriter oldXMLnewdebugString = new StringWriter();
			oldXMLnewstreamResult.setWriter(oldXMLnewdebugString);
			tFactory.newTransformer().transform(new DOMSource(oldXMLdoc),
					oldXMLnewstreamResult);
			log.debug("new oldXML = " + oldXMLnewdebugString.getBuffer().toString());

			oldExchange.getIn().setBody(oldXMLdoc);

		} catch (TransformerConfigurationException e) {
			log.error("TransformerConfigurationException!", e);
		} catch (DOMException e) {
			log.error("DOMException!", e);
		} catch (TransformerException e) {
			log.error("TransformerException!", e);
		}
		return oldExchange;
	}

}
