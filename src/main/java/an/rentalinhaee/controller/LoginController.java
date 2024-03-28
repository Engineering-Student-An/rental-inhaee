package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.StudentRole;
import an.rentalinhaee.domain.dto.*;
import an.rentalinhaee.service.EmailService;
import an.rentalinhaee.service.FeeStudentService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Pattern;


@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final StudentService studentService;
    private final FeeStudentService feeStudentService;

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinRequest", new JoinRequest());
        return "home/join/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest,
                       BindingResult bindingResult,
                       HttpSession session, Model model) {
        if (studentService.checkStuIdDuplicate(joinRequest.getStuId())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "stuId", "이미 가입되어 있습니다!"));
        }

        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }


        if (!Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$").matcher(joinRequest.getPhoneNumber()).matches()) {
            bindingResult.addError(new FieldError("joinRequest",
                    "phoneNumber", "전화번호 형식이 올바르지 않습니다!"));
        }

//        List<String> readRequest = readOneExcel(joinRequest.getStuId());
        List<String> readRequest = feeStudentService.findOne(joinRequest.getStuId());
        if(readRequest.get(0).isEmpty()){
             bindingResult.addError(new FieldError("joinRequest",
                     "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        }else if(!readRequest.get(1).equals(joinRequest.getName())){
            bindingResult.addError(new FieldError("joinRequest",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }


        if (bindingResult.hasErrors()) {
            return "home/join/join";
        }
        model.addAttribute("joinRequest.phoneNumber", joinRequest.getPhoneNumber());
        session.setAttribute("joinRequest", joinRequest);
        return "home/join/joinMessage";

    }

    private final EmailService emailService;

    @GetMapping("/join/verify")
    public String verifyEmail(Model model, HttpSession session) {

        session.setAttribute("joinRequest", (JoinRequest) session.getAttribute("joinRequest"));
        model.addAttribute("emailForm", new EmailForm());
        model.addAttribute("isSent", false);
        model.addAttribute("verifyCodeForm", new VerifyCodeForm());

        return "home/join/verifyEmail";
    }

    @PostMapping("/join/verify")
    public String sendEmail(@ModelAttribute EmailForm emailForm,
                            @ModelAttribute VerifyCodeForm verifyCodeForm,
                            BindingResult bindingResult, HttpSession session, Model model) {

        String email = emailForm.email;
        if(email.isEmpty()) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소를 입력해주세요!"));
        }
        else if(!email.contains("@") || !email.contains(".")) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소가 올바르지 않습니다!"));
        }
        if(bindingResult.hasErrors()) {
            return "home/join/verifyEmail";
        }

        String verifyCode = emailService.sendEmail(emailForm.email);
        model.addAttribute("isSent", true);

        session.setAttribute("joinRequest", (JoinRequest) session.getAttribute("joinRequest"));
        session.setAttribute("verifyCode", verifyCode);
        return "home/join/verifyEmail";
    }

    @PostMapping("/join/verify/code")
    public String verifyCodeCheck(@ModelAttribute("verifyCodeForm") VerifyCodeForm verifyCodeForm,
                                  HttpSession session, BindingResult bindingResult, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");

        // 인증 문자 입력 했는지 검증
        if(verifyCodeForm.getCode().isEmpty()) {
            bindingResult.addError(new FieldError("code",
                    "code", "인증 문자를 입력해주세요!"));
        }

        // 인증 문자와 동일한지 검증
        if(!verifyCodeForm.getCode().equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/join/verify");
            return "error/errorMessage";
        }

        // 회원가입
        JoinRequest joinRequest = (JoinRequest) session.getAttribute("joinRequest");
        studentService.join(joinRequest);

        model.addAttribute("errorMessage", "회원가입이 완료되었습니다! 로그인 페이지로 돌아갑니다.");
        model.addAttribute("nextUrl", "/login");
        return "error/errorMessage";

    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "home/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
                        HttpServletRequest httpServletRequest, Model model) {

        Student student = studentService.login(loginRequest);

        if (student == null) {
            bindingResult.reject("loginFail", "학번 또는 비밀번호가 틀립니다!");
        }

        if (bindingResult.hasErrors()) {
            return "home/login";
        }

        httpServletRequest.getSession().invalidate();
        HttpSession httpSession = httpServletRequest.getSession(true);

        httpSession.setAttribute("loginStuId", student.getStuId());
        httpSession.setAttribute("loginName", student.getName());
        httpSession.setMaxInactiveInterval(60 * 60);    // 로그인 기간 1시간 설정

        if(student.getRole() == StudentRole.ADMIN) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(true);
        if (httpSession != null) {
            httpSession.invalidate();
        }

        return "redirect:/";
    }
    @GetMapping("/changePassword")
    public String changePassword(Model model, HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(true);

        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("phoneNumber", studentService.findStudent(loginStuId(httpSession)).getPhoneNumber());
        return "home/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("loginStuId") String stuId,
                                @RequestParam("loginName") String name,
                                @Valid @ModelAttribute ChangePasswordRequest request,
                                BindingResult bindingResult, Model model) {

        String password = studentService.findStudent(stuId).getPassword();


        if(!request.getCurrentPassword().equals(password)){
            bindingResult.addError(new FieldError("request",
                    "currentPassword", "현재 비밀번호와 동일하지 않습니다!"));
        }

        if(request.getChangePassword().equals(password)){
            bindingResult.addError(new FieldError("request",
                    "changePassword", "현재 비밀번호와 동일하게 변경 불가합니다!"));
        }

        if (!request.getChangePassword().equals(request.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (bindingResult.hasErrors()) {
            return "home/changePassword";
        }

        studentService.changePassword(stuId, request);
        if(stuId.equals("ADMIN")){
            return "redirect:/admin";
        }
        return "redirect:/";
    }

    @ModelAttribute("loginStuId")
    public String loginStuId(HttpSession httpSession) {

        if(httpSession.getAttribute("loginStuId") != null) return httpSession.getAttribute("loginStuId").toString();
        return null;

    }

    @ModelAttribute("loginName")
    public String loginName(HttpSession httpSession) {
        if(httpSession.getAttribute("loginStuId") != null) return httpSession.getAttribute("loginName").toString();
        return null;
    }
}
