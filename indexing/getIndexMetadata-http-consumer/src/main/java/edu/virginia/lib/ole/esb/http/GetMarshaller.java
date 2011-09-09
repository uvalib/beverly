package edu.virginia.lib.ole.esb.http;

import java.io.StringReader;
import java.util.Map;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamSource;

import org.apache.servicemix.http.endpoints.DefaultHttpConsumerMarshaler;

public class GetMarshaller extends DefaultHttpConsumerMarshaler {

	@Override
	public MessageExchange createExchange(HttpServletRequest request,
			ComponentContext context) throws Exception {
		MessageExchange me = context.getDeliveryChannel()
				.createExchangeFactory().createExchange(getDefaultMep());
		NormalizedMessage in = me.createMessage();
		// create dummy message body
		in.setContent(new StreamSource(new StringReader("<dummy/>")));
	
		// it's okay to ignore the cast warning here because getParameterMap()
		// is documented to return a Map<String, String[]>
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = request.getParameterMap();
		for (String paramname : parameters.keySet()) {
			in.setProperty(paramname, parameters.get(paramname)[0]);
		}			
		me.setMessage(in, "in");
		return me;
	}

}
