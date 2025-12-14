/**
 * TerminalJavadocs Build Script
 * Builds CSS (with PostCSS) and JS for multiple page types
 */

import { readFile, writeFile, mkdir, readdir, copyFile } from 'fs/promises';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';
import postcss from 'postcss';
import postcssImport from 'postcss-import';
import postcssNested from 'postcss-nested';
import cssnano from 'cssnano';
import { minify } from 'terser';

const __dirname = dirname(fileURLToPath(import.meta.url));

/**
 * Entry points for each page type
 */
const ENTRY_POINTS = {
  site: 'entry-site.css',
  javadoc: 'entry-javadoc.css',
  coverage: 'entry-coverage.css',
  jxr: 'entry-jxr.css',
};

const PATHS = {
  srcDir: join(__dirname, 'src'),
  jsDir: join(__dirname, 'src', 'js'),
  outDir: join(__dirname, 'dist'),
  // Maven plugin resources directory for bundled styles
  pluginResources: join(__dirname, '..', 'terminaljavadocs-maven-plugin', 'src', 'main', 'resources', 'styles'),
};

/**
 * Build CSS for a specific entry point
 */
async function buildCSSEntry(name, entryFile) {
  const entryPath = join(PATHS.srcDir, entryFile);
  const css = await readFile(entryPath, 'utf8');

  const expanded = await postcss([
    postcssImport(),
    postcssNested(),
  ]).process(css, { from: entryPath });

  const minified = await postcss([
    postcssImport(),
    postcssNested(),
    cssnano({ preset: 'default' }),
  ]).process(css, { from: entryPath });

  const baseName = `terminaljavadocs-${name}`;
  await writeFile(join(PATHS.outDir, `${baseName}.css`), expanded.css);
  await writeFile(join(PATHS.outDir, `${baseName}.min.css`), minified.css);

  return { name, expanded: expanded.css.length, minified: minified.css.length };
}

/**
 * Build all CSS entry points
 */
async function buildAllCSS() {
  const results = [];
  for (const [name, entryFile] of Object.entries(ENTRY_POINTS)) {
    const result = await buildCSSEntry(name, entryFile);
    results.push(result);
  }
  return results;
}

/**
 * Build JS (concatenation + terser minification)
 */
async function buildJS() {
  const files = await readdir(PATHS.jsDir);
  const jsFiles = files.filter(f => f.endsWith('.js')).sort();

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
 * Copy built files to Maven plugin resources
 */
async function copyToPlugin() {
  await mkdir(PATHS.pluginResources, { recursive: true });

  // Copy each CSS entry point
  for (const name of Object.keys(ENTRY_POINTS)) {
    const baseName = `terminaljavadocs-${name}`;
    await copyFile(
      join(PATHS.outDir, `${baseName}.min.css`),
      join(PATHS.pluginResources, `${baseName}.min.css`)
    );
  }

  // Copy JS
  await copyFile(
    join(PATHS.outDir, 'terminaljavadocs.min.js'),
    join(PATHS.pluginResources, 'terminaljavadocs.min.js')
  );

  console.log('  → Copied to Maven plugin resources');
}

/**
 * Main build
 */
async function build() {
  const start = Date.now();

  try {
    await mkdir(PATHS.outDir, { recursive: true });

    const [cssResults, js] = await Promise.all([buildAllCSS(), buildJS()]);

    console.log(`✓ Built in ${Date.now() - start}ms`);

    // Log CSS sizes
    for (const css of cssResults) {
      const size = (css.minified / 1024).toFixed(1);
      console.log(`  ${css.name}: ${size} KB`);
    }

    const jsSize = (js.minified / 1024).toFixed(1);
    console.log(`  JS: ${jsSize} KB`);

    // Copy to Maven plugin resources
    await copyToPlugin();
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
