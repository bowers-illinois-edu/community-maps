// Code to send an email with a resume later link

$(document).ready(function() {

  var sent = $("<div>Email has been sent</div>").dialog({
    autoOpen: false, 
    modal: true,
    buttons: {
      "Ok": function() { 
        $(this).dialog("close"); 
       }
    }
  });

  $("div#resume-popup").dialog({
    autoOpen: false,
    height: 300,
    width: 300,
    modal: true,
    buttons: {
      "Send me a link": function() {
        var dlog = this;
        $.get("/resume", {email: $("#resume-popup input.email").val(), id: $("input#id").val()}, function() {
          $(dlog).dialog("close");
          sent.dialog("open");
        });
      },
      "Cancel": function() {
        $(this).dialog("close");
      }
    }
  });

  $("a#resume").click(function() {
    $("div#resume-popup").dialog("open");
  });
});
