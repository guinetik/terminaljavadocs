# Quick Start Guide

This guide walks you through setting up Terminal Javadocs with detailed explanations of each configuration option.

## Prerequisites

- Maven 3.6+
- Java 8+

## Step 1: Add the Parent POM

In your project's `pom.xml`, set Terminal Javadocs as your parent:

```xml
<parent>
    <groupId>com.guinetik</groupId>
    <artifactId>terminaljavadocs</artifactId>
    <version>1.0.2</version>
</parent>
```

This gives your project access to:
- Pre-configured Maven Site Plugin
- Javadoc Plugin with terminal theme
- JaCoCo Plugin for coverage reports
- All the CSS/JS resources for the dark theme

## Step 2: Create the Site Descriptor

Maven uses a file called `site.xml` to control how your documentation site looks. Create this file at `src/site/site.xml`.

### The Complete site.xml

Here's a full example with every option explained:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/DECORATION/1.8.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/DECORATION/1.8.0
        https://maven.apache.org/xsd/decoration-1.8.0.xsd"
    name="My Project">

    <!-- The skin controls the overall look and feel -->
    <skin>
        <groupId>org.apache.maven.skins</groupId>
        <artifactId>maven-fluido-skin</artifactId>
        <version>2.0.0-M8</version>
    </skin>

    <!-- Fluido Skin configuration options -->
    <custom>
        <fluidoSkin>
            <!-- Navigation bar at the top of the page -->
            <topBarEnabled>true</topBarEnabled>

            <!-- Sidebar with table of contents (disable for cleaner look) -->
            <sideBarEnabled>false</sideBarEnabled>

            <!-- Your logo in the navbar -->
            <topBarIcon>
                <name>My Project</name>
                <alt>My Project Logo</alt>
                <src>https://example.com/logo.svg</src>
                <href>/index.html</href>
            </topBarIcon>

            <!-- IMPORTANT: Use navbar-dark for the terminal theme -->
            <navBarStyle>navbar-dark</navBarStyle>

            <!-- Show line numbers in code blocks -->
            <sourceLineNumbersEnabled>true</sourceLineNumbersEnabled>

            <!-- Hide the generation date in footer -->
            <skipGenerationDate>true</skipGenerationDate>
        </fluidoSkin>
    </custom>

    <body>
        <!-- Include our theme CSS and syntax highlighting -->
        <head>
            <![CDATA[
                <!-- Prism.js for syntax highlighting -->
                <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css" rel="stylesheet" />
                <!-- Our terminal-themed Prism override -->
                <link href="./css/prism-terminal.css" rel="stylesheet" />
                <!-- Prism core -->
                <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js" defer></script>
                <!-- Java syntax support -->
                <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js" defer></script>
                <!-- Our custom JS enhancements -->
                <script src="./js/custom.js" defer></script>
            ]]>
        </head>

        <!-- Navigation menu structure -->
        <menu name="Overview">
            <item name="Introduction" href="index.html"/>
        </menu>

        <menu name="Documentation">
            <item name="Javadoc" href="apidocs/index.html"/>
        </menu>

        <!-- Automatically includes project reports -->
        <menu name="Project Information" ref="reports"/>
    </body>
</project>
```

## Understanding the Fluido Skin Options

### Layout Options

| Option | Values | Description |
|--------|--------|-------------|
| `topBarEnabled` | `true`/`false` | Shows a navigation bar at the top with your logo and menu |
| `sideBarEnabled` | `true`/`false` | Shows a sidebar with page table of contents. Disable for a cleaner look |
| `navBarStyle` | `navbar-dark`, `navbar-light` | **Must be `navbar-dark`** for Terminal Javadocs theme |

### Top Bar Icon (Logo)

```xml
<topBarIcon>
    <name>Display Name</name>      <!-- Text shown next to logo -->
    <alt>Alt text</alt>            <!-- Accessibility text -->
    <src>https://url/logo.svg</src> <!-- URL to your logo image -->
    <href>/index.html</href>       <!-- Where clicking the logo goes -->
</topBarIcon>
```

### Display Options

| Option | Values | Description |
|--------|--------|-------------|
| `sourceLineNumbersEnabled` | `true`/`false` | Show line numbers in code blocks |
| `skipGenerationDate` | `true`/`false` | Hide "Generated on..." in footer |
| `gitHub` | URL | Adds a GitHub ribbon/link to your repo |

### Adding a GitHub Ribbon

```xml
<fluidoSkin>
    <gitHub>
        <projectId>username/repo</projectId>
        <ribbonOrientation>right</ribbonOrientation>
        <ribbonColor>gray</ribbonColor>
    </gitHub>
</fluidoSkin>
```

### Twitter Follow Button

Add a Twitter follow button to your site:

```xml
<fluidoSkin>
    <twitter>
        <user>your_username</user>
        <showUser>true</showUser>
        <showFollowers>true</showFollowers>
    </twitter>
</fluidoSkin>
```

| Option | Description |
|--------|-------------|
| `user` | Your Twitter/X username (without @) |
| `showUser` | Display the username in the button |
| `showFollowers` | Show follower count |

### Google Analytics

Track your site visitors:

```xml
<fluidoSkin>
    <googleAnalytics>
        <accountId>G-XXXXXXXXXX</accountId>
        <anonymizeIp>false</anonymizeIp>
        <forceSSL>false</forceSSL>
    </googleAnalytics>
</fluidoSkin>
```

| Option | Description |
|--------|-------------|
| `accountId` | Your GA4 measurement ID (starts with G-) |
| `anonymizeIp` | Anonymize visitor IP addresses |
| `forceSSL` | Force HTTPS for analytics requests |

## Step 3: Add Content Pages

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

## Step 4: Build Your Site

```bash
mvn clean site
```

Open `target/site/index.html` in your browser to see the result.

## Using Maven Properties

You can use Maven properties in `site.xml` to avoid hardcoding values:

### In your pom.xml:

```xml
<properties>
    <terminaljavadocs.project.name>My Project</terminaljavadocs.project.name>
    <terminaljavadocs.project.logo>https://example.com/logo.svg</terminaljavadocs.project.logo>
</properties>
```

### In your site.xml:

```xml
<project name="${terminaljavadocs.project.name}">
    ...
    <topBarIcon>
        <name>${terminaljavadocs.project.name}</name>
        <src>${terminaljavadocs.project.logo}</src>
    </topBarIcon>
    ...
</project>
```

## Adding More Prism Language Support

The default setup includes Java. To highlight other languages, add more Prism components:

```xml
<head>
    <![CDATA[
        <!-- ... existing includes ... -->

        <!-- Add XML support -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-xml-doc.min.js" defer></script>

        <!-- Add Bash support -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-bash.min.js" defer></script>

        <!-- Add Python support -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-python.min.js" defer></script>
    ]]>
</head>
```

See [Prism.js components](https://prismjs.com/#supported-languages) for all available languages.

## Next Steps

- [Customization Guide](customization.html) - Override colors, add custom CSS
- [JaCoCo Theme](customization.html#jacoco) - Enable dark coverage reports
