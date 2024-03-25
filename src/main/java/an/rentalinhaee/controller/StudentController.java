package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RentalSearch;
import an.rentalinhaee.repository.StudentSearch;
import an.rentalinhaee.service.FeeStudentService;
import an.rentalinhaee.service.RentalService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final RentalService rentalService;

    @GetMapping("/student/list")
    public String list(@ModelAttribute("studentSearch") StudentSearch studentSearch,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                       Model model) {

        PageRequest pageRequest;

        // 학번, 이름 모두 포함하는 학생들 추가
        if (studentSearch.getStuId() != null && studentSearch.getName() != null) {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            model.addAttribute("students", studentService.findStudentContainingStuIdAndName(studentSearch.getStuId(), studentSearch.getName(), pageRequest));
        } else {
            pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            model.addAttribute("students", studentService.findAllStudent(pageRequest));
        }
        model.addAttribute("studentSearch", studentSearch);
        return"student/list";
    }

    @GetMapping("/student/{stuId}/find")
    public String findOneStudent(@PathVariable("stuId") String stuId, Model model,
                                 @ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                                 @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                                 HttpServletRequest httpServletRequest) {

        Student student = studentService.findStudent(stuId);
        if(rentalSearch.getStuId()==null) rentalSearch.setStuId(stuId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("status", "rentalDate").descending());
        Page<Rental> rentalList;
        if(rentalSearch.getRentalStatus() == null){
            rentalList = rentalService.findMyRentalList(student.getId(), pageRequest);
        } else{
            rentalList = rentalService.findRentals(rentalSearch, pageRequest);
        }

        model.addAttribute("student", student);
        model.addAttribute("rentalList", rentalList);
        HttpSession httpSession = httpServletRequest.getSession(true);
        model.addAttribute("adminPassword", studentService.findStudent(loginStuId(httpSession)).getPassword());
        return "student/studentInfo";
    }

    private final FeeStudentService feeStudentService;


    @GetMapping("/student/{stuId}/delete")
    public String deleteStudent(@PathVariable("stuId") String stuId, Model model) {
        if(studentService.delete(stuId)) {
            model.addAttribute("errorMessage", "계정 삭제가 완료되었습니다.\n대여 정보, 게시글, 댓글이 모두 삭제되었습니다.");
            model.addAttribute("nextUrl", "/student/list");
        } else {
            model.addAttribute("errorMessage", "대여중인 물품을 모두 반납하고 계정 삭제를 진행해주세요!");
            model.addAttribute("nextUrl", "/student/" + stuId + "/find");

        }
        return "error/errorMessage";

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
