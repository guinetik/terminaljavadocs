/**
 * TerminalJavadocs Build Script
 * Builds CSS (with PostCSS) and JS (concatenation)
 */

import { readFile, writeFile, mkdir, readdir, cp } from 'fs/promises';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';
import postcss from 'postcss';
import postcssImport from 'postcss-import';
import postcssNested from 'postcss-nested';
import cssnano from 'cssnano';
import { minify } from 'terser';

const __dirname = dirname(fileURLToPath(import.meta.url));

const PATHS = {
  srcDir: join(__dirname, 'src'),
  jsDir: join(__dirname, 'src', 'js'),
  outDir: join(__dirname, 'dist'),
  stagingDir: join(__dirname, '..', 'target', 'staging'),
  pluginResourcesDir: join(__dirname, '..', 'terminaljavadocs-maven-plugin', 'src', 'main', 'resources', 'styles'),
};

/**
 * CSS Entry Points Configuration
 * - entry: source file name in src/
 * - output: base name for output file (will add .css or .min.css)
 * - expanded: whether to output expanded (non-minified) version
 * - plugin: whether to copy to maven plugin resources (for InjectSiteStylesMojo)
 *
 * Plugin output names must match InjectSiteStylesMojo.PageType enum:
 *   COVERAGE -> terminaljavadocs-coverage.min.css
 *   JXR      -> terminaljavadocs-jxr.min.css
 *   JAVADOC  -> terminaljavadocs-javadoc.min.css
 *   SITE     -> terminaljavadocs-site.min.css
 */
const CSS_ENTRIES = [
  { entry: 'main.css', output: 'terminaljavadocs', expanded: true, plugin: false },
  { entry: 'page.css', output: 'terminaljavadocs-site', expanded: false, plugin: true },
  { entry: 'landings.css', output: 'terminaljavadocs-landings', expanded: false, plugin: false },
  { entry: 'javadocs.css', output: 'terminaljavadocs-javadoc', expanded: false, plugin: true },
  { entry: 'jacoco.css', output: 'terminaljavadocs-coverage', expanded: false, plugin: true },
  { entry: 'jxr.css', output: 'terminaljavadocs-jxr', expanded: false, plugin: true },
];

/**
 * Build a single CSS entry point
 */
async function buildCSSEntry({ entry, output, expanded, plugin }) {
  const entryPath = join(PATHS.srcDir, entry);
  const css = await readFile(entryPath, 'utf8');

  const results = { entry, output, sizes: {}, plugin };

  // Build expanded version if requested
  if (expanded) {
    const expandedResult = await postcss([
      postcssImport(),
      postcssNested(),
    ]).process(css, { from: entryPath });

    await writeFile(join(PATHS.outDir, `${output}.css`), expandedResult.css);
    results.sizes.expanded = expandedResult.css.length;
  }

  // Always build minified version
  const minifiedResult = await postcss([
    postcssImport(),
    postcssNested(),
    cssnano({ preset: 'default' }),
  ]).process(css, { from: entryPath });

  await writeFile(join(PATHS.outDir, `${output}.min.css`), minifiedResult.css);
  results.sizes.minified = minifiedResult.css.length;

  // Copy to plugin resources if flagged
  if (plugin) {
    await writeFile(join(PATHS.pluginResourcesDir, `${output}.min.css`), minifiedResult.css);
  }

  return results;
}

/**
 * Build all CSS entry points
 */
async function buildCSS() {
  return await Promise.all(CSS_ENTRIES.map(buildCSSEntry));
}

/**
 * Build inject.js separately (standalone utility)
 */
async function buildInject() {
  const injectPath = join(PATHS.jsDir, 'inject.js');
  const content = await readFile(injectPath, 'utf8');

  const minified = await minify(content, { compress: true, mangle: true });

  await writeFile(join(PATHS.outDir, 'inject.min.js'), minified.code);

  return { expanded: content.length, minified: minified.code.length };
}

/**
 * Build main JS bundle (concatenation + terser minification)
 * Excludes inject.js since it's built separately
 */
async function buildJS() {
  const files = await readdir(PATHS.jsDir);
  const jsFiles = files
    .filter(f => f.endsWith('.js') && f !== 'inject.js')
    .sort();

  let combined = '/* TerminalJavadocs */\n';

  for (const file of jsFiles) {
    const content = await readFile(join(PATHS.jsDir, file), 'utf8');
    combined += `\n/* === ${file} === */\n${content}\n`;
  }

  // Minify with terser
  const minified = await minify(combined, { compress: true, mangle: true });

  await writeFile(join(PATHS.outDir, 'terminaljavadocs.js'), combined);
  await writeFile(join(PATHS.outDir, 'terminaljavadocs.min.js'), minified.code);

  return { expanded: combined.length, minified: minified.code.length };
}

/**
 * Copy dist/ contents to staging directory
 */
async function copyToStaging() {
  await mkdir(PATHS.stagingDir, { recursive: true });
  await cp(PATHS.outDir, PATHS.stagingDir, { recursive: true });
}

/**
 * Main build
 */
async function build() {
  const start = Date.now();

  try {
    // Create output directories
    await mkdir(PATHS.outDir, { recursive: true });
    await mkdir(PATHS.pluginResourcesDir, { recursive: true });

    const [cssResults, js, inject] = await Promise.all([
      buildCSS(),
      buildJS(),
      buildInject(),
    ]);

    // Copy JS to plugin resources (for InjectSiteStylesMojo)
    await writeFile(
      join(PATHS.pluginResourcesDir, 'terminaljavadocs.min.js'),
      await readFile(join(PATHS.outDir, 'terminaljavadocs.min.js'))
    );

    // Copy to staging
    await copyToStaging();

    const elapsed = Date.now() - start;

    // Count plugin resources
    const pluginCssCount = cssResults.filter(r => r.plugin).length;

    console.log(`\n✓ Built in ${elapsed}ms\n`);
    console.log('CSS Outputs (dist/):');
    console.log('─'.repeat(50));

    for (const result of cssResults) {
      const minSize = (result.sizes.minified / 1024).toFixed(1);
      const pluginMarker = result.plugin ? ' → plugin' : '';
      if (result.sizes.expanded) {
        const expSize = (result.sizes.expanded / 1024).toFixed(1);
        console.log(`  ${result.output}.css`.padEnd(38) + `${expSize} KB`);
        console.log(`  ${result.output}.min.css`.padEnd(38) + `${minSize} KB${pluginMarker}`);
      } else {
        console.log(`  ${result.output}.min.css`.padEnd(38) + `${minSize} KB${pluginMarker}`);
      }
    }

    console.log('\nJS Outputs (dist/):');
    console.log('─'.repeat(50));
    const jsSize = (js.minified / 1024).toFixed(1);
    const injectSize = (inject.minified / 1024).toFixed(1);
    console.log(`  terminaljavadocs.min.js`.padEnd(38) + `${jsSize} KB → plugin`);
    console.log(`  inject.min.js`.padEnd(38) + `${injectSize} KB`);

    console.log('\n✓ Copied to ../target/staging/');
    console.log(`✓ Copied ${pluginCssCount} CSS + 1 JS to maven plugin resources\n`);

  } catch (err) {
    console.error('✗ Build failed:', err.message);
    process.exit(1);
  }
}

// Watch mode
if (process.argv.includes('--watch')) {
  const { watch } = await import('fs');
  console.log('Watching src/ for changes...\n');
  build();
  watch(join(__dirname, 'src'), { recursive: true }, () => build());
} else {
  build();
}
