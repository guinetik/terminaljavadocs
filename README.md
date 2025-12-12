# Terminal Javadocs

[![Maven Central](https://img.shields.io/maven-central/v/com.guinetik/terminaljavadocs.svg)](https://central.sonatype.com/artifact/com.guinetik/terminaljavadocs)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Dark terminal-themed Maven site styling for Java documentation. Features a sleek black-and-green aesthetic for your Maven sites, Javadoc, and JaCoCo coverage reports.

![Terminal Javadocs Main Site](main.png)

## Features

- **Dark Terminal Theme** - Black background with terminal green accents
- **Prism.js Syntax Highlighting** - Green-on-black code blocks
- **Styled Javadoc** - Custom header with navigation back to docs
- **JaCoCo Theme** - Dark coverage reports with themed progress bars
- **Responsive Design** - Works on mobile devices
- **Maven Fluido Skin** - Built on top of the popular Fluido skin
- **Zero Config** - Just inherit and go

## Screenshots

### Javadoc

![Javadoc with Terminal Theme](javadocs.png)

### JaCoCo Coverage Reports

![JaCoCo Coverage with Terminal Theme](coverage.png)

## Quick Start

### 1. Set as Parent in Your Project

```xml
<parent>
    <groupId>com.guinetik</groupId>
    <artifactId>terminaljavadocs</artifactId>
    <version>1.0.0</version>
</parent>
```

### 2. Configure Your Project Properties

```xml
<properties>
    <terminaljavadocs.project.name>My Project</terminaljavadocs.project.name>
    <terminaljavadocs.project.logo>https://example.com/logo.svg</terminaljavadocs.project.logo>
</properties>
```

### 3. Add site.xml

Create `src/site/site.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/DECORATION/1.8.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/DECORATION/1.8.0 https://maven.apache.org/xsd/decoration-1.8.0.xsd"
    name="${terminaljavadocs.project.name}">

    <skin>
        <groupId>org.apache.maven.skins</groupId>
        <artifactId>maven-fluido-skin</artifactId>
        <version>2.0.0-M8</version>
    </skin>

    <custom>
        <fluidoSkin>
            <topBarEnabled>true</topBarEnabled>
            <sideBarEnabled>false</sideBarEnabled>
            <topBarIcon>
                <name>${terminaljavadocs.project.name}</name>
                <alt>${terminaljavadocs.project.name}</alt>
                <src>${terminaljavadocs.project.logo}</src>
                <href>/index.html</href>
            </topBarIcon>
            <navBarStyle>navbar-dark</navBarStyle>
            <sourceLineNumbersEnabled>true</sourceLineNumbersEnabled>
            <skipGenerationDate>true</skipGenerationDate>
        </fluidoSkin>
    </custom>

    <body>
        <head>
            <![CDATA[
                <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css" rel="stylesheet" />
                <link href="./css/prism-terminal.css" rel="stylesheet" />
                <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js" defer></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js" defer></script>
                <script src="./js/custom.js" defer></script>
            ]]>
        </head>

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

### 4. Build Your Site

```bash
mvn clean site
```

## JaCoCo Theme

To apply the dark theme to JaCoCo reports, activate the profile:

```bash
mvn clean site -Pterminaljavadocs-jacoco
```

Or add to your pom.xml to always activate:

```xml
<profiles>
    <profile>
        <id>terminaljavadocs-jacoco</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
</profiles>
```

## Project Structure

```
terminaljavadocs/
├── pom.xml                              # Parent POM - inherit from this
└── terminaljavadocs-resources/          # Resources JAR
    └── src/main/resources/
        ├── javadoc.css                  # Javadoc dark theme
        ├── site-resources/              # Unpacked to target/site/
        │   ├── css/
        │   │   ├── site.css             # Main site theme
        │   │   └── prism-terminal.css   # Code highlighting
        │   ├── js/
        │   │   └── custom.js            # Site enhancements
        │   └── coverage/
        │       └── index.html           # JaCoCo iframe wrapper
        └── jacoco-resources/            # JaCoCo theme
            ├── report.css               # Dark theme CSS
            ├── prettify.css             # Source highlighting
            └── *.gif                    # Coverage bar images
```

## How It Works

When your project inherits from `terminaljavadocs`:

1. **Pre-site phase**: `maven-dependency-plugin` unpacks resources from `terminaljavadocs-resources` JAR
2. **Pre-site phase**: `maven-resources-plugin` copies CSS/JS to `target/site/`
3. **Site phase**: Javadoc plugin uses the unpacked `javadoc.css`
4. **Site phase** (with profile): JaCoCo theme is copied over default styles

## Customization

Override the CSS by placing files in your project's `src/site/resources/css/` - they will be merged with the theme.

### Available Properties

| Property | Default | Description |
|----------|---------|-------------|
| `terminaljavadocs.project.name` | `${project.name}` | Display name in headers |
| `terminaljavadocs.project.logo` | (empty) | Logo URL for Javadoc header |
| `terminaljavadocs.version` | `1.0.0` | Version of resources to use |

## License

Apache License 2.0
