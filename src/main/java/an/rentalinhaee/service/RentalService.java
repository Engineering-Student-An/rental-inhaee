package an.rentalinhaee.service;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.RentalStatus;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;

    // 대여 시작
    @Transactional
    public Long rental(String stuId, Long itemId) {
        // 엔티티 조회
        Student student = studentRepository.findByStuId(stuId);
        Item item = itemRepository.findItemById(itemId);

        // 대여 생성
        Rental rental = Rental.createRental(student, item);

        // 대여 저장
        rentalRepository.save(rental);

        return rental.getId();
    }

    // 대여 종료
    @Transactional
    public void finishRental(Long rentalId) {
        Rental rental = rentalRepository.findOneById(rentalId);
        rental.finish();
    }

    public Rental findRentalByRentalId(Long rentalId){
        return rentalRepository.findOneById(rentalId);
    }

    // 특정 학생의 렌탈 리스트 검색
    public Page<Rental> findMyRentalList(Long id, Pageable pageable) {
        return rentalRepository.findRentalsByStudent_Id(id, pageable);
    }

    // 특정 학생의 특정 아이템 렌탈 리스트 검색
    public Boolean existsByStudentIdAndItemId(Long studentId, Long itemId) {
        Collection<RentalStatus> collection = new ArrayList<>();
        collection.add(RentalStatus.FINISH);
        collection.add(RentalStatus.FINISH_OVERDUE);

        return rentalRepository.existsByStudent_IdAndItemIdAndStatusNotIn(studentId, itemId, collection);
    }

    // 대여 리스트 검색
    public Page<Rental> findRentals(RentalSearch rentalSearch, Pageable pageable) {
        Student student = studentRepository.findByStuId(rentalSearch.getStuId());
        Long id = null;
        if(student != null) id = student.getId();

        return rentalRepository.findRentalsByStatusAndStudent_Id(rentalSearch.getRentalStatus(), id, pageable);
    }

    public Page<Rental> findAll(Pageable pageable){
        return rentalRepository.findAll(pageable);
    }


    private final RentalQueryRepository rentalQueryRepository;
    public Page<Rental> findRentalByStuId_Status_Item(RentalSearch rentalSearch, Pageable pageable) {
        return rentalQueryRepository.findByStuId_Status_ItemName(rentalSearch, pageable);

    }
}