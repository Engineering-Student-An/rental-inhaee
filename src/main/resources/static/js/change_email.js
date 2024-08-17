$(document).ready(function() {
    $("#signupForm").submit(function(event) {
        event.preventDefault(); // 폼 제출 기본 동작 방지

        // 로딩 창 표시
        $("#loadingModal").css("display", "block");

        // 데이터 수집 (예시: 이메일)
        var email = $("#email").val();

        // AJAX를 통한 비동기 요청
        $.ajax({
            type: "POST",
            url: "/sendEmail", // 이메일을 보내는 서버의 API 주소
            data: { email: email },
            success: function(response) {
                // 성공적으로 메일을 보냈을 때

                $("#loadingModal").css("display", "none"); // 로딩 창 숨기기
                alert("인증 메일이 발송되었습니다. 메일을 확인해 주세요.");
            },
            error: function(error) {
                // 요청 실패 시

                $("#loadingModal").css("display", "none"); // 로딩 창 숨기기
                alert("메일 발송에 실패했습니다. 다시 시도해 주세요.");
            }
        });
    });
});
