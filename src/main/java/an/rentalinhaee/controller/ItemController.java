package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.domain.dto.ItemForm;
import an.rentalinhaee.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/item/new")
    public String createItemForm(Model model) {

        model.addAttribute("form", new ItemForm());
        return "item/createItemForm";
    }

    @PostMapping(value = "/item/new")
    public String createItem(@ModelAttribute("form") ItemForm form, BindingResult bindingResult) {

        if(form.getAllStockQuantity() < 0){
            bindingResult.addError(new FieldError("form", "allStockQuantity", "0 이상의 재고 수량을 입력해주세요!"));
        }
        if(form.getCategory().isEmpty()) {
            bindingResult.addError(new FieldError("form", "category", "카테고리를 입력해주세요!"));
        }
        if(bindingResult.hasErrors()) return "item/createItemForm";

        Item item = new Item();
        item.setName(form.getName());
        item.setCategory(form.getCategory());
        item.setAllStockQuantity(form.getAllStockQuantity());
        item.setStockQuantity(form.getAllStockQuantity());

        itemService.saveItem(item);
        return "redirect:/item/list";
    }

    @GetMapping("/item/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        Item item = itemService.findOneItem(itemId);

        ItemForm form = new ItemForm();

        form.setId(item.getId());
        form.setName(item.getName());
        form.setAllStockQuantity(item.getAllStockQuantity());
        form.setIngStockQuantity(item.getAllStockQuantity() - item.getStockQuantity());
        form.setCategory(item.getCategory());

        model.addAttribute("form", form);
        return "item/updateItemForm";
    }

    @PostMapping("/item/{itemId}/edit")
    public String updateItem(@PathVariable(value = "itemId") Long itemId, @ModelAttribute("form") ItemForm form,
                             BindingResult bindingResult) {
        if(form.getCategory().isEmpty()) {
            bindingResult.addError(new FieldError("form", "category", "카테고리를 입력해주세요!"));
        }
        if(form.getAllStockQuantity() < 0){
            bindingResult.addError(new FieldError("form", "allStockQuantity", "0 이상의 재고 수량을 입력해주세요!"));
        } else if(form.getAllStockQuantity() < form.getIngStockQuantity()){
            bindingResult.addError(new FieldError("form", "allStockQuantity", "대여중인 수량 이상의 재고 수량을 입력해주세요!"));
        }
        if(bindingResult.hasErrors()) return "item/updateItemForm";
        itemService.updateItem(itemId, form.getName(), form.getAllStockQuantity(), form.getCategory());

        return "redirect:/item/list";
    }

    @GetMapping("/item/{itemId}/delete")
    public String deleteItem(@PathVariable(value = "itemId") Long itemId, Model model){
        Item item = itemService.findOneItem(itemId);
        if(item.getStockQuantity() != item.getAllStockQuantity()){
            model.addAttribute("errorMessage", "현재 대여중인 물품은 삭제할 수 없습니다!");
            model.addAttribute("nextUrl", "/item/list");
            return "error/errorMessage";
        }

        itemService.deleteItem(itemId);
        return "redirect:/item/list";
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
