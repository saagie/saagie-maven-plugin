package io.saagie.maven;

/**
 * Created by pleresteux on 14/09/16.
 */
public class ManagerProperties {

    private String urlApi;
    private String login;
    private String password;
    private String realm;
    private String platformId;
    private String jobName;
    private String jobId;
    private String jobType;
    private String jobCategory;
    private String jarName;
    private String languageVersion;
    private String cpu;
    private String mem;
    private String disk;
    private String arguments;
    private String releaseNote;
    private String description;

    public ManagerProperties() {
    }

    public String getUrlApi() {
        return urlApi;
    }

    public ManagerProperties setUrlApi(String urlApi) {
        this.urlApi = urlApi;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public ManagerProperties setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ManagerProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRealm() {
        return realm;
    }

    public ManagerProperties setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public String getPlatformId() {
        return platformId;
    }

    public ManagerProperties setPlatformId(String platformId) {
        this.platformId = platformId;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public ManagerProperties setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public ManagerProperties setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public ManagerProperties setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public ManagerProperties setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
        return this;
    }

    public String getJarName() {
        return jarName;
    }

    public ManagerProperties setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public ManagerProperties setLanguageVersion(String languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    public String getCpu() {
        return cpu;
    }

    public ManagerProperties setCpu(String cpu) {
        this.cpu = cpu;
        return this;
    }

    public String getMem() {
        return mem;
    }

    public ManagerProperties setMem(String mem) {
        this.mem = mem;
        return this;
    }

    public String getDisk() {
        return disk;
    }

    public ManagerProperties setDisk(String disk) {
        this.disk = disk;
        return this;
    }

    public String getArguments() {
        return arguments;
    }

    public ManagerProperties setArguments(String arguments) {
        this.arguments = arguments;
        return this;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public ManagerProperties setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ManagerProperties setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "ManagerProperties{" +
                "urlApi='" + urlApi + '\'' +
                ", login='" + login + '\'' +
                ", realm='" + realm + '\'' +
                ", platformId='" + platformId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobId='" + jobId + '\'' +
                ", jobType='" + jobType + '\'' +
                ", jobCategory='" + jobCategory + '\'' +
                ", jarName='" + jarName + '\'' +
                ", languageVersion='" + languageVersion + '\'' +
                ", cpu='" + cpu + '\'' +
                ", mem='" + mem + '\'' +
                ", disk='" + disk + '\'' +
                ", arguments='" + arguments + '\'' +
                ", releaseNote='" + releaseNote + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
