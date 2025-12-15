# Quick Start Guide

This guide walks you through setting up Terminal Javadocs with detailed explanations.

## Prerequisites

- Maven 3.6+
- Java 8+

## Step 1: Add the Plugin

Add the Terminal Javadocs plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.guinetik</groupId>
            <artifactId>terminaljavadocs-maven-plugin</artifactId>
            <version>1.0.44</version>
            <executions>
                <execution>
                    <id>inject-styles</id>
                    <phase>site</phase>
                    <goals>
                        <goal>inject-styles</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

That's all you need! The plugin will automatically:
- Detect page types (Javadoc, JaCoCo, JXR, Maven site pages)
- Inject the appropriate dark theme CSS
- Add syntax highlighting scripts

## Step 2: Build Your Site

```bash
mvn clean site
```

Open `target/site/index.html` in your browser to see the result.

## Step 3 (Optional): Configure site.xml

For additional control over your site's navigation and branding, create `src/site/site.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/DECORATION/1.8.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/DECORATION/1.8.0
        https://maven.apache.org/xsd/decoration-1.8.0.xsd"
    name="My Project">

    <skin>
        <groupId>org.apache.maven.skins</groupId>
        <artifactId>maven-fluido-skin</artifactId>
        <version>2.0.0-M8</version>
    </skin>

    <custom>
        <fluidoSkin>
            <topBarEnabled>true</topBarEnabled>
            <sideBarEnabled>false</sideBarEnabled>
            <!-- IMPORTANT: Use navbar-dark for best results -->
            <navBarStyle>navbar-dark</navBarStyle>
        </fluidoSkin>
    </custom>

    <body>
        <menu name="Overview">
            <item name="Introduction" href="index.html"/>
        </menu>

        <menu name="Documentation">
            <item name="Javadoc" href="apidocs/index.html"/>
        </menu>

        <menu name="Project Information" ref="reports"/>
    </body>
</project>
```

## Understanding Fluido Skin Options

### Layout Options

| Option | Values | Description |
|--------|--------|-------------|
| `topBarEnabled` | `true`/`false` | Shows a navigation bar at the top |
| `sideBarEnabled` | `true`/`false` | Shows a sidebar with page table of contents |
| `navBarStyle` | `navbar-dark`, `navbar-light` | Use `navbar-dark` for Terminal theme |

### Top Bar Icon (Logo)

```xml
<topBarIcon>
    <name>Display Name</name>
    <alt>Alt text</alt>
    <src>https://url/logo.svg</src>
    <href>/index.html</href>
</topBarIcon>
```

### GitHub Ribbon

```xml
<fluidoSkin>
    <gitHub>
        <projectId>username/repo</projectId>
        <ribbonOrientation>right</ribbonOrientation>
        <ribbonColor>gray</ribbonColor>
    </gitHub>
</fluidoSkin>
```

## Multi-Module Projects

For multi-module Maven projects, add the plugin to each module that generates a site, or add it to the parent pom to inherit.

### Example Parent POM

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.guinetik</groupId>
            <artifactId>terminaljavadocs-maven-plugin</artifactId>
            <version>1.0.44</version>
            <executions>
                <execution>
                    <id>inject-styles</id>
                    <phase>site</phase>
                    <goals>
                        <goal>inject-styles</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Generating Landing Pages

For multi-module projects, you can generate landing pages that list all modules with coverage and source cross-reference reports:

```bash
mvn clean install site site:stage \
    com.guinetik:terminaljavadocs-maven-plugin:generate-landing-pages \
    com.guinetik:terminaljavadocs-maven-plugin:inject-styles
```

This creates:
- **`coverage.html`** - Lists all modules with JaCoCo coverage reports
- **`source-xref.html`** - Lists all modules with JXR source cross-reference

These pages are placed in `target/staging/` and automatically include only modules that actually have reports.

### Why site:stage?

- Creates an aggregated site with all module reports accessible
- Generates proper relative paths between modules
- Required for proper multi-module site structure

## Adding Content Pages

Create markdown files in `src/site/markdown/`:

```
src/site/
├── site.xml
└── markdown/
    ├── index.md        # Your home page
    └── guide.md        # Additional pages
```

### Example index.md

```markdown
# Welcome to My Project

This is the home page of your documentation.

## Code Example

Here's some Java code with syntax highlighting:

\`\`\`java
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello!");
    }
}
\`\`\`
```

## Enabling Reports

### JaCoCo Coverage Reports

Add JaCoCo to your build:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

And add it to your reporting section:

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
        </plugin>
    </plugins>
</reporting>
```

### JXR Source Cross-Reference

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jxr-plugin</artifactId>
            <version>3.3.2</version>
        </plugin>
    </plugins>
</reporting>
```

## Next Steps

- [Customization Guide](customization.html) - Override colors, add custom CSS
