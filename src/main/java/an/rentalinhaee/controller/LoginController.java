package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.StudentRole;
import an.rentalinhaee.domain.dto.ChangePasswordRequest;
import an.rentalinhaee.domain.dto.JoinRequest;
import an.rentalinhaee.domain.dto.LoginRequest;
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

import static an.rentalinhaee.ReadExcel.readExcel;


@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final StudentService studentService;

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinRequest", new JoinRequest());
        return "home/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest,
                       BindingResult bindingResult, Model model) {
        if (studentService.checkStuIdDuplicate(joinRequest.getStuId())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "stuId", "이미 가입되어 있습니다!"));
        }

        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        List<String> readRequest = readExcel(joinRequest.getStuId());

        if(readRequest.get(0).isEmpty()){
             bindingResult.addError(new FieldError("joinRequest",
                     "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        }else if(!readRequest.get(1).equals(joinRequest.getName())){
            bindingResult.addError(new FieldError("joinRequest",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }


        if (bindingResult.hasErrors()) {
            return "home/join";
        }

        studentService.join(joinRequest);
        return "redirect:/";
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
    public String changePassword(Model model) {

        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
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
