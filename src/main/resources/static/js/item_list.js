// 'resetButton' 버튼에 클릭 이벤트 리스너 추가
document.getElementById('reset-btn').addEventListener('click', function() {
    // 'category'와 'name' 입력 필드의 값을 초기화
    document.getElementById('category').value = '';
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
