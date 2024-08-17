const indexSwitchButton = document.getElementById('index-switch-button');

indexSwitchButton.addEventListener('mouseover', () => {
    indexSwitchButton.classList.add('active');
});

indexSwitchButton.addEventListener('mouseout', () => {
    indexSwitchButton.classList.remove('active');
});

indexSwitchButton.addEventListener('click', function() {
    window.location.href = '/home';
});