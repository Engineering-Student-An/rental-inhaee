package an.rentalinhaee.controller.admin;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RentalSearch;
import an.rentalinhaee.service.RentalService;
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

