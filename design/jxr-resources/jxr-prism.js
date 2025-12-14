/**
 * JXR to Prism Converter
 *
 * This script converts Maven JXR syntax highlighting to Prism.js highlighting.
 * It extracts the source code from JXR's pre-highlighted HTML and re-highlights
 * it using Prism's superior Java tokenizer.
 */
(function() {
  'use strict';

  // Wait for DOM and Prism to be ready
  function init() {
    if (typeof Prism === 'undefined') {
      console.warn('Prism not loaded, falling back to JXR highlighting');
      return;
    }

    convertJxrToPrism();
  }

  /**
   * Extracts plain text from JXR-highlighted HTML, preserving line structure
   */
  function extractSourceText(preElement) {
    // Clone to avoid modifying original during extraction
    const clone = preElement.cloneNode(true);

    // Remove line number links - they have class jxr_linenumber
    const lineNumbers = clone.querySelectorAll('.jxr_linenumber, a.jxr_linenumber');
    lineNumbers.forEach(ln => ln.remove());

    // Get text content - this strips all HTML tags
    let text = clone.textContent || clone.innerText;

    return text;
  }

  /**
   * Main conversion function
   */
  function convertJxrToPrism() {
    // Find the source code pre element
    const preElement = document.querySelector('pre');
    if (!preElement) {
      console.warn('No pre element found');
      return;
    }

    // Check if already processed
    if (preElement.dataset.prismProcessed) {
      return;
    }

    // Store original line numbers for restoration
    const lineNumberLinks = [];
    preElement.querySelectorAll('a.jxr_linenumber').forEach((link, index) => {
      lineNumberLinks.push({
        lineNum: link.textContent.trim(),
        href: link.getAttribute('href'),
        name: link.getAttribute('name') || link.getAttribute('id')
      });
    });

    // Extract the source code text
    const sourceText = extractSourceText(preElement);

    // Create new structure
    const container = document.createElement('div');
    container.className = 'jxr-prism-container';

    // Create line numbers column
    const lineNumbersDiv = document.createElement('div');
    lineNumbersDiv.className = 'jxr-line-numbers';

    // Create code container
    const codeContainer = document.createElement('div');
    codeContainer.className = 'jxr-code-container';

    // Create the code element for Prism
    const codeElement = document.createElement('code');
    codeElement.className = 'language-java';
    codeElement.textContent = sourceText;

    // Create new pre for Prism
    const newPre = document.createElement('pre');
    newPre.className = 'language-java line-numbers';
    newPre.appendChild(codeElement);

    // Apply Prism highlighting
    Prism.highlightElement(codeElement);

    // Now split the highlighted code into lines and add line numbers
    const highlightedHtml = codeElement.innerHTML;
    const lines = highlightedHtml.split('\n');

    // Build line numbers
    lines.forEach((line, index) => {
      const lineNum = index + 1;
      const lineLink = document.createElement('a');
      lineLink.className = 'jxr_linenumber';
      lineLink.textContent = lineNum;
      lineLink.href = '#L' + lineNum;
      lineLink.id = 'L' + lineNum;
      lineLink.name = 'L' + lineNum;
      lineNumbersDiv.appendChild(lineLink);
      lineNumbersDiv.appendChild(document.createTextNode('\n'));
    });

    codeContainer.appendChild(newPre);
    container.appendChild(lineNumbersDiv);
    container.appendChild(codeContainer);

    // Replace original pre
    preElement.parentNode.replaceChild(container, preElement);

    // Mark as processed
    container.dataset.prismProcessed = 'true';

    console.log('JXR source converted to Prism highlighting');
  }

  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
