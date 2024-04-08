package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ChangePasswordRequest;
import an.rentalinhaee.domain.dto.EmailForm;
import an.rentalinhaee.service.EmailService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/changeInfo")
    public String changeInfo(Model model) {


//        Student student = (Student) model.getAttribute("loginStudent");
//
//        System.out.println("student.getEmail() = " + student.getEmail());

        model.addAttribute("isPasswordChecked", false);
        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", false);

        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/student/changeInfo";
        }

        return "student/changeInfo";
    }

    @PostMapping("/changeInfo/verify/password")
    public String changePassword(@RequestParam("password") String password,
                                 Model model){

        Student student = (Student) model.getAttribute("loginStudent");

        if(!studentService.passwordCheck(student.getStuId(), password)) {
            model.addAttribute("errorMessage", "현재 비밀번호와 동일하지 않습니다!");
            model.addAttribute("nextUrl", "/changeInfo");

            return "error/errorMessage";
        }

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", false);
        model.addAttribute("isEmailChecked", false);

        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/student/changeInfo";
        }
        return "student/changeInfo";
    }

    private final EmailService emailService;
    @PostMapping("/changeInfo/verify/email")
    public String verifyEmail(@RequestParam("email") String email, HttpSession httpSession, Model model) {

//        Student student = (Student) model.getAttribute("loginStudent");

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", false);

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/passwordEmail");

        httpSession.setAttribute("verifyCode", authCode);

        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/student/changeInfo";
        }
        return "student/changeInfo";
    }

    @PostMapping("/changeInfo/verify/email/code")
    public String verifyEmailCode(@RequestParam("code") String code, HttpSession httpSession, Model model) {

        String verifyCode = (String) httpSession.getAttribute("verifyCode");
        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다!");
            model.addAttribute("nextUrl", "/changeInfo");
            return "error/errorMessage";
        }

        Student student = (Student) model.getAttribute("loginStudent");

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", true);

        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/student/changeInfo";
        }
        return "student/changeInfo";
    }

    @GetMapping("/changeInfo/changePassword")
    public String changePasswordNext(Model model) {

        model.addAttribute("request", new ChangePasswordRequest());

        model.addAttribute("isMobile", model.getAttribute("isMobile"));
        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/student/changePassword";
        }
        return "student/changePassword";
    }

    @PostMapping("/changeInfo/changePassword")
    public String changePasswordForm(@Valid @ModelAttribute("request") ChangePasswordRequest request,
                                     BindingResult bindingResult, Model model) {

        Student student = (Student) model.getAttribute("loginStudent");

        if(studentService.passwordCheck(student.getStuId(), request.getChangePassword())){
            bindingResult.addError(new FieldError("request",
                    "changePassword", "현재 비밀번호와 동일하게 변경 불가합니다!"));
        } else if (!request.getChangePassword().equals(request.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호가 동일하지 않습니다!"));
        }
        if (bindingResult.hasErrors()) {
            if((boolean) model.getAttribute("isMobile")) {
                return "mobile/student/changePassword";
            }
            return "student/changePassword";
        }

        String stuId = student.getStuId();
        studentService.changePassword(stuId, request);

        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다!");
        model.addAttribute("nextUrl", "/changeInfo");
        return "error/errorMessage";


    }

    @GetMapping("/changeInfo/email")
    public String changeEmail(Model model) {

        model.addAttribute("emailForm", new EmailForm());
        model.addAttribute("isSent", false);
        return "student/changeEmail";
    }

    @PostMapping("/changeInfo/email")
    public String changeEmailSend(@ModelAttribute EmailForm emailForm,
                                  BindingResult bindingResult, Model model, HttpSession session) {

        String email = emailForm.getEmail();

        if(email.equals(((Student) model.getAttribute("loginStudent")).getEmail())) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "현재 이메일 주소와 동일합니다!"));
        }

        if(email.isEmpty()) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소를 입력해주세요!"));
        } else if(!email.contains("@") || !email.contains(".")) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소가 올바르지 않습니다!"));
        }

        if(bindingResult.hasErrors()) {
            return "student/changeEmail";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/emailChangeEmail");

        session.setAttribute("verifyCode", authCode);
        model.addAttribute("isSent", true);

        return "student/changeEmail";
    }

    @PostMapping("/changeInfo/email/verify")
    public String verifyCodeCheck(@RequestParam("email") String email,
                                  @RequestParam("code") String code,
                                  HttpSession session, Model model) {


        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");

        // 인증 문자와 동일한지 검증
        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/changeInfo/email");
            return "error/errorMessage";
        }

        Student loginStudent = (Student) model.getAttribute("loginStudent");
        loginStudent.editEmail(email);

        model.addAttribute("errorMessage", "이메일이 변경되었습니다!");
        model.addAttribute("nextUrl", "/changeInfo");
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
