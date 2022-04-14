import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
public static final String REMOTE_SERVICE_URI =
        "https://api.nasa.gov/planetary/apod?api_key=" +
                "hFCfg2MbTzAcD7ac3hzSPSEoaxQdbyEZBebxCVFo";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);

        String body = new String(response.getEntity().getContent().readAllBytes(),
                StandardCharsets.UTF_8);

        Post post = mapper.readValue(body, Post.class);

        String url = post.getUrl();

        int lastIndex = url.lastIndexOf("/");
        String targetFileName = url.substring(lastIndex + 1);

        HttpGet request1 = new HttpGet(url);
        request1.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response1 = httpClient.execute(request1);

        try {FileOutputStream fos = new FileOutputStream(targetFileName);
           byte[] bytes = response1.getEntity().getContent().readAllBytes();
        fos.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
