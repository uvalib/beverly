package edu.virginia.lib.beverly;

import static edu.virginia.lib.beverly.Constants.repo1Url;
import static edu.virginia.lib.beverly.Constants.test1dissemination;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lombok.SneakyThrows;

import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ITestGetIndexingMetadata extends GenericTest {

	final private DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder builder;

	@Before
	public void createParser() throws ParserConfigurationException {
		log("Creating XML parser...");
		builder = factory.newDocumentBuilder();
	}

	/*
	 * This is clunky-- we'd like to use a JUnit @Beforeclass to build the
	 * objects and then just let the tests run, but see:
	 * http://team.ops4j.org/browse/PAXEXAM-288
	 */

	private static boolean objectsAreBuilt = false;

	@Before
	public void buildObjects() throws IOException, SAXException {
		if (!objectsAreBuilt) {
			new BuildFedoraObjects().buildObjects();
		}
		objectsAreBuilt = true;
	}

	/*
	 * Same-ish deal here: we need to make sure that Beverly herself is up and
	 * running before testing her.
	 */

	private static boolean beverlyIsRunning = false;

	@Before
	@SneakyThrows(InvalidSyntaxException.class)
	public void checkBeverly() throws InterruptedException {
		if (!beverlyIsRunning) {
			ServiceTracker tracker = new ServiceTracker(
					context,
					context.createFilter("(camel.context.name=repository-indexer)"),
					null);
			tracker.open();
			tracker.waitForService(0);
			CamelContext camel = (CamelContext) tracker.getService();
			// now we know that Beverly is running
			camel = null;
			context.ungetService(tracker.getServiceReference());
			tracker.close();
		}
		beverlyIsRunning = true;
	}

	//@Test
	public void hangFire() {
		pause();
	}

	@Test
	public void testFedoraIsRunning() throws IOException {
		log("Testing for Fedora's availability...");
		URL url = new URL(repo1Url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		assertEquals("Fedora was not available!", 200, conn.getResponseCode());
		log("Found Fedora.");
	}

	@Test
	public void testObjectsExist() throws IOException {
		URL url;
		HttpURLConnection conn;
		{
			log("Testing for existence of Indexable content model...");
			url = new URL(repo1Url + "/objects/indexable:cm");
			conn = (HttpURLConnection) url.openConnection();
			assertEquals("Indexable content model was not available!", 200,
					conn.getResponseCode());
			log("Found Indexable content model.");
		}
		{
			log("Testing for existence of Indexable service definition...");
			url = new URL(repo1Url + "/objects/indexable:sdef");
			conn = (HttpURLConnection) url.openConnection();
			assertEquals("Indexable service definition was not available!",
					200, conn.getResponseCode());
			log("Found Indexable service definition.");
		}
		{
			log("Testing for existence of Indexable service deployment...");
			url = new URL(repo1Url + "/objects/indexable:sdep");
			conn = (HttpURLConnection) url.openConnection();
			assertEquals("Indexable service deployment was not available!",
					200, conn.getResponseCode());
			log("Found Indexable service deployment.");
		}
		{
			log("Testing for existence of test:1 object...");
			url = new URL(repo1Url + "/objects/test:1");
			conn = (HttpURLConnection) url.openConnection();
			assertEquals("test:1 object was not available!", 200,
					conn.getResponseCode());
			log("Found test:1 object.");
		}
	}

	@Test
	public void testGetIndexingMetadataReturns200() throws IOException {
		log("Testing for successful return from indexing dissemination "
				+ test1dissemination + "...");

		URL url = new URL(test1dissemination);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		assertEquals("Error in retrieving " + test1dissemination
				+ " dissemination!", 200, conn.getResponseCode());
	}

	@Test
	public void testGetIndexingMetadataReturnsXML() {
		log("Testing for XML return from indexing dissemination "
				+ test1dissemination + "...");

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
				+ test1dissemination + " is reasonable Solr XML...");

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

	@SneakyThrows
	private void pause() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
	    String sample = br.readLine();
	}

}
