# Terminal Javadocs

Dark terminal-themed Maven site styling for Java documentation.

## Features

- **Dark Terminal Theme** - Black background with terminal green accents
- **Prism.js Syntax Highlighting** - Green-on-black code blocks
- **Styled Javadoc** - Custom header with navigation back to docs
- **JaCoCo Theme** - Dark coverage reports with themed progress bars
- **JXR Theme** - Dark source cross-reference browser
- **Dynamic Landing Pages** - Automatically generates `coverage.html` and `source-xref.html` listing only modules with actual reports
- **Responsive Design** - Works on mobile devices
- **Auto-Detection** - Detects and styles Javadoc, JaCoCo, JXR, and site pages automatically
- **Zero Config** - Just add the plugin and go

## Example Code

Here's what syntax highlighting looks like:

```java
public class HelloWorld {
    /**
     * The main entry point.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello, Terminal!");
    }
}
```

## Installation

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

Then run:

```bash
mvn clean site
```

That's it! Your themed site will be at `target/site/`.

See the [Quick Start](quickstart.html) guide for full setup instructions.
