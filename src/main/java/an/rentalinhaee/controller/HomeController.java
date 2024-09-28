package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.*;
import an.rentalinhaee.service.*;
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
    private final BoardService boardService;
    private final FeeStudentService feeStudentService;
    private final RentalService rentalService;
    private final InhaEEService inhaEEService;

    @GetMapping(value = {"", "/"})
    public String index(Model model){


//        model.addAttribute("ann", ruleRepository.findAnnouncementById(1L));
//
//
//        model.addAttribute("recentBoard", boardService.findRecentBoard());
//        model.addAttribute("hotBoard", boardService.findHotBoard());
//
//        model.addAttribute("hotItems", itemService.findHotItems());
//        model.addAttribute("recentNotice", boardService.findRecentNotice());

//        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        return "home/index";

    }


    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Student loginStudent = (Student) model.getAttribute("loginStudent");

        session.setAttribute("previousPage", "/home");

        List<Rental> myRentalINGList = null;

        if (loginStudent != null) {
            myRentalINGList = rentalService.findMyRentalINGList(loginStudent.getId());
        }
        model.addAttribute("rentalList", myRentalINGList);

        model.addAttribute("recentNotice", boardService.findRecentNotice());
        model.addAttribute("importantPosts", inhaEEService.importantPostParser());
        model.addAttribute("recentPosts", inhaEEService.recentPostParser());

        return "home/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        
        model.addAttribute("isMobile", model.getAttribute("isMobile"));

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

    @GetMapping("/findPassword/info")
    public String findPasswordForm(Model model) {
        model.addAttribute("infoForm", new InfoForm());

        return "home/findPassword";
    }

    @PostMapping("/findPassword/info")
    public String findPasswordInfo(@ModelAttribute("infoForm") InfoForm infoForm,
                                   BindingResult bindingResult, HttpSession session) {
        
        if(infoForm.getStuId() == null || infoForm.getStuId().isEmpty()) {
            bindingResult.addError(new FieldError("infoForm",
                    "stuId", "학번을 입력해주세요!"));
        }
        if(infoForm.getPhoneNumber() == null || infoForm.getPhoneNumber().isEmpty()) {
            bindingResult.addError(new FieldError("infoForm",
                    "phoneNumber", "전화번호를 입력해주세요!"));
        }

        Student findStudent = studentService.findStudent(infoForm.getStuId());
        if(findStudent == null) {
            bindingResult.addError(new FieldError("infoForm",
                    "stuId", "일치하는 학번의 계정이 없습니다. 회원가입 해 주세요!"));
        } else {
            if(!infoForm.getPhoneNumber().equals(findStudent.getPhoneNumber())) {
                bindingResult.addError(new FieldError("infoForm",
                        "phoneNumber", "등록된 비밀번호와 다릅니다. 확인 후 입력해주세요!"));
            }
        }

        if(bindingResult.hasErrors()) {
            return "home/findPassword";
        }

        session.setAttribute("findStudent", findStudent);
        return "redirect:/findPassword/email";
    }

    @GetMapping("/findPassword/email")
    public String findPasswordEmailForm(HttpSession session, Model model) {
        session.setAttribute("findStudent", session.getAttribute("findStudent"));
        model.addAttribute("emailForm", new EmailForm());
        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", false);
        return "home/findPassword_email";
    }

    @PostMapping("/findPassword/email")
    public String findPasswordEmail(@ModelAttribute EmailForm emailForm, Model model, HttpSession session) {
        Student findStudent = (Student) session.getAttribute("findStudent");

        if(!emailForm.getEmail().equals(findStudent.getEmail())) {
            model.addAttribute("errorMessage", "등록된 이메일과 동일하지 않습니다!");
            model.addAttribute("nextUrl", "/findPassword/email");

            return "error/errorMessage";
        }
        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("emailForm", emailForm);

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(emailForm.getEmail(), authCode, "email/passwordEmail");

        session.setAttribute("verifyCode", authCode);
        session.setAttribute("findStudent", findStudent);

        return "home/findPassword_email";
    }

    @PostMapping("/findPassword/email/verify")
    public String verifyEmailCode(@RequestParam("code") String code, HttpSession session, Model model) {

        String verifyCode = (String) session.getAttribute("verifyCode");

        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다!");
            model.addAttribute("nextUrl", "/findPassword/email");
            return "error/errorMessage";
        }

        session.setAttribute("findStudent", session.getAttribute("findStudent"));
        model.addAttribute("errorMessage", "인증 문자가 확인되었습니다.\n 비밀번호 재설정 페이지로 넘어갑니다!");
        model.addAttribute("nextUrl", "/findPassword/reset");
        return "error/errorMessage";

    }

    @GetMapping("/findPassword/reset")
    public String resetPasswordForm(HttpSession session, Model model) {
        model.addAttribute("request", new ChangePasswordRequest());
        session.setAttribute("findStudent", session.getAttribute("findStudent"));
        return "home/resetPassword";
    }

    @PostMapping("/findPassword/reset")
    public String resetPassword(@ModelAttribute("request") ChangePasswordRequest request, BindingResult bindingResult, HttpSession session, Model model) {

        System.out.println("request.getChangePassword() = " + request.getChangePassword());
        System.out.println("request = " + request.getChangePasswordCheck());
        System.out.println(("request.getChangePassword() == request.getChangePasswordCheck() = " + request.getChangePassword()).equals(request.getChangePasswordCheck()));

        if(request.getChangePassword().isEmpty()) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호를 입력해주세요!"));
        }
        if(request.getChangePasswordCheck().isEmpty()) {
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호를 한번 더 입력해주세요!"));
        }
        if(!request.getChangePassword().equals(request.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 일치하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호가 일치하지 않습니다!"));
        }

        if(bindingResult.hasErrors()) {
            return "home/resetPassword";
        }

        Student student = (Student) session.getAttribute("findStudent");
        studentService.changePassword(student.getStuId(), request);

        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다. 로그인을 진행해 주세요!");
        model.addAttribute("nextUrl", "/login");
        return "error/errorMessage";
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