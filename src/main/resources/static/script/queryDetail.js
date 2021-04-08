
const mime = 'application/x-cypher-query';

const querySample = document.getElementById('querySample');
var queryVisualizer = CodeMirror(function(elt) {
  querySample.parentNode.replaceChild(elt, querySample);
}, {
	mode: mime,
    indentWithTabs: true,
    lineNumbers: true,
    matchBrackets: true,
    readOnly: 'nocursor',
    theme: 'neo',
    value: querySample.value
});