$("#programSelect").change(function() {
	if($("#programSelect option:selected").attr("value") === undefined)
		$("#programSource").prop('disabled', false);
	else
		$("#programSource").prop('disabled', true);
	
});

$("#querySelect").change(function() {
	if($("#querySelect option:selected").attr("value") === undefined)
		$("#querySource").prop('disabled', false);
	else
		$("#querySource").prop('disabled', true);
	
});