package com.guinetik.terminaljavadocs.plugin;

/**
 * Data transfer object representing a Maven module's report metadata.
 *
 * <p>
 * Used by {@link GenerateLandingPagesMojo} to collect information about modules
 * that have generated reports (coverage, xref) and render them into landing pages.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * ModuleReport report = new ModuleReport(
 *     "my-module",
 *     "Core utilities library",
 *     "my-module"
 * );
 * // Use in template: report.getArtifactId() -> "my-module"
 * }</pre>
 *
 * @see GenerateLandingPagesMojo
 */
public class ModuleReport {

    private final String artifactId;
    private final String description;
    private final String relativePath;

    /**
     * Creates a new module report with the specified metadata.
     *
     * @param artifactId   the Maven artifact ID of the module (e.g., "my-module")
     * @param description  the module's description from pom.xml, may be {@code null}
     * @param relativePath the relative path from the landing page to this module's reports
     */
    public ModuleReport(String artifactId, String description, String relativePath) {
        this.artifactId = artifactId;
        this.description = description;
        this.relativePath = relativePath;
    }

    /**
     * Returns the Maven artifact ID of this module.
     *
     * @return the artifact ID, never {@code null}
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Returns the module's description from its pom.xml.
     *
     * @return the description, or {@code null} if not specified in the POM
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the relative path from the landing page to this module's report directory.
     *
     * <p>
     * This path is used to construct links in the generated landing pages,
     * e.g., {@code ./my-module/jacoco/index.html}.
     *
     * @return the relative path to the module's reports
     */
    public String getRelativePath() {
        return relativePath;
    }
}
