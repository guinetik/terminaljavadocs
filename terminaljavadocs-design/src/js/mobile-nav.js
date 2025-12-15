/**
 * Navigation Handler
 * - Injects minimal header on pages without #topbar (JaCoCo, Javadoc, etc.)
 * - Handles mobile hamburger menu on pages with full navbar
 * - Injects project title into brand
 *
 * Tokens replaced at build time by InjectSiteStylesMojo:
 *   %%PROJECT_LOGO%% - URL to project logo (from terminaljavadocs.project.logo)
 *   %%PROJECT_NAME%% - Project name (from terminaljavadocs.project.name)
 */
(function() {
  'use strict';

  // Project branding (tokens replaced at build time by InjectSiteStylesMojo)
  // Using array access pattern to prevent Terser from evaluating at minify time
  var PROJECT_LOGO = ['%%PROJECT_LOGO%%'][0];
  var PROJECT_NAME = ['%%PROJECT_NAME%%'][0];

  // Token markers for runtime detection (Terser can't optimize these away)
  var TOKEN_MARKER = '%%PROJECT';

  // Fallback SVG if no logo URL is configured
  var FALLBACK_SVG = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="28" height="28"><circle cx="50" cy="85" r="8" fill="#00ff41"/><path d="M50 10 L30 75 L40 75 L50 45 L60 75 L70 75 Z" fill="#00ff41"/></svg>';

  /**
   * Check if a token was replaced (doesn't start with %%)
   */
  function isTokenReplaced(value) {
    return value && value.indexOf(TOKEN_MARKER) !== 0 && value.length > 0;
  }

  /**
   * Get the logo HTML - either an img tag for URL or fallback SVG
   */
  function getLogoHtml() {
    if (isTokenReplaced(PROJECT_LOGO)) {
      return '<img src="' + PROJECT_LOGO + '" alt="' + getProjectName() + '" width="28" height="28" />';
    }
    return FALLBACK_SVG;
  }

  /**
   * Get the project name - use token value or fallback
   */
  function getProjectName() {
    if (isTokenReplaced(PROJECT_NAME)) {
      return PROJECT_NAME;
    }
    return 'Terminal Javadocs';
  }

  /**
   * Calculate root path based on current location
   */
  function getRootPath() {
    var path = window.location.pathname;
    var depth = (path.match(/\//g) || []).length - 1;
    if (depth <= 0) return './';
    return '../'.repeat(depth);
  }

  /**
   * Inject minimal header for pages without navbar
   */
  function injectMinimalHeader() {
    var root = getRootPath();

    var header = document.createElement('header');
    header.id = 'topbar';
    header.className = 'topbar-minimal';
    header.innerHTML =
      '<div class="topbar-minimal-inner">' +
        '<a href="' + root + 'index.html" class="brand" title="Back to Home">' +
          getLogoHtml() +
          '<span class="project-title">' + getProjectName() + '</span>' +
        '</a>' +
      '</div>';

    document.body.insertBefore(header, document.body.firstChild);
    document.body.classList.add('topBarEnabled');
  }

  /**
   * Initialize mobile navigation for full navbar
   */
  function initMobileNav() {
    var hamburger = document.querySelector('#topbar .btn-navbar');
    var nav = document.querySelector('#topbar nav.nav-collapse ul.nav');

    if (!hamburger || !nav) return;

    // Inject project title into brand element
    var brandLink = document.querySelector('#topbar .brand');
    var brandImg = document.querySelector('#topbar .brand img');
    if (brandLink && brandImg && !document.querySelector('#project-title')) {
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
      nav.querySelectorAll('.dropdown').forEach(function(dd) {
        dd.classList.remove('open');
      });
    }

    function closeAllDropdowns() {
      nav.querySelectorAll('.dropdown').forEach(function(dd) {
        dd.classList.remove('open');
      });
    }

    // Disable Bootstrap's dropdown handling on mobile
    function disableBootstrapDropdowns() {
      nav.querySelectorAll('.dropdown-toggle[data-toggle]').forEach(function(toggle) {
        toggle.setAttribute('data-toggle-disabled', toggle.getAttribute('data-toggle'));
        toggle.removeAttribute('data-toggle');
      });
    }

    function enableBootstrapDropdowns() {
      nav.querySelectorAll('.dropdown-toggle[data-toggle-disabled]').forEach(function(toggle) {
        toggle.setAttribute('data-toggle', toggle.getAttribute('data-toggle-disabled'));
        toggle.removeAttribute('data-toggle-disabled');
      });
    }

    function updateMode() {
      if (isMobile()) {
        disableBootstrapDropdowns();
      } else {
        enableBootstrapDropdowns();
        closeMenu();
      }
    }

    updateMode();

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

    nav.addEventListener('click', function(e) {
      if (!isMobile()) return;

      var toggle = e.target.closest('.dropdown-toggle');
      if (!toggle) return;

      e.preventDefault();
      e.stopPropagation();

      var dropdown = toggle.closest('.dropdown');
      if (!dropdown) return;

      var wasOpen = dropdown.classList.contains('open');
      closeAllDropdowns();

      if (!wasOpen) {
        dropdown.classList.add('open');
      }
    });

    document.addEventListener('click', function(e) {
      if (!isOpen) return;
      if (!nav.contains(e.target) && !hamburger.contains(e.target)) {
        closeMenu();
      }
    });

    nav.querySelectorAll('a:not(.dropdown-toggle)').forEach(function(link) {
      link.addEventListener('click', function() {
        var href = this.getAttribute('href');
        if (href && href.indexOf('#') === 0) {
          setTimeout(closeMenu, 100);
        }
      });
    });

    window.addEventListener('resize', function() {
      updateMode();
    });
  }

  /**
   * Main init
   */
  function init() {
    var topbar = document.querySelector('#topbar');
    var terminalHeader = document.querySelector('.terminal-header');

    if (terminalHeader) {
      // Landing page with its own header - do nothing
      // The header is already styled by landing.css
      return;
    }

    if (!topbar) {
      // No navbar - inject minimal header
      injectMinimalHeader();
    } else {
      // Has navbar - init mobile nav
      initMobileNav();
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
