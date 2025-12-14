/**
 * TerminalJavadocs Injection Utility
 *
 * Provides window-level functions to inject the appropriate CSS and JS
 * for different page types.
 *
 * Usage:
 *   <script src="/path/to/inject.min.js"></script>
 *   <script>injectJavadocs();</script>
 */
(function() {
  'use strict';

  // Auto-detect base path from this script's location
  var currentScript = document.currentScript || (function() {
    var scripts = document.getElementsByTagName('script');
    return scripts[scripts.length - 1];
  })();

  var basePath = currentScript.src.substring(0, currentScript.src.lastIndexOf('/'));

  // Track what's been injected to avoid duplicates
  var injected = { css: null, js: false };

  /**
   * Inject a CSS file
   */
  function injectCSS(file) {
    if (injected.css === file) return;

    // Remove previously injected CSS if different
    if (injected.css) {
      var existing = document.querySelector('link[data-terminaljavadocs]');
      if (existing) existing.remove();
    }

    var link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = basePath + '/' + file;
    link.setAttribute('data-terminaljavadocs', 'true');
    document.head.appendChild(link);
    injected.css = file;
  }

  /**
   * Inject the shared JS file
   */
  function injectJS() {
    if (injected.js) return;

    var script = document.createElement('script');
    script.src = basePath + '/terminaljavadocs.min.js';
    script.setAttribute('data-terminaljavadocs', 'true');
    document.body.appendChild(script);
    injected.js = true;
  }

  /**
   * Main injection function
   */
  function inject(cssFile) {
    injectCSS(cssFile);
    injectJS();
  }

  // Entry point mapping
  var entryPoints = {
    page:     'terminaljavadocs-page.min.css',
    landings: 'terminaljavadocs-landings.min.css',
    javadocs: 'terminaljavadocs-javadocs.min.css',
    jacoco:   'terminaljavadocs-jacoco.min.css',
    jxr:      'terminaljavadocs-jxr.min.css',
    all:      'terminaljavadocs.min.css'
  };

  // Expose on window
  window.injectPage     = function() { inject(entryPoints.page); };
  window.injectLandings = function() { inject(entryPoints.landings); };
  window.injectJavadocs = function() { inject(entryPoints.javadocs); };
  window.injectJacoco   = function() { inject(entryPoints.jacoco); };
  window.injectJxr      = function() { inject(entryPoints.jxr); };
  window.injectAll      = function() { inject(entryPoints.all); };

  // Also expose as namespaced object for those who prefer it
  window.TerminalJavadocs = {
    inject: inject,
    injectPage: window.injectPage,
    injectLandings: window.injectLandings,
    injectJavadocs: window.injectJavadocs,
    injectJacoco: window.injectJacoco,
    injectJxr: window.injectJxr,
    injectAll: window.injectAll,
    basePath: basePath,
    entryPoints: entryPoints
  };

})();
