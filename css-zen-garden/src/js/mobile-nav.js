/**
 * Mobile Navigation & Project Title
 * Handles hamburger menu toggle and injects project title into navbar
 */
(function() {
  'use strict';

  function init() {
    var hamburger = document.querySelector('#topbar .btn-navbar');
    var nav = document.querySelector('#topbar nav.nav-collapse ul.nav');

    if (!hamburger || !nav) return;

    // Inject project title into brand element
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
      // Close all dropdowns
      nav.querySelectorAll('.dropdown').forEach(function(dd) {
        dd.classList.remove('open');
      });
    }

    function closeAllDropdowns() {
      nav.querySelectorAll('.dropdown').forEach(function(dd) {
        dd.classList.remove('open');
      });
    }

    // Disable Bootstrap's dropdown handling on mobile by removing data-toggle
    // We'll handle it ourselves
    function disableBootstrapDropdowns() {
      nav.querySelectorAll('.dropdown-toggle[data-toggle]').forEach(function(toggle) {
        toggle.setAttribute('data-toggle-disabled', toggle.getAttribute('data-toggle'));
        toggle.removeAttribute('data-toggle');
      });
    }

    // Re-enable Bootstrap dropdowns on desktop
    function enableBootstrapDropdowns() {
      nav.querySelectorAll('.dropdown-toggle[data-toggle-disabled]').forEach(function(toggle) {
        toggle.setAttribute('data-toggle', toggle.getAttribute('data-toggle-disabled'));
        toggle.removeAttribute('data-toggle-disabled');
      });
    }

    // Check and set mobile/desktop mode
    function updateMode() {
      if (isMobile()) {
        disableBootstrapDropdowns();
      } else {
        enableBootstrapDropdowns();
        closeMenu();
      }
    }

    // Initial mode setup
    updateMode();

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

    // Handle clicks on dropdown toggles (use delegation on nav)
    nav.addEventListener('click', function(e) {
      if (!isMobile()) return;

      // Find if we clicked on a dropdown-toggle or inside one
      var toggle = e.target.closest('.dropdown-toggle');
      if (!toggle) return;

      e.preventDefault();
      e.stopPropagation();

      var dropdown = toggle.closest('.dropdown');
      if (!dropdown) return;

      var wasOpen = dropdown.classList.contains('open');

      // Close all other dropdowns
      closeAllDropdowns();

      // Toggle this one
      if (!wasOpen) {
        dropdown.classList.add('open');
      }
    });

    // Close menu when clicking outside
    document.addEventListener('click', function(e) {
      if (!isOpen) return;
      if (!nav.contains(e.target) && !hamburger.contains(e.target)) {
        closeMenu();
      }
    });

    // Handle nav link clicks - close menu for same-page anchors
    nav.querySelectorAll('a:not(.dropdown-toggle)').forEach(function(link) {
      link.addEventListener('click', function() {
        var href = this.getAttribute('href');
        if (href && href.indexOf('#') === 0) {
          setTimeout(closeMenu, 100);
        }
      });
    });

    // Update mode on resize
    window.addEventListener('resize', function() {
      updateMode();
    });
  }

  // Run on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
