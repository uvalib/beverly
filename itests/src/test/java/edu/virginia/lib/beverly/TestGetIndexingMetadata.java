package edu.virginia.lib.beverly;

import static edu.virginia.lib.beverly.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RunWith(JUnit4TestRunner.class)
public class TestGetIndexingMetadata extends GenericTest {

	final private DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder builder;

	@Before
	public void createParser() throws ParserConfigurationException {
		log("Creating XML parser...");
		builder = factory.newDocumentBuilder();
	}

	@Test
	public void testGetIndexingMetadataReturns200() throws IOException {
		log("Testing for successful return from indexing dissemination "
				+ test1dissemination);

		URL url = new URL(test1dissemination);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		assertEquals("Error in retrieving " + test1dissemination
				+ " dissemination!", 200, conn.getResponseCode());
	}

	@Test
	public void testGetIndexingMetadataReturnsXML() {
		log("Testing for XML return from indexing dissemination "
				+ test1dissemination);

		try {
			URL url = new URL(test1dissemination);
			Document doc = builder.parse(url.openStream());
		} catch (SAXException e) {
			fail(test1dissemination + " was not parseable XML!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not load " + test1dissemination + "!");
		}
	}

	@Test
	public void testGetIndexingMetadataReturnsReasonableSolrXml()
			throws SAXException {
		log("Testing that return from indexing dissemination "
				+ test1dissemination + " is reasonable Solr XML");

		Document doc = null;
		try {
			URL url = new URL(test1dissemination);
			doc = builder.parse(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Could not load " + test1dissemination + "!");
		}
		NodeList nodes = doc.getElementsByTagName("add");
		assertEquals("Not exactly one <add/> element in " + test1dissemination
				+ " results!", nodes.getLength(), 1);
		nodes = doc.getElementsByTagName("doc");
		assertEquals("Not exactly one <doc/> element in " + test1dissemination
				+ " results!", nodes.getLength(), 1);
	}

}
