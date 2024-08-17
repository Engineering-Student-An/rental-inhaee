document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('write-btn');

    mainSwitchButton.addEventListener('mouseover', () => {
        mainSwitchButton.classList.add('active');
    });

    mainSwitchButton.addEventListener('mouseout', () => {
        mainSwitchButton.classList.remove('active');
    });

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/item/request';
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const checkRequestBtn = document.getElementById("checkRequestBtn");

    checkRequestBtn.addEventListener("click", function() {
        const id = this.getAttribute("data-id"); // 버튼의 data-id 속성에서 id 값 가져오기
        console.log('id : ' + id);
        fetch(`/api/item/request/${id}/check`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("요청이 확인되었습니다.");
                    window.location.href='/item/request/list';
                } else {
                    alert("요청 확인에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("오류 발생:", error);
                alert("서버와의 통신 중 오류가 발생했습니다.");
            });
    });
});
