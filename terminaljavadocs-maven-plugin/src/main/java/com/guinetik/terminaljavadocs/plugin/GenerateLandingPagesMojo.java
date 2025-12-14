package com.guinetik.terminaljavadocs.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates landing pages for code coverage and source xref reports by scanning
 * reactor modules for actual generated reports.
 */
@Mojo(
    name = "generate-landing-pages",
    defaultPhase = org.apache.maven.plugins.annotations.LifecyclePhase.POST_SITE,
    aggregator = true
)
public class GenerateLandingPagesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(
        property = "terminaljavadocs.project.name",
        defaultValue = "${project.name}"
    )
    private String projectName;

    @Parameter(property = "terminaljavadocs.skip", defaultValue = "false")
    private boolean skip;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping landing page generation");
            return;
        }

        // Skip if this is not an aggregator build (only run on parent POM)
        if (!"pom".equals(project.getPackaging())) {
            getLog().debug(
                "Skipping landing page generation: not a POM project"
            );
            return;
        }

        try {
            List<ModuleReport> coverageModules = new ArrayList<>();
            List<ModuleReport> xrefModules = new ArrayList<>();

            // Scan all reactor projects for reports
            List<MavenProject> projects = session.getProjects();
            getLog().info(
                "Scanning " + projects.size() + " reactor projects for reports"
            );
            for (MavenProject reactorProject : projects) {
                String artifactId = reactorProject.getArtifactId();
                String description = reactorProject.getDescription() != null
                    ? reactorProject.getDescription()
                    : "";
                String buildDir = reactorProject.getBuild().getDirectory();

                // Check for coverage reports - try staging first, then site
                File jacocoIndex = new File(buildDir, "staging/jacoco/index.html");
                if (!jacocoIndex.exists()) {
                    jacocoIndex = new File(buildDir, "site/jacoco/index.html");
                }
                getLog().debug(
                    "Checking for coverage in " + artifactId + " at: " + jacocoIndex.getAbsolutePath()
                );
                if (jacocoIndex.exists()) {
                    coverageModules.add(
                        new ModuleReport(artifactId, description, artifactId)
                    );
                    getLog().info("Found coverage report in: " + artifactId);
                } else {
                    getLog().debug(
                        "No coverage report found for " + artifactId
                    );
                }

                // Check for xref reports - try staging first, then site
                File xrefIndex = new File(buildDir, "staging/xref/index.html");
                if (!xrefIndex.exists()) {
                    xrefIndex = new File(buildDir, "site/xref/index.html");
                }
                getLog().debug(
                    "Checking for xref in " + artifactId + " at: " + xrefIndex.getAbsolutePath()
                );
                if (xrefIndex.exists()) {
                    xrefModules.add(
                        new ModuleReport(artifactId, description, artifactId)
                    );
                    getLog().info("Found xref report in: " + artifactId);
                } else {
                    getLog().debug(
                        "No xref report found for " + artifactId
                    );
                }
            }

            // Determine the output directory - prefer staging if it exists, otherwise use site
            File outputDir = new File(buildDirectory, "staging");
            if (!outputDir.exists()) {
                outputDir = new File(buildDirectory, "site");
            }
            outputDir.mkdirs();

            // Generate coverage page if there are modules with coverage reports
            if (!coverageModules.isEmpty()) {
                String coverageHtml = generateCoveragePage(
                    coverageModules,
                    projectName
                );
                Path coveragePath = Paths.get(
                    outputDir.getAbsolutePath(),
                    "coverage.html"
                );
                Files.write(
                    coveragePath,
                    coverageHtml.getBytes(StandardCharsets.UTF_8)
                );
                getLog().info(
                    "Generated coverage landing page: " + coveragePath
                );
            }

            // Generate xref page if there are modules with xref reports
            if (!xrefModules.isEmpty()) {
                String xrefHtml = generateXrefPage(xrefModules, projectName);
                Path xrefPath = Paths.get(
                    outputDir.getAbsolutePath(),
                    "source-xref.html"
                );
                Files.write(
                    xrefPath,
                    xrefHtml.getBytes(StandardCharsets.UTF_8)
                );
                getLog().info("Generated xref landing page: " + xrefPath);
            }

            if (coverageModules.isEmpty() && xrefModules.isEmpty()) {
                getLog().info("No modules with coverage or xref reports found");
            }
        } catch (IOException e) {
            throw new MojoExecutionException(
                "Failed to generate landing pages",
                e
            );
        }
    }

    private String generateCoveragePage(
        List<ModuleReport> modules,
        String projectName
    ) throws IOException {
        String template = loadTemplate("templates/coverage-page.html");

        // Generate module rows
        StringBuilder rows = new StringBuilder();
        for (ModuleReport module : modules) {
            String row =
                "                <tr>\n" +
                "                    <td>" +
                escapeHtml(module.getArtifactId()) +
                "</td>\n" +
                "                    <td>" +
                escapeHtml(module.getDescription()) +
                "</td>\n" +
                "                    <td><a href=\"./" +
                escapeHtml(module.getArtifactId()) +
                "/jacoco/index.html\">View Coverage →</a></td>\n" +
                "                </tr>\n";
            rows.append(row);
        }

        // Replace placeholders
        String result = template
            .replace("{{project.name}}", escapeHtml(projectName))
            .replace("{{module-rows}}", rows.toString());

        return result;
    }

    private String generateXrefPage(
        List<ModuleReport> modules,
        String projectName
    ) throws IOException {
        String template = loadTemplate("templates/xref-page.html");

        // Generate module rows
        StringBuilder rows = new StringBuilder();
        for (ModuleReport module : modules) {
            String row =
                "                <tr>\n" +
                "                    <td>" +
                escapeHtml(module.getArtifactId()) +
                "</td>\n" +
                "                    <td>" +
                escapeHtml(module.getDescription()) +
                "</td>\n" +
                "                    <td><a href=\"./" +
                escapeHtml(module.getArtifactId()) +
                "/xref/index.html\">Browse Source →</a></td>\n" +
                "                </tr>\n";
            rows.append(row);
        }

        // Replace placeholders
        String result = template
            .replace("{{project.name}}", escapeHtml(projectName))
            .replace("{{module-rows}}", rows.toString());

        return result;
    }

    private String loadTemplate(String resourcePath) throws IOException {
        // Try 1: Load from classloader (for packaged plugin JAR) - most reliable
        InputStream is = null;

        // Try thread context classloader
        is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(resourcePath);

        // Try class classloader
        if (is == null) {
            is =
                GenerateLandingPagesMojo.class.getClassLoader().getResourceAsStream(
                    resourcePath
                );
        }

        // Try Class.getResourceAsStream with leading slash
        if (is == null) {
            is = GenerateLandingPagesMojo.class.getResourceAsStream(
                "/" + resourcePath
            );
        }

        if (is != null) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Try 2: Load from plugin module filesystem (for development/build time)
        // This allows the template to be loaded even if the JAR isn't fully built yet
        try {
            // Look for the template in the plugin module's source directory
            // by scanning common Maven plugin locations relative to the class file
            String classPath =
                GenerateLandingPagesMojo.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();

            // If running from target/classes, look in src/main/resources
            if (classPath.contains("target" + File.separator + "classes")) {
                Path targetClasses = Paths.get(classPath);
                Path pluginModule = targetClasses.getParent().getParent(); // Go up from target/classes to plugin module
                Path templatePath = pluginModule.resolve(
                    "src/main/resources/" + resourcePath
                );

                if (Files.exists(templatePath)) {
                    getLog().debug(
                        "Loading template from filesystem: " + templatePath
                    );
                    return new String(
                        Files.readAllBytes(templatePath),
                        StandardCharsets.UTF_8
                    );
                }
            }
        } catch (Exception e) {
            getLog().debug(
                "Could not load template from filesystem: " + e.getMessage()
            );
        }

        throw new IOException(
            "Template not found: " +
                resourcePath +
                ". Make sure templates are in src/main/resources/ or packaged in the plugin JAR."
        );
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
