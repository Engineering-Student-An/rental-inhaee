package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RentalSearch;
import an.rentalinhaee.service.ItemService;
import an.rentalinhaee.service.RentalService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final StudentService studentService;
    private final ItemService itemService;

    @GetMapping("/rental")
    public String createForm(Model model) {

        model.addAttribute("categories", itemService.findCategories());
        model.addAttribute("items", itemService.findAllItems());

        return "rental/rentalForm";

    }

    @PostMapping("/rental")
    public String rental(@RequestParam("category") String category, Model model) {


        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", itemService.findCategories());

        // 카테고리에 속한 아이템 추가
        model.addAttribute("itemsByCategory", itemService.findItemsByCategory(category));

        return "rental/rentalForm";
    }

    @PostMapping("/rental/complete")
    public String rentalComplete(@RequestParam("itemId") Long itemId, Model model) {

        String stuId = (String) model.getAttribute("loginStuId");
        Long studentId = studentService.findStudent(stuId).getId();

        if(rentalService.existsByStudentIdAndItemId(studentId, itemId)){
            model.addAttribute("errorMessage", "현재 대여중인 물품은 추가로 대여할 수 없습니다!\n반납 후 대여하세요!");
            model.addAttribute("nextUrl", "/rental");
            return "error/errorMessage";
        }
        rentalService.rental(stuId, itemId);

        model.addAttribute("errorMessage", "물품 대여 신청을 완료했습니다.\n" + LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "까지 반납을 완료해주세요." +
                "\n그 이후에 반납 시 연체료가 부가됩니다!");
        model.addAttribute("nextUrl", "/");
        return "error/errorMessage";
    }

    @GetMapping("/rental/findOne")
    public String rentalList(@RequestParam(required = false, defaultValue = "1", value = "page") int page,
                             @ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                             Model model) {

        Student student = studentService.findStudent((String) model.getAttribute("loginStuId"));

        rentalSearch.setStuId(student.getStuId());

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("status").and(Sort.by("rentalDate")));
        Page<Rental> rentalList;
        if(rentalSearch.getRentalStatus() == null){
            rentalList = rentalService.findMyRentalList(student.getId(), pageRequest);
        } else{
            rentalList = rentalService.findRentals(rentalSearch, pageRequest);
        }

        model.addAttribute("myRentalList", rentalList);

        return "rental/findOne";
    }

    @PostMapping("/rental/findOne/{rentalId}/finish")
    public String finishRental(@PathVariable("rentalId") Long rentalId, Model model) {
        if(ChronoUnit.DAYS.between(rentalService.findRentalByRentalId(rentalId).getRentalDate(), LocalDate.now()) > 3){
            model.addAttribute("errorMessage", "연체중인 물품의 반납은 관리자를 통해 가능합니다.");
            model.addAttribute("nextUrl", "/rental/findOne");
            model.addAttribute("newUrl", "https://pf.kakao.com/_CxjxlxiM");
            return "error/errorMessageNewTab";
        }

        model.addAttribute("errorMessage", "해당 물품을 반납 처리 하시겠습니까?\n반납이 이루어지지 않았을 경우, 불이익이 주어질 수 있습니다!");

        model.addAttribute("yesUrl","/rental/findOne/" + rentalId + "/finish/confirm");
        model.addAttribute("noUrl","/rental/findOne");
        return "error/yesOrNoMessage";
    }

    @GetMapping("/rental/findOne/{rentalId}/finish/confirm")
    public String finishRentalConfirm(@PathVariable("rentalId") Long rentalId) {
        rentalService.finishRental(rentalId);
        return "redirect:/rental/findOne";
    }

    @PostMapping("/rental/finish/{rentalId}")
    public String adminFinishRental(@PathVariable("rentalId") Long rentalId, @RequestParam String stuId, RedirectAttributes redirectAttributes) {
        rentalService.finishRental(rentalId);

        redirectAttributes.addAttribute("stuId", stuId);
        return "redirect:/student/{stuId}/find";
    }

    @GetMapping("/rental/list")
    public String allList(@ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model){

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("status").descending());

        Page<Rental> rentalByStuIdStatusItem = rentalService.findRentalByStuId_Status_Item(rentalSearch, pageRequest);
        model.addAttribute("rentals", rentalByStuIdStatusItem);

        return "rental/list";
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
