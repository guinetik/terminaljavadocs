# Terminal Javadocs

[![Maven Central](https://img.shields.io/maven-central/v/com.guinetik/terminaljavadocs.svg)](https://central.sonatype.com/artifact/com.guinetik/terminaljavadocs)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Dark terminal-themed Maven site styling for Java documentation. Features a sleek black-and-green aesthetic for your Maven sites, Javadoc, JXR source cross-reference, and JaCoCo coverage reports.

![Terminal Javadocs Main Site](main.png)

## Features

- **Dark Terminal Theme** - Black background with terminal green accents
- **Auto-Detection** - Automatically detects and styles Javadoc, JaCoCo, JXR, and site pages
- **Prism.js Syntax Highlighting** - Green-on-black code blocks
- **Styled Javadoc** - Custom header with navigation back to docs
- **JaCoCo Theme** - Dark coverage reports with themed progress bars
- **JXR Theme** - Dark source cross-reference browser
- **Responsive Design** - Works on mobile devices
- **Zero Config** - Just add the plugin and go

## Screenshots

### Javadoc

![Javadoc with Terminal Theme](javadocs.png)

### JaCoCo Coverage Reports

![JaCoCo Coverage with Terminal Theme](coverage.png)

## Quick Start

### 1. Add the Plugin

Add this to your `pom.xml`:

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

### 2. Build Your Site

```bash
mvn clean site
```

That's it! Your themed site will be generated at `target/site/`.

The plugin automatically:
- Detects page types (Javadoc, JaCoCo, JXR, site pages)
- Injects the appropriate CSS for each page type
- Adds syntax highlighting and custom scripts

## Multi-Module Projects

For multi-module projects, add the plugin to each module, or to the parent with inheritance.

### Landing Pages

To generate landing pages (`coverage.html`, `source-xref.html`) that list all modules with reports:

```bash
mvn clean install site site:stage \
    com.guinetik:terminaljavadocs-maven-plugin:generate-landing-pages \
    com.guinetik:terminaljavadocs-maven-plugin:inject-styles
```

## Documentation

- **[Quick Start Guide](https://terminaljavadocs.guinetik.com/quickstart.html)** - Detailed setup instructions
- **[Customization](https://terminaljavadocs.guinetik.com/customization.html)** - Advanced theming and overrides

## How It Works

The `terminaljavadocs-maven-plugin`:

1. **Site phase**: Scans generated HTML files in `target/site/`
2. **Auto-detects**: Identifies page types by path and content (Javadoc, JaCoCo, JXR, site)
3. **Injects styles**: Adds `<link>` and `<script>` tags for the appropriate theme CSS/JS
4. **Copies resources**: Places theme assets in `target/site/terminal-styles/`

No parent POM required. No manual CSS configuration. Just add the plugin.

## License

Apache License 2.0
