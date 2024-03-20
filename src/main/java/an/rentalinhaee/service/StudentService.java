package an.rentalinhaee.service;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.dto.ChangePasswordRequest;
import an.rentalinhaee.domain.dto.JoinRequest;
import an.rentalinhaee.domain.dto.LoginRequest;
import an.rentalinhaee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * 학번 중복 검증
     * True면 중복
     */
    public boolean checkStuIdDuplicate(String stuId){
        return studentRepository.existsByStuId(stuId);
    }

    /**
     * 회원가입
     */
    public void join(JoinRequest joinRequest){
        studentRepository.save(joinRequest.toEntity());
    }

    // 비밀번호 변경
    public void changePassword(String stuId, ChangePasswordRequest request) {
        Student student = studentRepository.findByStuId(stuId);
        student.editPassword(request.getChangePassword());
    }

    /**
     * 로그인
     */
    public Student login(LoginRequest loginRequest) {
        Student student = studentRepository.findByStuId(loginRequest.getStuId());

        if (student == null) {
            return null;
        }

        if(!student.getPassword().equals(loginRequest.getPassword())){
            return null;
        }

        return student;
    }

    /**
     * 학생 단건 조회
     */
    public Student findStudent(String stuId){
        return studentRepository.findByStuId(stuId);

    }

    public Page<Student> findStudentContainingStuIdAndName(String stuId, String name, Pageable pageable){
        return studentRepository.findStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
    }

    /**
     * 학생 전체 조회
     */
    public Page<Student> findAllStudent(Pageable pageable){
        return studentRepository.findAll(pageable);
    }


}
