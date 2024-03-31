package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.ItemSearch;
import an.rentalinhaee.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

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


    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
