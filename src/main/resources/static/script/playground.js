
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

const mimeJava = 'text/x-java';
const mimeCypher = 'application/x-cypher-query';

const textAreaQuery = document.getElementById('querySource');
var queryVisualizer = CodeMirror.fromTextArea(textAreaQuery, {
	mode: mimeCypher,
    indentWithTabs: true,
    lineNumbers: true,
    matchBrackets: true,
    theme: 'neo'
});

const textAreaProgram = document.getElementById('programSource');
var programVisualizer = CodeMirror.fromTextArea(textAreaProgram, {
	mode: mimeJava,
    indentWithTabs: true,
    lineNumbers: true,
    matchBrackets: true
});

$("#querySelect").on("autocomplete.select", function(_, item) {
	queryVisualizer.setValue(item.queryText);
});

if (!$("#srcCb").is(':checked')) {
	programVisualizer.setValue("");
    programVisualizer.setOption("readOnly", "nocursor");
    $("#programSelect").removeAttr("disabled");
    $(".CodeMirror").addClass("disabled");
}

$("#srcCb").change(function() {
	if (!$(this).is(':checked')) {
		programVisualizer.setValue("");
    	programVisualizer.setOption("readOnly", "nocursor");
    	$("#programSelect").removeAttr("disabled");
    	$(".CodeMirror").addClass("disabled");
    } else {
		programVisualizer.setOption("readOnly", false);
		$(".CodeMirror").removeClass("disabled");
		$("#programSelect").attr("disabled","disabled");
	}
});
