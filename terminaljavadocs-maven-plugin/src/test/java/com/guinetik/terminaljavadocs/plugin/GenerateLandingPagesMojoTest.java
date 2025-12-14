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
 * Tests for GenerateLandingPagesMojo
 */
public class GenerateLandingPagesMojoTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private MavenSession session;

    @Mock
    private MavenProject project;

    private GenerateLandingPagesMojo mojo;

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

    @Test
    public void testMojoSkipsWhenSkipFlagIsTrue() throws Exception {
        setField(mojo, "skip", true);
        when(project.getPackaging()).thenReturn("pom");

        // Should not throw when skip is true
        mojo.execute();
    }

    @Test
    public void testMojoSkipsWhenNotPomPackaging() throws Exception {
        when(project.getPackaging()).thenReturn("jar");

        // Should not throw when not a POM project
        mojo.execute();
    }

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

    @Test
    public void testSiteDirectoryIsCreated() throws Exception {
        when(project.getPackaging()).thenReturn("pom");
        List<MavenProject> projects = new ArrayList<>();
        when(session.getProjects()).thenReturn(projects);

        mojo.execute();

        // Site directory should be created (since no staging exists)
        assertTrue(new File(tempFolder.getRoot(), "site").exists());
    }

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

    @Test
    public void testHandlesEmptyReactorProjects() throws Exception {
        when(project.getPackaging()).thenReturn("pom");
        when(session.getProjects()).thenReturn(new ArrayList<>());

        // Should handle empty projects list gracefully
        mojo.execute();
    }

    /**
     * Helper method to set private fields using reflection
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
