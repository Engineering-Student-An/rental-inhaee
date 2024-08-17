function confirmDelete() {
    var result = confirm("정말로 계정을 삭제하시겠습니까?\n해당 학생의 대여 기록도 모두 사라집니다.");
    if (result) {
        // 사용자가 'OK'를 클릭한 경우, 비밀번호 입력받음
        var password = prompt("관리자 계정의 비밀번호를 입력해주세요.");
        document.getElementById('passwordInput').value = password;
    } else {
        // 사용자가 'Cancel'을 클릭한 경우, 폼 제출 취소
        event.preventDefault();
        return false;
    }
}


function adminCancel(id, stuId) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "/admin/rental/" + id + "/finish");

    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "stuId");
    hiddenField.setAttribute("value", stuId);

    form.appendChild(hiddenField);
    document.body.appendChild(form);
    form.submit();
}

