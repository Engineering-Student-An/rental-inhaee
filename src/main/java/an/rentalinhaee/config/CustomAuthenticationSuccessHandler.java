package an.rentalinhaee.config;

import an.rentalinhaee.domain.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 관리자 권한 체크
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // 관리자 권한이 있을 경우의 로직
            response.sendRedirect("/admin"); // ADMIN으로 리다이렉트
        } else {
            // 이전 페이지가 없다면 홈으로 리다이렉트
            if (savedRequest == null) {
                response.sendRedirect("/");
            } else {    // 이전 페이지가 있다면 이전 페이지로 리다이렉트
                String targetUrl = savedRequest.getRedirectUrl();
                if(targetUrl.contains("/board") && targetUrl.contains("/like")) {   // 게시글 좋아요 누른 경우

                    String[] parts = targetUrl.split("/");
                    int boardId = Integer.parseInt(parts[4]);
                    response.sendRedirect("/board/" + boardId);
                } else if(targetUrl.contains("/reply") && targetUrl.contains("/like")) {   // 댓글 좋아요 누른 경우
                    // URL에서 boardId 쿼리 파라미터 추출
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(targetUrl);
                    UriComponents uriComponents = uriBuilder.build();
                    String boardId = uriComponents.getQueryParams().getFirst("boardId");
                    // 게시글 상세 페이지로 리다이렉트
                    response.sendRedirect("/board/" + boardId);
                } else {
                    response.sendRedirect(targetUrl);
                }

            }
        }

        // loginStudent 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            HttpSession session = request.getSession();
            Student loginStudent = userDetails.getStudent(); // CustomUserDetails에서 Student 정보를 가져올 수 있는 메서드 제공 가정

            // 세션에 Student 정보 저장
            session.setAttribute("loginStudent", loginStudent);

        }
    }
}
