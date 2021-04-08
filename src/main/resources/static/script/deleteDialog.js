
$('#deleteModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget);
	var deleteUrl = button.data('url');
	var modal = $(this);
	modal.find('.modal-footer a').attr("href", deleteUrl);
});