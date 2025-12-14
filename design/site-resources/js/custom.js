/**
 * TerminalJavadocs - Mobile Navigation & Syntax Highlighting
 */
(function() {
  document.addEventListener('DOMContentLoaded', function() {
    // Initialize Prism syntax highlighting
    initSyntaxHighlighting();

    // Initialize mobile navigation
    var hamburger = document.querySelector('#topbar .btn-navbar');
    var nav = document.querySelector('#topbar nav.nav-collapse ul.nav');
    var container = document.querySelector('#topbar .navbar-inner > .container');

    if (!hamburger || !nav) return;

    // Inject project title into brand element (shown on desktop, centered on mobile via CSS)
    var brandLink = document.querySelector('#topbar .brand');
    var brandImg = document.querySelector('#topbar .brand img');
    if (brandLink && brandImg) {
      var titleText = brandImg.alt || document.title.split('–')[0].trim();

      var projectTitle = document.createElement('span');
      projectTitle.id = 'project-title';
      projectTitle.textContent = titleText;
      projectTitle.className = 'project-title';
      brandLink.appendChild(projectTitle);
    }

    var isOpen = false;

    function isMobile() {
      return window.innerWidth <= 992;
    }

    function closeMenu() {
      isOpen = false;
      nav.classList.remove('open');
      nav.querySelectorAll('.dropdown.open').forEach(function(dd) {
        dd.classList.remove('open');
      });
    }

    // Toggle menu on hamburger click
    hamburger.addEventListener('click', function(e) {
      e.preventDefault();
      e.stopPropagation();

      if (!isMobile()) return;

      isOpen = !isOpen;
      if (isOpen) {
        nav.classList.add('open');
      } else {
        closeMenu();
      }
    });

    // Close menu when clicking outside - use mousedown to avoid racing with link clicks
    document.addEventListener('mousedown', function(e) {
      if (isOpen && !nav.contains(e.target) && !hamburger.contains(e.target)) {
        closeMenu();
      }
    });

    // Handle nav link clicks - let them navigate naturally
    nav.querySelectorAll('a:not(.dropdown-toggle)').forEach(function(link) {
      link.addEventListener('click', function(e) {
        // For same-page anchors, close menu after brief delay
        var href = this.getAttribute('href');
        if (href && href.indexOf('#') === 0) {
          setTimeout(closeMenu, 100);
        }
      });
    });

    // Handle dropdown toggles on mobile
    nav.querySelectorAll('.dropdown-toggle').forEach(function(toggle) {
      toggle.addEventListener('click', function(e) {
        if (!isMobile()) return;

        e.preventDefault();
        e.stopPropagation();

        var parent = this.parentElement;
        var wasOpen = parent.classList.contains('open');

        // Close all other dropdowns
        nav.querySelectorAll('.dropdown.open').forEach(function(dd) {
          dd.classList.remove('open');
        });

        // Toggle this one
        if (!wasOpen) {
          parent.classList.add('open');
        }
      });
    });

    // Close nav when window resizes to desktop
    window.addEventListener('resize', function() {
      if (window.innerWidth > 992) {
        closeMenu();
      }
    });
  });

  /**
   * Initialize Prism.js syntax highlighting
   */
  function initSyntaxHighlighting() {
    var codeBlocks = document.querySelectorAll('pre code, pre.source');

    codeBlocks.forEach(function(code) {
      var pre = code.tagName === 'PRE' ? code : code.parentElement;
      var content = code.textContent || '';
      var hasLanguageClass = code.className && code.className.match(/language-/);

      if (!hasLanguageClass) {
        var language = detectLanguage(content, code.className);
        if (language) {
          code.classList.add('language-' + language);
          pre.classList.add('language-' + language);
        }
      }

      if (code.className && code.className.match(/language-(\w+)/)) {
        var lang = code.className.match(/language-(\w+)/)[1];
        if (!pre.classList.contains('language-' + lang)) {
          pre.classList.add('language-' + lang);
        }
      }
    });

    // Highlight when Prism is ready
    if (typeof Prism !== 'undefined') {
      Prism.highlightAll();
    } else {
      var attempts = 0;
      var interval = setInterval(function() {
        if (typeof Prism !== 'undefined') {
          Prism.highlightAll();
          clearInterval(interval);
        } else if (++attempts > 50) {
          clearInterval(interval);
        }
      }, 100);
    }
  }

  /**
   * Detect programming language from code content
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
    return 'java';
  }
})();
