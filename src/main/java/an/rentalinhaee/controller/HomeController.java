package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RuleRepository;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class HomeController {

    private final StudentService studentService;
    private final RuleRepository ruleRepository;
    private final BoardService boardService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, @SessionAttribute(name = "loginStuId", required = false) String stuId){

        Student loginStudent = studentService.findStudent(stuId);

        if (loginStudent != null) {
            model.addAttribute("ann", ruleRepository.findAnnouncementById(1L));

            model.addAttribute("recentBoard", boardService.findRecentBoard());
            model.addAttribute("hotBoard", boardService.findHotBoard());
        }
        return "home/home";
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
