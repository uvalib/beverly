package edu.virginia.lib.beverly;

import javax.inject.Inject;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.junit.Before;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.osgi.framework.Constants;

public class GenericTest {

	@Inject
	CommandProcessor cp;

	/*
	 * tracks whether the broker has already been started. We might use a JUnit
	 * 
	 * @BeforeClass to do this, but Pax Exam doesn't yet support that:
	 * http://team.ops4j.org/browse/PAXEXAM-288
	 */
	private static boolean brokerStarted = false;

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

	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE,
				"*,org.apache.felix.service.*;status=provisional");
		return probe;
	}
	
	/*
	 * This method returns the configuration that Pax Exam uses to set up the
	 * test container. This is factored into the class KarafConfig for brevity.
	 */
	@Configuration
	public Option[] config() {
		return KarafConfig.config();
	}

	protected void log(String msg) {
		System.out.println(msg);
	}

}
