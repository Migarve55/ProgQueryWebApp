$(document).ready(function() {
	$("#languageDropdownMenuButton a").click(function(e) {
		e.preventDefault(); // cancel the link behaviour
		var languageSelectedText = $(this).text();
		var languageSelectedValue = $(this).attr("value");
		$("#btnLanguage").text(languageSelectedText);
		window.location.replace('?lang=' + languageSelectedValue);
		return false;
	});
});

// Para bootstrap select 

var custom_defaults = {
    noneSelectedText: multiNoSelected,
    noneResultsText: multiNoResult,
    selectAllText: multiSelAll,
    deselectAllText: multiDelAll,
    multipleSeparator: ', '
};

(function ($) {
  	$.fn.selectpicker.defaults = custom_defaults;
})(jQuery);

$( document ).ready(function() {
    $('[required]').on('change invalid', function() {
		var textfield = $(this).get(0);
		$(this).prop('title', errorEmpty);
		textfield.setCustomValidity('');
		if (!textfield.validity.valid) {
			if (textfield.validity.typeMismatch)
		    	textfield.setCustomValidity(errorFormat);  
		    else
		      	textfield.setCustomValidity(errorEmpty);
		}
	});
});

