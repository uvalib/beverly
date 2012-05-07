package edu.virginia.lib.beverly;

import java.io.IOException;
import java.net.MalformedURLException;

import lombok.SneakyThrows;

import org.apache.camel.Route;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.osgi.framework.Constants;

import com.google.common.base.Function;

public class TestPropagation {
	
	final private String test1 = "http://localhost:8080/fedora/objects/test:1";
	final private String indexMetadata = "";
	
	@Test
	@SneakyThrows(MalformedURLException.class)
	public void testGetIndexingMetadataReturnsXml() throws IOException {
		
		
	}
	
	
	/*
	 * This method returns the configuration that Pax Exam uses
	 * to set up the test container. This is factored into the
	 * class KarafConfig for reuse.
	 */
	@Configuration
	public Option[] config() {
		return KarafConfig.config();
	}

	/*
	 * We want to allow the Felix Gogo CLI to be loaded into the test
	 * container, but the exports of the Gogo packages are provisional,
	 * so we annotate the test probe-bundle as follows.
	 */
	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE,
				"*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	private class getRouteName implements Function<Route, String> {

		@Override
		public String apply(Route route) {
			return route.getEndpoint().getEndpointUri();
		}

	}

	private void log(String msg) {
		System.out.println(msg);
	}

}
