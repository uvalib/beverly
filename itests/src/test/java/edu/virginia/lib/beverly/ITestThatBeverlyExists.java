package edu.virginia.lib.beverly;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ITestThatBeverlyExists extends GenericTest {

	// TO DO add the other route names here
	private final List<String> ROUTENAMES = Arrays.asList(
			"seda://service:indexing:index",
			"direct://service:indexing:indexIndexableObject");

	private CamelContext camel;

	@Inject
	@Filter(value = "osgi.blueprint.container.symbolicname=edu.virginia.lib.repository-indexing", timeout = 20000)
	BlueprintContainer blueprint;
	
	private ServiceTracker tracker;
	
	@Inject
	CommandProcessor cp;

	@Test
	public void testBlueprintContainerExists() throws Exception {
		log("Testing for Blueprint container...");
		assertNotNull("Could not find Blueprint container for Beverly!",
				blueprint);
		log("Found it! It includes the following components:");
		for (Object id : blueprint.getComponentIds()) {
			log((String) id);
		}
	}

	@Test
	public void testCamelContextExists() throws InterruptedException,
			InvalidSyntaxException {
		log("Testing for Camel context...");
		assertNotNull("Could not find Camel context for Beverly!", camel);
		log("Found CamelContext with name: " + camel.getName());
	}

	@Test
	public void testRoutesExist() throws InterruptedException,
			InvalidSyntaxException {
		log("Testing for Camel routes...");
		log("Found CamelContext with name: " + camel.getName());
		// get names from routes
		List<String> routenames = Lists.transform(camel.getRoutes(),
				new getRouteName());
		log("Found routes with these From URIs:");
		// print 'em
		log(Joiner.on("\n").skipNulls().join(routenames));
		// are the ones we know should exist where they ought be?
		assertTrue(routenames.containsAll(ROUTENAMES));
		log("The right routes exist!");
	}
	
	@Before
	public void getCamelContext() throws InterruptedException, InvalidSyntaxException {
		// we want to do this programmatically, not via injection
		// because the MQ broker may not be fully started yet
		// and we will have to wait for it by waitForService()
		// That's very hard with injection.
		tracker = new ServiceTracker(
				context,
				context.createFilter("(camel.context.name=repository-indexer)"),
				null);
		tracker.open();
		tracker.waitForService(0);
		camel = (CamelContext) tracker.getService();
	}
	
	@After
	public void releaseCamelContext() {
		camel = null;
		context.ungetService(tracker.getServiceReference());
		tracker.close();
	}

	private class getRouteName implements Function<Route, String> {

		@Override
		public String apply(Route route) {
			return route.getEndpoint().getEndpointUri();
		}

	}

}
