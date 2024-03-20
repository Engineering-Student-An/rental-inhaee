package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository <Student, Long> {

    boolean existsByStuId(String stuId);

    Student findByStuId(String stuId);

    // 학번과 이름 모두 포함하는 학생 찾음
    Page<Student> findStudentByStuIdContainingAndNameContaining(String stuId, String name, Pageable pageable);

    // 모든 학생 찾음
    Page<Student> findAll(Pageable pageable);
}
