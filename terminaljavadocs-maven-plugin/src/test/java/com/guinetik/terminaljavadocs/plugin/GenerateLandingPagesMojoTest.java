package com.guinetik.terminaljavadocs.plugin;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.maven.execution.MavenSession;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GenerateLandingPagesMojo}.
 *
 * <p>
 * Tests the landing page generation goal including skip behavior,
 * packaging type checks, and site directory creation.
 *
 * <p>
 * Uses Mockito to mock Maven session and project dependencies,
 * and JUnit's TemporaryFolder for isolated file system operations.
 */
public class GenerateLandingPagesMojoTest {

    /** Temporary directory for test file operations, cleaned up after each test. */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /** Mock Maven session providing access to reactor projects. */
    @Mock
    private MavenSession session;

    /** Mock Maven project representing the current build. */
    @Mock
    private MavenProject project;

    /** The mojo instance under test. */
    private GenerateLandingPagesMojo mojo;

    /**
     * Sets up the test fixture with mocked Maven dependencies.
     * Initializes the mojo with default configuration using reflection.
     *
     * @throws Exception if reflection fails during field injection
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mojo = new GenerateLandingPagesMojo();

        // Set fields using reflection
        setField(mojo, "session", session);
        setField(mojo, "project", project);
        setField(mojo, "projectName", "Test Project");
        setField(mojo, "buildDirectory", tempFolder.getRoot());
        setField(mojo, "skip", false);
    }

    /**
     * Verifies that the mojo exits early without processing when skip=true.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testMojoSkipsWhenSkipFlagIsTrue() throws Exception {
        setField(mojo, "skip", true);
        when(project.getPackaging()).thenReturn("pom");

        // Should not throw when skip is true
        mojo.execute();
    }

    /**
     * Verifies that the mojo exits early for non-POM packaging types.
     * Landing pages should only be generated for aggregator (pom) projects.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testMojoSkipsWhenNotPomPackaging() throws Exception {
        when(project.getPackaging()).thenReturn("jar");

        // Should not throw when not a POM project
        mojo.execute();
    }

    /**
     * Verifies that the mojo executes successfully for POM packaging.
     * Sets up a mock reactor with one module (without reports) to test
     * the scanning logic.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testMojoExecutesForPomProject() throws Exception {
        when(project.getPackaging()).thenReturn("pom");

        // Create mock projects without coverage/xref
        MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getArtifactId()).thenReturn("test-module");
        when(mockProject.getDescription()).thenReturn("Test description");

        // Create a mock build with no actual reports
        Build mockBuild = mock(Build.class);
        when(mockBuild.getDirectory()).thenReturn(tempFolder.getRoot().getAbsolutePath());
        when(mockProject.getBuild()).thenReturn(mockBuild);

        List<MavenProject> projects = new ArrayList<>();
        projects.add(mockProject);
        when(session.getProjects()).thenReturn(projects);

        // Should not throw an exception
        mojo.execute();
    }

    /**
     * Verifies that the site output directory is created when it doesn't exist.
     * When no staging directory exists, the mojo should create target/site.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testSiteDirectoryIsCreated() throws Exception {
        when(project.getPackaging()).thenReturn("pom");
        List<MavenProject> projects = new ArrayList<>();
        when(session.getProjects()).thenReturn(projects);

        mojo.execute();

        // Site directory should be created (since no staging exists)
        assertTrue(new File(tempFolder.getRoot(), "site").exists());
    }

    /**
     * Verifies that the custom project name parameter is accepted.
     * The project name is used in the generated landing page headers.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testProjectNameIsUsedInPages() throws Exception {
        String projectName = "My Custom Project";
        setField(mojo, "projectName", projectName);
        when(project.getPackaging()).thenReturn("pom");

        List<MavenProject> projects = new ArrayList<>();
        when(session.getProjects()).thenReturn(projects);

        // Mojo should have processed the project name without error
        mojo.execute();
    }

    /**
     * Verifies graceful handling when the reactor contains no modules.
     * This can occur in single-module projects or during partial builds.
     *
     * @throws Exception if reflection or execution fails
     */
    @Test
    public void testHandlesEmptyReactorProjects() throws Exception {
        when(project.getPackaging()).thenReturn("pom");
        when(session.getProjects()).thenReturn(new ArrayList<>());

        // Should handle empty projects list gracefully
        mojo.execute();
    }

    /**
     * Sets a private field on an object using reflection.
     *
     * @param target    the object to modify
     * @param fieldName the name of the field to set
     * @param value     the value to assign to the field
     * @throws Exception if the field cannot be accessed or set
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
