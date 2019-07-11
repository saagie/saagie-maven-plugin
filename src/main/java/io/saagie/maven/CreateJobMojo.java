package io.saagie.maven;

import org.apache.maven.plugins.annotations.Mojo;
import org.jetbrains.annotations.NotNull;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

/**
 * Created by pleresteux on 14/09/16.
 */
@Mojo(name = "create", requiresDependencyResolution = COMPILE)
public class CreateJobMojo extends AbstractSaagieMojo {

    @Override
    public void execute() {
        loadProperties();
        try {
            loadCredentials(managerProperties);
            ManagerRestClient managerRestClient = new ManagerRestClient(getLog(), managerProperties);
            managerRestClient.checkManagerConnection();
            String directory = project.getBuild().getDirectory();
            String filename = managerRestClient.uploadFile(directory, managerProperties.getJarName());
            String body = requestBodyBuilder(managerProperties.getPlatformId(),
                    managerProperties.getJobType(),
                    managerProperties.getJobCategory(),
                    managerProperties.getCpu(),
                    managerProperties.getDisk(),
                    managerProperties.getMem(),
                    filename,
                    generateJobTemplate(managerProperties.getArguments()),
                    managerProperties.getLanguageVersion(),
                    managerProperties.getReleaseNote(),
                    managerProperties.getDescription(),
                    managerProperties.getJobName());
            getLog().debug("  >> Job creation request body: " + body);
            Integer jobId = managerRestClient.createJob(body);
            getLog().info("  >> Job created : " + generateJobURL(jobId));
        } catch (Exception e) {
            getLog().error(e);
        }
    }

    @NotNull
    private String requestBodyBuilder(String platformId, String objectType, String jobCategory,
                                      String cpu, String disk, String memory, String filename, String args,
                                      String languageVersion, String releaseNote, String description, String jobName) {
        return "{" +
                "\"platform_id\": \"" + platformId + "\", " +
                "\"always_email\": false" + ", " +
                "\"capsule_code\": \"" + objectType + "\", " +
                "\"category\": \"" + jobCategory + "\", " +
                "\"current\": {" +
                "   \"cpu\": " + cpu + ", " +
                "   \"disk\": " + disk + ", " +
                "   \"memory\": " + memory + ", " +
                "   \"file\": \"" + filename + "\", " +
                "   \"template\": \"" + args + "\", " +
                "   \"options\": {" +
                "       \"language_version\": \"" + languageVersion + "\"" +
                "   }," +
                "   \"releaseNote\": \"" + releaseNote + "\"" +
                "}," +
                "\"description\": \"" + description + "\", " +
                "\"manual\": true, " +
                "\"name\": \"" + jobName + "\", " +
                "\"retry\": \"\", " +
                "\"schedule\": \"R0/2016-07-06T15:47:52.051Z/P0Y0M1DT0H0M0S\"" +
                "}";
    }


}
