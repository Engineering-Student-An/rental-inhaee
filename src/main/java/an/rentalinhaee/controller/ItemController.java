package an.rentalinhaee.controller;

import an.rentalinhaee.domain.ItemRequest;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ItemRequestForm;
import an.rentalinhaee.repository.ItemSearch;
import an.rentalinhaee.service.ItemRequestService;
import an.rentalinhaee.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @GetMapping("/item/list")
    public String list(@ModelAttribute("itemSearch") ItemSearch itemSearch,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        PageRequest pageRequest;

        // 학번, 이름 모두 포함하는 학생들 추가
        if (itemSearch.getCategory() != null && itemSearch.getName() != null) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
            model.addAttribute("items", itemService.findItemsByCategoryAndName(itemSearch.getCategory(), itemSearch.getName(), pageRequest));
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
            model.addAttribute("items", itemService.findAllItems(pageRequest));
        }

        model.addAttribute("itemSearch", itemSearch);
        return "item/list";
    }

    @GetMapping("/item/request/list")
    public String requestList(Model model) {

        model.addAttribute("requestList", itemRequestService.findAll());

        return "item/requestList";
    }

    @GetMapping("/item/request")
    public String requestForm(Model model) {
        ItemRequestForm itemRequestForm = new ItemRequestForm();

        model.addAttribute("itemRequestForm", itemRequestForm);
        model.addAttribute("itemList", itemService.findAllItems());
        return "item/request";
    }

    @PostMapping("/item/request")
    public String request(@ModelAttribute ItemRequestForm itemRequestForm, BindingResult bindingResult, Model model) {
        if(itemRequestForm.getItemName().isEmpty()) {
            bindingResult.addError(new FieldError
                    ("itemRequestForm", "itemName", "물품을 선택하세요"));
        }
        if(itemRequestForm.getContent().isEmpty()) {
            bindingResult.addError(new FieldError
                    ("itemRequestForm", "content", "요청 사항을 선택하세요"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemRequestForm", itemRequestForm);
            return "item/request";
        }

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        ItemRequest itemRequest = new ItemRequest(loginStudent.getStuId(), loginStudent.getName(), itemRequestForm.getItemName(), itemRequestForm.getContent(), false);

        itemRequestService.save(itemRequest);
        return "redirect:/item/request/list";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
