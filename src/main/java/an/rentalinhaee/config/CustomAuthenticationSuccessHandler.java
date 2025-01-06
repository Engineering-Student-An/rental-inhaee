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
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null){
            String targetUrl = savedRequest.getRedirectUrl();

            if(targetUrl.contains("like")) {

                String[] parts = targetUrl.split("/");
                String boardId = parts[4];
                response.sendRedirect("/board/" + boardId);
            }
            else {
                response.sendRedirect(targetUrl);
            }
        } else {
            response.sendRedirect("/home");
        }

        // loginStudent 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Student loginStudent = userDetails.getStudent(); // CustomUserDetails에서 Student 정보를 가져올 수 있는 메서드 제공 가정

            // 세션에 Student 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginStudent", loginStudent);
        }

    }
}
