package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/item/list")
    public String list(Model model, HttpSession httpSession) {
        List<Item> items = itemService.findAllItems();
        model.addAttribute("items", items);


        String stuId = (String) httpSession.getAttribute("stuId");
        model.addAttribute("stuId", stuId);

        return "item/list";
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
