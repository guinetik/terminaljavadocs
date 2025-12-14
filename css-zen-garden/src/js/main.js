/**
 * TerminalJavadocs - Main JS Entry
 * Loads Prism.js and initializes syntax highlighting
 * Theme is in our CSS - we only load Prism core + language grammars
 */
(function() {
  'use strict';

  // Prism CDN - core + languages only (theme is in our CSS)
  var PRISM_CORE = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js';
  var PRISM_LANGS = [
    'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-markup.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-bash.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-json.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-properties.min.js'
  ];

  /**
   * Load a script and return a promise
   */
  function loadScript(src) {
    return new Promise(function(resolve, reject) {
      var script = document.createElement('script');
      script.src = src;
      script.onload = resolve;
      script.onerror = reject;
      document.head.appendChild(script);
    });
  }

  /**
   * Detect language from code content
   */
  function detectLanguage(content, className) {
    if (className) {
      if (className.indexOf('java') !== -1) return 'java';
      if (className.indexOf('xml') !== -1) return 'xml';
      if (className.indexOf('bash') !== -1 || className.indexOf('shell') !== -1) return 'bash';
      if (className.indexOf('json') !== -1) return 'json';
    }

    var trimmed = content.trim();
    if (trimmed.match(/^<\?xml/) || trimmed.match(/^<(dependency|plugin|project|groupId|artifactId)/)) return 'xml';
    if (trimmed.match(/^(package|import|public\s+class|public\s+interface|@\w+)/m)) return 'java';
    if (trimmed.match(/^(\$|#!\/bin\/(ba)?sh|mvn |npm |git )/m)) return 'bash';
    if (trimmed.match(/^\s*[\[{]/) && trimmed.match(/[\]}]\s*$/)) return 'json';

    return null;
  }

  /**
   * Prepare code blocks with language classes
   */
  function prepareCodeBlocks() {
    var codeBlocks = document.querySelectorAll('pre code, pre.source');

    codeBlocks.forEach(function(code) {
      var pre = code.tagName === 'PRE' ? code : code.parentElement;
      var content = code.textContent || '';
      var hasLanguageClass = code.className && code.className.match(/language-/);

      // Auto-detect language if not specified
      if (!hasLanguageClass) {
        var language = detectLanguage(content, code.className);
        if (language) {
          code.classList.add('language-' + language);
          pre.classList.add('language-' + language);
        }
      }

      // Sync language class to pre element
      var match = code.className && code.className.match(/language-(\w+)/);
      if (match) {
        var lang = match[1];
        if (!pre.classList.contains('language-' + lang)) {
          pre.classList.add('language-' + lang);
        }
      }
    });
  }

  /**
   * Initialize Prism highlighting
   */
  function highlight() {
    if (typeof Prism !== 'undefined') {
      Prism.highlightAll();
      console.log('✓ Prism syntax highlighting applied');
    }
  }

  /**
   * Main init
   */
  function init() {
    prepareCodeBlocks();

    // Load Prism core, then languages, then highlight
    loadScript(PRISM_CORE)
      .then(function() {
        return Promise.all(PRISM_LANGS.map(loadScript));
      })
      .then(highlight)
      .catch(function(err) {
        console.warn('Prism loading failed:', err);
      });
  }

  // Run on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
