package an.rentalinhaee.config;

import an.rentalinhaee.domain.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        HttpSession session = request.getSession();
        String redirectUrl = (String) session.getAttribute("prevPage");

        // 관리자 권한 체크
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // 관리자 권한이 있을 경우의 로직
            response.sendRedirect("/admin"); // ADMIN으로 리다이렉트
        } else {
            if(!redirectUrl.isEmpty()) {
                // 세션에서 "prevPage" 속성을 제거
                session.removeAttribute("prevPage");
                // 이전 페이지의 URL로 리다이렉트
                redirectStrategy.sendRedirect(request, response, redirectUrl);

            } else {
                response.sendRedirect("/");
            }
        }

        // loginStudent 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Student loginStudent = userDetails.getStudent(); // CustomUserDetails에서 Student 정보를 가져올 수 있는 메서드 제공 가정

            // 세션에 Student 정보 저장
            session.setAttribute("loginStudent", loginStudent);

        }
    }
}
