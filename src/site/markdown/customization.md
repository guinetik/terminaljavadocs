# Customization Guide

Learn how to customize Terminal Javadocs to match your project's branding.

## Overriding CSS

You can override any of our CSS by creating files in your project's `src/site/resources/css/` directory. These files are merged with the theme.

### Creating Custom Styles

Create `src/site/resources/css/custom.css`:

```css
/* Change the terminal green to a different accent color */
:root {
    --terminal-green: #00ff88;
    --terminal-green-dim: #00aa55;
}

/* Custom link colors */
a {
    color: var(--terminal-green);
}

/* Custom heading styles */
h1, h2, h3 {
    border-bottom: 1px solid var(--terminal-green-dim);
}
```

### Including Your Custom CSS

Add it to your `site.xml` head section:

```xml
<head>
    <![CDATA[
        <link href="./css/custom.css" rel="stylesheet" />
    ]]>
</head>
```

## Theme Variables

Terminal Javadocs uses CSS custom properties that you can override:

| Variable | Default | Description |
|----------|---------|-------------|
| `--terminal-green` | `#00ff00` | Primary accent color |
| `--terminal-green-dim` | `#00aa00` | Secondary/muted green |
| `--terminal-bg` | `#0a0a0a` | Background color |
| `--terminal-text` | `#e0e0e0` | Main text color |

## Customizing the Navbar

### Change Navbar Background

```css
.navbar {
    background-color: #1a1a2e !important;
}
```

### Custom Logo Styling

```css
.navbar-brand img {
    border-radius: 4px;
    margin-right: 8px;
}
```

## Report Theming

The plugin automatically detects and themes all report types:

| Report Type | Detection | Theme Applied |
|-------------|-----------|---------------|
| Javadoc | `apidocs/` path | Dark Javadoc CSS + header |
| JaCoCo | `jacoco/` path | Dark coverage tables and bars |
| JXR | `xref/` path | Dark source browser |
| Maven Site | Fluido skin markers | Dark site theme |

No configuration required - the plugin injects appropriate CSS/JS for each page type.

## Customizing Javadoc Header

The Javadoc pages include a custom header with your logo. Configure it with Maven properties:

### Using Maven Properties

Set these in your `pom.xml`:

```xml
<properties>
    <terminaljavadocs.project.name>My Project</terminaljavadocs.project.name>
    <terminaljavadocs.project.logo>https://example.com/logo.svg</terminaljavadocs.project.logo>
</properties>
```

### Completely Custom Javadoc Header

Override the Javadoc plugin configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <configuration>
                <header>
                    <![CDATA[
                        <div class="my-custom-header">
                            <a href="/">Back to Docs</a>
                        </div>
                    ]]>
                </header>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Adding Syntax Highlighting Languages

The plugin includes Prism.js with common languages. For additional languages, add them to your `site.xml`:

### Available Prism Languages

```xml
<!-- Common languages -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-python.min.js" defer></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-sql.min.js" defer></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-yaml.min.js" defer></script>
```

### Using the Autoloader

Instead of manually adding each language, use Prism's autoloader:

```xml
<head>
    <![CDATA[
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/plugins/autoloader/prism-autoloader.min.js" defer></script>
    ]]>
</head>
```

The autoloader automatically loads language definitions when needed.

## Dynamic Landing Pages

For multi-module projects, the plugin can generate landing pages that list all modules with reports.

### How It Works

The `generate-landing-pages` goal scans all modules in your reactor build and:

1. Checks each module for actual generated reports:
   - JaCoCo coverage: `target/staging/*/jacoco/index.html`
   - JXR source xref: `target/staging/*/xref/overview-summary.html`
2. Generates landing pages listing only modules with actual reports
3. Creates proper links from the aggregated site to module reports

### Generated Pages

**`coverage.html`**
- Lists all modules with JaCoCo coverage reports
- Shows module names and descriptions
- Links directly to each module's coverage report

**`source-xref.html`**
- Lists all modules with JXR source cross-reference
- Shows module names and descriptions
- Links directly to each module's source browser

### Generating Landing Pages

Landing pages require direct goal invocation (they scan the entire reactor):

