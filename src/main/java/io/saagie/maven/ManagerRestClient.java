package io.saagie.maven;

import com.google.gson.Gson;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    public void checkManagerConnection() throws MojoExecutionException {
        log.debug("Check Manager Connection ... ");

        ResponseEntity<String> response = restTemplate.exchange(create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId()),
                                                                HttpMethod.GET,
                                                                new HttpEntity<String>(
                                                                        createHeaders()),
                                                                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during check Manager Connection (ErrorCode : " + response.getStatusCode() + " )");
            throw new MojoExecutionException("Error during check SaagieManager connection");
        }
        log.info("Connection to Manager : OK");
    }


    public String uploadFile(String directory, String path) throws URISyntaxException, IOException, MojoExecutionException {
        log.debug("  >> Upload File ... ");

        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
        multipartMap.add("file", new UrlResource(Paths.get(directory, path).toUri()));
        HttpHeaders headers = createHeaders();
        headers.setContentType(new MediaType("multipart", "form-data"));
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job/upload", HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during upload file (ErrorCode : " + response.getStatusCode() + " )");
            throw new MojoExecutionException("Error during jar upload");
        }

        FileName fielName = gson.fromJson(response.getBody(), FileName.class);
        log.info("  >> Upload File OK");
        return fielName.getFileName();
    }

    public Integer createJob(String body) throws MojoExecutionException {
        log.debug("  >> Create Job ... ");
        ResponseEntity<String> response = restTemplate.exchange(
                create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job"),
                HttpMethod.POST,
                new HttpEntity<String>(body, createHeaders()),
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during create job(ErrorCode : " + response.getStatusCode() + " )");
            throw new MojoExecutionException("Error during the job creation");
        }
        Job job = gson.fromJson(response.getBody(), Job.class);
        return job.getId();
    }


    public Job checkJobExists() throws MojoExecutionException {
        log.debug("Check Job {" + managerProperties.getJobId() + "} Exists ... ");

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job/" + managerProperties.getJobId()),
                                             HttpMethod.GET,
                                             new HttpEntity<String>(
                                                     createHeaders()),
                                             String.class);
        } catch (HttpClientErrorException e) {
            log.error("Error during check Job Exists {id:" + managerProperties.getJobId() + "}");
            throw new MojoExecutionException("Error during existing job validation");
        }
        Job job = gson.fromJson(response.getBody(), Job.class);
        if (job != null &&
                managerProperties.getJobName().equals(job.getName()) &&
                managerProperties.getJobCategory().equals(job.getCategory())) {
            log.info("Job {id:" + managerProperties.getJobId() +
                             ", name:" + managerProperties.getJobName() +
                             ", category:" + managerProperties.getJobCategory() +
                             "} exists");
        } else {
            log.error("Error, the job don't correspond : Requested : {id:" + managerProperties.getJobId() +
                              ", name:" + managerProperties.getJobName() +
                              ", category:" + managerProperties.getJobCategory() +
                              "} - In platform : {id:" + job.getId() +
                              ", name:" + job.getName() +
                              ", category:" + job.getCategory() +
                              "}");
            throw new MojoExecutionException("Error during existing job validation");
        }
        return job;
    }

    public void updateJob(Job job) throws MojoExecutionException {
        log.debug("  >> Update Job ... ");
        String body = gson.toJson(job);
        ResponseEntity<String> response = restTemplate.exchange(
                create(managerProperties.getUrlApi() + "/platform/" + managerProperties.getPlatformId() + "/job/" + managerProperties.getJobId() + "/version"),
                HttpMethod.POST,
                new HttpEntity<String>(body, createHeaders()),
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error during update job(ErrorCode : " + response.getStatusCode() + " )");
            throw new MojoExecutionException("Error during the job update");
        }
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
        private String name;
        private String category;
        private Current current;

        public Job() {
        }

        public Integer getId() {
            return id;
        }

        public Job setId(Integer id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public Job setName(String name) {
            this.name = name;
            return this;
        }

        public String getCategory() {
            return category;
        }

        public Job setCategory(String category) {
            this.category = category;
            return this;
        }

        public Current getCurrent() {
            return current;
        }

        public Job setCurrent(Current current) {
            this.current = current;
            return this;
        }

        class Current {
            private Integer id;
            private Integer job_id;
            private Integer number;
            private String template;
            private String file;
            private String creation_date;

            public Current() {
            }

            public Integer getId() {
                return id;
            }

            public Current setId(Integer id) {
                this.id = id;
                return this;
            }

            public Integer getJob_id() {
                return job_id;
            }

            public Current setJob_id(Integer job_id) {
                this.job_id = job_id;
                return this;
            }

            public Integer getNumber() {
                return number;
            }

            public Current setNumber(Integer number) {
                this.number = number;
                return this;
            }

            public String getTemplate() {
                return template;
            }

            public Current setTemplate(String template) {
                this.template = template;
                return this;
            }

            public String getFile() {
                return file;
            }

            public Current setFile(String file) {
                this.file = file;
                return this;
            }

            public String getCreation_date() {
                return creation_date;
            }

            public Current setCreation_date(String creation_date) {
                this.creation_date = creation_date;
                return this;
            }
        }
    }

}
