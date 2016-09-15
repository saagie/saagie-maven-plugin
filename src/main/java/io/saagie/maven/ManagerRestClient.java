package io.saagie.maven;

import com.google.gson.Gson;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static java.net.URI.create;

/**
 * Created by pleresteux on 14/09/16.
 */
public class ManagerRestClient {

    private final Log log;
    private final ManagerProperties managerProperties;
    Gson gson = new Gson();
    private RestTemplate restTemplate = restTemplate();

    public ManagerRestClient(Log log, ManagerProperties managerProperties) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.log = log;
        this.managerProperties = managerProperties;
    }


    private RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
        CloseableHttpClient httpclient = HttpClients
                .custom()
                .setMaxConnPerRoute(10) //to allow more than 2 connections in //
                .setSSLSocketFactory(sslsf).build();
        requestFactory.setHttpClient(httpclient);
        requestFactory.setConnectTimeout(3000);
        return new RestTemplate(requestFactory);
    }

    public void checkManagerConnection() {
        log.debug("Check Manager Connection ... ");

        ResponseEntity<String> response = restTemplate.exchange(create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId()),
                                                                HttpMethod.GET,
                                                                new HttpEntity<String>(
                                                                        createHeaders()),
                                                                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during check Manager Connection (ErrorCode : " + response.getStatusCode() + " )");
            return;
        }
        log.info("Connection to Manager : OK");
    }


    public String uploadFile(String directory, String path) throws URISyntaxException, IOException {
        log.debug("  >> Upload File ... ");

        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
        multipartMap.add("file", new UrlResource(Paths.get(directory, path).toUri()));
        HttpHeaders headers = createHeaders();
        headers.setContentType(new MediaType("multipart", "form-data"));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job/upload", HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during upload file (ErrorCode : " + response.getStatusCode() + " )");
            return null;
        }

        FileName fielName = gson.fromJson(response.getBody(), FileName.class);
        log.info("  >> Upload File OK");
        return fielName.getFileName();
    }

    public Integer createJob(String body) {
        log.debug("  >> Create Job ... ");
        ResponseEntity<String> response = restTemplate.exchange(
                create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job"),
                HttpMethod.POST,
                new HttpEntity<String>(body, createHeaders()),
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during upload file (ErrorCode : " + response.getStatusCode() + " )");
            return null;
        }
        Job job = gson.fromJson(response.getBody(), Job.class);
        return job.getId();
    }


    private HttpHeaders createHeaders() {
        return new HttpHeaders() {
            {
                String auth = managerProperties.getLogin() + ":" + managerProperties.getPassword();
                String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes(Charset.forName("UTF-8")));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
    }

    class FileName {
        private String fileName;

        public FileName() {
        }

        public String getFileName() {
            return fileName;
        }

        public FileName setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
    }

    class Job {
        private Integer id;
        private String status;
        private String subDomain;

        public Job() {
        }

        public Integer getId() {
            return id;
        }

        public Job setId(Integer id) {
            this.id = id;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public Job setStatus(String status) {
            this.status = status;
            return this;
        }

        public String getSubDomain() {
            return subDomain;
        }

        public Job setSubDomain(String subDomain) {
            this.subDomain = subDomain;
            return this;
        }
    }
}
