document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('write-btn');

    mainSwitchButton.addEventListener('mouseover', () => {
        mainSwitchButton.classList.add('active');
    });

    mainSwitchButton.addEventListener('mouseout', () => {
        mainSwitchButton.classList.remove('active');
    });

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/proposal/new';
    });
});
