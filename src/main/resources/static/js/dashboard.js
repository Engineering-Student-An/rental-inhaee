function cancel(id) {
    var previousPage = document.referrer; // 이전 페이지 URL 얻기
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "rental/findOne/" + id + "/finish");
    document.body.appendChild(form);
    form.submit();
}

let currentIndex = 0;

function moveSlide(direction) {
    const cards = document.querySelectorAll('.pricing-card');
    const totalCards = cards.length;

    // 인덱스 조정
    currentIndex += direction;

    // 인덱스 범위 조정
    if (currentIndex < 0) {
        currentIndex = 0; // 첫 번째 카드로 이동
    } else if (currentIndex >= totalCards) {
        currentIndex = totalCards - 1; // 마지막 카드로 이동
    }

    // 카드 이동
    const offset = -currentIndex * 100; // 100% 너비로 슬라이드
    document.querySelector('.pricing-cards').style.transform = `translateX(${offset}%)`;
}

document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('login-btn');

    mainSwitchButton.addEventListener('mouseover', () => {
        mainSwitchButton.classList.add('active');
    });

    mainSwitchButton.addEventListener('mouseout', () => {
        mainSwitchButton.classList.remove('active');
    });

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/login';
    });
});
