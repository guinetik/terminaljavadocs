/**
 * TerminalJavadocs Build Script
 * Builds CSS (with PostCSS) and JS (concatenation)
 */

import { readFile, writeFile, mkdir, readdir } from 'fs/promises';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';
import postcss from 'postcss';
import postcssImport from 'postcss-import';
import postcssNested from 'postcss-nested';
import cssnano from 'cssnano';
import { minify } from 'terser';

const __dirname = dirname(fileURLToPath(import.meta.url));

const PATHS = {
  cssEntry: join(__dirname, 'src', 'main.css'),
  jsDir: join(__dirname, 'src', 'js'),
  outDir: join(__dirname, 'dist'),
};

/**
 * Build CSS
 */
async function buildCSS() {
  const css = await readFile(PATHS.cssEntry, 'utf8');

  const expanded = await postcss([
    postcssImport(),
    postcssNested(),
  ]).process(css, { from: PATHS.cssEntry });

  const minified = await postcss([
    postcssImport(),
    postcssNested(),
    cssnano({ preset: 'default' }),
  ]).process(css, { from: PATHS.cssEntry });

  await writeFile(join(PATHS.outDir, 'terminaljavadocs.css'), expanded.css);
  await writeFile(join(PATHS.outDir, 'terminaljavadocs.min.css'), minified.css);

  return { expanded: expanded.css.length, minified: minified.css.length };
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
 * Main build
 */
async function build() {
  const start = Date.now();

  try {
    await mkdir(PATHS.outDir, { recursive: true });

    const [css, js] = await Promise.all([buildCSS(), buildJS()]);

    const cssSize = (css.minified / 1024).toFixed(1);
    const jsSize = (js.minified / 1024).toFixed(1);

    console.log(`✓ Built in ${Date.now() - start}ms`);
    console.log(`  CSS: ${cssSize} KB  JS: ${jsSize} KB`);
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
