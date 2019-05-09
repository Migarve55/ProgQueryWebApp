
function updateUI(progress, status) {
	$("#msg").text(status);
	$("#progressBar").css("width", progress + "%");
}

function setErrorUI() {
	 updateUI(100, "Error");
	 $("#progressBar")
	 	.addClass("bg-danger")
	 	.removeClass("bg-success");
	 setTimeout(function() {
		 window.location.replace("/");
	 }, 500);
}

function updateProgressBar() {
	$.ajax( "/analyzer/progress" )
	  .done(function(data) {
		  updateUI(data.progress, data.status);
		  if (data.error === true) {
			  setErrorUI();
	  	  } else if (data.progress >= 100) {
			  setTimeout(function() {
				  window.location.replace("/result/last");
			  }, 500);
		  }
	  })
	  .fail(function() {
		  setErrorUI();
	  })
}

updateProgressBar();

setInterval(function() {
	updateProgressBar();
}, 1000);