package an.rentalinhaee.service;

import an.rentalinhaee.ReadExcel;
import an.rentalinhaee.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStudentService {

    // 학생회비 납부 명단 (학번, 이름 포함)
    public Page<Student> findFeeStudent(String stuId, String name, Pageable pageable){
        List<Student> allList = ReadExcel.readAllExcel();
        List<Student> findList = new ArrayList<>();

        for (Student student : allList) {
            if(student.getStuId().contains(stuId) && student.getName().contains(name)) {
                findList.add(student);
            }
        }
        return convertListToPage(findList, pageable.getPageNumber(), pageable.getPageSize());
    }

    // 학생회비 납부 전체 명단
    public Page<Student> findFeeList(Pageable pageable){
        List<Student> findList = ReadExcel.readAllExcel();
        return convertListToPage(findList, pageable.getPageNumber(), pageable.getPageSize());
    }

    public Page<Student> convertListToPage(List<Student> list, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageRequest, list.size());
    }
}
