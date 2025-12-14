package com.guinetik.terminaljavadocs.plugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleReportTest {

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
