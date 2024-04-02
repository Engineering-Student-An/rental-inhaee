package an.rentalinhaee.service;

import an.rentalinhaee.domain.FeeStudent;
import an.rentalinhaee.repository.FeeStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStudentService {

    private final FeeStudentRepository feeStudentRepository;

    // 학생회비 납부 명단 학번, 이름 포함 조회
    public Page<FeeStudent> findFeeStudentContainingStuIdAndName(String stuId, String name, Pageable pageable) {
        return feeStudentRepository.findFeeStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
    }

    // 학생회비 납부 명단 전체 조회
    public Page<FeeStudent> findAllFeeStudent(Pageable pageable) {
        return feeStudentRepository.findAll(pageable);
    }

    public boolean checkFeeStudentDuplicate(String stuID) {
        return feeStudentRepository.existsByStuId(stuID);
    }


    @Transactional
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

    @Transactional
    public void delete(String stuId) {
        feeStudentRepository.delete(feeStudentRepository.findByStuId(stuId));
    }

    @Transactional
    public void deleteAll() {
        feeStudentRepository.deleteAll();
    }

}
