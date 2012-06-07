package edu.virginia.lib.camel.builder.xslt;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.spi.ClassResolver;
import org.apache.camel.builder.xml.XsltUriResolver;

/**
 * A simple URIResolver implementation that can be used
 * to add support for the resolution of HTTP URIs to an 
 * existing wrapped URIResolver, or stand alone as a 
 * limited URIResolver that only supports HTTP URIs. 
 */
public class HttpXsltUriResolver implements URIResolver {

	private URIResolver parent;

	private int timeout;

	/**
	 * The default constructor.  HTTP connection and
	 * read timeouts default to 30 seconds and unless
	 * the 'parent' property is set, only HTTP URIs
	 * are resolved by this instance.
	 */
    public HttpXsltUriResolver() {
        this.parent = parent;
        timeout = 30000;
    }

	/**
	 * Sets the "parent" URIResolver.  When non-null this
	 * class will delegate URI resolution to the parent in
	 * all cases *except* HTTP URIs.
	 */
	public void setParent(URIResolver parent) {
		this.parent = parent;
	}
	
	public URIResolver getParent() {
		return parent;
	}
	
	/**
	 * Sets the connection and read timeouts for HTTP
	 * connections.  The provided value is in milliseconds.
	 */
	public void setTimeout(int timeoutMs) {
		timeout = timeoutMs;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * Resolves HTTP URI's or delegates to the 'parent' (if set)
	 * for all other protocols.  The current implementation uses
	 * a simple StreamSource that wraps the InputStream returned
	 * by an HttpURLConnection object.
	 * @throws TransformerException if there is no parent and
	 * the protocol isn't HTTP, if there's an IOException while
	 * creating an InputStream to the content at the provided href,
	 * or if processing has been delegated to the parent and that 
	 * parent URIResolver throws a TransformerException.
	 */
    public Source resolve(String href, String base) throws TransformerException {
        if (href != null && href.toLowerCase().startsWith("http:")) {
            try {
			    URL url = new URL(href);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                return new StreamSource(conn.getInputStream());
            } catch (IOException ex) {
			    throw new TransformerException(ex);
			}
        } else if (parent != null) {
            return parent.resolve(href, base);
        } else {
			throw new TransformerException("Unsupported protocol (only http is supported): " + href);
		}
    }

}
