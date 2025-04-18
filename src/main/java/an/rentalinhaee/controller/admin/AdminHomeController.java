package an.rentalinhaee.controller.admin;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RuleRepository;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHomeController {

    private final BoardService boardService;
    private final RuleRepository ruleRepository;
    private final StudentService studentService;

    @GetMapping({"/", ""})
    public String adminHome(HttpSession session) {


        String loginStuId = SecurityContextHolder.getContext().getAuthentication().getName();
        Student loginStudent = studentService.findStudent(loginStuId);


        if (loginStudent != null) {

            session.setAttribute("loginStudent", session.getAttribute("loginStudent"));
        }

        return "admin/home";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
