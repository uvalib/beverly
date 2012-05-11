package edu.virginia.lib.camel.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class RedirectHttpClientConfigurer implements HttpClientConfigurer {

	public RedirectHttpClientConfigurer() {
	}

	@Override
	public void configureHttpClient(HttpClient client) {
		HttpParams params = client.getParams();
		HttpClientParams.setRedirecting(params, true);
		((AbstractHttpClient) client).setRedirectStrategy(new MindlessRedirectStrategy());
	}

	public class MindlessRedirectStrategy implements RedirectStrategy {

		@Override
		public boolean isRedirected(HttpRequest request, HttpResponse response,
				HttpContext context) throws ProtocolException {
			return true;
		}

		@Override
		public HttpUriRequest getRedirect(HttpRequest request,
				HttpResponse response, HttpContext context)
				throws ProtocolException {
			URI location = null;
			try {
				location = new URI(response.getLastHeader("Location").getValue());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			RequestWrapper rerequest = new RequestWrapper(request);
			rerequest.setURI(location);
			return rerequest;
		}

	}
}
