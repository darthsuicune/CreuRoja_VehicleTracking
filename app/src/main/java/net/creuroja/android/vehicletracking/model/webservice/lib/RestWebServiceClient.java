package net.creuroja.android.vehicletracking.model.webservice.lib;

import net.creuroja.android.vehicletracking.model.webservice.Response;
import net.creuroja.android.vehicletracking.model.webservice.ResponseFactory;

import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class RestWebServiceClient {
	String protocol;
	String host;
	HttpURLConnection connection;
	ResponseFactory responseFactory;
	Response response;
	URL url;

	public RestWebServiceClient(ResponseFactory factory, String protocol, String serverUrl) {
		this.responseFactory = factory;
		this.protocol = protocol;
		this.host = serverUrl;
	}

	public Response get(String resource, List<WebServiceOption> headerOptions,
						List<WebServiceOption> getOptions) throws IOException {
		try {
			setUpConnection(resource, headerOptions, getOptions);
			response = responseFactory.build(connection);
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private void setUpConnection(String resource, List<WebServiceOption> headerOptions,
								 List<WebServiceOption> urlOptions) throws IOException {
		url = new URL(protocol + "://" + host + "/" + resourceWithOptions(resource, urlOptions));
		connection = (HttpURLConnection) url.openConnection();
		addHeaders(headerOptions);
	}

	private String resourceWithOptions(String resource, List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder(resource);
		if (options.size() > 0) {
			builder.append("?");
			builder.append(encodeOptions(options));
		}
		return builder.toString();
	}

	private void addHeaders(List<WebServiceOption> options) {
		for (WebServiceOption option : options) {
			connection.setRequestProperty(option.key, option.value);
		}
	}

	private String encodeOptions(List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < options.size(); i++) {
			WebServiceOption option = options.get(i);
			builder.append(URLEncoder.encode(option.key, "UTF-8"));
			builder.append("=");
			builder.append(URLEncoder.encode(option.value, "UTF-8"));
			if (i < options.size() - 1) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public Response post(String resource, List<WebServiceOption> headerOptions,
						 List<WebServiceOption> urlOptions, List<WebServiceOption> postOptions)
			throws IOException, JSONException {
		try {
			setUpConnection(resource, headerOptions, urlOptions);
			writePostOptions(postOptions);
			response = responseFactory.build(connection);
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private void writePostOptions(List<WebServiceOption> options)
			throws IOException, JSONException {
		String toWrite = encodeOptions(options);
		connection.setDoOutput(true);
		connection.setFixedLengthStreamingMode(toWrite.length());
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		try {
			writer.write(toWrite);
			writer.flush();
		} finally {
			writer.close();
		}
	}
}