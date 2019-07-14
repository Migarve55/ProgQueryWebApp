$("#javaArgs").hide();
$(".compOpt").change(function() {
	if($(".compOpt option:selected").attr("value") === "java")
		$("#javaArgs").show();
	else
		$("#javaArgs").hide();
});