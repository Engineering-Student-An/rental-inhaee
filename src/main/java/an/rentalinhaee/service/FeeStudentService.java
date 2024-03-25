package an.rentalinhaee.service;

import an.rentalinhaee.domain.FeeStudent;
import an.rentalinhaee.repository.FeeStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStudentService {

    private final FeeStudentRepository feeStudentRepository;

    // 학생회비 납부 명단 단건 추가
    public void addFeeStudent(String stuId, String name) {
        feeStudentRepository.save(new FeeStudent(stuId, name));
    }

    // 학생회비 납부 명단 엑셀 업로드 => 다중 객체 추가


    // 학생회비 납부 명단 학번, 이름 포함 조회
    public Page<FeeStudent> findFeeStudentContainingStuIdAndName(String stuId, String name, Pageable pageable) {
        return feeStudentRepository.findFeeStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
    }

    // 학생회비 납부 명단 전체 조회
    public Page<FeeStudent> findAllFeeStudent(Pageable pageable) {
        return feeStudentRepository.findAll(pageable);
    }

    public void save(FeeStudent feeStudent) {
        feeStudentRepository.save(feeStudent);
    }

    public List<String> findOne(String stuId) {
        FeeStudent findStudent = feeStudentRepository.findByStuId(stuId);
        List<String> result = new ArrayList<>();

        if(findStudent == null) {
            result.add("");
            result.add("");
            return result;
        }

        result.add(findStudent.getStuId());
        result.add(findStudent.getName());

        return result;
    }

    public void delete(String stuId) {
        feeStudentRepository.delete(feeStudentRepository.findByStuId(stuId));
    }

    public void deleteAll() {
        feeStudentRepository.deleteAll();
    }

    // 학생회비 납부 명단 학번, 이름 포함 조회
//    public Page<Student> findFeeStudent(String stuId, String name, Pageable pageable){
//        List<Student> allList = ReadExcel.readAllExcel();
//        List<Student> findList = new ArrayList<>();
//
//        for (Student student : allList) {
//            if(student.getStuId().contains(stuId) && student.getName().contains(name)) {
//                findList.add(student);
//            }
//        }
//        return convertListToPage(findList, pageable.getPageNumber(), pageable.getPageSize());
//    }

    // 학생회비 납부 명단 전체 조회
//    public Page<Student> findFeeList(Pageable pageable){
//        List<Student> findList = ReadExcel.readAllExcel();
//        return convertListToPage(findList, pageable.getPageNumber(), pageable.getPageSize());
//    }
//
//    public Page<Student> convertListToPage(List<Student> list, int page, int size) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        int start = (int) pageRequest.getOffset();
//        int end = Math.min((start + pageRequest.getPageSize()), list.size());
//        return new PageImpl<>(list.subList(start, end), pageRequest, list.size());
//    }
}
