package edu.virginia.lib.camel.parsing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Message;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.osgi.service.log.LogService;

public class TikaParser {
	
	private LogService log;

	private Tika tika;

	public TikaParser() {
		tika = new Tika();
		tika.setMaxStringLength(-1);
	}

	public String parse(Message msg) throws IOException, TikaException {

		String mimeType = msg.getHeader("MIMEtype", String.class);
		InputStream stream = new ByteArrayInputStream(msg.getBody(byte[].class));
		debug("Recovered message body for Tika parsing.");
		// parse it with Tika and return XML with the metadata
		Metadata metadata = new Metadata();
		metadata.add("Content-Type", mimeType);
		debug("Parsing with Tika using Content-Type: " +  mimeType);
		tika.parse(stream, metadata);
		stream.close();
		StringBuffer fields = new StringBuffer();
		for (String name : metadata.names()) {
			for (String value : metadata.getValues(name)) {
				fields.append("\t" + writeElement(name, value));
			}
		}
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ writeElement("Metadata", fields.toString());
	}

	private String writeElement(String name, String body) {
		return "<" + name + ">" + body + "</" + name + ">\n";
	}
	
	private void debug (String msg) {
		log.log(LogService.LOG_DEBUG, msg);
	}

	public LogService getLog() {
		return log;
	}

	public void setLog(LogService log) {
		this.log = log;
	}

}
