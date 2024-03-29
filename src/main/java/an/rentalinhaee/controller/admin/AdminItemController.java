package an.rentalinhaee.controller.admin;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.domain.dto.ItemForm;
import an.rentalinhaee.repository.ItemSearch;
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
@RequestMapping("/admin")
public class AdminItemController {

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
        return "admin/item/list";
    }

    @GetMapping("/item/new")
    public String createItemForm(Model model) {

        model.addAttribute("form", new ItemForm());
        return "admin/item/createItemForm";
    }

    @PostMapping("/item/new")
    public String createItem(@ModelAttribute("form") ItemForm form, BindingResult bindingResult) {

        if(form.getAllStockQuantity() < 0){
            bindingResult.addError(new FieldError("form", "allStockQuantity", "0 이상의 재고 수량을 입력해주세요!"));
        }
        if(form.getCategory().isEmpty()) {
            bindingResult.addError(new FieldError("form", "category", "카테고리를 입력해주세요!"));
        }
        if(bindingResult.hasErrors()) return "admin/item/createItemForm";

        Item item = new Item();
        item.setName(form.getName());
        item.setCategory(form.getCategory());
        item.setAllStockQuantity(form.getAllStockQuantity());
        item.setStockQuantity(form.getAllStockQuantity());

        itemService.saveItem(item);
        return "redirect:/admin/item/list";
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
        return "admin/item/updateItemForm";
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
        if(bindingResult.hasErrors()) return "admin/item/updateItemForm";
        itemService.updateItem(itemId, form.getName(), form.getAllStockQuantity(), form.getCategory());

        return "redirect:/admin/item/list";
    }

    @GetMapping("/item/{itemId}/delete")
    public String deleteItem(@PathVariable(value = "itemId") Long itemId, Model model){
        Item item = itemService.findOneItem(itemId);
        if(item.getStockQuantity() != item.getAllStockQuantity()){
            model.addAttribute("errorMessage", "현재 대여중인 물품은 삭제할 수 없습니다!");
            model.addAttribute("nextUrl", "/admin/item/list");
            return "error/errorMessage";
        }

        itemService.deleteItem(itemId);
        return "redirect:/admin/item/list";
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
