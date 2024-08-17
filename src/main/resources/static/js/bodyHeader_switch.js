document.addEventListener('DOMContentLoaded', function () {
    let mainSwitchButton = document.getElementById('main-switch-button');

    mainSwitchButton.addEventListener('click', function (event) {
        window.location.href='/home';
    });
});

