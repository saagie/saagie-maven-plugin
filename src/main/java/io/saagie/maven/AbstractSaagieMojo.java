package io.saagie.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Created by pleresteux on 14/09/16.
 */
abstract class AbstractSaagieMojo extends AbstractMojo {

    /**
     * The Maven Project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;
    /**
     * URL of the Saagie Manager : Prod by default
     */
    @Parameter(property = "urlApi", readonly = true, required = false, defaultValue = "https://manager.prod.saagie.io/api/v1")
    String urlApi;
    /**
     * login in the manager
     */
    @Parameter(property = "login", readonly = true, required = false)
    String login;
    /**
     * password in the manager
     */
    @Parameter(property = "password", readonly = true, required = false)
    String password;
    /**
     * The id of the plateform
     */
    @Parameter(property = "platformId", readonly = true, required = true)
    String platformId;
    /**
     * The name of the job
     */
    @Parameter(property = "jobName", readonly = true, required = true)
    String jobName;
    /**
     * The category of the job
     */
    @Parameter(property = "jobCategory", readonly = true, required = true)
    String jobCategory;
    /**
     * The type of the job
     */
    @Parameter(property = "jobType", readonly = true, required = true, defaultValue = "java-scala")
    String jobType;
    /**
     * The id of the job (only for update)
     */
    @Parameter(property = "jobId", readonly = true, required = false)
    String jobId;
    /**
     * The name of the jar
     */
    @Parameter(property = "jarName", readonly = true, required = true, defaultValue = "${project.build.finalName}-jar-with-dependencies.jar")
    String jarName;
    /**
     * The language version
     */
    @Parameter(property = "languageVersion", readonly = true, required = true, defaultValue = "8")
    String languageVersion;
    /**
     * The cpu limit
     */
    @Parameter(property = "cpu", readonly = true, required = true, defaultValue = "0.5")
    String cpu;
    /**
     * The disk limit
     */
    @Parameter(property = "disk", readonly = true, required = true, defaultValue = "1024")
    String disk;
    /**
     * The memory limit
     */
    @Parameter(property = "mem", readonly = true, required = true, defaultValue = "512")
    String mem;
    ManagerProperties managerProperties = new ManagerProperties();
    @Component
    private Settings settings;
    @Component(role = SecDispatcher.class, hint = "mng-4384")
    private SecDispatcher secDispatcher;
    /**
     * The server id (in settings.xml
     */

    @Parameter(property = "serverId", readonly = true, required = true, defaultValue = "saagie-manager")
    private String serverId;

    /**
     * Load properties and generate ManagerProperties
     */
    protected void loadProperties() {
        getLog().info("Start Saagie Maven Plugin");

        managerProperties
                .setUrlApi(urlApi)
                .setLogin(login)
                .setPassword(password)
                .setPlatformId(platformId)
                .setJobName(jobName)
                .setJobCategory(jobCategory)
                .setJobType(jobType)
                .setJobId(jobId)
                .setJarName(jarName)
                .setCpu(cpu)
                .setMem(mem)
                .setDisk(disk)
                .setLanguageVersion(languageVersion);


        getLog().debug("ManagerProperties : " + managerProperties);
    }

    /**
     * Get Login/password from settings.xml / server
     */
    protected void loadCredentials(ManagerProperties managerProperties) throws MojoExecutionException, SecDispatcherException {
        if (settings != null) {
            final Server server = settings.getServer(serverId);
            if (server != null) {
                final String username = server.getUsername();
                String password = server.getPassword();
                if (null != password &&
                        password.startsWith("{") &&
                        password.endsWith("}")) {
                    password = secDispatcher.decrypt(password);
                }
                managerProperties.setLogin(username);
                managerProperties.setPassword(password);
            }
        }
    }

    /**
     * Generate URL of the job
     *
     * @param jobId
     */
    protected String generateURLJob(Integer jobId) {
        return managerProperties.getUrlApi().replace("/api/v1", "/#/manager/" + managerProperties.getPlatformId() + "/job/" + jobId);
    }
}
