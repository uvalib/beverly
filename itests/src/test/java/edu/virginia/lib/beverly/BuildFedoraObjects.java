package edu.virginia.lib.beverly;

import static edu.virginia.lib.beverly.Constants.repo1Url;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;

import lombok.SneakyThrows;

import org.osgi.framework.FrameworkUtil;

import com.google.common.io.ByteStreams;

/*
 * Just a utility class to load and ingest our test Fedora objects.
 */
public class BuildFedoraObjects {

	final private static String repo1ObjectsUrlString = repo1Url
			+ "/objects/new";

	@SneakyThrows
	protected void buildObjects() {

		log("Building and ingesting Fedora object into " + repo1Url);

		/*
		 * Pull object FOXML from this bundle, where it is found under classpath
		 * "foxml/*". Any resource matching "*.xml" will be assumed to be FOXML
		 * and ingested.
		 */
		Enumeration<URL> objectUrls = FrameworkUtil.getBundle(this.getClass())
				.findEntries("foxml", "*.xml", true);
		
		HttpURLConnection conn;
		URL repo1ObjectsUrl = new URL(repo1ObjectsUrlString);

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("fedoraAdmin", "fedoraAdmin"
						.toCharArray());
			}
		});

		// blast them one by one at the REST API
		for (URL objectUrl : Collections.list(objectUrls)) {
			log("Now ingesting " + objectUrl + " into " + repo1Url);
			conn = (HttpURLConnection) repo1ObjectsUrl.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type", "text/xml");
			InputStream from = objectUrl.openStream();
			OutputStream to = conn.getOutputStream();
			ByteStreams.copy(from, to);
			from.close();
			to.close();
			log("Received " + conn.getResponseCode() + " HTTP response from Fedora.");
		}
	}

	protected void log(String msg) {
		System.out.println(msg);
	}
}
