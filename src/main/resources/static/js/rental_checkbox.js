document.getElementById('category').addEventListener('change', function() {
    document.getElementById('categoryForm').submit();
});

function openModal(button) {
    const modal = document.getElementById('rentalModal');
    const modalContent = modal.querySelector('.modal-content');

    selectedItemId = button.dataset.itemId; // 아이템 ID 저장
    const itemName = button.dataset.itemName;
    const itemStockQuantity = button.dataset.itemStockQuantity; // 소문자 사용
    const itemAllStockQuantity = button.dataset.itemAllStockQuantity; // 소문자 사용

    // 모달에 아이템 정보를 출력
    const modalItemInfo = document.getElementById('modalItemInfo');
    modalItemInfo.innerHTML = `
    <p class="text-lg font-semibold mb-4"><strong>${itemName}</strong></p><hr/><br>
    <p class="left-text"><strong>대여 가능 재고 수량:</strong> ${itemStockQuantity} 개</p>
    <p class="left-text"><strong>보유 총 재고 수량:</strong> ${itemAllStockQuantity} 개</p>
`;


    modal.classList.remove('hidden', 'hide');
    modal.classList.add('show'); // 모달을 보이도록 설정
    setTimeout(() => {
        modalContent.classList.add('scale-in');
    }, 0); // 애니메이션 시작
}

function closeModal() {
    const modal = document.getElementById('rentalModal');
    const modalContent = modal.querySelector('.modal-content');

    if (modalContent) {
        modalContent.classList.remove('scale-in');
        modalContent.classList.add('scale-out');

        setTimeout(() => {
            modal.classList.remove('show');
            modal.classList.add('hidden'); // 모달을 숨기기
            modalContent.classList.remove('scale-out'); // 애니메이션 클래스 초기화
        }, 300); // 애니메이션 시간과 같게 설정
    } else {
        console.error("modalContent가 존재하지 않습니다.");
    }
}

// 모달 외부 클릭 시 모달 닫기
document.getElementById('rentalModal').addEventListener('click', function(event) {
    if (event.target === this) { // 클릭한 대상이 모달 자체일 경우
        closeModal(); // 모달 닫기
    }
});

let notificationVisible = false; // 알림창 표시 여부를 추적하는 변수
let failNotificationVisible = false;

function confirmRental() {
    const itemId = selectedItemId; // 선택된 아이템 ID를 가져옵니다.

    const params = new URLSearchParams();
    params.append('itemId', itemId);

    fetch('/rental/complete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString() // URL 인코딩된 형태로 전송
    })
        .then(response => {
            return response.json(); // JSON 응답으로 변환
        })
        .then(data => {
            closeModal(); // 모달 닫기

            // 다음 URL로 리디렉션
            if (!data.nextUrl) {
                showFailNotification();
            } else {
                showNotification(data.nextUrl); // 알림창과 함께 nextUrl 전달
            }
        })
        .catch((error) => {
            console.error('실패:', error);
            alert(error.message); // 에러 메시지 표시
        });
}


let nextUrl = ''; // 전역 변수로 nextUrl 선언

function showNotification(url) {
    if (notificationVisible) return; // 이미 표시된 경우 함수 종료

    nextUrl = url; // nextUrl 저장
    const notification = document.getElementById('notification-success');
    notification.style.display = 'flex'; // 알림창 표시
    notification.style.animation = 'slideIn 0.5s forwards'; // 애니메이션 적용

    notificationVisible = true; // 알림창 표시 상태를 true로 설정

    // 5초 후 자동으로 닫기
    setTimeout(() => {
        closeNotification(nextUrl); // nextUrl을 전달하여 닫기
    }, 5000);
}



// 알림창을 닫는 함수
function closeNotification(url) {
    const notification = document.getElementById('notification-success');
    notification.style.animation = 'slideOut 0.5s forwards'; // 닫힐 때 애니메이션 적용

    // 애니메이션이 끝난 후에 알림창 숨기기
    setTimeout(() => {
        notification.style.display = 'none'; // 알림창 숨기기
        notificationVisible = false; // 알림창 표시 상태를 false로 설정

        // url이 존재하면 리디렉션
        if (url) {
            window.location.href = url; // 리디렉션
        }
    }, 500); // 애니메이션 시간과 동일하게 설정
}


// 알림창을 표시하는 함수
function showFailNotification() {
    if (failNotificationVisible) return; // 이미 표시된 경우 함수 종료

    const notification2 = document.getElementById('notification-fail');
    notification2.style.display = 'flex'; // 알림창 표시
    notification2.style.animation = 'slideIn 0.5s forwards'; // 애니메이션 적용

    failNotificationVisible = true; // 알림창 표시 상태를 true로 설정
    // 5초 후 자동으로 닫기
    setTimeout(() => {
        closeFailNotification();
    }, 5000);
}

// 알림창을 닫는 함수
function closeFailNotification() {
    const notification2 = document.getElementById('notification-fail');
    notification2.style.animation = 'slideOut 0.5s forwards'; // 닫힐 때 애니메이션 적용

    // 애니메이션이 끝난 후에 알림창 숨기기
    setTimeout(() => {
        notification2.style.display = 'none'; // 알림창 숨기기
        failNotificationVisible = false; // 알림창 표시 상태를 false로 설정
    }, 500); // 애니메이션 시간과 동일하게 설정
}


// 현재 날짜에서 3일 후의 날짜 계산
const returnDate = new Date();
returnDate.setDate(returnDate.getDate() + 3);

// 날짜를 원하는 형식으로 포맷
const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
const formattedDate = returnDate.toLocaleDateString('ko-KR', options).replace(/\./g, '/');

// HTML에 날짜 삽입
document.getElementById('return-date').innerText = formattedDate;