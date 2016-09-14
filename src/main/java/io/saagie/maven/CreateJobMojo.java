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
        getLog().info("Start Saagie Maven Plugin");

        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties
                .setUrlApi(urlApi)
                .setLogin(login)
                .setPassword(password)
                .setPlatformId(platformId)
                .setJobName(jobName)
                .setJobCategory(jobCategory)
                .setJobType(jobType)
                .setJarName(jarName)
                .setCpu(cpu)
                .setMem(mem)
                .setDisk(disk)
                .setLanguageVersion(languageVersion);


        getLog().info("ManagerProperties : " + managerProperties);
        try {
            loadCredentials(managerProperties);
            ManagerRestClient managerRestClient = new ManagerRestClient(getLog(), managerProperties);
            managerRestClient.checkManagerConnection();
            String directory = project.getBuild().getDirectory();
            String filename = managerRestClient.uploadFile(directory, managerProperties.getJarName());
            String body = "{\"platform_id\":\"" + managerProperties.getPlatformId() + "\",\"capsule_code\":\"" + managerProperties.getJobType() + "\",\"category\":\"" + managerProperties.getJobCategory() + "\",\"current\":{\"template\":\"java -jar {file}\",\"file\":\"" + filename + "\"},\"options\":{\"language_version\":\"" + managerProperties.getLanguageVersion() + "\"},\"cpu\":" + managerProperties.getCpu() + ",\"memory\":" + managerProperties.getMem() + ",\"disk\":" + managerProperties.getDisk() + ",\"manual\":true,\"name\":\"" + managerProperties.getJobName() + "\",\"retry\":\"\",\"schedule\":\"R0/2016-07-06T15:47:52.051Z/P0Y0M1DT0H0M0S\"}";
            Integer jobId = managerRestClient.createJob(body);
            getLog().info("Job : " + jobId);
        } catch (Exception e) {
            getLog().error(e);
        }
    }
}
