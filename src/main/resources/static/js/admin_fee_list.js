// 'resetButton' 버튼에 클릭 이벤트 리스너 추가
document.getElementById('reset-btn').addEventListener('click', function() {
    // 'category'와 'name' 입력 필드의 값을 초기화
    document.getElementById('stuId').value = '';
    document.getElementById('name').value = '';
});

document.addEventListener("DOMContentLoaded", function() {
    var itemTable = document.getElementById("itemTable");
    var rowCount = itemTable.getElementsByTagName("tbody")[0].getElementsByTagName("tr").length;

    console.log(rowCount); // 값을 확인합니다.

    // itemsSize가 빈 문자열이거나 "0"일 경우 알림창을 띄웁니다.
    if (rowCount === 0) {
        alert("현재 등록된 물품이 없습니다.");
    }
});

document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('rental-btn');

    mainSwitchButton.addEventListener('mouseover', () => {
        mainSwitchButton.classList.add('active');
    });

    mainSwitchButton.addEventListener('mouseout', () => {
        mainSwitchButton.classList.remove('active');
    });

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/rental';
    });
});


var tableAdded = false;
function addTable() {
    var tableContainer = document.getElementById('tableContainer');
    if(tableAdded) {
        tableContainer.innerHTML = '';
        tableAdded = false;
    } else {
        var newTable = '<br><h4>학생회비 납부 학생 추가</h4>' +
        '<div>' +
        '<table class="table table-striped small-font">' +
        '<thead>' +
        '<tr>' +
        '<th>학번</th>' +
        '<th>이름</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<tr>' +
        '<td><input type="text" class="form-control small-font" name="newStuId" placeholder="학번 입력"></td>' +
        '<td><input type="text" class="form-control small-font" name="newName" placeholder="이름 입력"></td>' +
        '</tr>' +
        '</tbody>' +
        '</table>' +
        '</div>';

        var addButton = '<button id="addButton" class="material-symbols-outlined"  ' +
        'style="color: #0b5ed7; background: none; border: none; cursor: pointer; font-size: 30px" ' +
        'onclick="addStudent()">person_add</button><br>'

        // 새로운 행을 기존 표의 tbody에 추가
        tableContainer.innerHTML += newTable + addButton;
        tableAdded = true;
    }
}
function addStudent() {
    var stuId = document.getElementsByName('newStuId')[0].value;
    var name = document.getElementsByName('newName')[0].value;
    location.href = `/admin/student/feeList/add?newStuId=${stuId}&newName=${name}`;
}


var windowAdded = false;
function addWindow() {
    var windowContainer = document.getElementById('windowContainer');
    if(windowAdded) {
        windowContainer.innerHTML = '';
        windowAdded = false;
    } else {
        var newWindow = '<br><h4>학생회비 납부 명단 업로드 (엑셀 파일만 업로드!)</h4>' +
        '<form action="/admin/student/feeList/upload" method="POST" enctype="multipart/form-data">' +
        '<input type="file" class="btn btn-outline-secondary btn-sm" name="excelFile">' +
        '<button type="submit" style="border: none; background: none; cursor: pointer">' +
        '<span class="material-symbols-outlined" style="color: #20c997; vertical-align: top; font-size: 30px">upload_file</span>' +
        '</button>' +
        '</input>' +
        '</form><br>' +
        '<p></p>';

        windowContainer.innerHTML += newWindow;
        windowAdded = true;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const deleteButton = document.querySelector("button[type='submit'][form='deleteForm']");
    const checkboxes = document.querySelectorAll("input[type='checkbox'][name='stuIdList']");
    function updateButtonState() {
        const isAnyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
        deleteButton.disabled = !isAnyChecked;
    }
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener("change", updateButtonState);
    });
    updateButtonState();
});

function confirmDelete() {
    var result = confirm("정말로 삭제하시겠습니까?");
    if (result) {
        // 사용자가 'OK'를 클릭한 경우, 폼 제출
        return true;
    } else {
        // 사용자가 'Cancel'을 클릭한 경우, 폼 제출 취소
    return false;
    }
}
