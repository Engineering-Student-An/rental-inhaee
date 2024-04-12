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
    public String home(Model model){

//        String loginStuId = SecurityContextHolder.getContext().getAuthentication().getName();
//        Student loginStudent = studentService.findStudent(loginStuId);

        model.addAttribute("ann", ruleRepository.findAnnouncementById(1L));

        model.addAttribute("recentBoard", boardService.findRecentBoard());
        model.addAttribute("hotBoard", boardService.findHotBoard());


        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        return (boolean) model.getAttribute("isMobile") ? "mobile/home/home" : "home/home";

    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("loginRequest", new LoginRequest());
        
        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        String referrer = request.getHeader("Referer");
        if(!referrer.contains("/error")) {
            // 이전 페이지의 URL을 세션에 "prevPage"라는 이름으로 저장
            session.setAttribute("prevPage", referrer != null ? referrer : "/");
        }

        model.addAttribute("referrer", referrer);
        return "home/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {

        model.addAttribute("errorMessage", "일치하는 회원 정보가 없습니다.\n로그인 정보를 확인해주세요.");
        model.addAttribute("nextUrl", "/login");

        return "error/errorMessage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(true);
        if (httpSession != null) {
            httpSession.invalidate();
        }

        return "redirect:/";
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

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/joinEmail");
        session.setAttribute("verifyCode", authCode);

        model.addAttribute("isSent", true);


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

    @GetMapping("/findPassword")
    public String findPasswordForm(Model model) {

        model.addAttribute("stuId", "");
        model.addAttribute("email", "");

        findPasswordModel(model, false, false, false);
        return "home/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("stuId") String stuId, Model model) {

        Student student = studentService.findStudent(stuId);

        if(student == null) {
            model.addAttribute("errorMessage", "해당 학번으로 가입된 계정이 없습니다.\n회원가입을 진행해주세요!\n'확인'을 누르면 회원가입 페이지로 이동합니다.");
            model.addAttribute("yesUrl", "/join");
            model.addAttribute("noUrl", "/findPassword");
            return "error/yesOrNoMessage";
        }

        model.addAttribute("stuId", stuId);
        model.addAttribute("email", student.getEmail());
        findPasswordModel(model, true, false, false);
        return "home/findPassword";

    }

    @PostMapping("/findPassword/{stuId}/email")
    public String sendEmail(@PathVariable("stuId") String stuId, @RequestParam("email") String email, Model model, HttpSession session) {

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/passwordEditEmail");

        session.setAttribute("verifyCode", authCode);


        model.addAttribute("email", studentService.findStudent(stuId).getEmail());

        findPasswordModel(model, true, true, false);

        return "home/findPassword";
    }

    @PostMapping("/findPassword/{stuId}/email/verify")
    public String verifyEmail(@PathVariable("stuId") String stuId, @RequestParam("code") String code, HttpSession session, Model model) {

        String verifyCode = (String) session.getAttribute("verifyCode");
        model.addAttribute("email", studentService.findStudent(stuId).getEmail());

        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/findPassword");
            return "error/errorMessage";
        }

        findPasswordModel(model, true, true, true);

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
        model.addAttribute("nextUrl", "/login");
        return "error/errorMessage";
    }

    private static void findPasswordModel(Model model, boolean isStuIdInput, boolean isEmailSent, boolean isEmailChecked) {
        model.addAttribute("isStuIdInput", isStuIdInput);
        model.addAttribute("isEmailSent", isEmailSent);
        model.addAttribute("isEmailChecked", isEmailChecked);
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }

    @ModelAttribute("isMobile")
    public boolean isMobile(HttpSession session) {
        if(session.getAttribute("isMobile") != null) {
            return (boolean) session.getAttribute("isMobile");
        }
        return false;
    }
}