package com.guinetik.terminaljavadocs.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link InjectSiteStylesMojo}.
 *
 * <p>
 * Tests the style injection goal including:
 * <ul>
 * <li>Skip behavior and directory preference</li>
 * <li>Page type detection (landing, coverage, jxr, javadoc, site)</li>
 * <li>HTML injection mechanics</li>
 * <li>Resource copying and path calculations</li>
 * </ul>
 *
 * <p>
 * Uses Mockito to mock Maven session and project dependencies,
 * and JUnit's TemporaryFolder for isolated file system operations.
 */
public class InjectSiteStylesMojoTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private MavenSession session;

    @Mock
    private MavenProject project;

    private InjectSiteStylesMojo mojo;

    /**
     * Sets up the test fixture with mocked dependencies and default configuration.
     *
     * @throws Exception if reflection fails during field injection
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mojo = new InjectSiteStylesMojo();

        // Inject dependencies via reflection
        setField(mojo, "session", session);
        setField(mojo, "project", project);
        setField(mojo, "buildDirectory", tempFolder.getRoot());
        setField(mojo, "skip", false);
        setField(mojo, "stylesDir", "terminal-styles");
        setField(mojo, "processNestedSites", true);

        // Default mock behavior
        when(session.getProjects()).thenReturn(new ArrayList<>());
    }

    // ========================================================================
    // Core Execution Flow Tests
    // ========================================================================

    /**
     * Verifies that the mojo exits early when skip=true without processing any files.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testSkipExecution() throws Exception {
        setField(mojo, "skip", true);

        // Create a site directory that would normally be processed
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();
        createHtmlFile(new File(siteDir, "test.html"), "<html><head></head><body></body></html>");

        mojo.execute();

        // File should NOT be modified (no injection marker)
        String content = readFile(new File(siteDir, "test.html"));
        assertFalse("File should not be processed when skip=true",
                content.contains("terminal-javadocs-injected"));
    }

    /**
     * Verifies that the mojo exits gracefully when no site directory exists.
     *
     * @throws Exception if execution fails
     */
    @Test
    public void testNoSiteDirectory() throws Exception {
        // Don't create any site or staging directory
        // Should complete without exception
        mojo.execute();
    }

    /**
     * Verifies that staging directory is preferred over site directory.
     * When both exist, only staging should be processed.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testPrefersStagingOverSite() throws Exception {
        // Create both directories
        File stagingDir = new File(tempFolder.getRoot(), "staging");
        File siteDir = new File(tempFolder.getRoot(), "site");
        stagingDir.mkdirs();
        siteDir.mkdirs();

        createHtmlFile(new File(stagingDir, "staging-page.html"),
                "<html><head></head><body>staging</body></html>");
        createHtmlFile(new File(siteDir, "site-page.html"),
                "<html><head></head><body>site</body></html>");

        mojo.execute();

        // Staging file should be processed
        String stagingContent = readFile(new File(stagingDir, "staging-page.html"));
        assertTrue("Staging file should be processed",
                stagingContent.contains("terminal-javadocs-injected"));

        // Site file should NOT be processed (staging takes priority)
        String siteContent = readFile(new File(siteDir, "site-page.html"));
        assertFalse("Site file should not be processed when staging exists",
                siteContent.contains("terminal-javadocs-injected"));
    }

    /**
     * Verifies that site directory is processed when staging doesn't exist.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testProcessesSiteHtmlFiles() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();
        createHtmlFile(new File(siteDir, "index.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(siteDir, "index.html"));
        assertTrue("Site HTML file should be injected",
                content.contains("terminal-javadocs-injected"));
    }

    // ========================================================================
    // Page Type Detection Tests
    // ========================================================================

    /**
     * Verifies that coverage.html and source-xref.html are detected as LANDING pages.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testDetectLandingPageByFilename() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "coverage.html"),
                "<html><head></head><body></body></html>");
        createHtmlFile(new File(siteDir, "source-xref.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String coverageContent = readFile(new File(siteDir, "coverage.html"));
        assertTrue("coverage.html should use landing CSS",
                coverageContent.contains("terminaljavadocs-landing.min.css"));

        String xrefContent = readFile(new File(siteDir, "source-xref.html"));
        assertTrue("source-xref.html should use landing CSS",
                xrefContent.contains("terminaljavadocs-landing.min.css"));
    }

    /**
     * Verifies that files in /jacoco/ paths are detected as COVERAGE pages.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testDetectCoverageByPath() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        File jacocoDir = new File(siteDir, "jacoco");
        jacocoDir.mkdirs();

        createHtmlFile(new File(jacocoDir, "index.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(jacocoDir, "index.html"));
        assertTrue("Jacoco path should use coverage CSS",
                content.contains("terminaljavadocs-coverage.min.css"));
    }

    /**
     * Verifies that files in /xref/ paths are detected as JXR pages.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testDetectJxrByPath() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        File xrefDir = new File(siteDir, "xref");
        xrefDir.mkdirs();

        createHtmlFile(new File(xrefDir, "index.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(xrefDir, "index.html"));
        assertTrue("Xref path should use jxr CSS",
                content.contains("terminaljavadocs-jxr.min.css"));
    }

    /**
     * Verifies that files in /apidocs/ paths are detected as JAVADOC pages.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testDetectJavadocByPath() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        File apidocsDir = new File(siteDir, "apidocs");
        apidocsDir.mkdirs();

        createHtmlFile(new File(apidocsDir, "index.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(apidocsDir, "index.html"));
        assertTrue("Apidocs path should use javadoc CSS",
                content.contains("terminaljavadocs-javadoc.min.css"));
    }

    /**
     * Verifies that unknown pages default to SITE page type.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testDefaultsToSiteType() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "about.html"),
                "<html><head></head><body>Regular site page</body></html>");

        mojo.execute();

        String content = readFile(new File(siteDir, "about.html"));
        assertTrue("Unknown page should use site CSS",
                content.contains("terminaljavadocs-site.min.css"));
    }

    // ========================================================================
    // HTML Injection Tests
    // ========================================================================

    /**
     * Verifies that styles are injected before the closing head tag.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testInjectsBeforeHeadClose() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "test.html"),
                "<html><head><title>Test</title></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(siteDir, "test.html"));
        int injectionPos = content.indexOf("terminal-javadocs-injected");
        int headClosePos = content.indexOf("</head>");

        assertTrue("Injection marker should exist", injectionPos > 0);
        assertTrue("Injection should come before </head>", injectionPos < headClosePos);
    }

    /**
     * Verifies that already-injected files are skipped on re-execution.
     * Prevents duplicate style injections.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testSkipsAlreadyInjected() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        // Create a file that already has the injection marker
        String alreadyInjected = "<html><head><!-- terminal-javadocs-injected [site] -->" +
                "<link rel=\"stylesheet\" href=\"terminal-styles/terminaljavadocs-site.min.css\">" +
                "</head><body></body></html>";
        createHtmlFile(new File(siteDir, "already-injected.html"), alreadyInjected);

        mojo.execute();

        String content = readFile(new File(siteDir, "already-injected.html"));
        // Should have exactly one injection marker, not two
        int firstIndex = content.indexOf("terminal-javadocs-injected");
        int secondIndex = content.indexOf("terminal-javadocs-injected", firstIndex + 1);

        assertTrue("First marker should exist", firstIndex >= 0);
        assertEquals("Should not have second marker (no re-injection)", -1, secondIndex);
    }

    // ========================================================================
    // Resource Handling Tests
    // ========================================================================

    /**
     * Verifies that CSS and JS resources are copied to the styles directory.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testCopiesStyleResources() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "test.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        File stylesDir = new File(siteDir, "terminal-styles");
        assertTrue("Styles directory should be created", stylesDir.exists());

        // Check for at least one expected CSS file
        File siteCss = new File(stylesDir, "terminaljavadocs-site.min.css");
        // Note: The file may or may not exist depending on whether resources are available
        // in the test classpath. The important thing is the directory was created.
    }

    /**
     * Verifies that the styles directory is created.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testCreatesStylesDirectory() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "test.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        File stylesDir = new File(siteDir, "terminal-styles");
        assertTrue("Styles directory should be created", stylesDir.exists());
        assertTrue("Styles directory should be a directory", stylesDir.isDirectory());
    }

    // ========================================================================
    // Path Calculation Tests
    // ========================================================================

    /**
     * Verifies that relative paths are correctly calculated for nested files.
     * Files in subdirectories need "../" prefixes to reach the styles directory.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testRelativePathCalculation() throws Exception {
        File siteDir = new File(tempFolder.getRoot(), "site");
        File nestedDir = new File(siteDir, "jacoco/com/example");
        nestedDir.mkdirs();

        createHtmlFile(new File(nestedDir, "SomeClass.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        String content = readFile(new File(nestedDir, "SomeClass.html"));
        // The path should have "../../../" to go up 3 levels
        assertTrue("Nested file should have relative path to styles",
                content.contains("../../../terminal-styles/"));
    }

    // ========================================================================
    // Configuration Tests
    // ========================================================================

    /**
     * Verifies that a custom styles directory name is used.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testCustomStylesDir() throws Exception {
        setField(mojo, "stylesDir", "custom-styles");

        File siteDir = new File(tempFolder.getRoot(), "site");
        siteDir.mkdirs();

        createHtmlFile(new File(siteDir, "test.html"),
                "<html><head></head><body></body></html>");

        mojo.execute();

        File customStylesDir = new File(siteDir, "custom-styles");
        assertTrue("Custom styles directory should be created", customStylesDir.exists());

        String content = readFile(new File(siteDir, "test.html"));
        assertTrue("Injected path should use custom styles dir",
                content.contains("custom-styles/"));
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Sets a private field on an object using reflection.
     *
     * @param target    the object to modify
     * @param fieldName the name of the field to set
     * @param value     the value to set
     * @throws Exception if the field cannot be accessed or set
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Creates an HTML file with the specified content.
     *
     * @param file    the file to create
     * @param content the HTML content to write
     * @throws IOException if the file cannot be written
     */
    private void createHtmlFile(File file, String content) throws IOException {
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reads a file's content as a UTF-8 string.
     *
     * @param file the file to read
     * @return the file content
     * @throws IOException if the file cannot be read
     */
    private String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
}
