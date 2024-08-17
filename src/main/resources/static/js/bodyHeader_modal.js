document.addEventListener('DOMContentLoaded', function () {
    let alarmButton = document.getElementById("alarm-button");
    let modal = document.getElementById("modal");

    // 알림 버튼 클릭 시 모달 열기
    alarmButton.addEventListener('click', function () {
        modal.classList.remove('hidden'); // 모달 보이기
        setTimeout(() => {
            modal.classList.add('show'); // 애니메이션 시작
        }, 10); // 약간의 지연을 주어 애니메이션 효과를 보이게 함
    });


    // 외부 클릭 시 모달 닫기
    modal.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.classList.remove('show'); // 애니메이션 종료
            setTimeout(() => {
                modal.classList.add('hidden'); // 모달 숨기기
            }, 300); // 애니메이션 시간과 동일한 시간 후에 숨김
        }
    });
});