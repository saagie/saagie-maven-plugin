package io.saagie.maven;

import org.apache.maven.plugins.annotations.Mojo;

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
            String body = "{" +
                    "\"platform_id\": \"" + managerProperties.getPlatformId() + "\", " +
                    "\"always_email\": false" + ", " +
                    "\"capsule_code\": \"" + managerProperties.getJobType() + "\", " +
                    "\"category\": \"" + managerProperties.getJobCategory() + "\", " +
                    "\"current\": {" +
                    "   \"cpu\": " + managerProperties.getCpu() + ", " +
                    "   \"disk\": " + managerProperties.getDisk() + ", " +
                    "   \"memory\": " + managerProperties.getMem() + ", " +
                    "   \"file\": \"" + filename + "\", " +
                    "   \"template\": \"" + generateJobTemplate(managerProperties.getArguments()) + "\", " +
                    "   \"options\": {" +
                    "       \"language_version\": \"" + managerProperties.getLanguageVersion() + "\"" +
                    "   }," +
                    "   \"releaseNote\": \"" + managerProperties.getReleaseNote() + "\"" +
                    "}," +
                    "\"description\": \"" + managerProperties.getDescription() + "\", " +
                    "\"manual\": true, " +
                    "\"name\": \"" + managerProperties.getJobName() + "\", " +
                    "\"retry\": \"\", " +
                    "\"schedule\": \"R0/2016-07-06T15:47:52.051Z/P0Y0M1DT0H0M0S\"" +
                    "}";
            getLog().debug("  >> Job creation request body: " + body);
            Integer jobId = managerRestClient.createJob(body);
            getLog().info("  >> Job created : " + generateJobURL(jobId));
        } catch (Exception e) {
            getLog().error(e);
        }
    }
}
