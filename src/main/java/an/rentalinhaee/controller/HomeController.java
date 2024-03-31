package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.*;
import an.rentalinhaee.repository.RuleRepository;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.EmailService;
import an.rentalinhaee.service.FeeStudentService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class HomeController {

    private final StudentService studentService;
    private final RuleRepository ruleRepository;
    private final BoardService boardService;
    private final FeeStudentService feeStudentService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, /*@SessionAttribute(name = "loginStuId", required = false) String stuId,*/ HttpSession session){


        String loginStuId = SecurityContextHolder.getContext().getAuthentication().getName();
        Student loginStudent = studentService.findStudent(loginStuId);


//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
//        GrantedAuthority auth = iter.next();
//        String role = auth.getAuthority();

        if (loginStudent != null) {
            model.addAttribute("ann", ruleRepository.findAnnouncementById(1L));

            model.addAttribute("recentBoard", boardService.findRecentBoard());
            model.addAttribute("hotBoard", boardService.findHotBoard());
        }
        return "home/home";
    }

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
        model.addAttribute("verifyCodeForm", new VerifyCodeForm());
        model.addAttribute("isSent", false);

        return "home/join/verifyEmail";
    }

    @PostMapping("/join/verify")
    public String sendEmail(@ModelAttribute EmailForm emailForm,
                            BindingResult bindingResult,
                            @ModelAttribute VerifyCodeForm verifyCodeForm,
                            HttpSession session, Model model) {

        String email = emailForm.getEmail();
        if(email.isEmpty()) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소를 입력해주세요!"));
        } else if(!email.contains("@") || !email.contains(".")) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소가 올바르지 않습니다!"));
        }

        session.setAttribute("joinRequest", (JoinRequest) session.getAttribute("joinRequest"));
        if(bindingResult.hasErrors()) {
            return "home/join/verifyEmail";
        }

        String verifyCode = emailService.sendEmail(emailForm.getEmail(), "email/joinEmail");
        model.addAttribute("isSent", true);


        session.setAttribute("verifyCode", verifyCode);
        return "home/join/verifyEmail";
    }

    @PostMapping("/join/verify/code")
    public String verifyCodeCheck(@ModelAttribute("verifyCodeForm") VerifyCodeForm verifyCodeForm,
                                  @RequestParam("email") String email,
                                  HttpSession session, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");

        // 인증 문자와 동일한지 검증
        if(!verifyCodeForm.getCode().equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/join/verify");
            return "error/errorMessage";
        }

        // 인증 문자 동일하면 회원가입
        JoinRequest joinRequest = (JoinRequest) session.getAttribute("joinRequest");
        System.out.println("email = " + email);
        studentService.join(joinRequest, email);

        model.addAttribute("errorMessage", "회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");
        model.addAttribute("nextUrl", "/login");
        return "error/errorMessage";

    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());

        return "home/login";
    }

//    @PostMapping("/login")
//    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
//                        HttpServletRequest httpServletRequest, Model model) {
//
//        Student student = studentService.login(loginRequest);
//
//        if (student == null) {
//            bindingResult.reject("loginFail", "학번 또는 비밀번호가 틀립니다!");
//        }
//
//        if (bindingResult.hasErrors()) {
//            return "home/login";
//        }
//
//        httpServletRequest.getSession().invalidate();
//        HttpSession httpSession = httpServletRequest.getSession(true);
//
//        httpSession.setAttribute("loginStuId", student.getStuId());
//        httpSession.setAttribute("loginName", student.getName());
//        httpSession.setMaxInactiveInterval(60 * 60);    // 로그인 기간 1시간 설정
//
//
//
//        if(student.getRole() == StudentRole.ADMIN) {
//            return "redirect:/admin";
//        }
//        return "redirect:/";
//    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(true);
        if (httpSession != null) {
            httpSession.invalidate();
        }

        return "redirect:/";
    }

    @GetMapping("/findPassword")
    public String findPasswordForm(Model model) {
        model.addAttribute("isStuIdInput", false);
        model.addAttribute("stuId", "");
        model.addAttribute("email", "");
        model.addAttribute("isEmailSent", false);
        model.addAttribute("isEmailChecked", false);
        return "home/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("stuId") String stuId, Model model) {
        System.out.println("stuId = " + stuId);
        model.addAttribute("isStuIdInput", true);
        model.addAttribute("stuId", stuId);
        model.addAttribute("email", studentService.findStudent(stuId).getEmail());
        model.addAttribute("isEmailSent", false);
        model.addAttribute("isEmailChecked", false);
        return "home/findPassword";

    }

    @PostMapping("/findPassword/{stuId}/email")
    public String sendEmail(@PathVariable("stuId") String stuId, @RequestParam("email") String email, Model model, HttpSession session) {

        String verifyCode = emailService.sendEmail(email, "email/passwordEditEmail");
        System.out.println("verifyCode = " + verifyCode);
        model.addAttribute("isStuIdInput", true);
        model.addAttribute("email", studentService.findStudent(stuId).getEmail());
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", false);
        session.setAttribute("verifyCode", verifyCode);
        return "home/findPassword";
    }

    @PostMapping("/findPassword/{stuId}/email/verify")
    public String verifyEmail(@PathVariable("stuId") String stuId, @RequestParam("code") String code, HttpSession session, Model model) {

        String verifyCode = (String) session.getAttribute("verifyCode");
        model.addAttribute("isStuIdInput", true);
        model.addAttribute("email", studentService.findStudent(stuId).getEmail());
        model.addAttribute("isEmailSent", true);


        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/findPassword");
            return "error/errorMessage";
        }

        model.addAttribute("isEmailChecked", true);
        return "home/findPassword";
    }



    @GetMapping("/findPassword/{stuId}/reset")
    public String resetPasswordForm(@PathVariable("stuId") String stuId, Model model) {

        model.addAttribute("request", new ChangePasswordRequest());
        return "home/resetPassword";
    }

    @PostMapping("/findPassword/{stuId}/reset")
    public String resetPassword(@PathVariable("stuId") String stuId,
                                @Valid @ModelAttribute("request") ChangePasswordRequest request,
                                BindingResult bindingResult, Model model) {

        Student student = studentService.findStudent(stuId);

        if (!request.getChangePassword().equals(request.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호가 동일하지 않습니다!"));
        }
        if (bindingResult.hasErrors()) {
            return "home/resetPassword";
        }

        studentService.changePassword(stuId, request);

        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다!\n변경된 비밀번호로 로그인 해주세요.");
        model.addAttribute("nextUrl", "/");
        return "error/errorMessage";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
