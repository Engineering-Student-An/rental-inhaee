package an.rentalinhaee.repository;

import an.rentalinhaee.domain.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOrderByIdDesc();

    ItemRequest findItemRequestById(Long id);
}
