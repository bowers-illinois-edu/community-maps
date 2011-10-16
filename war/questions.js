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
      
      if ($("div.mc input:checked", widget).length > 0) {
        importance.show();
      } else {
        importance.hide();
      } 
      
      $("div.mc input:checked", widget).each(function(idx) {
        var jthis = $(this);
        var splitId = jthis.attr("id").split("-");
        var lastId = splitId[splitId.length - 1];
        var radio = $("div.sc input[value='" + lastId + "']", widget).parent().show();
      });

      $("div.mc input:not(:checked)", widget).each(function(idx) {
        var jthis = $(this);
        var splitId = jthis.attr("id").split("-");
        var lastId = splitId[splitId.length - 1];
        var radio = $("div.sc input[value='" + lastId + "']", widget).parent().hide();
      });});
  });

  $("div.election-choice").each(function(idx){
    var widget = this;
    $("div.vote-choice", widget).hide();

    $("div.did-vote input[value=yes]", this).click(function() {
      $("div.vote-choice", widget).slideDown();
    });

    $("div.did-vote input[value=no]", this).click(function() {
      $("div.vote-choice", widget).slideUp();
    });
  });
});
