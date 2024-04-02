package an.rentalinhaee.controller.admin;

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

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRentalController {

    private final RentalService rentalService;
    private final StudentService studentService;
    private final ItemService itemService;




//    @GetMapping("/rental/findOne")
//    public String rentalList(@RequestParam(required = false, defaultValue = "1", value = "page") int page,
//                             @ModelAttribute("rentalSearch") RentalSearch rentalSearch,
//                             Model model) {
//
//        Student student = (Student) model.getAttribute("loginStudent");
//
//        rentalSearch.setStuId(student.getStuId());
//
//        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("status").and(Sort.by("rentalDate")));
//        Page<Rental> rentalList;
//        if(rentalSearch.getRentalStatus() == null){
//            rentalList = rentalService.findMyRentalList(student.getId(), pageRequest);
//        } else{
//            rentalList = rentalService.findRentals(rentalSearch, pageRequest);
//        }
//
//        model.addAttribute("myRentalList", rentalList);
//
//        return "rental/findOne";
//    }
//
//    @PostMapping("/rental/findOne/{rentalId}/finish")
//    public String finishRental(@PathVariable("rentalId") Long rentalId, Model model) {
//        if(ChronoUnit.DAYS.between(rentalService.findRentalByRentalId(rentalId).getRentalDate(), LocalDate.now()) > 3){
//            model.addAttribute("errorMessage", "연체중인 물품의 반납은 관리자를 통해 가능합니다.");
//            model.addAttribute("nextUrl", "/rental/findOne");
//            model.addAttribute("newUrl", "https://pf.kakao.com/_CxjxlxiM");
//            return "error/errorMessageNewTab";
//        }
//
//        model.addAttribute("errorMessage", "해당 물품을 반납 처리 하시겠습니까?\n반납이 이루어지지 않았을 경우, 불이익이 주어질 수 있습니다!");
//
//        model.addAttribute("yesUrl","/rental/findOne/" + rentalId + "/finish/confirm");
//        model.addAttribute("noUrl","/rental/findOne");
//        return "error/yesOrNoMessage";
//    }
//
//    @GetMapping("/rental/findOne/{rentalId}/finish/confirm")
//    public String finishRentalConfirm(@PathVariable("rentalId") Long rentalId) {
//        rentalService.finishRental(rentalId);
//        return "redirect:/rental/findOne";
//    }

    @PostMapping("/rental/{rentalId}/finish")
    public String adminFinishRental(@PathVariable("rentalId") Long rentalId, @RequestParam String stuId, RedirectAttributes redirectAttributes) {

        rentalService.finishRental(rentalId);

        redirectAttributes.addAttribute("stuId", stuId);
        return "redirect:/admin/student/{stuId}";
    }

    @GetMapping("/rental/list")
    public String allList(@ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model){

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("rentalDate").descending().and(Sort.by("status").descending()));


        Page<Rental> rentalByStuIdStatusItem = rentalService.findRentalByStuId_Status_Item(rentalSearch, pageRequest);
        model.addAttribute("rentals", rentalByStuIdStatusItem);

        return "admin/rental/list";
    }


    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
