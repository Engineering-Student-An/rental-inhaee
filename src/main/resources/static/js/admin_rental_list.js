// 'resetButton' 버튼에 클릭 이벤트 리스너 추가
document.getElementById('reset-btn').addEventListener('click', function() {
    // 'category'와 'name' 입력 필드의 값을 초기화
    document.getElementById('stuId').value = '';
    document.getElementById('itemName').value = '';
    document.getElementById('rentalStatus').value = '';
});