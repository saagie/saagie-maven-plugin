package io.saagie.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


/**
 * Created by pleresteux on 15/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractSaagieMojoTest {

    private static final String URL_API = "https://manager.prod.saagie.io/api/v1";
    private static final String JOB_TYPE = "java-scala";
    private static final String JAR_NAME = "test-jar-with-dependencies.jar";
    private static final String LANGUAGE_VERSION = "8";
    private static final String CPU = "0.5";
    private static final String DISK = "1024";
    private static final String MEM = "512";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String PLATFORM_ID = "1";
    private static final String JOB_NAME = "MavenProject";
    private static final String JOB_CATEGORY = "processing";
    private static final String SERVER_ID = "saagie-manager";

    @Mock
    Settings settings;

    @Mock
    SecDispatcher secDispatcher;


    @InjectMocks
    AbstractSaagieMojo abstractSaagieMojo = new AbstractSaagieMojo() {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {

        }
    };


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(abstractSaagieMojo, "serverId", SERVER_ID);
        // DefaultValues
        ReflectionTestUtils.setField(abstractSaagieMojo, "urlApi", URL_API);
        ReflectionTestUtils.setField(abstractSaagieMojo, "jobType", JOB_TYPE);
        ReflectionTestUtils.setField(abstractSaagieMojo, "jarName", JAR_NAME);
        ReflectionTestUtils.setField(abstractSaagieMojo, "languageVersion", LANGUAGE_VERSION);
        ReflectionTestUtils.setField(abstractSaagieMojo, "cpu", CPU);
        ReflectionTestUtils.setField(abstractSaagieMojo, "disk", DISK);
        ReflectionTestUtils.setField(abstractSaagieMojo, "mem", MEM);
        // Override values
        ReflectionTestUtils.setField(abstractSaagieMojo, "login", LOGIN);
        ReflectionTestUtils.setField(abstractSaagieMojo, "password", PASSWORD);
        ReflectionTestUtils.setField(abstractSaagieMojo, "platformId", PLATFORM_ID);
        ReflectionTestUtils.setField(abstractSaagieMojo, "jobName", JOB_NAME);
        ReflectionTestUtils.setField(abstractSaagieMojo, "jobCategory", JOB_CATEGORY);
    }


    @Test
    public void should_load_properties() {
        //Given
        //When
        abstractSaagieMojo.loadProperties();
        //Then
        //default values
        ManagerProperties managerProperties = abstractSaagieMojo.managerProperties;
        assertThat(managerProperties.getUrlApi()).isEqualTo(URL_API);
        assertThat(managerProperties.getJobType()).isEqualTo(JOB_TYPE);
        assertThat(managerProperties.getJarName()).isEqualTo(JAR_NAME);
        assertThat(managerProperties.getLanguageVersion()).isEqualTo(LANGUAGE_VERSION);
        assertThat(managerProperties.getCpu()).isEqualTo(CPU);
        assertThat(managerProperties.getDisk()).isEqualTo(DISK);
        assertThat(managerProperties.getMem()).isEqualTo(MEM);
        //Override values
        assertThat(managerProperties.getLogin()).isEqualTo(LOGIN);
        assertThat(managerProperties.getPassword()).isEqualTo(PASSWORD);
        assertThat(managerProperties.getPlatformId()).isEqualTo(PLATFORM_ID);
        assertThat(managerProperties.getJobName()).isEqualTo(JOB_NAME);
        assertThat(managerProperties.getJobCategory()).isEqualTo(JOB_CATEGORY);
    }

    @Test
    public void should_generate_url() {
        //Given
        abstractSaagieMojo.loadProperties();
        //When
        String urlJob = abstractSaagieMojo.generateURLJob(1);
        //Then
        assertThat(urlJob).isEqualTo("https://manager.prod.saagie.io/#/manager/1/job/1");
    }


    @Test
    public void should_load_credentials_not_encrypt() throws SecDispatcherException, MojoExecutionException {
        //Given
        Server server = Mockito.mock(Server.class);
        abstractSaagieMojo.loadProperties();
        ManagerProperties managerProperties = abstractSaagieMojo.managerProperties;
        when(settings.getServer(SERVER_ID)).thenReturn(server);
        when(server.getUsername()).thenReturn("your-username");
        when(server.getPassword()).thenReturn("your-password");
        //When
        abstractSaagieMojo.loadCredentials(managerProperties);
        //Then
        assertThat(managerProperties.getLogin()).isEqualTo("your-username");
        assertThat(managerProperties.getPassword()).isEqualTo("your-password");
        verifyZeroInteractions(secDispatcher);
    }

    @Test
    public void should_load_credentials_encrypt() throws SecDispatcherException, MojoExecutionException {
        //Given
        Server server = Mockito.mock(Server.class);
        abstractSaagieMojo.loadProperties();
        ManagerProperties managerProperties = abstractSaagieMojo.managerProperties;
        when(settings.getServer(SERVER_ID)).thenReturn(server);
        when(server.getUsername()).thenReturn("your-username");
        when(server.getPassword()).thenReturn("{4sNnX2vJXuoH6StIbCWwORqaF4nhRMOPffdmB9YHhuw=}");
        when(secDispatcher.decrypt(anyString())).thenReturn("your-password");
        //When
        abstractSaagieMojo.loadCredentials(managerProperties);
        //Then
        assertThat(managerProperties.getLogin()).isEqualTo("your-username");
        assertThat(managerProperties.getPassword()).isEqualTo("your-password");
    }

}