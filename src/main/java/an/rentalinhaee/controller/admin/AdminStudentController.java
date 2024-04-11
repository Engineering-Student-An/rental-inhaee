package an.rentalinhaee.controller.admin;


import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.StudentRole;
import an.rentalinhaee.repository.RentalSearch;
import an.rentalinhaee.repository.StudentSearch;
import an.rentalinhaee.service.RentalService;
import an.rentalinhaee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminStudentController {

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

        if((boolean) model.getAttribute("isMobile")) {
            return "mobile/admin/student/list";
        }
        return"admin/student/list";
    }

    @GetMapping("/student/{stuId}")
    public String findOneStudent(@PathVariable("stuId") String stuId, Model model,
                                 @ModelAttribute("rentalSearch") RentalSearch rentalSearch,
                                 @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                                 HttpServletRequest httpServletRequest) {

        Student student = studentService.findStudent(stuId);
        if(rentalSearch.getStuId()==null) rentalSearch.setStuId(stuId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("status").and(Sort.by("rentalDate")));
        Page<Rental> rentalList;
        if(rentalSearch.getRentalStatus() == null){
            rentalList = rentalService.findMyRentalList(student.getId(), pageRequest);
        } else{
            rentalList = rentalService.findRentals(rentalSearch, pageRequest);
        }

        model.addAttribute("student", student);
        model.addAttribute("rentalList", rentalList);
        HttpSession httpSession = httpServletRequest.getSession(true);
        model.addAttribute("adminPassword", ((Student) model.getAttribute("loginStudent")).getPassword());

        if ((boolean) model.getAttribute("isMobile")) {
            return "mobile/admin/student/studentInfo";
        }
        return "admin/student/studentInfo";
    }



    @PostMapping("/student/{stuId}/delete")
    public String deleteStudent(@PathVariable("stuId") String stuId, @RequestParam("password") String password, Model model) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(studentService.passwordCheck(((Student) model.getAttribute("loginStudent")).getStuId() ,password)){
            if(studentService.findStudent(stuId).getRole().equals(StudentRole.ADMIN) ) {
                model.addAttribute("errorMessage", "관리자 계정은 삭제할 수 없습니다.");
                model.addAttribute("nextUrl", "/admin/student/list");
                return "error/errorMessage";
            }
            if(studentService.delete(stuId)) {
                model.addAttribute("errorMessage", "계정 삭제가 완료되었습니다.\n대여 정보, 게시글, 댓글이 모두 삭제되었습니다.");
                model.addAttribute("nextUrl", "/admin/student/list");
            } else {
                model.addAttribute("errorMessage", "대여중인 물품을 모두 반납하고 계정 삭제를 진행해주세요!");
                model.addAttribute("nextUrl", "/admin/student/" + stuId);

            }
        } else {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다!");
            model.addAttribute("nextUrl", "/admin/student/" + stuId);
        }

        return "error/errorMessage";

    }


    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }

    @ModelAttribute("isMobile")
    public boolean isMobile(HttpSession session) {
        if(session.getAttribute("isMobile") != null) {
            return (boolean) session.getAttribute("isMobile");
        }
        return false;
    }
}
