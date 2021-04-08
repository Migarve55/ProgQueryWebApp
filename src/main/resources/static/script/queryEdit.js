
const mime = 'application/x-cypher-query';

const textArea = document.getElementById('queryEditor');
var queryVisualizer = CodeMirror.fromTextArea(textArea, {
	mode: mime,
    indentWithTabs: true,
    lineNumbers: true,
    matchBrackets: true,
    autofocus: true,
    theme: 'neo'
});