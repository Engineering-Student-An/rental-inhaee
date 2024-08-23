package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Proposal;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ProposalForm;
import an.rentalinhaee.service.ProposalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping("/proposal/list")
    public String proposalList(Model model,
                               @RequestParam(required = false, value = "proposalPage", defaultValue = "1") int proposalPage) {

        PageRequest noticePageRequest = PageRequest.of(proposalPage - 1, 10, Sort.by("writeTime").descending());
        Page<Proposal> proposals = proposalService.findAll(noticePageRequest);
        model.addAttribute("proposals", proposals);

        model.addAttribute("proposalPage", proposalPage);

        return "proposal/list";
    }

    @GetMapping("/proposal/new")
    public String createProposalForm(Model model) {

        model.addAttribute("proposalForm", new ProposalForm());

        return "proposal/createProposalForm";
    }

    @PostMapping("/proposal/new")
    public String createProposal(@ModelAttribute ProposalForm proposalForm, BindingResult bindingResult, Model model) {

        if (proposalForm.getTitle().isEmpty()) {
            bindingResult.addError(new FieldError("proposalForm", "title", "제목을 입력하세요"));
        }
        if (proposalForm.getTitle().length() > 20) {
            bindingResult.addError(new FieldError("proposalForm", "title", "20자 이내로 입력해주세요"));
        }
        if (proposalForm.getContent().isEmpty()) {
            bindingResult.addError(new FieldError("proposalForm", "content", "내용을 입력하세요"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("proposalForm", proposalForm);
            return "proposal/createProposalForm";
        }


        Student student = (Student) model.getAttribute("loginStudent");
        Proposal proposal = new Proposal(student.getStuId(), student.getName(),
                proposalForm.getTitle(), proposalForm.getContent(), LocalDateTime.now(), proposalForm.isSecret());


        proposalService.saveProposal(proposal);
        return "redirect:/proposal/list";
    }

    @GetMapping("/proposal/{id}")
    public String showOneProposal(@PathVariable("id") Long id, Model model) {

        Proposal proposal = proposalService.findOne(id);

        if(proposal.isSecret()){
            model.addAttribute("isSecret", true);
        } else {
            model.addAttribute("isSecret", false);
        }

        model.addAttribute("proposal", proposal);

        return "proposal/showOne";

    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
