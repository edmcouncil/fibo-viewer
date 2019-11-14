window.onscroll = function() {scrollFunction()};

function scrollFunction() {
  if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
    document.getElementById("goToTopBtn").style.display = "block";
  } else {
    document.getElementById("goToTopBtn").style.display = "none";
  }
}

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}

function typeQuery(text){
  console.log(text);
  $("#value").val(text);
}

function showMetaInformation(iriText){
  $path = ""; 
  $.ajax({
    url: $path,
    dataType: 'json',
    contentType: 'application/javascript',
    type: 'GET',
    data: urlToDisplay,
    success: function (response) {
      console.log(response.responseText);
      $('#ontoDetails').html(response.responseText);
    }
  })
    .fail(function (response) {
      console.log("cannot load onto details from server, " + JSON.stringify(response));
      //JSON.stringify(response);
      $('#ontoDetails').html(response.responseText);
      $his = JSON.parse(localStorage.getItem("onto_history"));
      $it = JSON.parse(localStorage.getItem("curr_history"));
      $it++;
      $his.length = $it;
      $his.push(urlToDisplay);
      localStorage.setItem("onto_history", JSON.stringify($his));

      
      localStorage.setItem("curr_history", JSON.stringify($it));
      $('#btn-back').removeClass("d-none");
      $('#btn-next').addClass("d-none");
    });

}