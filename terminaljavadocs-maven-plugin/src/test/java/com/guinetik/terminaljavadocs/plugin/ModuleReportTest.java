package com.guinetik.terminaljavadocs.plugin;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ModuleReport}.
 *
 * <p>
 * Validates the immutable DTO behavior including construction,
 * null handling, and getter methods.
 */
public class ModuleReportTest {

    /**
     * Verifies that all constructor arguments are correctly stored and
     * returned by their respective getter methods.
     */
    @Test
    public void testModuleReportConstruction() {
        String artifactId = "my-module";
        String description = "Module description";
        String relativePath = "my-module";

        ModuleReport report = new ModuleReport(artifactId, description, relativePath);

        assertEquals(artifactId, report.getArtifactId());
        assertEquals(description, report.getDescription());
        assertEquals(relativePath, report.getRelativePath());
    }

    /**
     * Verifies that a null description is preserved (not converted to empty string).
     * This matches the behavior expected when a module has no description in its POM.
     */
    @Test
    public void testModuleReportWithNullDescription() {
        String artifactId = "my-module";
        String description = null;
        String relativePath = "my-module";

        ModuleReport report = new ModuleReport(artifactId, description, relativePath);

        assertEquals(artifactId, report.getArtifactId());
        assertNull(report.getDescription());
        assertEquals(relativePath, report.getRelativePath());
    }

    /**
     * Verifies that an empty string description is preserved as-is.
     * Distinguishes between "no description" (null) and "empty description" ("").
     */
    @Test
    public void testModuleReportWithEmptyDescription() {
        String artifactId = "my-module";
        String description = "";
        String relativePath = "my-module";

        ModuleReport report = new ModuleReport(artifactId, description, relativePath);

        assertEquals(artifactId, report.getArtifactId());
        assertEquals("", report.getDescription());
        assertEquals(relativePath, report.getRelativePath());
    }

    /**
     * Verifies that two ModuleReport instances with identical values
     * return the same values from their getters.
     *
     * <p>
     * Note: ModuleReport does not override equals/hashCode, so this test
     * validates value equality through getters, not object equality.
     */
    @Test
    public void testModuleReportEquality() {
        ModuleReport report1 = new ModuleReport("module1", "desc1", "path1");
        ModuleReport report2 = new ModuleReport("module1", "desc1", "path1");

        // Test that getters return same values
        assertEquals(report1.getArtifactId(), report2.getArtifactId());
        assertEquals(report1.getDescription(), report2.getDescription());
        assertEquals(report1.getRelativePath(), report2.getRelativePath());
    }
}
