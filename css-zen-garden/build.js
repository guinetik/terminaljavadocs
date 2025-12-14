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
};

/**
 * CSS Entry Points Configuration
 * - entry: source file name in src/
 * - output: base name for output file (will add .css or .min.css)
 * - expanded: whether to output expanded (non-minified) version
 */
const CSS_ENTRIES = [
  { entry: 'main.css', output: 'terminaljavadocs', expanded: true },
  { entry: 'page.css', output: 'terminaljavadocs-page', expanded: false },
  { entry: 'landings.css', output: 'terminaljavadocs-landings', expanded: false },
  { entry: 'javadocs.css', output: 'terminaljavadocs-javadocs', expanded: false },
  { entry: 'jacoco.css', output: 'terminaljavadocs-jacoco', expanded: false },
  { entry: 'jxr.css', output: 'terminaljavadocs-jxr', expanded: false },
];

/**
 * Build a single CSS entry point
 */
async function buildCSSEntry({ entry, output, expanded }) {
  const entryPath = join(PATHS.srcDir, entry);
  const css = await readFile(entryPath, 'utf8');

  const results = { entry, output, sizes: {} };

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
    await mkdir(PATHS.outDir, { recursive: true });

    const [cssResults, js, inject] = await Promise.all([
      buildCSS(),
      buildJS(),
      buildInject(),
    ]);

    // Copy to staging
    await copyToStaging();

    const elapsed = Date.now() - start;

    console.log(`\n✓ Built in ${elapsed}ms\n`);
    console.log('CSS Outputs:');
    console.log('─'.repeat(50));

    for (const result of cssResults) {
      const minSize = (result.sizes.minified / 1024).toFixed(1);
      if (result.sizes.expanded) {
        const expSize = (result.sizes.expanded / 1024).toFixed(1);
        console.log(`  ${result.output}.css`.padEnd(38) + `${expSize} KB`);
        console.log(`  ${result.output}.min.css`.padEnd(38) + `${minSize} KB`);
      } else {
        console.log(`  ${result.output}.min.css`.padEnd(38) + `${minSize} KB`);
      }
    }

    console.log('\nJS Outputs:');
    console.log('─'.repeat(50));
    const jsSize = (js.minified / 1024).toFixed(1);
    const injectSize = (inject.minified / 1024).toFixed(1);
    console.log(`  terminaljavadocs.min.js`.padEnd(38) + `${jsSize} KB`);
    console.log(`  inject.min.js`.padEnd(38) + `${injectSize} KB`);

    console.log('\n✓ Copied to ../target/staging/\n');

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
