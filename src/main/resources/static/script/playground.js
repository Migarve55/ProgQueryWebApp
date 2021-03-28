
$('#programSelect').autoComplete({
    resolverSettings: {
        url: '/program/search',
        requestThrottling: 250
    },
    formatResult: (program) => {
		return {
			id : program.id,
			text: program.name	
		}
	},
	preventEnter: true
});

$('#querySelect').autoComplete({
    resolverSettings: {
        url: '/query/search',
        requestThrottling: 250
    },
    formatResult: (query) => {
		return {
			id : query.id,
			text: query.name
		}
	},
	preventEnter: true
});

$("#querySelect").on("autocomplete.select", function(_, item) {
	$("#querySource").val(item.queryText);
});

$("#programSource").attr("disabled","disabled");

$("#srcCb").change(function() {
	if (!$(this).is(':checked')) {
    	$("#programSource").attr("disabled","disabled");
    	$("#programSelect").removeAttr("disabled");
    } else {
		$("#programSelect").attr("disabled","disabled");
		$("#programSource").removeAttr("disabled");
	}
});