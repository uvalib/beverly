package edu.virginia.lib.beverly;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;

import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationFilePutOption;
import org.openengsb.labs.paxexam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.Option;

public class KarafConfig {
	public static Option[] config() {
		return new Option[] {
				// here we set up the basic construction of our test container
				karafDistributionConfiguration()
						.frameworkUrl(
								maven().groupId("org.apache.karaf")
										.artifactId("apache-karaf").type("zip")
										.versionAsInProject())
						.karafVersion("2.2.7")
						.unpackDirectory(new File("target/karaf/")),
				// junitBundles(),
				compendiumProfile(),
				// just in case we want to look at debris, we should keep the
				// containers: let 'mvn clean' clean up if needed.
				keepRuntimeFolder(), logLevel(LogLevel.DEBUG),
				// the tests use Guava to simplify some collections jazz
				mavenBundle("com.google.guava", "guava").versionAsInProject(),
				// of course, Beverly needs an MQ plant
				scanFeatures(maven("org.apache.activemq", "activemq-karaf")
						.type("xml").classifier("features"),
						"activemq-blueprint"),
				// and we want to install Beverly herself
				scanFeatures(maven("edu.virginia.lib", "repository-indexing")
						.type("xml").classifier("features"),
						"repository-indexing") };
	}
}
