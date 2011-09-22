/*******************************************************************************
 * Utility functions for on-map drawing
 * 
 * (c) 2011 Mark M. Fredrickson
 *
 * Requires jQuery and Google Maps (v3) to be loaded already.
 * Load this before other demo files.
 *
 ******************************************************************************/

makeButton = function(text) {
  return(jQuery("<a class = 'fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'><span class = 'ui-button-text'>" + text + "</span></a>"))
}
