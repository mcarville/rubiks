package com.esgui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class ElasticSearchProxy {

	
	
	private static final String ELASTICSEARCH_URL_PATTERN = "%s://%s:%s/%s/%s";
	private static final String ELASTIC_SEARCH_PROTOCOL = "elasticSearchProtocol";
	private static final String ELASTIC_SEARCH_PROTOCOL_DEFAULT = "http";
	
	private static final String ELASTIC_SEARCH_HOSTNAME = "elasticSearchHostname";
	private static final String ELASTIC_SEARCH_HOSTNAME_DEFAULT = "192.168.71.133";
	
	private static final String ELASTIC_SEARCH_PORT = "elasticSearchPort";
	

	protected String buildElasticSearchUrl(String index, String requestHandler) {
		
		String protocol = StringUtils.isNotEmpty(System.getenv(ELASTIC_SEARCH_PROTOCOL)) ? System.getenv(ELASTIC_SEARCH_PROTOCOL) : ELASTIC_SEARCH_PROTOCOL_DEFAULT;
		String hostname = StringUtils.isNotEmpty(System.getenv(ELASTIC_SEARCH_HOSTNAME)) ? System.getenv(ELASTIC_SEARCH_HOSTNAME) : ELASTIC_SEARCH_HOSTNAME_DEFAULT;  
		int port = StringUtils.isNotEmpty(System.getenv(ELASTIC_SEARCH_PORT)) ? Integer.parseInt(System.getenv(ELASTIC_SEARCH_PORT)) : 9200;
		
		return String.format(ELASTICSEARCH_URL_PATTERN, protocol, hostname, port, index, requestHandler);
	}
	
	@RequestMapping(path = "/elasticsearch/{index}/{requestHandler}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public StreamingResponseBody index(
			@PathVariable("index") String index,
			@PathVariable("requestHandler") String requestHandler,
			@RequestHeader MultiValueMap<String,String> requestHeaders, @RequestBody String requestBody) throws ClientProtocolException,
			IOException {

		HttpClient httpClient = HttpClients.createDefault();

		String esUrl = buildElasticSearchUrl(index, requestHandler);
		HttpPost httpPost = new HttpPost(esUrl);
		
		if(requestHeaders != null) {
			for(Entry<String, List<String>> header : requestHeaders.entrySet()) {
				if( ! header.getKey().equalsIgnoreCase("Content-Length")) {
					for(String headerValue : header.getValue()) {
						httpPost.addHeader(header.getKey(), headerValue);
					}
				}
			}
		}
		
		if (StringUtils.isNotEmpty(requestBody))
			httpPost.setEntity(new StringEntity(requestBody));

		final HttpResponse httpResponse = httpClient.execute(httpPost);

		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value())
			return new StreamingResponseBody() {
				@Override
				public void writeTo(OutputStream outputStream) throws IOException {
					InputStream inputStream = httpResponse.getEntity().getContent();
					IOUtils.copy(inputStream, outputStream);
					IOUtils.closeQuietly(inputStream);
				}
			};
		throw new IllegalStateException(String.format("Not expected http status '%s' from %s", httpResponse.getStatusLine().getStatusCode(), esUrl));
	}


}
