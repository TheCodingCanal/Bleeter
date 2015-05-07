package bleeter.bleets;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BleetServices {
	@Autowired
	private BleetRepository bleetRepository;
	
	public static final String SENTIMENT_HOST = "sentiment.vivekn.com/api/text/";
	public static final int PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE = 0;
	public static final Sort DEFAULT_SORT = new Sort(Direction.ASC, "username");

	public Page<Bleet> findBleets(String uid, int p, Sort s){
		Pageable page = new PageRequest(p, PAGE_SIZE, s);
		return bleetRepository.findByUid(uid, page);
	}
	
	public Page<Bleet> findAllBleets(int p, Sort s) {
		Pageable page = new PageRequest(p, PAGE_SIZE, s);
		return bleetRepository.findAll(page);
	}
	
	public Page<Bleet> addBleet(String uid, Bleet newBleet) {
		readSentiment(newBleet);
		bleetRepository.insert(newBleet);
		return findBleets(uid, DEFAULT_PAGE, DEFAULT_SORT);
	}
	
	public Page<Bleet> deleteBleet(String uid, String bid) {
		bleetRepository.delete(bid);
		return findBleets(uid, DEFAULT_PAGE, DEFAULT_SORT);
	}

	public Page<Bleet> updateBleet(String uid, String bid, String bleet,
			Boolean privatecomment) {
		Bleet newBleet = bleetRepository.findOne(bid);
		readSentiment(newBleet);
		newBleet.setBleet(bleet);
		newBleet.setPrivateComment(privatecomment);
		bleetRepository.delete(bid);
		return addBleet(uid, newBleet);
	}
	
	private void readSentiment (Bleet bleet) {
		try {
		URI uri = new URIBuilder()
		.setScheme("http")
		.setHost(SENTIMENT_HOST)
		.build();
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.setHeader("Cache-Control", "no-cache");
		String body = "txt=" + bleet.getBleet();
		httppost.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom()
		        .setSocketTimeout(5000)
		        .setConnectTimeout(5000)
		        .build();
		
		httppost.setConfig(requestConfig);
		System.out.println(httppost);
		Header[] headers = httppost.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
		System.out.println(httppost.getEntity());
		CloseableHttpResponse response = httpclient.execute(httppost);		
		
		HttpEntity result = response.getEntity();
		InputStream stream = result.getContent();			
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode root = mapper.readTree(stream);
		JsonNode res = root.get("result");
		bleet.setSentiment(res.get("sentiment").asText());
		double conf = res.get("confidence").asDouble();
		bleet.setConfidence((float) conf);
		stream.close();
		httpclient.close();
		} catch (Exception e) {
			System.out.println(e);
		} 
	}

	
	
	
	
}