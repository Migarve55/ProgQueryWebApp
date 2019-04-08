
function updateUI(progress, status) {
	$("#msg").text(status);
	$("#progressBar").css("width", progress + "%");
}

function updateProgressBar() {
	$.ajax( "/progress" )
	  .done(function(data) {
		  updateUI(data.progress, data.status);
		  if (data.progress >= 100) {
			  setTimeout(function() {
				  window.location.replace("/report");
			  }, 800);
		  }
	  })
	  .fail(function() {
		  updateUI(100, "Error");
		  setTimeout(function() {
				window.location.replace("/");
		  }, 1000);
	  })
}

updateProgressBar();

setInterval(function() {
	updateProgressBar();
}, 1500);