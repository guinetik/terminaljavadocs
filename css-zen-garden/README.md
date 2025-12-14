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

Add to your browser console or inject via site customization:

```javascript
// CSS
const css = document.createElement('link');
css.rel = 'stylesheet';
css.href = 'terminaljavadocs.min.css';
document.head.appendChild(css);

// JS (Prism syntax highlighting + mobile nav)
const js = document.createElement('script');
js.src = 'terminaljavadocs.min.js';
document.body.appendChild(js);
```

## Architecture

```
src/
├── main.css              # Entry point - imports everything
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
│   ├── site.css          # Main site pages
│   └── coverage.css      # JaCoCo coverage reports
├── utilities/            # Utility classes
│   ├── effects.css       # Glow, scanlines, animations
│   └── print.css         # Print styles
└── js/                   # JavaScript
    ├── main.js           # Prism.js loader + syntax highlighting
    └── mobile-nav.js     # Hamburger menu + project title injection
```

## Build System

- **PostCSS** with `postcss-import` (resolves @imports) and `postcss-nested` (nesting support)
- **cssnano** for CSS minification
- **Terser** for JS minification

Outputs:
- `dist/terminaljavadocs.css` - Expanded CSS
- `dist/terminaljavadocs.min.css` - Minified CSS
- `dist/terminaljavadocs.js` - Expanded JS
- `dist/terminaljavadocs.min.js` - Minified JS

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
- Test documentation

## Reference

The `design/` folder contains the original monolithic CSS files that this modular system was ported from. Use as reference for edge cases.
