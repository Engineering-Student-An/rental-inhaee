package an.rentalinhaee.controller.admin;

import an.rentalinhaee.domain.Rule;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ChangeRuleRequest;
import an.rentalinhaee.repository.RuleRepository;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.RuleService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHomeController {

    private final BoardService boardService;
    private final RuleRepository ruleRepository;
    private final RuleService ruleService;

    @GetMapping({"/", ""})
    public String adminHome(HttpSession session, Model model) {


        String loginStuId = SecurityContextHolder.getContext().getAuthentication().getName();
        Student loginStudent = studentService.findStudent(loginStuId);


        if (loginStudent != null) {
            model.addAttribute("ann", ruleRepository.findAnnouncementById(1L));

            model.addAttribute("recentBoard", boardService.findRecentBoard());

            session.setAttribute("loginStudent", session.getAttribute("loginStudent"));
        }

        return "admin/home";
    }

    @GetMapping("/editAnnouncement")
    public String editAnnouncementForm(Model model){
        Rule ann = ruleRepository.findAnnouncementById(1L);

        ChangeRuleRequest request = new ChangeRuleRequest();
        request.setTitle(ann.getTitle());

        String replace = ann.getContent().replace("<br>", "");
        request.setContent(replace);


        model.addAttribute("request", request);
        return "admin/editAnnouncement";
    }

    @PostMapping("/editAnnouncement")
    public String editAnnouncement(@ModelAttribute("request") ChangeRuleRequest request, BindingResult bindingResult, Model model){

        if(request.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("request", "title", "제목을 입력하세요"));
        }
        if(request.getContent().isEmpty()){
            bindingResult.addError(new FieldError("request", "content", "내용을 입력하세요"));
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("request", request);
            return "admin/editAnnouncement";
        }

        String replace = request.getContent().replace("\n", "<br>");

        ruleService.updateAnnouncement(request.getTitle(), replace);

        return "redirect:/admin/";
    }

    private final StudentService studentService;

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
