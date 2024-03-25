package an.rentalinhaee.controller;

import an.rentalinhaee.domain.FeeStudent;
import an.rentalinhaee.repository.StudentSearch;
import an.rentalinhaee.service.FeeStudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FeeStudentController {

    private final FeeStudentService feeStudentService;


    @GetMapping("/student/feeList")
    public String feeList(@ModelAttribute("studentSearch") StudentSearch studentSearch,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model) {

        PageRequest pageRequest;

        // 학번, 이름 모두 포함하는 학생들 추가
        if (studentSearch.getStuId() != null && studentSearch.getName() != null) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId"));
            model.addAttribute("students", feeStudentService.findFeeStudentContainingStuIdAndName(studentSearch.getStuId(), studentSearch.getName(), pageRequest));
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId"));
            model.addAttribute("students", feeStudentService.findAllFeeStudent(pageRequest));

        }
        model.addAttribute("studentSearch", studentSearch);


        return"student/feeList";
    }

    @GetMapping("/student/feeList/add")
    public String createFeeStudentForm(@RequestParam("newStuId") String stuId, @RequestParam("newName") String name, Model model) {

        // writeExcel
        if(!stuId.isEmpty() && !name.isEmpty()) {
//            WriteExcel.writeToExcel(stuId, name);
            feeStudentService.save(new FeeStudent(stuId, name));
        } else {    // 학번 or 이름 비어있는 경우
            model.addAttribute("errorMessage", "학번과 이름에 공백이 있는지 확인해주세요!");
            model.addAttribute("nextUrl", "/student/feeList");
            return "error/errorMessage";
        }

        return "redirect:/student/feeList";
    }

    @PostMapping("/student/feeList/delete")
    public String deleteFeeStudents(@RequestParam("stuIdList") List<String> stuIdList) {
        for (String stuId : stuIdList) {
            feeStudentService.delete(stuId);
        }
        return "redirect:/student/feeList";
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
