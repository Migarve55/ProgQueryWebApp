$("#programSelect").change(function() {
	if($("#programSelect option:selected").attr("value") === undefined)
		$("#programSource").prop('disabled', false);
	else
		$("#programSource").prop('disabled', true);
	
});