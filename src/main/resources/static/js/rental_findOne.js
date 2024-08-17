function cancel(id) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "findOne/" + id + "/finish");
    document.body.appendChild(form);
    form.submit();
}