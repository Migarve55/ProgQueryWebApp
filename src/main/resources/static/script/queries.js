$("#addBtn").click(function() {
	var toAdd = $("#toAdd").val().trim(); 
	$("#toAdd").val("");
	if (toAdd.length <= 0)
		return;
	var newQuery = $('<option class="queryOp"></option>')
		.text(toAdd);
	$("#queriesList").append(newQuery);
});

$("#delBtn").click(function() {
	$(".queryOp:selected").each((i, q) => {
		$(q).remove();
	});
});