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

var defaults_es = {
    noneSelectedText: 'No hay selección',
    noneResultsText: 'No hay resultados {0}',
    countSelectedText: 'Seleccionados {0} de {1}',
    maxOptionsText: ['Límite alcanzado ({n} {var} max)', 'Límite del grupo alcanzado({n} {var} max)', ['elementos', 'element']],
    multipleSeparator: ', ',
    selectAllText: 'Seleccionar Todos',
    deselectAllText: 'Desmarcar Todos'
  };
  
var defaults_en = {
    noneSelectedText: 'Nothing selected',
    noneResultsText: 'No results match {0}',
    countSelectedText: function (numSelected, numTotal) {
      return (numSelected == 1) ? '{0} item selected' : '{0} items selected';
    },
    maxOptionsText: function (numAll, numGroup) {
      return [
        (numAll == 1) ? 'Limit reached ({n} item max)' : 'Limit reached ({n} items max)',
        (numGroup == 1) ? 'Group limit reached ({n} item max)' : 'Group limit reached ({n} items max)'
      ];
    },
    selectAllText: 'Select All',
    deselectAllText: 'Deselect All',
    multipleSeparator: ', '
  };

(function ($) {
	if (document.documentElement.lang === "es")
  		$.fn.selectpicker.defaults = defaults_es;
  	else if (document.documentElement.lang === "en")
  		$.fn.selectpicker.defaults = defaults_en;
})(jQuery);