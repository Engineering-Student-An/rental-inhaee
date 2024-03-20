package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Rule;
import an.rentalinhaee.domain.dto.ChangeRuleRequest;
import an.rentalinhaee.repository.RuleRepository;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.RuleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BoardService boardService;
    private final RuleRepository ruleRepository;
    private final RuleService ruleService;

    @GetMapping({"/", ""})
    public String adminHome(Model model, @SessionAttribute(name = "loginStuId", required = false) String stuId) {


        Rule ann = ruleRepository.findAnnouncementById(1L);
        model.addAttribute("ann", ann);

        model.addAttribute("recentBoard", boardService.findRecentBoard());

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
