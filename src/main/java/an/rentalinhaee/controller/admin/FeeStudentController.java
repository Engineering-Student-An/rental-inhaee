package an.rentalinhaee.controller.admin;

import an.rentalinhaee.ReadExcel;
import an.rentalinhaee.domain.FeeStudent;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.StudentSearch;
import an.rentalinhaee.service.FeeStudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
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


        return"admin/student/feeList";
    }

    @GetMapping("/student/feeList/add")
    public String createFeeStudentForm(@RequestParam("newStuId") String stuId, @RequestParam("newName") String name, Model model) {

        String message;

        if(!stuId.isEmpty() && !name.isEmpty()) {   // 학번과 이름 모두 기입

            if(feeStudentService.checkFeeStudentDuplicate(stuId)) { // 동일한 학번이 존재하는 경우
                message = "동일한 학번이 존재합니다!";
            } else {    // 동일한 학번이 존재하지 않는 경우
                feeStudentService.save(new FeeStudent(stuId, name));
                message = "학생회비 납부 명단에 추가 완료했습니다.";
            }

        } else {    // 학번 or 이름 비어있는 경우
            message = "학번과 이름에 공백이 있는지 확인해주세요!";
        }

        model.addAttribute("errorMessage", message);
        model.addAttribute("nextUrl", "/admin/student/feeList");
        return "error/errorMessage";
    }

    @PostMapping("/student/feeList/delete")
    public String deleteFeeStudents(@RequestParam("stuIdList") List<String> stuIdList) {
        for (String stuId : stuIdList) {
            feeStudentService.delete(stuId);
        }
        return "redirect:/admin/student/feeList";
    }

    @PostMapping("/student/feeList/upload")
    public String uploadExcelFile(@RequestParam("excelFile")MultipartFile file, Model model) throws IOException {

        // 업로드 파일이 존재하는지 검증
        if(file == null || file.isEmpty()){
            model.addAttribute("errorMessage", "선택된 파일이 없습니다!");
            model.addAttribute("nextUrl", "/admin/student/feeList");

            return "error/errorMessage";
        }

        // 업로드 된 파일 명
        String fileName = file.getOriginalFilename();

        // 파일 명이 .xlsx , .xls 로 끝나는지 (=엑셀파일인지) 검증
        if (fileName != null && !fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            model.addAttribute("errorMessage", "엑셀 파일을 업로드해주세요!");
            model.addAttribute("nextUrl", "/admin/student/feeList");

            return "error/errorMessage";
        }


        List<FeeStudent> feeStudents = ReadExcel.uploadExcel(file);
        feeStudentService.deleteAll();
        for (FeeStudent feeStudent : feeStudents) {
            feeStudentService.save(feeStudent);
        }

        return "redirect:/admin/student/feeList";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {
        if(session.getAttribute("loginStudent") != null) {
            return (Student) session.getAttribute("loginStudent");
        }
        return null;
    }
}
