package io.saagie.maven;

import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

/**
 * Created by pleresteux on 14/09/16.
 */
@Mojo(name = "update", requiresDependencyResolution = COMPILE)
public class UpdateJobMojo extends AbstractSaagieMojo {

    @Override
    public void execute() {
        loadProperties();
        try {
            loadCredentials(managerProperties);
            ManagerRestClient managerRestClient = new ManagerRestClient(getLog(), managerProperties);
            managerRestClient.checkManagerConnection();
            ManagerRestClient.Job job = managerRestClient.checkJobExists();
            String directory = project.getBuild().getDirectory();
            String filename = managerRestClient.uploadFile(directory, managerProperties.getJarName());
            job.getCurrent().setFile(filename);// Update file
            job.getCurrent().setTemplate(generateJobTemplate(managerProperties.getArguments()));
            job.getCurrent().getOptions().setLanguage_version(managerProperties.getLanguageVersion());
            job.getCurrent().setCpu(managerProperties.getCpu());
            job.getCurrent().setMemory(managerProperties.getMem());
            job.getCurrent().setDisk(managerProperties.getDisk());
            job.getCurrent().setReleaseNote(managerProperties.getReleaseNote());
            getLog().debug("  >> Updating job " + job.getId() + " with: " + job.toString());
            managerRestClient.updateJob(job);
            getLog().info("Job updated : " + generateJobURL(job.getId()) + "/versions");
        } catch (Exception e) {
            getLog().error(e);
        }
    }
}
