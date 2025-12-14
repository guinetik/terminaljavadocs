package com.guinetik.terminaljavadocs.plugin;

public class ModuleReport {
    private final String artifactId;
    private final String description;
    private final String relativePath;

    public ModuleReport(String artifactId, String description, String relativePath) {
        this.artifactId = artifactId;
        this.description = description;
        this.relativePath = relativePath;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getDescription() {
        return description;
    }

    public String getRelativePath() {
        return relativePath;
    }
}
