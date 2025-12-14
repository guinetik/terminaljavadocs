# TerminalJavadocs Design System

A CSS Zen Garden approach to styling Maven-generated documentation. Drop-in CSS/JS that transforms the default Apache Fluido theme into a terminal-inspired dark theme.

## Philosophy

The Maven site generator (with Fluido skin) produces consistent HTML structure across all pages - javadocs, coverage reports, source xref, etc. Instead of forking the skin or modifying templates, we overlay our styles on top of the existing markup. Pure CSS transformation, zero HTML changes.

## Quick Start

```bash
cd css-zen-garden
npm install
npm run build    # Builds and copies to ../target/staging/
npm run dev      # Serves staging on localhost:8800
```

## Injection

Use the injection utility to load the appropriate CSS bundle for your page type:

```javascript
// One-liner injection
const js = document.createElement('script');
js.src = '/inject.min.js';
js.onload = () => injectJavadocs();  // or injectPage(), injectJacoco(), etc.
document.body.appendChild(js);
```

### Available Injection Functions

| Function | CSS Bundle | Use For |
|----------|------------|---------|
| `injectPage()` | terminaljavadocs-page.min.css | Doxia markdown pages |
| `injectLandings()` | terminaljavadocs-landings.min.css | Custom landing pages |
| `injectJavadocs()` | terminaljavadocs-javadocs.min.css | Javadoc API documentation |
| `injectJacoco()` | terminaljavadocs-jacoco.min.css | JaCoCo coverage reports |
| `injectJxr()` | terminaljavadocs-jxr.min.css | JXR source cross-reference |
| `injectAll()` | terminaljavadocs.min.css | Full bundle (all pages) |

All functions inject the page-specific CSS plus the shared `terminaljavadocs.min.js`.

### Namespaced API

Functions are also available on `window.TerminalJavadocs`:

```javascript
TerminalJavadocs.injectJavadocs();
TerminalJavadocs.basePath;      // Auto-detected base path
TerminalJavadocs.entryPoints;   // Map of entry point names to CSS files
```

## Architecture

```
src/
├── _core.css             # Shared foundation (imported by all entry points)
├── main.css              # Full bundle entry point
├── page.css              # Doxia pages entry point
├── landings.css          # Landing pages entry point
├── javadocs.css          # Javadoc entry point
├── jacoco.css            # JaCoCo entry point
├── jxr.css               # JXR entry point
├── tokens/               # Design tokens (CSS custom properties)
│   ├── colors.css        # Palette: phosphor green, pure blacks
│   ├── typography.css    # Fonts: Fira Code, Space Grotesk
│   └── spacing.css       # Spacing scale, layout vars
├── base/                 # Foundation styles
│   ├── reset.css         # Box-sizing, antialiasing
│   └── elements.css      # Typography, links, lists
├── components/           # UI components
│   ├── navbar.css        # Fixed top nav, mobile hamburger
│   ├── sidebar.css       # Left column navigation
│   ├── tables.css        # Data tables
│   └── code.css          # Code blocks + Prism terminal theme
├── pages/                # Page-specific overrides
│   ├── site.css          # Core Doxia/Maven site layout (shared)
│   ├── landing.css       # Custom landing pages
│   ├── javadoc.css       # Javadoc API documentation
│   ├── coverage.css      # JaCoCo coverage reports
│   └── xref.css          # JXR source cross-reference
├── utilities/            # Utility classes
│   ├── effects.css       # Glow, scanlines, animations
│   └── print.css         # Print styles
└── js/                   # JavaScript
    ├── inject.js         # Injection utility (built separately)
    ├── main.js           # Prism.js loader + syntax highlighting
    ├── mobile-nav.js     # Hamburger menu + project title injection
    └── terminaljavadocs.js
```

### Entry Point Composition

Each entry point imports `_core.css` (tokens + base + components + site.css + utilities) plus its specific page styles:

| Entry Point | Includes |
|-------------|----------|
| `page.css` | _core.css |
| `landings.css` | _core.css + landing.css |
| `javadocs.css` | _core.css + javadoc.css |
| `jacoco.css` | _core.css + coverage.css |
| `jxr.css` | _core.css + javadoc.css + xref.css |
| `main.css` | _core.css + all page-specific CSS |

## Build System

- **PostCSS** with `postcss-import` (resolves @imports) and `postcss-nested` (nesting support)
- **cssnano** for CSS minification
- **Terser** for JS minification

### Build Outputs

```
dist/ (also copied to ../target/staging/)
├── terminaljavadocs.css          # Full bundle (expanded)
├── terminaljavadocs.min.css      # Full bundle (minified)
├── terminaljavadocs-page.min.css
├── terminaljavadocs-landings.min.css
├── terminaljavadocs-javadocs.min.css
├── terminaljavadocs-jacoco.min.css
├── terminaljavadocs-jxr.min.css
├── terminaljavadocs.js           # Main JS (expanded)
├── terminaljavadocs.min.js       # Main JS (minified)
└── inject.min.js                 # Injection utility
```

## Design Tokens

### Colors
```css
--bg-void: #000000;        /* Pure black */
--bg-base: #0a0a0a;        /* Near black */
--bg-raised: #111111;      /* Elevated surfaces */
--accent: #00ff41;         /* Phosphor green */
--accent-glow: rgba(0, 255, 65, 0.15);
--text-primary: #fafafa;
--text-secondary: #a1a1a1;
```

### Typography
```css
--font-display: 'Space Grotesk', sans-serif;  /* Headings */
--font-body: 'Fira Code', monospace;          /* Body text */
--font-mono: 'Fira Code', monospace;          /* Code */
```

### Spacing
```css
--sp-1: 4px;   --sp-2: 8px;   --sp-3: 12px;
--sp-4: 16px;  --sp-5: 24px;  --sp-6: 32px;
```

## JavaScript Features

### Injection Utility
- Auto-detects base path from script location
- Prevents duplicate injection
- Supports switching between page types

### Prism Syntax Highlighting
- Loads Prism.js core + language grammars from CDN
- Auto-detects language from code content (Java, XML, Bash, JSON)
- Custom terminal theme defined in CSS (not CDN theme)

### Mobile Navigation
- Hamburger menu toggle for screens <= 992px
- Disables Bootstrap's dropdown handling, implements custom touch-friendly dropdowns
- Injects project title from logo alt text, centers on mobile

## Responsive Breakpoints

- **Desktop**: > 992px - Full nav, sidebar visible > 1400px
- **Tablet**: <= 992px - Hamburger menu, hidden sidebar
- **Mobile**: <= 768px - Reduced padding, smaller fonts

## What Gets Styled

- Maven Fluido site pages (index, quickstart, etc.)
- Javadoc API documentation
- JaCoCo coverage reports
- JXR source cross-reference
- Custom landing pages

## Reference

The `design/` folder contains the original monolithic CSS files that this modular system was ported from. Use as reference for edge cases.
