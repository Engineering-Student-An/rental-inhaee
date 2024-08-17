document.addEventListener('DOMContentLoaded', function () {
    let userMenuButton = document.getElementById("user-menu-button");
    let dropdownMenu = document.getElementById("dropdown-menu");

    userMenuButton.addEventListener('click', function (event) {
        console.log("사용자 메뉴 버튼이 클릭되었습니다.");
        event.stopPropagation(); // 이벤트 전파 중단
        dropdownMenu.classList.toggle('hidden'); // 숨김 상태 토글
    });

    // 외부 클릭 시 드롭다운 숨김
    document.addEventListener('click', function (event) {
        if (!dropdownMenu.classList.contains('hidden')) {
            dropdownMenu.classList.add('hidden'); // 드롭다운 숨김
        }
    });
});

// 드롭다운 메뉴를 위한 스크립트
document.addEventListener('DOMContentLoaded', () => {
    const dropdown = document.querySelector('.dropdown');

    dropdown.addEventListener('mouseenter', () => {
        dropdown.querySelector('.dropdown-menu').style.display = 'block';
    });

    dropdown.addEventListener('mouseleave', () => {
        dropdown.querySelector('.dropdown-menu').style.display = 'none';
    });
});



document.addEventListener('DOMContentLoaded', () => {
    const button = document.getElementById('mobile-menu-button');
    const menu = document.getElementById('mobile-menu');

    button.addEventListener('click', () => {
        const expanded = button.getAttribute('aria-expanded') === 'true' || false;
        button.setAttribute('aria-expanded', !expanded);
        menu.style.display = expanded ? 'none' : 'block';
        button.querySelector('svg:first-of-type').classList.toggle('hidden', !expanded);
        button.querySelector('svg:last-of-type').classList.toggle('hidden', expanded);
    });
});
