package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ChangePasswordRequest;
import an.rentalinhaee.domain.dto.EmailForm;
import an.rentalinhaee.repository.RentalSearch;
import an.rentalinhaee.repository.StudentSearch;
import an.rentalinhaee.service.EmailService;
import an.rentalinhaee.service.FeeStudentService;
import an.rentalinhaee.service.RentalService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final RentalService rentalService;

    @GetMapping("/changeInfo")
    public String changeInfo(Model model, HttpServletRequest httpServletRequest) {

        HttpSession httpSession = httpServletRequest.getSession(true);

        model.addAttribute("phoneNumber", studentService.findStudent(loginStuId(httpSession)).getPhoneNumber());
        model.addAttribute("email", studentService.findStudent(loginStuId(httpSession)).getEmail());

        model.addAttribute("isPasswordChecked", false);
        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", false);

        return "student/changeInfo";
    }

    @PostMapping("/changeInfo/verify/password")
    public String changePassword(@RequestParam("password") String password,
                                 HttpServletRequest httpServletRequest,
                                 Model model){

        HttpSession httpSession = httpServletRequest.getSession(true);

        String stuId = (String) model.getAttribute("loginStuId");
        String currentPassword = studentService.findStudent(stuId).getPassword();

        if(!password.equals(currentPassword)) {
            model.addAttribute("errorMessage", "현재 비밀번호와 동일하지 않습니다!");
            model.addAttribute("nextUrl", "/changeInfo");

            return "error/errorMessage";
        }

        model.addAttribute("phoneNumber", studentService.findStudent(loginStuId(httpSession)).getPhoneNumber());
        model.addAttribute("email", studentService.findStudent(loginStuId(httpSession)).getEmail());

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", false);
        model.addAttribute("isEmailChecked", false);

        return "student/changeInfo";
    }

    private final EmailService emailService;
    @PostMapping("/changeInfo/verify/email")
    public String verifyEmail(@RequestParam("email") String email, HttpSession httpSession, Model model) {


        model.addAttribute("phoneNumber", studentService.findStudent(loginStuId(httpSession)).getPhoneNumber());
        model.addAttribute("email", studentService.findStudent(loginStuId(httpSession)).getEmail());

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", false);

        String verifyCode = emailService.sendEmail(email, "email/passwordEmail");

        httpSession.setAttribute("verifyCode", verifyCode);

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


        model.addAttribute("phoneNumber", studentService.findStudent(loginStuId(httpSession)).getPhoneNumber());
        model.addAttribute("email", studentService.findStudent(loginStuId(httpSession)).getEmail());

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", true);
        return "student/changeInfo";
    }

    @GetMapping("/changeInfo/changePassword")
    public String changePasswordNext(Model model) {

        model.addAttribute("request", new ChangePasswordRequest());
        return "student/changePassword";
    }

    @PostMapping("/changeInfo/changePassword")
    public String changePasswordForm(@Valid @ModelAttribute("request") ChangePasswordRequest request, BindingResult bindingResult,
                                     HttpSession httpSession, Model model) {

        Student student = studentService.findStudent((String) httpSession.getAttribute("loginStuId"));
        String password = student.getPassword();

        if(request.getChangePassword().equals(password)){
            bindingResult.addError(new FieldError("request",
                    "changePassword", "현재 비밀번호와 동일하게 변경 불가합니다!"));
        } else if (!request.getChangePassword().equals(request.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
                    "changePasswordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (bindingResult.hasErrors()) {
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

        if(email.equals(studentService.findStudent((String) model.getAttribute("loginStuId")).getEmail())) {
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

        String verifyCode = emailService.sendEmail(emailForm.getEmail(), "email/emailChangeEmail");
        model.addAttribute("isSent", true);

        session.setAttribute("verifyCode", verifyCode);
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

        studentService.changeEmail((String) model.getAttribute("loginStuId"), email);

        model.addAttribute("errorMessage", "이메일이 변경되었습니다!");
        model.addAttribute("nextUrl", "/changeInfo");
        return "error/errorMessage";

    }

    @GetMapping("/student/list")
    public String list(@ModelAttribute("studentSearch") StudentSearch studentSearch,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                       Model model) {

        PageRequest pageRequest;

        // 학번, 이름 모두 포함하는 학생들 추가
        if (studentSearch.getStuId() != null && studentSearch.getName() != null) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            model.addAttribute("students", studentService.findStudentContainingStuIdAndName(studentSearch.getStuId(), studentSearch.getName(), pageRequest));
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            model.addAttribute("students", studentService.findAllStudent(pageRequest));
        }
        model.addAttribute("studentSearch", studentSearch);
        return"student/list";
    }

    @GetMapping("/student/{stuId}/find")
    public String findOneStudent(@PathVariable("stuId") String stuId, Model model,
                                 @ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                                 @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                                 HttpServletRequest httpServletRequest) {

        Student student = studentService.findStudent(stuId);
        if(rentalSearch.getStuId()==null) rentalSearch.setStuId(stuId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("status").and(Sort.by("rentalDate")));
        Page<Rental> rentalList;
        if(rentalSearch.getRentalStatus() == null){
            rentalList = rentalService.findMyRentalList(student.getId(), pageRequest);
        } else{
            rentalList = rentalService.findRentals(rentalSearch, pageRequest);
        }

        model.addAttribute("student", student);
        model.addAttribute("rentalList", rentalList);
        HttpSession httpSession = httpServletRequest.getSession(true);
        model.addAttribute("adminPassword", studentService.findStudent(loginStuId(httpSession)).getPassword());
        return "student/studentInfo";
    }

    private final FeeStudentService feeStudentService;


    @GetMapping("/student/{stuId}/delete")
    public String deleteStudent(@PathVariable("stuId") String stuId, Model model) {
        if(studentService.delete(stuId)) {
            model.addAttribute("errorMessage", "계정 삭제가 완료되었습니다.\n대여 정보, 게시글, 댓글이 모두 삭제되었습니다.");
            model.addAttribute("nextUrl", "/student/list");
        } else {
            model.addAttribute("errorMessage", "대여중인 물품을 모두 반납하고 계정 삭제를 진행해주세요!");
            model.addAttribute("nextUrl", "/student/" + stuId + "/find");

        }
        return "error/errorMessage";

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
