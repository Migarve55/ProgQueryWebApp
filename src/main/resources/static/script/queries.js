$("#addBtn").click(function() {
	var toAdd = $("#toAdd").val(); 
	$("#toAdd").val("");
	$("#queriesList")
		.append("<option>" + toAdd + "</option>");
});

$("#delBtn").click(function() {
	
});