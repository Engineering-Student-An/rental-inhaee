package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.domain.Rental;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final StudentService studentService;
    private final ItemService itemService;

    @GetMapping("/rental")
    public String createForm(Model model, HttpSession httpSession) {

        String stuId = (String) httpSession.getAttribute("loginStuId");
        model.addAttribute("stuId", stuId);

        List<Item> itemList = itemService.findAllItems();
        model.addAttribute("itemList", itemList);

        return "rental/rentalForm";
    }

    @PostMapping("/rental")
    public String rental(@RequestParam("itemId") Long itemId, Model model) {

        String stuId = (String) model.getAttribute("loginStuId");
        Long studentId = studentService.findStudent(stuId).getId();


        if(rentalService.existsByStudentIdAndItemId(studentId, itemId)){
            model.addAttribute("errorMessage", "현재 대여중인 물품은 추가로 대여할 수 없습니다!\n반납 후 대여하세요!");
            model.addAttribute("nextUrl", "/rental");
            return "error/errorMessage";
        }
        rentalService.rental(stuId, itemId);
        return "redirect:/";
    }

    @GetMapping("/rental/findOne")
    public String rentalList(@RequestParam(required = false, defaultValue = "1", value = "page") int page,
                             Model model) {

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("status").descending());


        Page<Rental> myRentalList =
                rentalService.findMyRentalList(studentService.findStudent((String) model.getAttribute("loginStuId")).getId(), pageRequest);

        model.addAttribute("myRentalList", myRentalList);

        return "rental/findOne";
    }

    @PostMapping("/rental/findOne/{rentalId}/finish")
    public String finishRental(@PathVariable("rentalId") Long rentalId, Model model) {
        if(ChronoUnit.DAYS.between(rentalService.findRentalByRentalId(rentalId).getRentalDate(), LocalDate.now()) > 3){
            model.addAttribute("errorMessage", "연체중인 물품의 반납은 관리자를 통해 가능합니다. \n(관리자 번호) ");
            model.addAttribute("nextUrl", "/rental/myRentalList");
            return "error/errorMessage";
        }
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

        for (Rental rental : rentalByStuIdStatusItem) {
            System.out.println("rental.getId() = " + rental.getId());
            System.out.println("rental.getStatus() = " + rental.getStatus());
            System.out.println("rental.getStudent().getStuId() = " + rental.getStudent().getStuId());
            System.out.println("rental.getItem().getName() = " + rental.getItem().getName());
        }

//        if(rentalSearch.getRentalStatus() == null){
//            model.addAttribute("rentals", rentalService.findByStuId(rentalSearch.getStuId(), pageRequest));
//        } else{
//            model.addAttribute("rentals", rentalService.findByStatus(rentalSearch.getRentalStatus(), pageRequest));
//        }

//        model.addAttribute("rentals", rentalService.findByStatus(rentalSearch.getRentalStatus(), pageRequest));

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
