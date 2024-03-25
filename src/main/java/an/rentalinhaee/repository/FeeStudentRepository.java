package an.rentalinhaee.repository;

import an.rentalinhaee.domain.FeeStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeStudentRepository extends JpaRepository<FeeStudent, Long> {

    Page<FeeStudent> findFeeStudentByStuIdContainingAndNameContaining(String stuId, String name, Pageable pageable);

    Page<FeeStudent> findAll(Pageable pageable);

    FeeStudent findByStuId(String stuId);
}
