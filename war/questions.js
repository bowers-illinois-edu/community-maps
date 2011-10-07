/*******************************************************************************
 * Local question type support JavaScript
 * 
 * (c) 2011 Mark M. Fredrickson
 *
 * Requires jQuery and Google Maps (v3) to be loaded already.
 *
 ******************************************************************************/


jQuery(document).ready(function() {
  $("div.learn-composition").each(function(idx) {
    var widget = this;
    var importance = $("div.sc", this).hide();
    $("div.labeled-radio-button", importance).hide();
    $("div.mc input", widget).click(function() { 
      importance.show();
      $("div.mc input:checked", widget).each(function(idx) {
        var jthis = $(this);
        var splitId = jthis.attr("id").split("-");
        var lastId = splitId[splitId.length - 1];
        var radio = $("div.sc input[value='" + lastId + "']", widget).parent().show();
      });
    });
  });
});
