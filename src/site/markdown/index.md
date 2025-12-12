# Terminal Javadocs

Dark terminal-themed Maven site styling for Java documentation.

## Features

- **Dark Terminal Theme** - Black background with terminal green accents
- **Prism.js Syntax Highlighting** - Green-on-black code blocks
- **Styled Javadoc** - Custom header with navigation back to docs
- **JaCoCo Theme** - Dark coverage reports with themed progress bars
- **Responsive Design** - Works on mobile devices
- **Maven Fluido Skin** - Built on top of the popular Fluido skin
- **Zero Config** - Just inherit and go

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
<parent>
    <groupId>com.guinetik</groupId>
    <artifactId>terminaljavadocs</artifactId>
    <version>1.0.0</version>
</parent>
```

See the [Quick Start](quickstart.html) guide for full setup instructions.
