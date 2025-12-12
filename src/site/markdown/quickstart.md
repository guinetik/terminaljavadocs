# Quick Start

Get up and running with Terminal Javadocs in minutes.

## 1. Set as Parent

```xml
<parent>
    <groupId>com.guinetik</groupId>
    <artifactId>terminaljavadocs</artifactId>
    <version>1.0.0</version>
</parent>
```

## 2. Configure Properties

```xml
<properties>
    <terminaljavadocs.project.name>My Project</terminaljavadocs.project.name>
    <terminaljavadocs.project.logo>https://example.com/logo.svg</terminaljavadocs.project.logo>
</properties>
```

## 3. Create site.xml

Create `src/site/site.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/DECORATION/1.8.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/DECORATION/1.8.0
        https://maven.apache.org/xsd/decoration-1.8.0.xsd"
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

## 4. Build Your Site

```bash
mvn clean site
```

## JaCoCo Dark Theme

To apply the dark theme to JaCoCo coverage reports:

```bash
mvn clean site -Pterminaljavadocs-jacoco
```

Or enable it by default in your pom.xml:

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
