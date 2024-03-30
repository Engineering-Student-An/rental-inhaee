package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // rental_id 로 Rental 하나 조회
    Rental findOneById(Long id);

    // rentalStatus로 여러개 조회
    Page<Rental> findRentalsByStatus(RentalStatus rentalStatus, Pageable pageable);

    List<Rental> findRentalsByStatus(RentalStatus rentalStatus);

    // student로 여러개 조회
    Page<Rental> findRentalsByStudent_Id(Long id, Pageable pageable);

    Boolean existsByStudent_IdAndItemIdAndStatusNotIn(Long student_id, Long item_id, Collection<RentalStatus> status);

    Page<Rental> findRentalsByStudentStuIdContaining(String stuId, Pageable pageable);

    // rentalStatus + studentId로 여러개 조회
    Page<Rental> findRentalsByStatusAndStudent_Id(RentalStatus rentalStatus, Long id, Pageable pageable);

    void deleteRentalByItem_Id(Long itemId);

    Boolean existsByStudent_IdAndStatusNot(Long id, RentalStatus status);

    List<Rental> findRentalsByStudent_IdAndStatus(Long id, RentalStatus status);

    boolean existsByStudent_IdAndStatusNotIn(Long id, Collection<RentalStatus> status);

    List<Rental> findRentalsByStudent_IdAndStatusIn(Long id, Collection<RentalStatus> status);
}