```bash
mvn clean install site site:stage \
    com.guinetik:terminaljavadocs-maven-plugin:generate-landing-pages \
    com.guinetik:terminaljavadocs-maven-plugin:inject-styles
```

### Why Direct Invocation?

The `generate-landing-pages` goal is an aggregator mojo that needs to run once at the reactor root level, not per-module. This is a Maven limitation - aggregator mojos cannot be bound to lifecycle phases via configuration.

### Why Only Actual Reports?

The plugin scans for actual generated files instead of relying on configuration. This means:

- If a module generates coverage reports, it appears in `coverage.html`
- If a module has no tests, it won't appear (no empty reports)
- If you remove a module, it automatically disappears
- No broken links or 404 errors

### Customizing Landing Pages

The landing pages use semantic CSS class names for styling:

```html
<header class="terminal-header">
  <a class="terminal-brand">Brand</a>
  <nav class="terminal-nav">
    <span class="terminal-badge">Badge</span>
  </nav>
</header>

<main class="module-list">
  <table>
    <!-- Module rows -->
  </table>
</main>
```

You can override the styling in your custom CSS:

```css
.terminal-badge {
    background: var(--terminal-green);
    color: #000;
}
```

## Deploying to GitHub Pages

To deploy your Maven site to GitHub Pages, you need to configure `site:stage` and a GitHub Actions workflow.

### Required: Distribution Management Site Element

If you try to run `mvn site:stage` without this, you'll get:

```
Missing site information in the distribution management
```

Add this to your `pom.xml`:

```xml
<distributionManagement>
    <site>
        <id>github-pages</id>
        <url>https://YOUR_USERNAME.github.io/YOUR_REPO/</url>
    </site>
</distributionManagement>
```

The `<site>` element tells Maven where the staged site will be deployed, which it uses to calculate relative paths between modules.

### GitHub Actions Workflow

Create `.github/workflows/publish-site.yml`:

```yaml
name: Publish Maven Site to GitHub Pages

on:
  push:
    branches:
      - master
  workflow_dispatch:

permissions:
  contents: write
  pages: write
  id-token: write

concurrency:
  group: "pages"
  cancel-in-progress: false

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build project and Generate Maven Site
        run: mvn clean install site site:stage -DskipTests

      - name: Generate Landing Pages and Apply Theme
        run: |
          mvn com.guinetik:terminaljavadocs-maven-plugin:generate-landing-pages \
              com.guinetik:terminaljavadocs-maven-plugin:inject-styles

      - name: Deploy to GitHub Pages branch
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./target/staging
          publish_branch: gh-pages
          force_orphan: true
          user_name: 'github-actions[bot]'
          user_email: 'github-actions[bot]@users.noreply.github.com'
          commit_message: 'Deploy Maven Site'
```

### Enable GitHub Pages

In your repo settings:
1. Go to **Settings** > **Pages**
2. Set **Source** to "Deploy from a branch"
3. Select the `gh-pages` branch

## Multi-Module Projects

For multi-module Maven projects, each module can have its own `site.xml`, or you can share one.

### Shared site.xml

Place a common `site.xml` in your parent project. Child modules will inherit it.

### Module-Specific Overrides

Create `src/site/site.xml` in specific modules to override the parent's configuration.

### Site Staging

Use `site:stage` to aggregate all module sites:

```bash
mvn clean site site:stage
```

The aggregated site will be in `target/staging/`.

## Troubleshooting

### CSS Not Loading

1. Make sure you ran `mvn clean site` (not just `mvn site`)
2. Check that Terminal Javadocs resources are in `target/site/terminal-styles/`
3. Verify the plugin is configured with `<phase>site</phase>`

### Javadoc Not Themed

1. Ensure the plugin runs after Javadoc generation (it does by default in `site` phase)
2. Check `target/site/terminal-styles/` contains `javadoc.css`
3. Inspect the HTML - look for `<link>` tags pointing to terminal-styles

### JaCoCo Not Themed

1. Make sure JaCoCo reports are generated first (`mvn test` or `mvn verify`)
2. The plugin detects JaCoCo pages by the `/jacoco/` path
3. Check `target/site/jacoco/` exists before running `mvn site`
