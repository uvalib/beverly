package edu.virginia.lib.beverly;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@RunWith(JUnit4TestRunner.class)
//@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class TestThatBeverlyExists {

	// TO DO add the other route names here
	private final List<String> ROUTENAMES = Arrays.asList(
			"seda://service:indexing:index",
			"direct://service:indexing:indexIndexableObject");

	@Inject
	BundleContext context;

	@Inject
	@Filter(value = "osgi.blueprint.container.symbolicname=edu.virginia.lib.repository-indexing", timeout = 20000)
	BlueprintContainer blueprint;

	@Inject
	CommandProcessor cp;

	private ServiceTracker tracker = null;
	private CamelContext camel;

	/*
	 * tracks whether the broker has already been started. We might use a JUnit
	 * @BeforeClass to do this, but Pax Exam doesn't yet support that:
	 * http://team.ops4j.org/browse/PAXEXAM-288
	 */
	private boolean brokerStarted = false;

	@Before
	public void startMQ() throws Exception {
		if (!brokerStarted) {
			log("Starting ActiveMQ broker...");
			CommandSession cs = cp.createSession(System.in, System.out,
					System.err);
			cs.execute("activemq:create-broker -t blueprint");
		}
		brokerStarted = true;
	}

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
		log("Testing for Camel context...");
		assertNotNull("Could not find Camel context for Beverly!",
				camel);
		log("Found CamelContext with name: " + camel.getName());
		camel = null;
		tracker.close();
	}

	@Test
	public void testRoutesExist() throws InterruptedException,
			InvalidSyntaxException {
		// see above for why we don't just inject the CamelContext
		tracker = new ServiceTracker(
				context,
				context.createFilter("(camel.context.name=repository-indexer)"),
				null);
		tracker.open();
		tracker.waitForService(0);
		camel = (CamelContext) tracker.getService();
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
		camel = null;
		tracker.close();
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
